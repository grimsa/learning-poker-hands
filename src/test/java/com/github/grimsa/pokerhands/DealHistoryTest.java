package com.github.grimsa.pokerhands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DealHistoryTest {
    @Test
    void dealHistory_projectEulerDataset_correctResults() {
        final var dealHistory = new DealHistory(
                new TwoPlayerDealsSupplier(
                        new ClasspathFileReader("p054_poker.txt"),
                        new HandParser(new FiveCardHandFactory())
                )
        );

        assertEquals(1000, dealHistory.dealCount());
        assertEquals(376, dealHistory.countWinsOfPlayer(0));
        assertEquals(624, dealHistory.countWinsOfPlayer(1));
    }
}
