# St Mary's Digital Library System

A Java Swing + SQLite desktop application for managing books, members, and borrowing records.

## Prerequisites

- Java JDK 17+ installed (Java 11+ should also work).
- Internet access on first run to download dependencies.

## Quick Start (Linux/macOS)

```bash
./run.sh
```

Run with test data:

```bash
./run.sh --seed
```

## Quick Start (Windows)

```bat
run.bat
```

Run with test data:

```bat
run.bat --seed
```

## What the Scripts Do

- Create `lib/` and `out/` if missing.
- Download dependencies if missing:
	- SQLite JDBC
	- SLF4J API
	- SLF4J Simple
- Compile all Java sources into `out/`.
- Run the application.

## Database

- SQLite database file: `database/library.db`
- Tables are auto-created on startup if missing.
- Use `--seed` to populate with sample books, members, and borrow records.

## Manual Build and Run (Optional)

Compile:

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
```

Run:

```bash
java -cp "out:lib/*" Main
```

## Notes

- The application is a desktop GUI (Swing).
- If you see a native access warning from SQLite JDBC, it is safe to ignore for local use.