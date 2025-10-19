#!/usr/bin/env bash
set -euo pipefail

echo "==> Build phase starting"

# Paths (Render checks out your repo to /opt/render/project/src at build time)
PROJECT_ROOT="$(pwd)"
VENDOR_JAVA_DIR="$PROJECT_ROOT/vendor/java"
JAVA_TARBALL_URL="https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz"

# 1) Install JDK 17 into vendor/java (baked into the build image)
if [[ ! -x "$VENDOR_JAVA_DIR/bin/java" ]]; then
  echo "==> Downloading JDK 17 to vendor/java..."
  mkdir -p "$VENDOR_JAVA_DIR"
  curl -fsSL "$JAVA_TARBALL_URL" -o /tmp/java.tar.gz
  tar -xzf /tmp/java.tar.gz -C "$VENDOR_JAVA_DIR" --strip-components=1
  rm -f /tmp/java.tar.gz
else
  echo "==> Reusing cached JDK at vendor/java"
fi

export JAVA_HOME="$VENDOR_JAVA_DIR"
export PATH="$JAVA_HOME/bin:$PATH"
java -version

# 2) Install Python dependencies into Render's .venv (cached across builds)
echo "==> Installing Python dependencies"
python -V || true
pip -V || true
pip install --upgrade pip
pip install -r requirements.txt

# 3) Find the Py4J jar from the installed package
echo "==> Locating Py4J JAR"
PY4J_JAR="$(python - <<'PY'
import py4j, pathlib
p = pathlib.Path(py4j.__file__).with_name('py4j0.10.9.9.jar')
print(p)
PY
)"
if [[ ! -f "$PY4J_JAR" ]]; then
  echo "ERROR: Could not find py4j jar at $PY4J_JAR"
  exit 1
fi
echo "==> Using Py4J JAR: $PY4J_JAR"

# 4) Compile Java sources into build/java_classes
echo "==> Compiling Java"
BUILD_CLASSES_DIR="$PROJECT_ROOT/build/java_classes"
mkdir -p "$BUILD_CLASSES_DIR"

# If your Combine.java is not in repo root, adjust the path below.
javac -cp "$PY4J_JAR:." -d "$BUILD_CLASSES_DIR" Combine.java

echo "==> Build complete. Artifacts:"
echo "    - JDK:           $VENDOR_JAVA_DIR"
echo "    - Classes:       $BUILD_CLASSES_DIR"
echo "    - Py4J jar:      $PY4J_JAR"
