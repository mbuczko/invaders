Hello World!

Here I come to save this planet from pesky invaders hiding themselves somewhere in the clouds.

# Assumptions
Let's start with basic assumption which are the corner stone behind few decisions made during implementation:

1. Invaders are not smart enough to hide partially behind themselves, or more technically - they do not overlap each other.
This assumption simplifies a lot invader recognition as it implies that at given scanned region there might be at most 1 invader.
2. Sample is considered to be a 0-indexed cartesian plane wrapping at the edges. It begins at top-left corner with coordinates (0,0).

# Concepts
There are couple of definition to explain before going further down the road:

## Samples and regions
_Sample_ is an ascii radar sample stored internally as a sequence of lines. It is used as an input for scanning along with invaders definition. As result, sample gets printed with all found invaders replaced with their canonical shape.

Scanning bases on a variable-width rolling-window wrapping at the edges. It means that at every single step scanner tries to match
any of defined invaders adjusting window width to invader's one.

_Region_ is a name used internally to describe a rolling window. It has it's (`x`,`y`) coordinates within the plane as well as its `w` (width) and `h` (height). When applied to a sample data (through `invaders.region/region->str`) it returns concatenated substrings carved to match regions boundary. As an example, given that region narrows a sample down to `["aa" "bb" "cc"]` outcome string becomes `"aabbcc"`. There are multiple places (like detector functions) where this representation is easier to handle compared to vector of substrings.

_Detectors_ are a multi-methods that leverage fuzzy-matching functions from [clj-fuzzy](https://yomguithereal.github.io/clj-fuzzy/clojure.html) library. As for now two major string metric algorithms are being used: Levenshtein and Hamming (default) parametrized by `--algorithm` command line argument. The output from fuzzy matcher is a plain integer describing how much strings (region and invader definition) differ. If result is lower or equal given threshold - a `tolerance` (parametrized by `--tolerance` argument) strings are considered to be "similar enough".

In case of unsatisfactory results, a tolerance argument should be adjusted (usually increased).

# Futher optimization
1. Skip regions which overlap with already detected invaders. As invaders do not overlap, this could improve
speed of scanner - it wouldn't go again through regions which partially overlap with found invaders. Major trick here is to
be aware of edge wrapping.

# Installation (Mac)

1. Install clojure (`brew install clojure`)
2. Build an uberjar (`clj -T:build uber`)
3. Optionally run tests (`clj -T:build test`)
4. Run jar file with argument:

``` sh
java -jar ./target/invaders-0.3.0.jar <arguments>
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
java -jar ./target/invaders-0.3.0.jar -a levenshtein -f resources/samples/pesky.sample
java -jar ./target/invaders-0.3.0.jar -t 20 -f resources/samples/pesky.sample
```

Distributed under the Eclipse Public License version 1.0.
