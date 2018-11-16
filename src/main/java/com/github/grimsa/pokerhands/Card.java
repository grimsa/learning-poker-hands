package com.github.grimsa.pokerhands;

import java.util.Objects;

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
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE
    }

    enum Suit {
        CLUB,
        DIAMOND,
        HEART,
        SPADE
    }
}
