import subprocess
import time
import os
import signal
import sys
from pathlib import Path

# -----------------------------------------------------
# CONFIG
# -----------------------------------------------------
PROJECT_DIR = Path(__file__).resolve().parent
JAVA_CLASS = "Combine"
PY4J_JAR = PROJECT_DIR / "py4j0.10.9.9.jar"      # local + Render
APP_MODULE = "app.py"
PYTHON = sys.executable

# Render automatically sets this environment variable
IS_RENDER = os.environ.get("RENDER", "false") == "true"

os.chdir(PROJECT_DIR)
print(f"[*] Working directory: {os.getcwd()}")


# -----------------------------------------------------
# INSTALL JAVA (Render only)
# -----------------------------------------------------
def ensure_java_installed():
    if IS_RENDER:
        print("[*] Render environment detected.")

        os.environ["JAVA_HOME"] = "/tmp/java"
        os.environ["PATH"] = "/tmp/java/bin:" + os.environ["PATH"]

        if not shutil.which("java"):
            print("[*] Installing Java (OpenJDK 17)...")
            subprocess.check_call(
                "curl -L https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o java.tar.gz",
                shell=True,
            )
            subprocess.check_call("mkdir -p /tmp/java", shell=True)
            subprocess.check_call("tar -xzf java.tar.gz -C /tmp/java --strip-components=1", shell=True)

        subprocess.call("java -version", shell=True)


# -----------------------------------------------------
# COMPILE JAVA
# -----------------------------------------------------
def compile_java():
    java_file = f"{JAVA_CLASS}.java"
    class_file = f"{JAVA_CLASS}.class"

    needs_compile = True
    if os.path.exists(class_file):
        needs_compile = os.path.getmtime(java_file) > os.path.getmtime(class_file)

    if needs_compile:
        print("[*] Compiling Java...")
        subprocess.check_call([
            "javac",
            "-cp", f"{PY4J_JAR}:{PROJECT_DIR}",
            java_file
        ])
    else:
        print("[*] Java already compiled; skipping compilation.")


# -----------------------------------------------------
# START JAVA GATEWAY
# -----------------------------------------------------
def start_java_gateway():
    print("[*] Starting Java GatewayServer...")
    java_cmd = [
        "java",
        "-cp", f"{PY4J_JAR}:{PROJECT_DIR}",
        JAVA_CLASS
    ]

    process = subprocess.Popen(java_cmd)
    print(f"[*] Java process PID: {process.pid}")
    time.sleep(3)
    return process


# -----------------------------------------------------
# START FLASK
# -----------------------------------------------------
def start_flask():
    if IS_RENDER:
        print("[*] Starting Gunicorn (Render)...")
        subprocess.check_call(["gunicorn", "-w", "1", "-b", "0.0.0.0:5000", "app:app"])
    else:
        print("[*] Starting Flask (local)...")
        subprocess.check_call([PYTHON, APP_MODULE])


# -----------------------------------------------------
# MAIN STARTUP SEQUENCE
# -----------------------------------------------------
if __name__ == "__main__":
    try:
        compile_java()
        java_proc = start_java_gateway()
        start_flask()

    except KeyboardInterrupt:
        print("\n[*] Interrupted. Shutting down...")

    finally:
        print("[*] Killing Java process...")
        try:
            java_proc.send_signal(signal.SIGTERM)
            java_proc.wait(timeout=5)
        except Exception:
            try:
                java_proc.kill()
            except:
                pass

        print("[*] Done.")
