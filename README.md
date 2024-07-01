# Installation (Mac)

1. Install clojure (`brew install clojure`)
2. Build an uberjar (`clj -T:build uber`)
3. Optionally run tests (`clj -T:build test`)
4. Run jar file with argument:

``` sh
java -jar ./target/invaders-0.1.0-SNAPSHOT.jar <arguments>
```

Recognizable arguments:
```
  -t, --tolerance TOLERANCE  12       Detecting tolerance (the higher the more fuzz is tolerated).
  -a, --algorithm ALGO       hamming  Algorith used to detect invaders (levenshtein or hamming)
  -f, --filename FILENAME             A path to sample file
  -h, --help
```

Sample invocations:

``` sh
java -jar ./target/invaders-0.1.0-SNAPSHOT.jar -a levenshtein -f resources/samples/pesky.sample
java -jar ./target/invaders-0.1.0-SNAPSHOT.jar -t 20 -f resources/samples/pesky.sample
```

Distributed under the Eclipse Public License version 1.0.
