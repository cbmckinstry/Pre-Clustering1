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

# 3) Ensure the Py4J JAR exists under vendor/py4j
mkdir -p vendor/py4j

# Try to locate the jar inside site-packages (handles both naming styles)
PY4J_JAR_PATH=$(python - <<'PY'
import pathlib
try:
    import py4j
    base = pathlib.Path(py4j.__file__).parent
    cands = (list(base.glob("**/py4j*-0.10.9.9*.jar"))
             + list(base.glob("**/py4j0.10.9.9*.jar"))
             + list(base.glob("**/py4j*.jar")))
    print(str(cands[0]) if cands else "")
except Exception:
    print("")
PY
)

if [ -n "${PY4J_JAR_PATH}" ]; then
  cp -f "${PY4J_JAR_PATH}" vendor/py4j/
fi

# If still missing, download from Maven Central (hyphenated name), else fall back to pip wheel extraction
if [ ! -f vendor/py4j/py4j-0.10.9.9.jar ] && [ ! -f vendor/py4j/py4j0.10.9.9.jar ]; then
  echo "Downloading py4j 0.10.9.9 jar from Maven Central..."
  if ! curl -fsSL https://repo1.maven.org/maven2/org/py4j/py4j/0.10.9.9/py4j-0.10.9.9.jar -o vendor/py4j/py4j-0.10.9.9.jar; then
    echo "Primary download failed. Falling back to 'pip download'…"
    mkdir -p .tmp_py4j
    pip download --no-deps py4j==0.10.9.9 -d .tmp_py4j
    WHEEL=$(ls .tmp_py4j/py4j-0.10.9.9-*.whl 2>/dev/null | head -n1 || true)
    if [ -n "${WHEEL:-}" ]; then
      python - <<'PY'
import zipfile, pathlib, shutil
wheel = list(pathlib.Path(".tmp_py4j").glob("py4j-0.10.9.9-*.whl"))[0]
with zipfile.ZipFile(wheel) as zf:
    for n in zf.namelist():
        if n.endswith(".jar") and "py4j" in n:
            zf.extract(n, ".tmp_py4j")
            src = pathlib.Path(".tmp_py4j")/n
            dst = pathlib.Path("vendor/py4j")/src.name
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dst)
            print(src.name)
            break
PY
    else
      echo "ERROR: could not obtain py4j jar by any method."
      exit 1
    fi
  fi
fi

# Normalize a single variable for the jar path (handles either name)
if [ -f vendor/py4j/py4j-0.10.9.9.jar ]; then
  PY4J_JAR="vendor/py4j/py4j-0.10.9.9.jar"
else
  PY4J_JAR="vendor/py4j/py4j0.10.9.9.jar"
fi

# 4) Compile your Java gateway
export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"
javac -cp "$PY4J_JAR:." Combine.java

# 5) Cleanup (optional)
rm -rf .tmp_py4j || true
