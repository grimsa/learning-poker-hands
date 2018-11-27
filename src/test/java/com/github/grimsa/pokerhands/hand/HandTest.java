package com.github.grimsa.pokerhands.hand;

import com.github.grimsa.pokerhands.Hand;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class HandTest {
    private final HandParser handParser = new HandParser(new FiveCardHandFactory());

    @Test
    void sort_differentRanks_sortedByRank() {
        final var highCard = hand("2C 3C AC 8C 9D");
        final var pair = hand("6C 6D QC JC TC");
        final var twoPairs = hand("6C 6D JC JD 5C");
        final var threeOfAKind = hand("AC KC 5D 5H 5C");
        final var straight = hand("4C 5C 6S 7C 8C");
        final var flush = hand("2D 5D TD 8D 7D");
        final var fullHouse = hand("2C 2D 2H 8C 8D");
        final var fourOfAKind = hand("2C 2D 2H 2S 8D");
        final var straightFlush = hand("4C 5C 6C 7C 8C");
        final var royalFlush = hand("AC KC QC TC JC");
        final List<Hand> handsInRandomOrder = List.of(pair, twoPairs, threeOfAKind, fourOfAKind, straight, straightFlush, royalFlush, flush, highCard, fullHouse);

        final List<Hand> handsSortedByRank = handsInRandomOrder.stream()
                .sorted()
                .collect(Collectors.toList());

        final var handsInExpectedOrder = Arrays.asList(highCard, pair, twoPairs, threeOfAKind, straight, flush, fullHouse, fourOfAKind, straightFlush, royalFlush);
        assertEquals(handsInExpectedOrder, handsSortedByRank);
    }

    @Test
    void compareTo_highCardAndFirstHighestValueDiffers_highestCardDeterminesWinner() {
        final var handWithAce = hand("2C 3C AC 8C 9D");
        final var handWithKingAndSeven = hand("KC 2D 7C 4C 5C");

        assertTrue(handWithAce.compareTo(handWithKingAndSeven) > 0);
        assertTrue(handWithKingAndSeven.compareTo(handWithAce) < 0);
    }

    @Test
    void compareTo_highCardAndSameFourHighestValues_fifthHighestCardDeterminesWinner() {
        final var handWithLowestFour = hand("AC KC QC JC 4D");
        final var handWithLowestThree = hand("3C AD KD QD JD");

        assertTrue(handWithLowestFour.compareTo(handWithLowestThree) > 0);
        assertTrue(handWithLowestThree.compareTo(handWithLowestFour) < 0);
    }

    @Test
    void compareTo_pairAndPairValueDiffers_comparedByPairValue() {
        final var pairOfSixes = hand("6C 6D QC JC TC");
        final var pairOfFours = hand("AC KC 2D 4H 4S");

        assertTrue(pairOfSixes.compareTo(pairOfFours) > 0);
        assertTrue(pairOfFours.compareTo(pairOfSixes) < 0);
    }

    @Test
    void compareTo_pairAndSamePairAndDifferentKicker_comparedByKicker() {
        final var pairOfSixesWithQueenKicker = hand("6C 6D QC JC TC");
        final var pairOfSixesWithNineKicker = hand("9C 8C 2D 6H 6S");

        assertTrue(pairOfSixesWithQueenKicker.compareTo(pairOfSixesWithNineKicker) > 0);
        assertTrue(pairOfSixesWithNineKicker.compareTo(pairOfSixesWithQueenKicker) < 0);
    }

    @Test
    void compareTo_pairAndSamePairAndSameKicker_handsEqual() {
        final var pairOfSevens1 = hand("7C 7D 2C 3C 4C");
        final var pairOfSevens2 = hand("7H 7S 4H 3H 2S");

        assertEquals(0, pairOfSevens1.compareTo(pairOfSevens2));
        assertEquals(0, pairOfSevens2.compareTo(pairOfSevens1));
    }

    @Test
    void compareTo_twoPairsAndDifferentHigherPair_comparedByHigherPair() {
        final var pairOfJacksAndSixes = hand("6C 6D JC JD TC");
        final var pairOfTensAndNines = hand("TH TS 9H 9S 4S");

        assertTrue(pairOfJacksAndSixes.compareTo(pairOfTensAndNines) > 0);
        assertTrue(pairOfTensAndNines.compareTo(pairOfJacksAndSixes) < 0);
    }

    @Test
    void compareTo_twoPairsAndDifferentLowerPair_comparedByLowerPair() {
        final var pairOfJacksAndSixes = hand("6C 6D JC JD TC");
        final var pairOfJacksAndFours = hand("JH JS 4H 4S 5D");

        assertTrue(pairOfJacksAndSixes.compareTo(pairOfJacksAndFours) > 0);
        assertTrue(pairOfJacksAndFours.compareTo(pairOfJacksAndSixes) < 0);
    }

    @Test
    void compareTo_twoPairsAndSamePairsAndDifferentKicker_comparedByKicker() {
        final var pairOfJacksAndSixesWithFiveKicker = hand("6C 6D JC JD 5C");
        final var pairOfJacksAndSixesWithFourKicker = hand("JH JS 6H 6S 4D");

        assertTrue(pairOfJacksAndSixesWithFiveKicker.compareTo(pairOfJacksAndSixesWithFourKicker) > 0);
        assertTrue(pairOfJacksAndSixesWithFourKicker.compareTo(pairOfJacksAndSixesWithFiveKicker) < 0);
    }

    @Test
    void compareTo_twoPairsAndSamePairsAndSameKicker_handsEqual() {
        final var pairOfJacksAndSixesWithFiveKicker1 = hand("6C 6D JC JD 5C");
        final var pairOfJacksAndSixesWithFourKicker2 = hand("JH JS 6H 6S 5D");

        assertEquals(0, pairOfJacksAndSixesWithFiveKicker1.compareTo(pairOfJacksAndSixesWithFourKicker2));
        assertEquals(0, pairOfJacksAndSixesWithFourKicker2.compareTo(pairOfJacksAndSixesWithFiveKicker1));
    }

    @Test
    void compareTo_threeOfAKindAndSameValue_comparedByKickers() {
        final var threeSixesWithJackTenKicker = hand("6C 6D 6S JC TC");
        final var threeSixesWithJackFiveKicker = hand("6C 6D 6S JC 5C");

        assertTrue(threeSixesWithJackTenKicker.compareTo(threeSixesWithJackFiveKicker) > 0);
        assertTrue(threeSixesWithJackFiveKicker.compareTo(threeSixesWithJackTenKicker) < 0);
    }

    @Test
    void compareTo_threeOfAKindAndValueDiffers_comparedByValue() {
        final var threeSixes = hand("6C 6D 6S JC TC");
        final var threeFives = hand("AC KC 5D 5H 5C");

        assertTrue(threeSixes.compareTo(threeFives) > 0);
        assertTrue(threeFives.compareTo(threeSixes) < 0);
    }

    @Test
    void compareTo_straightAndSameValues_handsEqual() {
        final var straight1 = hand("4C 5C 6S 7C 8C");
        final var straight2 = hand("4D 5D 6C 8D 7D");

        assertEquals(0, straight1.compareTo(straight2));
        assertEquals(0, straight2.compareTo(straight1));
    }

    @Test
    void compareTo_straightAndDifferentValues_comparedByValue() {
        final var higherStraight = hand("4C 5C 6S 7C 8C");
        final var lowerStraight = hand("4D 5D 6C 7D 3D");

        assertTrue(higherStraight.compareTo(lowerStraight) > 0);
        assertTrue(lowerStraight.compareTo(higherStraight) < 0);
    }

    @Test
    void compareTo_flushAndSameValues_handsEqual() {
        final var flush1 = hand("2C 5C TC 7C 8C");
        final var flush2 = hand("2D 5D TD 8D 7D");

        assertEquals(0, flush1.compareTo(flush2));
        assertEquals(0, flush2.compareTo(flush1));
    }

    @Test
    void compareTo_flushAndFirstValueSame_comparedBySecondValue() {
        final var flushWithKingQueen = hand("4C KC QC 7C 8C");
        final var flushWithKingTen = hand("KD TD 6D 8D 7D");

        assertTrue(flushWithKingQueen.compareTo(flushWithKingTen) > 0);
        assertTrue(flushWithKingTen.compareTo(flushWithKingQueen) < 0);
    }

    @Test
    void compareTo_fullHouseAndSameValues_handsEqual() {
        final var fullHouse1 = hand("2C 2D 2H 7C 7D");
        final var fullHouse2 = hand("2C 2D 2S 7S 7D");

        assertEquals(0, fullHouse1.compareTo(fullHouse2));
        assertEquals(0, fullHouse2.compareTo(fullHouse1));
    }

    @Test
    void compareTo_fullHouseAndSameThree_comparedByPair() {
        final var fullHouseOfTwosAndEights = hand("2C 2D 2H 8C 8D");
        final var fullHouseOfTwosAndSevens = hand("2C 2D 2S 7S 7D");

        assertTrue(fullHouseOfTwosAndEights.compareTo(fullHouseOfTwosAndSevens) > 0);
        assertTrue(fullHouseOfTwosAndSevens.compareTo(fullHouseOfTwosAndEights) < 0);
    }

    @Test
    void compareTo_fullHouseAndDifferentThree_comparedByThree() {
        final var fullHouseOfThreesAndFives = hand("3C 3D 3H 5C 5D");
        final var fullHouseOfTwosAndSevens = hand("7S 7D 2C 2D 2S");

        assertTrue(fullHouseOfThreesAndFives.compareTo(fullHouseOfTwosAndSevens) > 0);
        assertTrue(fullHouseOfTwosAndSevens.compareTo(fullHouseOfThreesAndFives) < 0);
    }

    @Test
    void compareTo_fourOfAKindAndSameValues_handsEqual() {
        final var fourOfAKind1 = hand("2C 2D 2H 2S 8D");
        final var fourOfAKind2 = hand("2C 2D 2H 2S 8S");

        assertEquals(0, fourOfAKind1.compareTo(fourOfAKind2));
        assertEquals(0, fourOfAKind2.compareTo(fourOfAKind1));
    }

    @Test
    void compareTo_fourOfAKindAndSameFour_comparedByKicker() {
        final var fourTwosAndEight = hand("2C 2D 2H 2S 8D");
        final var fourTwosAndSeven = hand("2C 2D 2H 2S 7S");

        assertTrue(fourTwosAndEight.compareTo(fourTwosAndSeven) > 0);
        assertTrue(fourTwosAndSeven.compareTo(fourTwosAndEight) < 0);
    }

    @Test
    void compareTo_fourOfAKindAndDifferentFour_comparedByFour() {
        final var fourThrees = hand("6D 3C 3D 3H 3S");
        final var fourTwos = hand("2C 2D 2H 2S 7S");

        assertTrue(fourThrees.compareTo(fourTwos) > 0);
        assertTrue(fourTwos.compareTo(fourThrees) < 0);
    }

    @Test
    void compareTo_straightFlushAndSameValues_handsEqual() {
        final var straightFlush1 = hand("4C 5C 6C 7C 8C");
        final var straightFlush2 = hand("4D 5D 6D 8D 7D");

        assertEquals(0, straightFlush1.compareTo(straightFlush2));
        assertEquals(0, straightFlush2.compareTo(straightFlush1));
    }

    @Test
    void compareTo_straightFlushAndDifferentValues_comparedByValue() {
        final var higherStraightFlush = hand("4C 5C 6C 7C 8C");
        final var lowerStraightFlush = hand("3D 5D 4D 6D 7D");

        assertTrue(higherStraightFlush.compareTo(lowerStraightFlush) > 0);
        assertTrue(lowerStraightFlush.compareTo(higherStraightFlush) < 0);
    }

    private Hand hand(final String handString) {
        return handParser.apply(handString);
    }
}
