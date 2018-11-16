package com.github.grimsa.pokerhands;

import java.util.Objects;
import java.util.Set;

final class Hand implements Comparable<Hand> {
    private final Set<Card> cards;

    Hand(final Set<Card> cards) {
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A hand must hold exactly five cards.");
        }

        this.cards = Objects.requireNonNull(cards);
    }

    @Override
    public int compareTo(final Hand other) {
        throw new UnsupportedOperationException("not implemented");
    }
}
