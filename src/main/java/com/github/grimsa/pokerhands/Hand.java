package com.github.grimsa.pokerhands;

import com.github.grimsa.pokerhands.Card.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.grimsa.pokerhands.Card.Value.sortedDescending;

final class Hand implements Comparable<Hand> {
    private final Set<Card> cards;
    private final Map<Value, Set<Card>> cardsByValue;

    Hand(final Set<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A hand must hold exactly five cards.");
        }

        this.cards = Objects.requireNonNull(cards);
        this.cardsByValue = Collections.unmodifiableMap(cards.stream()
                .collect(Collectors.groupingBy(Card::getValue, () -> new EnumMap<>(Value.class), Collectors.toUnmodifiableSet())));
    }

    @Override
    public int compareTo(final Hand other) {
        return ThreeOfAKindComparator.INSTANCE
                .thenComparing(TwoPairsComparator.INSTANCE)
                .thenComparing(OnePairComparator.INSTANCE)
                .thenComparing(HighCardComparator.INSTANCE)
                .compare(this, other);
    }

    private static class HighCardComparator implements Comparator<Hand> {
        private static final HighCardComparator INSTANCE = new HighCardComparator();

        @Override
        public int compare(final Hand hand, final Hand otherHand) {
            final var valuesInHand = sortedDescending(hand.cardsByValue.keySet());
            final var valuesInOtherHand = sortedDescending(otherHand.cardsByValue.keySet());
            return IntStream.range(0, hand.cards.size())
                    .map(index -> valuesInHand.get(index).compareTo(valuesInOtherHand.get(index)))
                    .filter(comparisonResult -> comparisonResult != 0)
                    .findFirst()
                    .orElse(0);
        }
    }

    private static class OnePairComparator implements Comparator<Hand> {
        private static final OnePairComparator INSTANCE = new OnePairComparator();

        @Override
        public int compare(final Hand hand, final Hand otherHand) {
            final var pair1 = findPairOrNull(hand);
            final var pair2 = findPairOrNull(otherHand);
            return Comparator.nullsFirst(Comparator.<Value>naturalOrder())
                    .compare(pair1, pair2);
        }

        private Value findPairOrNull(final Hand hand) {
            return hand.cardsByValue.values().stream()
                    .filter(cards -> cards.size() == 2)
                    .map(cards -> cards.iterator().next())
                    .map(Card::getValue)
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class TwoPairsComparator implements Comparator<Hand> {
        private static final TwoPairsComparator INSTANCE = new TwoPairsComparator();

        @Override
        public int compare(final Hand hand, final Hand otherHand) {
            final var pairsInHand1 = findTwoPairsOrNull(hand);
            final var pairsInHand2 = findTwoPairsOrNull(otherHand);
            return Comparator.nullsFirst(Comparator.comparing(Value::maxIn)
                    .thenComparing(Value::minIn))
                    .compare(pairsInHand1, pairsInHand2);
        }

        private Set<Value> findTwoPairsOrNull(final Hand hand) {
            return hand.cardsByValue.values().stream()
                    .filter(cards -> cards.size() == 2)
                    .map(cards -> cards.iterator().next())
                    .map(Card::getValue)
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> EnumSet.noneOf(Value.class)),
                            pairs -> pairs.size() == 2 ? pairs : null
                    ));
        }
    }

    private static class ThreeOfAKindComparator implements Comparator<Hand> {
        private static final ThreeOfAKindComparator INSTANCE = new ThreeOfAKindComparator();

        @Override
        public int compare(final Hand hand, final Hand otherHand) {
            final var threeOfAKind1 = findThreeOfAKindOrNull(hand);
            final var threeOfAKind2 = findThreeOfAKindOrNull(otherHand);
            return Comparator.nullsFirst(Comparator.<Value>naturalOrder())
                    .compare(threeOfAKind1, threeOfAKind2);
        }

        private Value findThreeOfAKindOrNull(final Hand hand) {
            return hand.cardsByValue.values().stream()
                    .filter(cards -> cards.size() == 3)
                    .map(cards -> cards.iterator().next())
                    .map(Card::getValue)
                    .findFirst()
                    .orElse(null);
        }
    }
}
