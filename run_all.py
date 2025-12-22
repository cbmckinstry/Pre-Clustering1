import subprocess
import time
import os
import signal
import sys
import shutil
from pathlib import Path

PROJECT_DIR = Path(__file__).resolve().parent
JAVA_CLASS = "Combine"
PY4J_JAR = PROJECT_DIR / "py4j0.10.9.9.jar"
APP_MODULE = "app.py"
PYTHON = sys.executable

IS_RENDER = os.environ.get("RENDER", "false").lower() == "true"

os.chdir(PROJECT_DIR)
print(f"[*] Working directory: {os.getcwd()}")


def ensure_java():
    if IS_RENDER:
        print("[*] Render environment detected")
        # Match your old start.sh behavior
        os.environ["JAVA_HOME"] = "/tmp/java"
        os.environ["PATH"] = "/tmp/java/bin:" + os.environ["PATH"]

        if not shutil.which("javac"):
            print("[*] Installing Java (OpenJDK 17) on Render...")
            subprocess.check_call(
                "curl -L https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_linux-x64_bin.tar.gz -o java.tar.gz",
                shell=True,
            )
            subprocess.check_call("mkdir -p /tmp/java", shell=True)
            subprocess.check_call("tar -xzf java.tar.gz -C /tmp/java --strip-components=1", shell=True)

        subprocess.call("java -version", shell=True)
    else:
        print("[*] Local environment detected")
        if not shutil.which("javac"):
            print("[!] 'javac' not found on PATH. Install a JDK locally.")
            sys.exit(1)
        subprocess.call(["java", "-version"])


def compile_java():
    java_file = f"{JAVA_CLASS}.java"
    class_file = f"{JAVA_CLASS}.class"

    if not PY4J_JAR.exists():
        print(f"[!] py4j jar not found at {PY4J_JAR}")
        sys.exit(1)

    needs_compile = True
    if os.path.exists(java_file) and os.path.exists(class_file):
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


def start_java_gateway():
    print("[*] Starting Java GatewayServer...")
    java_cmd = [
        "java",
        "-cp", f"{PY4J_JAR}:{PROJECT_DIR}",
        JAVA_CLASS
    ]
    proc = subprocess.Popen(java_cmd)
    print(f"[*] Java process PID: {proc.pid}")
    # Give it time to open the Py4J port
    time.sleep(3)
    return proc


def start_web_app():
    if IS_RENDER:
        print("[*] Starting Gunicorn on Render...")
        subprocess.check_call(["gunicorn", "-w", "1", "-b", "0.0.0.0:5000", "app:app"])
    else:
        print("[*] Starting Flask dev server locally...")
        subprocess.check_call([PYTHON, APP_MODULE])


if __name__ == "__main__":
    java_proc = None
    try:
        ensure_java()
        compile_java()
        java_proc = start_java_gateway()
        start_web_app()
    except KeyboardInterrupt:
        print("\n[*] Interrupted by user.")
    except subprocess.CalledProcessError as e:
        print(f"[!] Subprocess failed: {e}")
    finally:
        if java_proc is not None:
            print("[*] Stopping Java GatewayServer...")
            try:
                java_proc.send_signal(signal.SIGTERM)
                java_proc.wait(timeout=5)
            except Exception:
                try:
                    java_proc.kill()
                except Exception:
                    pass
        print("[*] Done.")
