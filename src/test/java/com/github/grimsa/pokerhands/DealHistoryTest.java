package com.github.grimsa.pokerhands;

import com.github.grimsa.generic.ClasspathFile;
import com.github.grimsa.pokerhands.deal.TwoPlayerDealsFactory;
import com.github.grimsa.pokerhands.hand.HandFromFiveCardsFactory;
import com.github.grimsa.pokerhands.hand.HandFromStringFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DealHistoryTest {
    @Test
    void dealHistory_projectEulerDataset_correctResults() {
        final var dealHistory = new DealHistory(
                new TwoPlayerDealsFactory(
                        new ClasspathFile("p054_poker.txt"),
                        new HandFromStringFactory(new HandFromFiveCardsFactory())
                )
        );

        assertEquals(1000, dealHistory.dealCount());
        assertEquals(376, dealHistory.countWinsOfPlayer(0));
        assertEquals(624, dealHistory.countWinsOfPlayer(1));
    }
}
