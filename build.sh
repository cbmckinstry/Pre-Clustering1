#!/bin/bash
set -euo pipefail

# 1) Install JDK into a persistent folder in the project tree
mkdir -p vendor/java
if [ ! -x vendor/java/bin/java ]; then
  echo "Installing JDK 17 to vendor/java..."
  curl -fsSL https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o /tmp/java.tar.gz
  tar -xzf /tmp/java.tar.gz -C vendor/java --strip-components=1
fi

# 2) Python deps
pip install -r requirements.txt

# 3) Make sure the Py4J JAR is available & compile Java
# Option A (simplest): vendor the JAR into your repo under vendor/py4j/py4j0.10.9.9.jar
# Option B (dynamic): locate the JAR inside site-packages and copy it next to your sources
PY4J_JAR_PATH=$(python - <<'PY'
import sys, pathlib
try:
    import py4j
    p = pathlib.Path(py4j.__file__).parent
    # Common locations for the jar inside the package
    candidates = list(p.glob("**/py4j*.jar"))
    if candidates:
        print(str(candidates[0]))
    else:
        print("")
except Exception:
    print("")
PY
)

mkdir -p vendor/py4j
if [ -n "$PY4J_JAR_PATH" ]; then
  cp -f "$PY4J_JAR_PATH" vendor/py4j/
elif [ ! -f vendor/py4j/py4j0.10.9.9.jar ]; then
  echo "Could not find py4j jar in site-packages. Downloading from Maven Central..."
  curl -fsSL https://repo1.maven.org/maven2/org/py4j/py4j/0.10.9.9/py4j-0.10.9.9.jar -o vendor/py4j/py4j0.10.9.9.jar
fi

# 4) Compile your Java gateway
export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"
javac -cp "vendor/py4j/py4j0.10.9.9.jar:." Combine.java
