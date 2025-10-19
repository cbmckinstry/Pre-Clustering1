#!/usr/bin/env bash
set -euo pipefail

echo "==> Build phase starting"

PROJECT_ROOT="$(pwd)"
VENDOR_JAVA_DIR="$PROJECT_ROOT/vendor/java"
BUILD_DIR="$PROJECT_ROOT/build"
BUILD_CLASSES_DIR="$BUILD_DIR/java_classes"
BUILD_LIB_DIR="$BUILD_DIR/lib"
JAVA_TARBALL_URL="https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz"

mkdir -p "$BUILD_LIB_DIR" "$BUILD_CLASSES_DIR"

# 1) JDK 17 into vendor/java (cached in image)
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

# 3) Resolve a py4j*.jar
echo "==> Resolving py4j JAR"
FOUND_JAR=""

# (a) Respect explicit override
if [[ -n "${PY4J_JAR:-}" && -f "$PY4J_JAR" ]]; then
  FOUND_JAR="$PY4J_JAR"
  echo "    Using PY4J_JAR from env: $FOUND_JAR"
fi

# (b) Look in repo root for a jar file
if [[ -z "$FOUND_JAR" ]]; then
  ROOT_JAR="$(ls -1 "$PROJECT_ROOT"/py4j*.jar 2>/dev/null | head -n 1 || true)"
  if [[ -n "$ROOT_JAR" ]]; then
    FOUND_JAR="$ROOT_JAR"
    echo "    Found py4j jar in repo root: $FOUND_JAR"
  fi
fi

# (c) Ask Python where py4j lives, then glob for any py4j*.jar under site-packages
if [[ -z "$FOUND_JAR" ]]; then
  FOUND_JAR="$(python - <<'PY'
import sys, pathlib, site, sysconfig
cands = []

# Try import then search near it
try:
    import py4j
    base = pathlib.Path(py4j.__file__).parent
    for j in base.rglob("py4j*.jar"):
        cands.append(j)
except Exception:
    pass

# Site-packages (system + user + venv)
for p in set(site.getsitepackages() + [site.getusersitepackages()]):
    pth = pathlib.Path(p)
    if pth.exists():
        for j in pth.rglob("py4j*.jar"):
            cands.append(j)

# Fallback: sysconfig purelib path
try:
    pure = pathlib.Path(sysconfig.get_paths().get("purelib",""))
    if pure.exists():
        for j in pure.rglob("py4j*.jar"):
            cands.append(j)
except Exception:
    pass

print(cands[0] if cands else "")
PY
)"
  if [[ -n "$FOUND_JAR" ]]; then
    echo "    Found py4j jar via Python: $FOUND_JAR"
  fi
fi

if [[ -z "$FOUND_JAR" || ! -f "$FOUND_JAR" ]]; then
  echo "ERROR: Could not find a py4j*.jar."
  echo "  Tips:"
  echo "   - Ensure 'py4j' is in requirements.txt (e.g., py4j>=0.10.9.9)."
  echo "   - Or place the jar in repo root and name it like 'py4j0.10.9.9.jar'."
  echo "   - Or set PY4J_JAR to an absolute path."
  exit 1
fi

# Copy to a stable, cached path
cp -f "$FOUND_JAR" "$BUILD_LIB_DIR/py4j.jar"
echo "==> Using py4j: $FOUND_JAR -> $BUILD_LIB_DIR/py4j.jar"

# 4) Compile Java sources into build/java_classes
echo "==> Compiling Java"
javac -cp "$BUILD_LIB_DIR/py4j.jar:." -d "$BUILD_CLASSES_DIR" Combine.java

# 5) Persist a classpath file for start.sh
echo "$BUILD_LIB_DIR/py4j.jar:$BUILD_CLASSES_DIR" > "$BUILD_DIR/classpath.txt"

echo "==> Build complete"
echo "    - JDK:        $VENDOR_JAVA_DIR"
echo "    - Classes:    $BUILD_CLASSES_DIR"
echo "    - Py4J jar:   $BUILD_LIB_DIR/py4j.jar"
