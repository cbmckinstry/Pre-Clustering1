#!/usr/bin/env bash
set -euo pipefail

export JAVA_HOME=/opt/java-17
export PATH="$JAVA_HOME/bin:$PATH"

nohup java -Xms32m -Xmx128m -cp "py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

for i in {1..40}; do
  (echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 && break
  sleep 0.25
done

exec gunicorn -w 1 --threads 8 -t 900 -b 0.0.0.0:5000 app:app
