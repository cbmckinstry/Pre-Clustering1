#!/bin/bash
set -euo pipefail
export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"

# Pick the jar we have
if [ -f vendor/py4j/py4j-0.10.9.9.jar ]; then
  PY4J_JAR="vendor/py4j/py4j-0.10.9.9.jar"
else
  PY4J_JAR="vendor/py4j/py4j0.10.9.9.jar"
fi

# Start Java gateway
java -cp "$PY4J_JAR:." Combine > java_server.log 2>&1 &

# Wait for gateway port (adjust if your Combine uses a different one)
for i in {1..60}; do (echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 && break; sleep 1; done
(echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 || { tail -n 200 java_server.log; exit 1; }

exec gunicorn app:app
