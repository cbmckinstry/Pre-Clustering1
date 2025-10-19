#!/usr/bin/env bash
set -euo pipefail

echo "==> Start phase"

PROJECT_ROOT="$(pwd)"
JAVA_HOME="$PROJECT_ROOT/vendor/java"
PATH="$JAVA_HOME/bin:$PATH"
export JAVA_HOME PATH

# Resolve Py4J JAR from installed site-packages (works even after “wakeup”)
PY4J_JAR="$(python - <<'PY'
import py4j, pathlib
p = pathlib.Path(py4j.__file__).with_name('py4j0.10.9.9.jar')
print(p)
PY
)"

CLASSES_DIR="$PROJECT_ROOT/build/java_classes"
LOG_DIR="$PROJECT_ROOT/logs"
mkdir -p "$LOG_DIR"

if [[ ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "ERROR: JDK not found at $JAVA_HOME. Did build.sh run?"
  exit 1
fi
if [[ ! -d "$CLASSES_DIR" ]]; then
  echo "ERROR: Compiled classes not found at $CLASSES_DIR. Did build.sh run?"
  exit 1
fi
if [[ ! -f "$PY4J_JAR" ]]; then
  echo "ERROR: Py4J jar not found at $PY4J_JAR. Is py4j installed?"
  exit 1
fi

echo "==> Java version:"
java -version

# Launch Java gateway (your Combine main should start GatewayServer)
echo "==> Launching Java gateway: Combine"
CLASSPATH="$PY4J_JAR:$CLASSES_DIR"
nohup java -cp "$CLASSPATH" Combine > "$LOG_DIR/java_server.log" 2>&1 &

# Wait for the gateway port to be ready (default 25333 unless your code differs)
PY4J_PORT="${PY4J_PORT:-25333}"
echo "==> Waiting for Py4J gateway on port $PY4J_PORT ..."
ATTEMPTS=60
until timeout 1 bash -c "exec 3<>/dev/tcp/127.0.0.1/$PY4J_PORT" 2>/dev/null; do
  ((ATTEMPTS--)) || { echo "ERROR: Gateway did not start in time"; tail -n 200 "$LOG_DIR/java_server.log" || true; exit 1; }
  sleep 1
done
echo "==> Py4J gateway is up."

# Your Flask app should read REDIS_URL from the environment (Render dashboard).
# Nothing to do here as long as app.py uses os.environ['REDIS_URL'] or .get('REDIS_URL').
if [[ -n "${REDIS_URL:-}" ]]; then
  echo "==> REDIS_URL is set"
else
  echo "==> WARNING: REDIS_URL is not set (check Render env vars)"
fi

# Start Gunicorn (keep this light for 0.1 vCPU instances)
echo "==> Starting Gunicorn"
exec gunicorn -w 1 -b 0.0.0.0:5000 app:app
