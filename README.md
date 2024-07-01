Hello World!

Here I come to save this planet from pesky invaders hiding somewhere in the clouds.

# Concepts
There are couple of definition and assumptions to explain before going further down the road:

## Assumptions
1. Invaders are not smart enough to hide partially behind themselves, or more technically - they do not overlap each other.
This assumption simplifies a lot invader recognition as it implies that at given scanned region there might be at most 1 invader.
2. Sample is considered to be a 0-indexed cartesian plane wrapping at the edges. It begins at top-left corner with coordinates (0,0).

## Samples and regions
_Sample_ is an ascii radar sample stored internally as a sequence of lines. It is used as an input for scanning along with invaders definition. As result, sample gets printed with all found invaders replaced with their canonical shape.

Scanning bases on a variable-width rolling-window wrapping at the edges. It means that at every single step scanner tries to match
any of defined invaders adjusting window width to invader's one.

_Region_ is a name used internally to describe a rolling window. It has it's (`x`,`y`) coordinates within the plane as well as `width` and `height`. When applied to a sample data, it is turned into a sequence of substrings carved to match (wrapping) regions boundary. There are multiple places (like detector functions) where this representation is not the most effective one, therefore sequence is often transformed into a "flat" stringifed form, eg `["aa" "bb" "cc"]` becomes `"aabbcc"`.

_Detector_ is a multi-function which based on stringified region and invader's definition returns a boolean if both match each other
with some tolerance. As for now two major string metric algorithms are being used: Levenshtein and Hamming (parametrized by `--algorithm` command line argument). The output is compared to `tolerance` (parametrized by `--tolerance` argument) which might be used to control "fuzziness" during matching process.

# Futher optimization
1. Skip regions which overlap with already detected invaders (not finished). As invaders do not overlap, this could improve
speed of scanner - it wouldn't go again through regions which partially overlap with found invaders. Major trick here is to
be aware of edge wrapping.

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
