#!/usr/bin/env bash
set -euo pipefail

JDK_VERSION="17"
JDK_URL="https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz"
JDK_DIR="vendor/java"
PY4J_JAR="py4j0.10.9.9.jar"
SRC_JAVA="Combine.java"
CLASS_DIR="build/classes"

echo "==> Build start"

mkdir -p "${JDK_DIR}" "${CLASS_DIR}"

if [[ ! -x "${JDK_DIR}/bin/java" ]]; then
  echo "==> Downloading OpenJDK ${JDK_VERSION} ..."
  curl -fsSL "${JDK_URL}" -o /tmp/java.tar.gz
  tar -xzf /tmp/java.tar.gz -C "${JDK_DIR}" --strip-components=1
  rm -f /tmp/java.tar.gz
else
  echo "==> Reusing cached JDK at ${JDK_DIR}"
fi

export JAVA_HOME="$(pwd)/${JDK_DIR}"
export PATH="${JAVA_HOME}/bin:${PATH}"

echo "==> Java version:"
java -version

NEED_COMPILE=0
if [[ ! -f "${CLASS_DIR}/Combine.class" ]]; then
  NEED_COMPILE=1
elif [[ "${SRC_JAVA}" -nt "${CLASS_DIR}/Combine.class" ]]; then
  NEED_COMPILE=1
fi

if [[ "${NEED_COMPILE}" -eq 1 ]]; then
  echo "==> Compiling ${SRC_JAVA} -> ${CLASS_DIR}"
  javac -cp "${PY4J_JAR}:." -d "${CLASS_DIR}" "${SRC_JAVA}"
else
  echo "==> Java already compiled; skipping"
fi

mkdir -p logs
echo "==> Build complete"
