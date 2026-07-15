#!/usr/bin/env bash
# Smoke test for ccwc. Compiles must already have produced ./out (javac -d out src/ccwc/*.java).
# Verifies every distinct code path in Counter against the known-good values for test.txt
# documented in AGENTS.md / README.md: 7145 lines, 58164 words, 342190 bytes, 339292 chars.
#
# Runnable locally from the repo root: bash .github/scripts/smoke-test.sh
set -euo pipefail
cd "$(dirname "$0")/../.."

fail=0
check() { # desc expected actual
  if [ "$3" = "$2" ]; then
    echo "OK   $1"
  else
    echo "FAIL $1 -- expected [$2], got [$3]"
    fail=1
  fi
}

# --- File input, single flag: exercises the Files.size() -c shortcut (Counter.java:41) ---
check "-c file" 342190 "$(java -cp out ccwc.Main -c test.txt | awk '{print $1}')"
check "-l file" 7145   "$(java -cp out ccwc.Main -l test.txt | awk '{print $1}')"
check "-w file" 58164  "$(java -cp out ccwc.Main -w test.txt | awk '{print $1}')"
check "-m file" 339292 "$(java -cp out ccwc.Main -m test.txt | awk '{print $1}')"

# --- File input, default combo: the columnar printf branch (Main.java:45-46) ---
check "default file" "7145 58164 342190" "$(java -cp out ccwc.Main test.txt | awk '{print $1, $2, $3}')"

# --- Stdin input, single flag: -c alone exercises the raw 8KB-loop shortcut (Counter.java:69-76) ---
check "-c stdin" 342190 "$(cat test.txt | java -cp out ccwc.Main -c | awk '{print $1}')"
check "-l stdin" 7145   "$(cat test.txt | java -cp out ccwc.Main -l | awk '{print $1}')"
check "-w stdin" 58164  "$(cat test.txt | java -cp out ccwc.Main -w | awk '{print $1}')"
check "-m stdin" 339292 "$(cat test.txt | java -cp out ccwc.Main -m | awk '{print $1}')"

# --- Stdin input, default combo ---
check "default stdin" "7145 58164 342190" "$(cat test.txt | java -cp out ccwc.Main | awk '{print $1, $2, $3}')"

# --- Stdin, -c combined with another flag: the ONLY path that exercises the
#     CountingInputStream decorator (Counter.java:78-79) instead of either shortcut ---
result="$(cat test.txt | java -cp out ccwc.Main -c -l)"
check "-c -l stdin bytes (decorator path)" 342190 "$(sed -n 1p <<< "$result" | awk '{print $1}')"
check "-c -l stdin lines (decorator path)" 7145   "$(sed -n 2p <<< "$result" | awk '{print $1}')"

exit $fail
