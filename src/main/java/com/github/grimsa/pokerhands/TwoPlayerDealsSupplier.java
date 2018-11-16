package com.github.grimsa.pokerhands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class TwoPlayerDealsSupplier implements Supplier<List<Deal>> {
    private final String dealsFilePath;
    private final Function<String, Hand> handParser;

    TwoPlayerDealsSupplier(final String dealsFilePath, Function<String, Hand> handParser) {
        this.dealsFilePath = Objects.requireNonNull(dealsFilePath);
        this.handParser = Objects.requireNonNull(handParser);
    }

    @Override
    public List<Deal> get() {
        try (final InputStream resource = getDealsFileInputStream()) {
            return createBufferedReader(resource).lines()
                    .map(this::parseTwoPlayerDeal)
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load deals from file", e);
        }
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

    private InputStream getDealsFileInputStream() {
        return getClass().getClassLoader().getResourceAsStream(dealsFilePath);
    }

    private BufferedReader createBufferedReader(final InputStream resource) {
        return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
    }
}
