package pl.project.check.evaluators;

import org.junit.jupiter.api.Test;
import pl.project.cards.Card;
import pl.project.cards.Card.Rank;
import pl.project.cards.Card.Suit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StraightFlushEvaluatorTest {

    @Test
    void testEvaluate_ReturnsTrue_ForValidStraightFlush() {
        // Arrange
        StraightFlushEvaluator evaluator = new StraightFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertTrue(result, "StraightFlushEvaluator should return true for a valid straight flush.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNotStraight() {
        // Arrange
        StraightFlushEvaluator evaluator = new StraightFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.FOUR, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "StraightFlushEvaluator should return false when cards are not consecutive.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNotFlush() {
        // Arrange
        StraightFlushEvaluator evaluator = new StraightFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.CLUBS), // Different suit
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "StraightFlushEvaluator should return false when cards are not of the same suit.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNeitherStraightNorFlush() {
        // Arrange
        StraightFlushEvaluator evaluator = new StraightFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.CLUBS),
                new Card(Rank.SIX, Suit.SPADES),
                new Card(Rank.EIGHT, Suit.DIAMONDS),
                new Card(Rank.TEN, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "StraightFlushEvaluator should return false when hand is neither straight nor flush.");
    }

    @Test
    void testGetLayout_ReturnsStraightFlush() {
        // Arrange
        StraightFlushEvaluator evaluator = new StraightFlushEvaluator();

        // Act
        HandEvaluator.Layouts layout = evaluator.getLayout();

        // Assert
        assertEquals(HandEvaluator.Layouts.STRAIGHT_FLUSH, layout, "getLayout should return STRAIGHT_FLUSH.");
    }


}