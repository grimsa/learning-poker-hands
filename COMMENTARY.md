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
