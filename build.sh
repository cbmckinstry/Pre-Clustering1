#!/usr/bin/env bash
set -euo pipefail

JAVA_DIR="$HOME/java-17"

# Install JDK (only once — persists in Render cache)
if [ ! -d "$JAVA_DIR" ]; then
  echo "Installing Java 17 in $JAVA_DIR ..."
  curl -fsSL https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o /tmp/java.tar.gz
  mkdir -p "$JAVA_DIR"
  tar -xzf /tmp/java.tar.gz -C "$JAVA_DIR" --strip-components=1
fi

# Compile Java class at build time
"$JAVA_DIR/bin/javac" --release 17 -cp "py4j0.10.9.9.jar:." Combine.java

# Install Python deps
pip install --no-cache-dir -r requirements.txt
