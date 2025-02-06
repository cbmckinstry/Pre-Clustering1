#!/bin/bash

# Set writable directory for Java
export JAVA_HOME=/tmp/java
export PATH=$JAVA_HOME/bin:$PATH

# Install Java (if not already installed)
if [ ! -d "$JAVA_HOME" ]; then
    curl -L https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o java.tar.gz
    mkdir -p /tmp/java
    tar -xzf java.tar.gz -C /tmp/java --strip-components=1
fi

# Verify Java installation
java -version || echo "Java installation failed."

# Compile Java file
javac -cp "py4j0.10.9.9.jar:." Combine.java || echo "Java compilation failed."

# Start Java Gateway in the background
nohup java -cp "py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

# Allow Java some time to start
sleep 5

# Install Python dependencies
pip install --no-cache-dir -r requirements.txt || echo "Failed to install dependencies."

# Start Python web app with Gunicorn (5 workers, 5 min timeout)
gunicorn -w 5 -t 300 -b 0.0.0.0:5000 app:app || echo "Gunicorn failed to start."
