#!/bin/bash
set -euo pipefail

export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"
PY4J_JAR="$(ls vendor/py4j/py4j*.jar | head -n1)"

# Launch Java gateway
java -cp "$PY4J_JAR:." Combine > java_server.log 2>&1 &

# Wait for gateway (adjust port if your Combine uses a different one)
for i in {1..60}; do (echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 && break; sleep 1; done
(echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 || { tail -n 200 java_server.log; exit 1; }

# Run Flask app via Gunicorn on Render’s $PORT
: "${PORT:=8000}"
exec gunicorn --bind 0.0.0.0:"$PORT" app:app
