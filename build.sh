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

# Ensure py4j JAR exists at vendor/py4j and record its path
mkdir -p vendor/py4j

# 1) Try to locate the jar inside site-packages (works if pip wheel includes it)
PY4J_JAR_PATH=$(python - <<'PY'
import sys, pathlib
try:
    import py4j, pkgutil
    base = pathlib.Path(py4j.__file__).parent
    # Try both naming styles, anywhere under the package
    cands = list(base.glob("**/py4j*-0.10.9.9*.jar")) + list(base.glob("**/py4j0.10.9.9*.jar")) + list(base.glob("**/py4j*.jar"))
    print(str(cands[0]) if cands else "")
except Exception:
    print("")
PY
)

if [ -n "$PY4J_JAR_PATH" ]; then
  cp -f "$PY4J_JAR_PATH" vendor/py4j/
fi

# 2) If still missing, download from Maven Central (hyphenated name)
if [ ! -f vendor/py4j/py4j-0.10.9.9.jar ] && [ ! -f vendor/py4j/py4j0.10.9.9.jar ]; then
  echo "Downloading py4j 0.10.9.9 jar from Maven Central..."
  curl -fsSL https://repo1.maven.org/maven2/org/py4j/py4j/0.10.9.9/py4j-0.10.9.9.jar -o vendor/py4j/py4j-0.10.9.9.jar || {
    echo "Primary download failed. Falling back to 'pip download'…"
    # 3) Fallback: pip download the wheel and extract jar from it (works even if Maven Central is blocked)
    mkdir -p .tmp_py4j
    pip download --no-deps py4j==0.10.9.9 -d .tmp_py4j
    WHEEL=$(ls .tmp_py4j/py4j-0.10.9.9-*.whl | head -n1)
    if [ -n "$WHEEL" ]; then
      python - <<'PY'
import sys, zipfile, pathlib, shutil
wheel = list(pathlib.Path(".tmp_py4j").glob("py4j-0.10.9.9-*.whl"))[0]
with zipfile.ZipFile(wheel) as zf:
    for n in zf.namelist():
        if n.endswith(".jar") and "py4j" in n:
            zf.extract(n, ".tmp_py4j")
            src = pathlib.Path(".tmp_py4j")/n
            dst_dir = pathlib.Path("vendor/py4j"); dst_dir.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dst_dir / src.name)
            print(src.name)
            break
PY
    else
      echo "ERROR: could not obtain py4j jar by any method."
      exit 1
    fi
  }
fi

# Normalize a single variable for the jar path (handles either name)
if [ -f vendor/py4j/py4j-0.10.9.9.jar ]; then
  PY4J_JAR="vendor/py4j/py4j-0.10.9.9.jar"
else
  PY4J_JAR="vendor/py4j/py4j0.10.9.9.jar"
fi

# Compile with whatever jar name we found
export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"
javac -cp "$PY4J_JAR:." Combine.java
