package com.github.grimsa.pokerhands;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

final class DealHistory {
    private final List<Deal> deals;

    DealHistory(final Supplier<List<Deal>> dealsSupplier) {
        deals = Objects.requireNonNull(dealsSupplier.get());
    }

    int dealCount() {
        return deals.size();
    }

    int countWinsOfPlayer(int playerIndex) {
        final long winCount = deals.stream()
                .filter(deal -> deal.isWonBy(playerIndex))
                .count();
        return Math.toIntExact(winCount);
    }
}
