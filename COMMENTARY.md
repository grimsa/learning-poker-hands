# Notes on design and implementation

The problem is defined as:

> Given a list of hands dealt to two players, how many hands does Player 1 win?

## Step 1: Capturing the main domain concepts

#### DealHistory

`DealHistory` is our root abstraction - a collection of `Deal`s that has a single responsibility of counting wins of a specific player.

The abilities to get the total number of deals or to count wins of other players are not required to solve the problem at hand, but they make the class more generic and easier to test.

#### Deal

A `Deal` represents `Hand`s that different players hold in a single round.

We need to determine which hand wins, but we cannot do that in `Deal` without exposing the internals of a `Hand`, so let's delegate this responsibility.

#### Hand

A `Hand` represents the five `Card`s that a player holds.

Comparing two hands can produce three outcomes: first hand wins, second hand wins or both hands are of equal strength.
A `Hand` implements `java.lang.Comparable` to express this.

#### Card

A `Card` is defined by its value and suit.

As there are fixed sets of possible suits and values, let's model them as enumerations.

## Step 2: Implementing hand ranking

### Preparation: Adding tests

Let's add some tests before implementing hand ranking.

It is convenient to write tests using the same concise `Hand` representation as in the provided input file. It also lets us reuse the same parsing logic.

#### A note on parsing hands

We can consider adding `Hand(String)` and `Card(String)` constructors to perform the parsing there.
However, parsing strings is not the main concern of those classes.
On top of that, while representing a `Card` as `"2H"` seems pretty generic, representing a `Hand` as `"2H 3H 4H 5H 6H"` feels quite specific to the format of provided input file.

As this is a point of potential variability, let's better extract this logic into a separate `HandParser` class.

### Approach 1: Chained comparators

Let's extract comparison of each rank into its own `Comparator` and chain them together like this:

```java
    public int compareTo(final Hand other) {
        return RoyalFlushComparator.INSTANCE
                .thenComparing(StraightFlushComparator.INSTANCE)
                .thenComparing(FourOfAKindComparator.INSTANCE)
                .thenComparing(FullHouseComparator.INSTANCE)
                .thenComparing(FlushComparator.INSTANCE)
                .thenComparing(StraightComparator.INSTANCE)
                .thenComparing(ThreeOfAKindComparator.INSTANCE)
                .thenComparing(TwoPairsComparator.INSTANCE)
                .thenComparing(OnePairComparator.INSTANCE)
                .thenComparing(HighCardComparator.INSTANCE)
                .compare(this, other);
    }
```

Halfway through implementing it we see that some comparators are not isolated and actually depend on downstream comparators.

For example, if both hands have two pairs of equal values, `TwoPairsComparator` only compares the pairs and then expects downstream `HighCardComparator` to determine the winner.
However, the downstream `OnePairComparator` then has to be aware of such a case and not try to determine the winner based on one arbitrarily chosen pair.

The dependence on downstream comparators could be removed without much trouble, but the code is also starting to show some smells:
- coupling of responsibilities (determining if current comparator is the one to decide the winner + doing the actual comparison)
- duplication
- use of `null`s to indicate lack of value

Let's look for a better approach.

### Approach 2: A class for each rank

As we have distinct ranks in a game of poker, we should have corresponding types in our model as well.

A curious case here is the Royal Flush. It could be both a separate type or just one case of a Straight Flush.
But as we only care about ranking of hands and the same rules apply to both ranks, let's go with the latter approach for simplicity.

As we implement this approach, we see that the responsibilities are now better separated:
1. determining the rank of a hand (`FiveCardHandFactory`)
1. comparing two hands (every `Hand` subtype)

Another benefit is that every `Hand` subtype now holds just the data it needs, which makes individual comparison logic easier to understand.

#### A note on further simplifying
If a card can only be in one hand at a time, hands like `FullHouse`, `ThreeOfAKind` or `FourOfAKind` could store just one `Value`.

However, while comparing the remaining cards (kickers) as well adds a small amount of code, it also gives us flexibility to support more poker variants.
Let's assume we might need that down the road and keep this code.

## Step 3: Putting it all together

With a quick implementation of input file parsing logic we verify that we get [the expected answer](https://github.com/nayuki/Project-Euler-solutions/blob/master/Answers.txt).

As we revisit the quickly implemented `TwoPlayerDealsSupplier`, we notice it has two responsibilities we can separate:
1. reading a file and producing a list of lines (extract into a generic `ClasspathFileReader`)
1. producing `Deal` objects from these lines (keep in `TwoPlayerDealsSupplier`)

## Step 4: Leveraging packaging

First we separate the generic classes from those having anything to do with poker.

Then we follow the [reader-friendly packaging guidelines](https://javadevguy.wordpress.com/2017/12/18/happy-packaging/):
> 1. Packages should never depend on sub-packages
> 1. Sub-packages should not introduce new concepts, just more details

The resulting structure is this:
```
- <root package>
    - generic
        - ClasspathFileReader
        - Comparators
    - pokerhands
        - deal
            - TwoPlayerDealsSupplier
        - hand
            - Card
            - HandParser
            - FiveCardHandFactory
        - Deal
        - DealHistory
        - Hand
```

- `generic` package contains classes that could be reused in other projects or replaced by some general-purpose utility library
- `pokerhands` package contains classes representing the main concepts in the problem domain and subpackages encapsulating the secondary details
