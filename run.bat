@echo off

if not exist lib mkdir lib
if not exist out mkdir out

if not exist lib\sqlite-jdbc.jar (
    echo Downloading SQLite JDBC...
    powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.0.0/sqlite-jdbc-3.45.0.0.jar' -OutFile 'lib\sqlite-jdbc.jar'"
    if errorlevel 1 (
        echo Failed to download. Please download manually from:
        echo https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.0.0/sqlite-jdbc-3.45.0.0.jar
        exit /b 1
    )
    echo Downloaded successfully.
)

if not exist lib\slf4j-api.jar (
    echo Downloading SLF4J API...
    powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.11/slf4j-api-2.0.11.jar' -OutFile 'lib\slf4j-api.jar'"
    if errorlevel 1 (
        echo Failed to download. Please download manually from:
        echo https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.11/slf4j-api-2.0.11.jar
        exit /b 1
    )
    echo Downloaded successfully.
)

if not exist lib\slf4j-simple.jar (
    echo Downloading SLF4J Simple...
    powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.11/slf4j-simple-2.0.11.jar' -OutFile 'lib\slf4j-simple.jar'"
    if errorlevel 1 (
        echo Failed to download. Please download manually from:
        echo https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.11/slf4j-simple-2.0.11.jar
        exit /b 1
    )
    echo Downloaded successfully.
)

echo Compiling...
if exist sources.txt del /q sources.txt
for /r src %%f in (*.java) do echo %%f>> sources.txt
javac -cp "lib\*" -d out @sources.txt
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)
del /q sources.txt

echo Starting application...
java -cp "out;lib\*" Main %*