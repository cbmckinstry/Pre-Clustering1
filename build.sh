#!/bin/bash
set -euo pipefail
export PIP_DISABLE_PIP_VERSION_CHECK=1
PY4J_VER="0.10.9.9"

if [ ! -x vendor/java/bin/java ]; then
  echo "Installing JDK 17..."
  mkdir -p vendor/java
  curl -fsSL https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o /tmp/java.tar.gz
  tar -xzf /tmp/java.tar.gz -C vendor/java --strip-components=1
fi

# Python deps
python -m pip install -r requirements.txt

mkdir -p vendor/py4j
PY4J_JAR=$(python - <<'PY'
import sys, pathlib, zipfile, shutil, subprocess
ver="0.10.9.9"; dest=pathlib.Path("vendor/py4j"); dest.mkdir(parents=True, exist_ok=True)
jar=None
try:
    import py4j; base=pathlib.Path(py4j.__file__).parent
    cands=list(base.glob(f"**/py4j*{ver}*.jar"))+list(base.glob("**/py4j*.jar"))
    if cands: jar=shutil.copy2(cands[0], dest/cands[0].name)
except Exception: pass
if not any(dest.glob("py4j*.jar")):
    subprocess.check_call([sys.executable,"-m","pip","download","--no-deps",f"py4j=={ver}","-d",".tmp_py4j"])
    whl=next(pathlib.Path(".tmp_py4j").glob(f"py4j-{ver}-*.whl"))
    with zipfile.ZipFile(whl) as zf:
        n=next(s for s in zf.namelist() if s.endswith(".jar") and "py4j" in s)
        zf.extract(n,".tmp_py4j"); src=pathlib.Path(".tmp_py4j")/n
        shutil.copy2(src, dest/src.name)
print(next(dest.glob("py4j*.jar")))
PY
)

export JAVA_HOME="$PWD/vendor/java"
export PATH="$JAVA_HOME/bin:$PATH"

CLASSPATH="vendor/py4j/*:."
echo "Compiling with CLASSPATH=$CLASSPATH"
javac -cp "$CLASSPATH" Combine.java

rm -rf .tmp_py4j || true
