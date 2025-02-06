#!/bin/bash
# Set environment variables for Java (Render does not support sudo)
export JAVA_HOME=/opt/java/openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Install Java (if not already installed)
if [ ! -d "$JAVA_HOME" ]; then
    curl -L https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o java.tar.gz
    mkdir -p /opt/java
    tar -xzf java.tar.gz -C /opt/java
    mv /opt/java/jdk-17 /opt/java/openjdk
fi

# Verify Java installation
java -version

# Compile Java file
javac -cp "py4j0.10.9.9.jar:." Combine.java

# Start Java Gateway in the background
nohup java -cp "py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

# Allow Java some time to start
sleep 5

# Install dependencies in case they are missing
pip install --no-cache-dir -r requirements.txt

# Start Python web app with Gunicorn (5 workers, 5 min timeout)
gunicorn -w 5 -t 300 -b 0.0.0.0:5000 app:app
