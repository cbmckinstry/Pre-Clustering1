#!/usr/bin/env bash
set -euo pipefail

# -------- Settings (match build.sh) --------
JDK_DIR="vendor/java"
PY4J_JAR="py4j0.10.9.9.jar"
CLASS_DIR="build/classes"
JAVA_MAIN_CLASS="Combine"                 # class with your GatewayServer
PY4J_PORT="${PY4J_PORT:-25333}"           # default Py4J port (configurable via env)
GUNICORN_BIND="${GUNICORN_BIND:-0.0.0.0:5000}"
GUNICORN_WORKERS="${GUNICORN_WORKERS:-1}"
GUNICORN_TIMEOUT="${GUNICORN_TIMEOUT:-900}"  # long because Render tiny CPU
APP_MODULE="${APP_MODULE:-app:app}"

mkdir -p logs

# -------- Use the cached JDK from build --------
export JAVA_HOME="$(pwd)/${JDK_DIR}"
export PATH="${JAVA_HOME}/bin:${PATH}"

echo "==> Using JAVA_HOME=${JAVA_HOME}"
java -version || { echo "Java not available. Did build.sh run?"; exit 1; }

# -------- Start Java Gateway (precompiled) --------
CLASSPATH="${PY4J_JAR}:${CLASS_DIR}"

echo "==> Launching Java gateway: ${JAVA_MAIN_CLASS}"
# If a previous instance exists (e.g., hot restarts), try to kill it gracefully
if lsof -iTCP -sTCP:LISTEN -P | grep -q ":${PY4J_PORT}\b"; then
  echo "Detected an existing process on port ${PY4J_PORT}; attempting to stop it..."
  # Best-effort kill by port (Linux)
  PID_TO_KILL="$(lsof -t -iTCP:${PY4J_PORT} -sTCP:LISTEN || true)"
  if [[ -n "${PID_TO_KILL}" ]]; then kill "${PID_TO_KILL}" || true; fi
  sleep 1
fi

# Start in background
nohup java -cp "${CLASSPATH}" "${JAVA_MAIN_CLASS}" > logs/java_server.log 2>&1 &

# -------- Wait for the gateway to listen (robust vs fixed sleep) --------
echo "==> Waiting for Py4J gateway on port ${PY4J_PORT} ..."
ATTEMPTS=25
until (echo > /dev/tcp/127.0.0.1/${PY4J_PORT}) >/dev/null 2>&1; do
  ((ATTEMPTS--)) || {
    echo "Java gateway did not open port ${PY4J_PORT} in time."
    echo "Last 50 lines of logs/java_server.log:"
    tail -n 50 logs/java_server.log || true
    exit 1
  }
  sleep 0.4
done
echo "==> Py4J gateway is up."

# -------- Start Gunicorn (Python app) --------
echo "==> Starting Gunicorn (${APP_MODULE}) on ${GUNICORN_BIND}"
exec gunicorn \
  -w "${GUNICORN_WORKERS}" \
  -t "${GUNICORN_TIMEOUT}" \
  -b "${GUNICORN_BIND}" \
  "${APP_MODULE}"
