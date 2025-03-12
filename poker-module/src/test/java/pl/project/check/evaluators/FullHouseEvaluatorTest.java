package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import pl.project.cards.Card.Rank;
import pl.project.cards.Card.Suit;

class FullHouseEvaluatorTest {

    @Test
    void testEvaluate_ReturnsTrue_ForValidFullHouse() {
        // Arrange
        FullHouseEvaluator evaluator = new FullHouseEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.THREE, Suit.HEARTS),
                new Card(Rank.THREE, Suit.CLUBS),
                new Card(Rank.THREE, Suit.DIAMONDS),
                new Card(Rank.FIVE, Suit.SPADES),
                new Card(Rank.FIVE, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertTrue(result, "FullHouseEvaluator should return true for a valid full house.");
        assertEquals(5, evaluator.getPosition().size(), "The position should contain all cards of the full house.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNoFullHouse() {
        // Arrange
        FullHouseEvaluator evaluator = new FullHouseEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.THREE, Suit.CLUBS),
                new Card(Rank.FOUR, Suit.DIAMONDS),
                new Card(Rank.FIVE, Suit.SPADES),
                new Card(Rank.SIX, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "FullHouseEvaluator should return false when no full house is present.");
        assertTrue(evaluator.getPosition().isEmpty(), "The position should remain empty when no full house is found.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenOnlyThreeOfAKind() {
        // Arrange
        FullHouseEvaluator evaluator = new FullHouseEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.THREE, Suit.HEARTS),
                new Card(Rank.THREE, Suit.CLUBS),
                new Card(Rank.THREE, Suit.DIAMONDS),
                new Card(Rank.FOUR, Suit.SPADES),
                new Card(Rank.FIVE, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "FullHouseEvaluator should return false when only three of a kind is present.");
        assertTrue(evaluator.getPosition().isEmpty(), "The position should remain empty when no full house is found.");
    }

    @Test
    void testGetLayout_ReturnsFullHouse() {
        // Arrange
        FullHouseEvaluator evaluator = new FullHouseEvaluator();

        // Act
        HandEvaluator.Layouts layout = evaluator.getLayout();

        // Assert
        assertEquals(HandEvaluator.Layouts.FULL_HOUSE, layout, "getLayout should return FULL_HOUSE.");
    }

}