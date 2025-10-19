#!/bin/bash
export JAVA_HOME=/tmp/java
export PATH=$JAVA_HOME/bin:$PATH

if ! command -v java &> /dev/null; then
    echo "Installing Java..."
    curl -L https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o java.tar.gz
    mkdir -p /tmp/java
    tar -xzf java.tar.gz -C /tmp/java --strip-components=1
fi

java -version || echo "Java installation failed."

# Compile Java files
javac -cp "py4j0.10.9.9.jar:." Combine.java || echo "Java compilation failed."

# Start Java Gateway Server in the background
nohup java -cp "py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

# Allow Java some time to start
sleep 5

pip install -r requirements.txt redis || echo "Failed to install dependencies."

# Start Gunicorn with a single worker (Render has only 0.1 vCPU)
gunicorn -w 1 -b 0.0.0.0:5000 app:app || echo "Gunicorn failed to start."
