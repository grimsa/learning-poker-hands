package com.github.grimsa.pokerhands.hand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.github.grimsa.pokerhands.hand.Card.Value.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class CardTest {
    @Nested
    final class ValueTest {
        @Test
        void maxIn_always_highestValue() {
            assertEquals(TEN, maxIn(Set.of(FIVE, TEN, TWO)));
        }

        @Test
        void minIn_always_lowestValue() {
            assertEquals(THREE, minIn(Set.of(FIVE, TEN, THREE)));
        }

        @Test
        void sortedDescending_always_sorted() {
            assertEquals(List.of(SIX, FOUR, TWO), sortedDescending(Set.of(FOUR, TWO, SIX)));
        }
    }
}
