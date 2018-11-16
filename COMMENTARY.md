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
