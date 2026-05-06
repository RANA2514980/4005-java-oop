#!/bin/bash

mkdir -p lib out

# Download SQLite JDBC if not present
if [ ! -f "lib/sqlite-jdbc.jar" ]; then
    echo "Downloading SQLite JDBC..."
    wget -q "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.0.0/sqlite-jdbc-3.45.0.0.jar" -O "lib/sqlite-jdbc.jar"
    if [ $? -ne 0 ]; then
        echo "Failed to download SQLite JDBC."
        exit 1
    fi
    echo "Downloaded SQLite JDBC."
fi

# Download SLF4J API if not present
if [ ! -f "lib/slf4j-api.jar" ]; then
    echo "Downloading SLF4J API..."
    wget -q "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.11/slf4j-api-2.0.11.jar" -O "lib/slf4j-api.jar"
    if [ $? -ne 0 ]; then
        echo "Failed to download SLF4J API."
        exit 1
    fi
    echo "Downloaded SLF4J API."
fi

# Download SLF4J Simple binding if not present
if [ ! -f "lib/slf4j-simple.jar" ]; then
    echo "Downloading SLF4J Simple..."
    wget -q "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.11/slf4j-simple-2.0.11.jar" -O "lib/slf4j-simple.jar"
    if [ $? -ne 0 ]; then
        echo "Failed to download SLF4J Simple."
        exit 1
    fi
    echo "Downloaded SLF4J Simple."
fi

# Compile
echo "Compiling..."
javac -cp "lib/*" -d out $(find src -name "*.java")
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run
echo "Starting application..."
java -cp "out:lib/*" Main "$@"