package com.github.grimsa.pokerhands.deal;

import com.github.grimsa.pokerhands.Deal;
import com.github.grimsa.pokerhands.Hand;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class TwoPlayerDealsFactory implements Supplier<List<Deal>> {
    private final Supplier<List<String>> dealFileLinesSupplier;
    private final Function<String, Hand> handFactory;

    public TwoPlayerDealsFactory(final Supplier<List<String>> dealFileLinesSupplier, final Function<String, Hand> handFactory) {
        this.dealFileLinesSupplier = Objects.requireNonNull(dealFileLinesSupplier);
        this.handFactory = Objects.requireNonNull(handFactory);
    }

    @Override
    public List<Deal> get() {
        return dealFileLinesSupplier.get().stream()
                .map(this::parseTwoPlayerDeal)
                .collect(Collectors.toUnmodifiableList());
    }

    private Deal parseTwoPlayerDeal(final String lineInDealsFile) {
        if (lineInDealsFile.length() != 29) {
            throw new IllegalArgumentException("Unsupported deal file format. Offending line: " + lineInDealsFile);
        }

        final var firstPlayerHand = lineInDealsFile.substring(0, 14);
        final var secondPlayerHand = lineInDealsFile.substring(15);
        return new Deal(List.of(
                parse(firstPlayerHand),
                parse(secondPlayerHand)
        ));
    }

    private Hand parse(final String cardsAsString) {
        return handFactory.apply(cardsAsString);
    }
}
