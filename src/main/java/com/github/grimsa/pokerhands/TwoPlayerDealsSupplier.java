package com.github.grimsa.pokerhands;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class TwoPlayerDealsSupplier implements Supplier<List<Deal>> {
    private final Supplier<List<String>> dealFileLinesSupplier;
    private final Function<String, Hand> handParser;

    TwoPlayerDealsSupplier(final Supplier<List<String>> dealFileLinesSupplier, final Function<String, Hand> handParser) {
        this.dealFileLinesSupplier = Objects.requireNonNull(dealFileLinesSupplier);
        this.handParser = Objects.requireNonNull(handParser);
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
        return handParser.apply(cardsAsString);
    }
}
