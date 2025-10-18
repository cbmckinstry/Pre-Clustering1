#!/usr/bin/env bash
set -euo pipefail

JAVA_HOME="$HOME/java-17"
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

# Start Java gateway in background
nohup java -Xms32m -Xmx128m -cp "py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

# Wait for Java to open its port
for i in {1..40}; do
  (echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 && break
  sleep 0.25
done

# Start Flask via Gunicorn
exec gunicorn -w 1 --threads 8 -t 900 -b 0.0.0.0:5000 app:app

