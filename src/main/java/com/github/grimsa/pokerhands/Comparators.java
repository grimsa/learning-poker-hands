package com.github.grimsa.pokerhands;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

final class Comparators {
    private Comparators() {
    }

    static <T extends Comparable<? super T>> Comparator<List<T>> comparingListElements(final int listSize) {
        return IntStream.range(0, listSize)
                .mapToObj(index -> Comparator.comparing((List<T> list) -> list.get(index)))
                .reduce(Comparator::thenComparing)
                .orElseThrow();
    }
}
