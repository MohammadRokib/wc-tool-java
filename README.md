<a id="top"></a>

<div align="center">

# ccwc - A Java Implementation of the Unix `wc` Tool

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![CI](https://github.com/MohammadRokib/wc-tool-java/actions/workflows/ci.yml/badge.svg)](https://github.com/MohammadRokib/wc-tool-java/actions/workflows/ci.yml)

`ccwc` (Coding Challenges Word Count) is a custom implementation of the classic Unix `wc` (word count) command-line utility, written entirely in Java. It is built to be memory-efficient, scalable for massive files, and fully compatible with standard Unix pipelines.

This project was built as part of the [Build Your Own wc Tool Challenge](https://codingchallenges.fyi/challenges/challenge-wc).

</div>

<p align="center">
  <a href="#features">Explore the docs</a> ·
  <a href="https://github.com/MohammadRokib/wc-tool-java/issues">Report Bug</a> ·
  <a href="https://www.linkedin.com/in/your-profile/">LinkedIn</a> ·
  <a href="mailto:your.email@example.com">Email</a>
</p>

---

## Features

- **`-c`**: Count bytes in a file or stream.
- **`-l`**: Count lines (newline characters).
- **`-w`**: Count words (sequences of characters delimited by whitespace).
- **`-m`**: Count characters (correctly handles multi-byte UTF-8 encoded text).
  <br/>

- **Default Mode**: Output lines, words, and bytes simultaneously when no flag is provided.
- **Standard Input (stdin)**: Supports Unix piping (e.g., `cat file.txt | ccwc -l`).
- **Memory Safe**: Uses a streaming single-pass architecture. It can process files larger than available RAM without crashing.

<p align="right"><a href="#top">Back to top ⬆️</a></p>

---

## Prerequisites

- **Java Development Kit (JDK) 17** or higher.
- A terminal/command prompt environment.

<p align="right"><a href="#top">Back to top ⬆️</a></p>

---

## Installation & Building

Because this project uses standard Java libraries with no external dependencies, you can compile it directly using `javac`.

1. Clone the repository:
   ```bash
   git clone https://github.com/MohammadRokib/wc-tool-java.git
   cd ccwc
   ```

2. Compile the Java source files into an `out` directory:
   ```bash
   # On Linux / macOS / Git Bash / Windows CMD
   javac -d out src/ccwc/*.java
   ```

<p align="right"><a href="#top">Back to top ⬆️</a></p>

---

## Usage

The application is run via the `java` command, pointing to the `out` directory as the classpath.

### Syntax
```bash
java -cp out ccwc.Main [-c] [-l] [-w] [-m] [filename]
```

*If no `filename` is provided, the tool automatically reads from standard input (`stdin`).*

### Examples

**1. Count bytes in a file:**
```bash
$ java -cp out ccwc.Main -c test.txt
342190 test.txt
```

**2. Count lines in a file:**
```bash
$ java -cp out ccwc.Main -l test.txt
7145 test.txt
```

**3. Count words in a file:**
```bash
$ java -cp out ccwc.Main -w test.txt
58164 test.txt
```

**4. Count characters in a file (UTF-8 aware):**
```bash
$ java -cp out ccwc.Main -m test.txt
339292 test.txt
```

**5. Default mode (lines, words, bytes):**
```bash
$ java -cp out ccwc.Main test.txt
    7145    58164   342190 test.txt
```

**6. Reading from Standard Input (Piping):**
When reading from `stdin`, the filename is omitted from the output.
```bash
$ cat test.txt | java -cp out ccwc.Main -l
7145
```

<p align="right"><a href="#top">Back to top ⬆️</a></p>

---

## Architecture & Design

Instead of reading entire files into memory (which causes `OutOfMemoryError` on large files), `ccwc` uses a **single-pass, streaming architecture**.

The project is divided into four main components:

1. **`Main.java`**: The entry point. Delegates argument parsing to `Options`, invokes the `Counter`, and formats the standard output using `System.out.printf`.
2. **`Options.java`**: A Data Transfer Object (DTO) that parses command-line arguments into boolean flags. If no flags are provided, it automatically enables the default metrics (lines, words, bytes).
3. **`Counter.java`**: The core engine. It features two entry points:
    - `count(Path path, Options)`: For file inputs. Uses `Files.size()` for an instant O(1) byte count, avoiding unnecessary disk reads.
    - `count(InputStream in, Options)`: For standard input. Uses a shared `countFromStream` method that loops through decoded characters exactly once, checking for line breaks, word boundaries, and character counts simultaneously.
4. **`CountingInputStream.java`**: Extends `FilterInputStream` (Decorator Pattern). When reading from `stdin`, this class sits at the bottom of the stream stack, intercepting raw bytes to tally the total byte count as they flow up to the character decoder.

<p align="right"><a href="#top">Back to top ⬆️</a></p>

---

## Environment Notes (PowerShell Users)

If you are testing the standard input byte count (`-c`) on Windows using **PowerShell**, you may notice a 3-byte discrepancy compared to reading the file directly (e.g., `342187` instead of `342190`).

**Why?** PowerShell's `cat` alias (`Get-Content`) decodes files into .NET strings and silently strips the 3-byte UTF-8 Byte Order Mark (BOM) before piping the data to external executables like `java.exe`.

Your Java code is correct. To verify raw byte piping on Windows, use **Command Prompt (`cmd.exe`)** with the `type` command, or use **Git Bash**:
```cmd
:: In cmd.exe
type test.txt | java -cp out ccwc.Main -c
```

<p align="right"><a href="#top">Back to top ⬆️</a></p>

---

## Contact

Mohammad Rokib

- **[LinkedIn](https://www.linkedin.com/in/m0hammadrokib/)**
- **[Email](mailto:mohammadrokibkhan@gmail.com)**
- **[GitHub](https://github.com/MohammadRokib)**
- **[Project Link: wc-tool-java](https://github.com/MohammadRokib/wc-tool-java)**

<p align="right"><a href="#top">Back to top ⬆️</a></p>
