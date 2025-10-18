#!/usr/bin/env bash
set -euo pipefail

if [ ! -d /opt/java-17 ]; then
  curl -fsSL https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o /tmp/java.tar.gz
  mkdir -p /opt/java-17
  tar -xzf /tmp/java.tar.gz -C /opt/java-17 --strip-components=1
fi

/opt/java-17/bin/javac --release 17 -cp "py4j0.10.9.9.jar:." Combine.java

pip install --no-cache-dir -r requirements.txt
