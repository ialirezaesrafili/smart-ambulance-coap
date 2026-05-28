# Requirements & Setup

## Prerequisites

Before running the project, make sure the following are installed on your system:

* **Java 17** (or newer)
* **Maven 3.6+**
* **Git** (optional – for cloning the repository)

All other dependencies (Californium, Jackson, Jansi, etc.) are automatically downloaded by Maven during the build.

---

## Installation by Operating System

### Linux (Arch / Ubuntu / Debian)

**Install dependencies**

Arch Linux:

```bash
sudo pacman -S jdk17-openjdk maven git
```

### Verify
```shell
java -version
mvn -version
```
---
### Windows

1) Install Java 17 JDK – download from Adoptium
2) Install Maven – download from Apache Maven
3) Install Git – download from Git SCM

### Verify

```shell
java -version
mvn -version
git --version
```
---
### MacOS

```shell
brew install openjdk@17 maven git
```

### If Java is not automatically in your PATH:
```shell
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### Verify

```shell
java -version
mvn -version
git --version
```

