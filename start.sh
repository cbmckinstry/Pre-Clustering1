#!/bin/bash
sudo apt update && sudo apt install -y openjdk-17-jdk

# Compile Java file
javac -cp "py4j0.10.9.9.jar:." Combine.java

# Start Java Gateway in the background
nohup java -cp "py4j0.10.9.9.jar:." Combine > java_server.log 2>&1 &

# Allow Java some time to start
sleep 5

# Start Python web app with Gunicorn (5 workers, 5 min timeout)
gunicorn -w 5 -t 300 -b 0.0.0.0:5000 app:app
