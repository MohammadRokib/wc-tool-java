# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`ccwc` is a Java clone of the Unix `wc` command (a Coding Challenges exercise). It
counts bytes, lines, words, and characters in a file or from standard input.

## Build & run

There is no build tool (no Maven/Gradle) and no test suite. It is an IntelliJ IDEA
project that compiles with `javac` to `out/`. Run all commands from the repo root.

```bash
# Compile (mirrors what IntelliJ does)
javac -d out src/ccwc/*.java

# Run against a file
java -cp out ccwc.Main -l test.txt

# Run against stdin
cat test.txt | java -cp out ccwc.Main -w
```

`test.txt` is a sample input fixture (not a test). The project targets Java language
level 24 (`.idea/misc.xml`); a newer JDK also compiles it.

## Flags

`-c` bytes, `-l` lines, `-w` words, `-m` characters. With no flags, the default is
`-c -l -w` (matching `wc`). The first non-flag argument is the filename; if absent,
input is read from stdin.

## Architecture

Four classes in package `ccwc` (`src/ccwc/`):

- **`Main`** — entry point. Delegates parsing to `Options`, invokes `Counter`, then
  formats output in `printResults`. Output has two modes: the classic aligned `wc`
  format (`%8d %8d %8d filename`) only when exactly `-c -l -w` are active (and not
  `-m`); otherwise one metric per line.
- **`Options`** — parses flags and holds them as public fields; applies the
  "no flags → `-c -l -w`" default.
- **`Counter`** — the counting engine. Accumulates results in public fields
  (`bytes`, `lines`, `words`, `chars`).
- **`CountingInputStream`** — a `FilterInputStream` that tallies bytes read.

**Key design — single pass (the reason `CountingInputStream` exists):** line, word,
and char counts are gathered by decoding the stream as UTF-8 through a
`BufferedReader` in one pass. Byte counting normally can't share that pass (the
reader consumes decoded chars, not raw bytes), so `Counter` wraps the raw stream in
`CountingInputStream` to tally bytes *underneath* the reader — one read pass yields
all four counts. Two shortcuts avoid reading data when possible:
- File + only `-c` → `Files.size(path)`, no read at all.
- Stdin + only `-c` → a raw byte loop, no character decoding.

`Counter` has two `count()` overloads (one taking a `Path`, one taking an
`InputStream`) that funnel into the private `countFromStream`.

## Known gotcha

When reading from **stdin** with any flag other than the default combination,
`printResults` still appends `opts.fileName`, which is `null` — so output looks like
`58164 null`. The aligned default-format branch handles the null filename correctly;
the per-metric branch does not.
