#!/bin/bash
set -euo pipefail

export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"

# Start the Java gateway in the background
java -cp "vendor/py4j/py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

# Wait for the Py4J gateway to accept connections on port 25333 (adjust if your Combine uses another port)
echo "Waiting for Java gateway on 25333..."
for i in {1..60}; do
  (echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 && break
  sleep 1
done

# If it never came up, show the log for fast debugging
(echo > /dev/tcp/127.0.0.1/25333) >/dev/null 2>&1 || {
  echo "Java gateway failed to start. Tail of java_server.log:";
  tail -n 200 java_server.log;
  exit 1;
}

# Launch your Python web app
exec gunicorn app:app
