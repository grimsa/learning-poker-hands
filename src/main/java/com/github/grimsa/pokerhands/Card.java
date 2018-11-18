package com.github.grimsa.pokerhands;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;

final class Card {
    private final Value value;
    private final Suit suit;

    Card(final Value value, final Suit suit) {
        this.value = Objects.requireNonNull(value);
        this.suit = Objects.requireNonNull(suit);
    }

    Value getValue() {
        return value;
    }

    Suit getSuit() {
        return suit;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final var otherCard = (Card) other;
        return value == otherCard.value
                && suit == otherCard.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, suit);
    }

    enum Value {
        TWO('2'),
        THREE('3'),
        FOUR('4'),
        FIVE('5'),
        SIX('6'),
        SEVEN('7'),
        EIGHT('8'),
        NINE('9'),
        TEN('T'),
        JACK('J'),
        QUEEN('Q'),
        KING('K'),
        ACE('A');

        private final char symbol;

        Value(char symbol) {
            this.symbol = symbol;
        }

        static Value fromSymbol(char symbol) {
            return Stream.of(values())
                    .filter(value -> value.symbol == symbol)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown symbol: " + symbol));
        }

        static Value maxIn(final Collection<Value> values) {
            return values.stream().max(naturalOrder()).orElseThrow();
        }

        static Value minIn(final Collection<Value> values) {
            return values.stream().min(naturalOrder()).orElseThrow();
        }

        static List<Value> sortedDescending(final Collection<Value> values) {
            return values.stream().sorted(reverseOrder()).collect(Collectors.toUnmodifiableList());
        }
    }

    enum Suit {
        CLUB('C'),
        DIAMOND('D'),
        HEART('H'),
        SPADE('S');

        private final char symbol;

        Suit(char symbol) {
            this.symbol = symbol;
        }

        static Suit fromSymbol(char symbol) {
            return Stream.of(values())
                    .filter(suit -> suit.symbol == symbol)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown symbol: " + symbol));
        }
    }
}
