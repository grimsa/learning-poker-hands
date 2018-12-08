package com.github.grimsa.pokerhands;

import com.github.grimsa.pokerhands.Card.Suit;
import com.github.grimsa.pokerhands.Card.Value;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class HandParser implements Function<String, Hand> {
    @Override
    public Hand apply(String handAsString) {
        final var cards = Stream.of(handAsString.split(" "))
                .map(this::parseCard)
                .collect(Collectors.toUnmodifiableSet());
        return new Hand(cards);
    }

    private Card parseCard(final String cardString) {
        if (cardString == null || cardString.length() != 2) {
            throw new IllegalArgumentException("Expected a card representation of value and suite, got: " + cardString);
        }

        return new Card(
                Value.fromSymbol(cardString.charAt(0)),
                Suit.fromSymbol(cardString.charAt(1))
        );
    }
}
