#!/usr/bin/env bash
set -euo pipefail

echo "==> Build phase starting"

PROJECT_ROOT="$(pwd)"
VENDOR_JAVA_DIR="$PROJECT_ROOT/vendor/java"
BUILD_DIR="$PROJECT_ROOT/build"
BUILD_CLASSES_DIR="$BUILD_DIR/java_classes"
BUILD_LIB_DIR="$BUILD_DIR/lib"
JAVA_TARBALL_URL="https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz"

# 1) JDK into vendor/java (cached in image)
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

# 2) Python deps (cached by Render)
echo "==> Installing Python dependencies"
python -m pip install --upgrade pip
python -m pip install -r requirements.txt

# 3) Find py4j JAR (be flexible on version & path), copy to build/lib/py4j.jar
echo "==> Locating py4j JAR"
mkdir -p "$BUILD_LIB_DIR"
PY4J_JAR_CANDIDATE="$(
  python - <<'PY'
import sys, pathlib, glob, site
# Search likely places (site-packages paths + venv paths)
candidates = []
paths = []
try:
    import py4j
    paths.append(pathlib.Path(py4j.__file__).parent)
except Exception:
    pass

paths += [pathlib.Path(p) for p in site.getsitepackages()]
if sys.prefix:
    paths.append(pathlib.Path(sys.prefix) / "lib" / "python*")
# Glob any py4j*.jar inside any py4j or site-packages directory
for base in paths:
    for jar in base.rglob("py4j*.jar"):
        candidates.append(str(jar))
print(candidates[0] if candidates else "")
PY
)"

if [[ -z "$PY4J_JAR_CANDIDATE" || ! -f "$PY4J_JAR_CANDIDATE" ]]; then
  echo "ERROR: Could not find a py4j*.jar after installing requirements."
  echo "       Ensure 'py4j' is in requirements.txt (e.g., py4j>=0.10.9.9)."
  exit 1
fi

cp -f "$PY4J_JAR_CANDIDATE" "$BUILD_LIB_DIR/py4j.jar"
echo "==> Using py4j: $PY4J_JAR_CANDIDATE -> $BUILD_LIB_DIR/py4j.jar"

# 4) Compile Java sources into build/java_classes
echo "==> Compiling Java"
mkdir -p "$BUILD_CLASSES_DIR"
javac -cp "$BUILD_LIB_DIR/py4j.jar:." -d "$BUILD_CLASSES_DIR" Combine.java

# 5) Persist a classpath file for start.sh
echo "$BUILD_LIB_DIR/py4j.jar:$BUILD_CLASSES_DIR" > "$BUILD_DIR/classpath.txt"

echo "==> Build complete"
echo "    - JDK:        $VENDOR_JAVA_DIR"
echo "    - Classes:    $BUILD_CLASSES_DIR"
echo "    - Py4J jar:   $BUILD_LIB_DIR/py4j.jar"
