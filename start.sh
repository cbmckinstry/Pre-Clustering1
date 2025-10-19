#!/usr/bin/env bash
set -euo pipefail

echo "==> Start phase"

PROJECT_ROOT="$(pwd)"
JAVA_HOME="$PROJECT_ROOT/vendor/java"
PATH="$JAVA_HOME/bin:$PATH"
export JAVA_HOME PATH

BUILD_DIR="$PROJECT_ROOT/build"
CLASSPATH_FILE="$BUILD_DIR/classpath.txt"
LOG_DIR="$PROJECT_ROOT/logs"
mkdir -p "$LOG_DIR"

[[ -x "$JAVA_HOME/bin/java" ]] || { echo "ERROR: JDK missing"; exit 1; }
[[ -f "$CLASSPATH_FILE" ]] || { echo "ERROR: $CLASSPATH_FILE missing"; exit 1; }

CLASSPATH="$(cat "$CLASSPATH_FILE")"
PY4J_JAR="${CLASSPATH%%:*}"
CLASSES_DIR="${CLASSPATH#*:}"

[[ -f "$PY4J_JAR" ]] || { echo "ERROR: py4j jar missing at $PY4J_JAR"; exit 1; }
[[ -d "$CLASSES_DIR" ]] || { echo "ERROR: classes dir missing at $CLASSES_DIR"; exit 1; }

echo "==> Java version:"
java -version

echo "==> Launching Java gateway: Combine"
nohup java -cp "$PY4J_JAR:$CLASSES_DIR" Combine > "$LOG_DIR/java_server.log" 2>&1 &

PY4J_PORT="${PY4J_PORT:-25333}"
echo "==> Waiting for Py4J gateway on port $PY4J_PORT ..."
ATTEMPTS=60
until timeout 1 bash -c "exec 3<>/dev/tcp/127.0.0.1/$PY4J_PORT" 2>/dev/null; do
  ((ATTEMPTS--)) || { echo "ERROR: Gateway did not start"; tail -n 200 "$LOG_DIR/java_server.log" || true; exit 1; }
  sleep 1
done
echo "==> Py4J gateway is up."

if [[ -n "${REDIS_URL:-}" ]]; then echo "==> REDIS_URL is set"; else echo "==> WARNING: REDIS_URL not set"; fi

echo "==> Starting Gunicorn"
exec gunicorn -w 1 -b 0.0.0.0:5000 app:app
