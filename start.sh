#!/usr/bin/env bash
set -euo pipefail

# -------- Settings --------
JDK_DIR="vendor/java"
PY4J_JAR="py4j0.10.9.9.jar"
CLASS_DIR="build/classes"
JAVA_MAIN_CLASS="Combine"

# Render exposes the port via $PORT (preferred) or $RENDER_PORT; default to 5000 locally
APP_PORT="${PORT:-${RENDER_PORT:-5000}}"
GUNICORN_BIND="0.0.0.0:${APP_PORT}"
GUNICORN_WORKERS="${GUNICORN_WORKERS:-1}"
GUNICORN_TIMEOUT="${GUNICORN_TIMEOUT:-900}"
APP_MODULE="${APP_MODULE:-app:app}"

mkdir -p logs

# -------- Java setup --------
export JAVA_HOME="$(pwd)/${JDK_DIR}"
export PATH="${JAVA_HOME}/bin:${PATH}"

echo "==> Using JAVA_HOME=${JAVA_HOME}"
java -version || { echo "Java not available. Did build.sh run?"; exit 1; }

# -------- Start Java Gateway (precompiled) --------
CLASSPATH="${PY4J_JAR}:${CLASS_DIR}"
PY4J_PORT="${PY4J_PORT:-25333}"

echo "==> Launching Java gateway: ${JAVA_MAIN_CLASS} (port ${PY4J_PORT})"
# Start in background (no lsof; rely on port-wait loop)
nohup java -cp "${CLASSPATH}" "${JAVA_MAIN_CLASS}" > logs/java_server.log 2>&1 &

# -------- Wait for the gateway to listen (no lsof dependency) --------
echo "==> Waiting for Py4J gateway on port ${PY4J_PORT} ..."
ATTEMPTS=50
until (exec 3<>/dev/tcp/127.0.0.1/${PY4J_PORT}) >/dev/null 2>&1; do
  ((ATTEMPTS--)) || {
    echo "Java gateway did not open port ${PY4J_PORT} in time."
    echo "Last 100 lines of logs/java_server.log:"
    tail -n 100 logs/java_server.log || true
    exit 1
  }
  sleep 0.2
done
exec 3>&- || true
echo "==> Py4J gateway is up."

# -------- Ensure Gunicorn is available (fallback if build step missed it) --------
if ! python -c "import gunicorn" >/dev/null 2>&1; then
  echo "==> Gunicorn not found; installing fallback (consider adding it to requirements.txt)"
  pip install --no-cache-dir gunicorn || {
    echo "Failed to install gunicorn."
    exit 1
  }
fi

# -------- Start Gunicorn --------
echo "==> Starting Gunicorn (${APP_MODULE}) on ${GUNICORN_BIND}"
exec gunicorn \
  -w "${GUNICORN_WORKERS}" \
  -t "${GUNICORN_TIMEOUT}" \
  -b "${GUNICORN_BIND}" \
  "${APP_MODULE}"
