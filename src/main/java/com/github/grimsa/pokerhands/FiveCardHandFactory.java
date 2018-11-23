package com.github.grimsa.pokerhands;

import com.github.grimsa.pokerhands.Card.Value;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.grimsa.pokerhands.FiveCardHandFactory.BaseHand.Rank.*;
import static com.github.grimsa.pokerhands.Comparators.comparingListElements;

final class FiveCardHandFactory implements Function<Set<Card>, Hand> {
    @Override
    public Hand apply(final Set<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A hand must comprise five cards.");
        }

        final var allOfSameSuit = cards.stream().map(Card::getSuit).distinct().count() == 1;
        final var countsByValue = cards.stream().collect(Collectors.groupingBy(Card::getValue, () -> new EnumMap<>(Value.class), Collectors.counting()));
        final var distinctValuesOrderedByCardCountDesc = distinctValuesOrderedByCardCountDesc(countsByValue);

        if (allOfSameSuit && isStraight(countsByValue)) {
            return new StraightFlush(countsByValue.keySet());
        } else if (isFourOfAKind(countsByValue)) {
            return new FourOfAKind(distinctValuesOrderedByCardCountDesc);
        } else if (isFullHouse(countsByValue)) {
            return new FullHouse(distinctValuesOrderedByCardCountDesc);
        } else if (allOfSameSuit) {
            return new Flush(countsByValue.keySet());
        } else if (isStraight(countsByValue)) {
            return new Straight(countsByValue.keySet());
        } else if (isThreeOfAKind(countsByValue)) {
            return new ThreeOfAKind(distinctValuesOrderedByCardCountDesc);
        } else if (isTwoPairs(countsByValue)) {
            return new TwoPairs(distinctValuesOrderedByCardCountDesc);
        } else if (isOnePair(countsByValue)) {
            return new OnePair(distinctValuesOrderedByCardCountDesc);
        } else {
            return new HighCard(countsByValue.keySet());
        }
    }

    private List<Value> distinctValuesOrderedByCardCountDesc(final Map<Value, Long> countsByValue) {
        return countsByValue.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Entry::getKey)
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean isFourOfAKind(final Map<Value, Long> countsByValue) {
        return countsByValue.size() == 2
                && countsByValue.containsValue(4L);
    }

    private boolean isFullHouse(final Map<Value, Long> countsByValue) {
        return countsByValue.size() == 2
                && countsByValue.containsValue(3L)
                && countsByValue.containsValue(2L);
    }

    private boolean isStraight(final Map<Value, Long> countsByValue) {
        final var minValue = Value.minIn(countsByValue.keySet());
        final var maxValue = Value.maxIn(countsByValue.keySet());
        return countsByValue.size() == 5
                && EnumSet.range(minValue, maxValue).size() == 5;
    }

    private boolean isThreeOfAKind(final Map<Value, Long> countsByValue) {
        return countsByValue.size() == 3
                && countsByValue.containsValue(3L);
    }

    private boolean isTwoPairs(final Map<Value, Long> countsByValue) {
        return countsByValue.size() == 3
                && countsByValue.values().stream().filter(count -> count.equals(2L)).count() == 2;
    }

    private boolean isOnePair(final Map<Value, Long> countsByValue) {
        return countsByValue.size() == 4;
    }

    static abstract class BaseHand implements Hand {
        abstract Rank getRank();

        Comparator<Hand> comparingRank() {
            return Comparator.comparing(hand -> ((BaseHand) hand).getRank());
        }

        enum Rank {
            HIGH_CARD,
            ONE_PAIR,
            TWO_PAIRS,
            THREE_OF_A_KIND,
            STRAIGHT,
            FLUSH,
            FULL_HOUSE,
            FOUR_OF_A_KIND,
            STRAIGHT_FLUSH;
        }
    }

    private static class StraightFlush extends Straight {
        StraightFlush(final Set<Value> values) {
            super(values);
        }

        @Override
        public Rank getRank() {
            return STRAIGHT_FLUSH;
        }
    }

    private static class FourOfAKind extends BaseHand {
        private final Value four;
        private final Value kicker;

        FourOfAKind(final List<Value> distinctValuesOrderedByCardCountDesc) {
            four = distinctValuesOrderedByCardCountDesc.get(0);
            kicker = distinctValuesOrderedByCardCountDesc.get(1);
        }

        @Override
        public Rank getRank() {
            return FOUR_OF_A_KIND;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((FourOfAKind) hand).four)
                    .thenComparing(hand -> ((FourOfAKind) hand).kicker)
                    .compare(this, other);
        }
    }

    private static class FullHouse extends BaseHand {
        private final Value three;
        private final Value two;

        FullHouse(final List<Value> distinctValuesOrderedByCardCountDesc) {
            three = distinctValuesOrderedByCardCountDesc.get(0);
            two = distinctValuesOrderedByCardCountDesc.get(1);
        }

        @Override
        public Rank getRank() {
            return FULL_HOUSE;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((FullHouse) hand).three)
                    .thenComparing(hand -> ((FullHouse) hand).two)
                    .compare(this, other);
        }
    }

    private static class Flush extends BaseHand {
        private final List<Value> valuesDesc;

        Flush(final Set<Value> values) {
            valuesDesc = Value.sortedDescending(values);
        }

        @Override
        public Rank getRank() {
            return FLUSH;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((Flush) hand).valuesDesc, comparingListElements(5))
                    .compare(this, other);
        }
    }

    private static class Straight extends BaseHand {
        private final Value highest;

        Straight(final Set<Value> values) {
            highest = Value.maxIn(values);
        }

        @Override
        public Rank getRank() {
            return STRAIGHT;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((Straight) hand).highest)
                    .compare(this, other);
        }
    }

    private static class ThreeOfAKind extends BaseHand {
        private final Value three;
        private final List<Value> kickersDesc;

        ThreeOfAKind(final List<Value> distinctValuesOrderedByCardCountDesc) {
            three = distinctValuesOrderedByCardCountDesc.get(0);
            kickersDesc = Value.sortedDescending(distinctValuesOrderedByCardCountDesc.subList(1, 3));
        }

        @Override
        public Rank getRank() {
            return THREE_OF_A_KIND;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((ThreeOfAKind) hand).three)
                    .thenComparing(hand -> ((ThreeOfAKind) hand).kickersDesc, comparingListElements(2))
                    .compare(this, other);
        }
    }

    private static class TwoPairs extends BaseHand {
        private final Value higherPair;
        private final Value lowerPair;
        private final Value kicker;

        TwoPairs(final List<Value> distinctValuesOrderedByCardCountDesc) {
            final var pairValues = distinctValuesOrderedByCardCountDesc.subList(0, 2);
            higherPair = Value.maxIn(pairValues);
            lowerPair = Value.minIn(pairValues);
            kicker = distinctValuesOrderedByCardCountDesc.get(2);
        }

        @Override
        public Rank getRank() {
            return TWO_PAIRS;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((TwoPairs) hand).higherPair)
                    .thenComparing(hand -> ((TwoPairs) hand).lowerPair)
                    .thenComparing(hand -> ((TwoPairs) hand).kicker)
                    .compare(this, other);
        }
    }

    private static class OnePair extends BaseHand {
        private final Value pair;
        private final List<Value> kickersDesc;

        OnePair(final List<Value> distinctValuesOrderedByCardCountDesc) {
            pair = distinctValuesOrderedByCardCountDesc.get(0);
            kickersDesc = Value.sortedDescending(distinctValuesOrderedByCardCountDesc.subList(1, 4));
        }

        @Override
        public Rank getRank() {
            return ONE_PAIR;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((OnePair) hand).pair)
                    .thenComparing(hand -> ((OnePair) hand).kickersDesc, comparingListElements(3))
                    .compare(this, other);
        }
    }

    private static class HighCard extends BaseHand {
        private final List<Value> valuesDesc;

        HighCard(final Set<Value> values) {
            valuesDesc = Value.sortedDescending(values);
        }

        @Override
        public Rank getRank() {
            return HIGH_CARD;
        }

        @Override
        public int compareTo(Hand other) {
            return comparingRank()
                    .thenComparing(hand -> ((HighCard) hand).valuesDesc, comparingListElements(5))
                    .compare(this, other);
        }
    }
}
