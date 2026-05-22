# Requirements

Before running the project, install:

* Java 17+
* Maven 3+
* Git

---

# Installation

## Linux (Arch / Ubuntu / Debian)

### Install dependencies

**Arch Linux:**

```bash
sudo pacman -S jdk17-openjdk maven git
```

**Ubuntu / Debian:**

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven git -y
```

### Verify installation

```bash
java -version
mvn -version
```

---

## Windows

### Install manually:

1. Install Java 17 (JDK)

    * Download: https://adoptium.net/
2. Install Maven

    * Download: https://maven.apache.org/download.cgi
3. Install Git

    * Download: https://git-scm.com/downloads

### Verify in Command Prompt / PowerShell:

```bash
java -version
mvn -version
git --version
```

---

## macOS

### Install using Homebrew:

```bash
brew install openjdk@17 maven git
```

### Set Java (if needed):

```bash
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### Verify installation:

```bash
java -version
mvn -version
git --version
```

---

# Clone Project

```bash
git clone https://github.com/ialirezaesrafili/internet-of-thing-lab.git
cd internet-of-thing-lab
```

---

# Build Project

```bash
mvn clean compile
```

---

# Run Project

```bash
mvn exec:java -Dexec.mainClass="com.lab.iot.app.Application"
```

---

# Package Project

```bash
mvn package
```

Run JAR:

```bash
java -jar target/internet-of-thing-lab-1.0.0.jar
```
