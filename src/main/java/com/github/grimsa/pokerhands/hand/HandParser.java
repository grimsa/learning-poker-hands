package com.github.grimsa.pokerhands.hand;

import com.github.grimsa.pokerhands.Hand;
import com.github.grimsa.pokerhands.hand.Card.Suit;
import com.github.grimsa.pokerhands.hand.Card.Value;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HandParser implements Function<String, Hand> {
    private final Function<Set<Card>, Hand> handFactory;

    public HandParser(final Function<Set<Card>, Hand> handFactory) {
        this.handFactory = Objects.requireNonNull(handFactory);
    }

    @Override
    public Hand apply(String handAsString) {
        final var cards = Stream.of(handAsString.split(" "))
                .map(this::parseCard)
                .collect(Collectors.toUnmodifiableSet());
        return handFactory.apply(cards);
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
