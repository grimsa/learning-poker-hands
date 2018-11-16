package com.github.grimsa.pokerhands;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

final class Deal {
    private final List<Hand> playerHands;

    Deal(final List<Hand> playerHands) {
        this.playerHands = Objects.requireNonNull(playerHands);
    }

    boolean isWonBy(final int playerIndex) {
        if (playerIndex < 0 || playerIndex >= playerHands.size()) {
            throw new IndexOutOfBoundsException(playerIndex);
        }

        return playerHands.indexOf(getBestHand()) == playerIndex;
    }

    private Hand getBestHand() {
        return playerHands.stream()
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }
}
