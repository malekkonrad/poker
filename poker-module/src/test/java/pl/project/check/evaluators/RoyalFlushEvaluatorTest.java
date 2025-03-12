package pl.project.check.evaluators;

import org.junit.jupiter.api.Test;
import pl.project.cards.Card;
import pl.project.cards.Card.Rank;
import pl.project.cards.Card.Suit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoyalFlushEvaluatorTest {


    @Test
    void testEvaluate_ReturnsTrue_ForValidRoyalFlush() {
        // Arrange
        RoyalFlushEvaluator evaluator = new RoyalFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.ACE, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertTrue(result, "RoyalFlushEvaluator should return true for a valid royal flush.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNotAllCardsAreConsecutive() {
        // Arrange
        RoyalFlushEvaluator evaluator = new RoyalFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.NINE, Suit.HEARTS) // Not a royal flush
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "RoyalFlushEvaluator should return false when cards are not a valid royal flush.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNotAllCardsAreSameSuit() {
        // Arrange
        RoyalFlushEvaluator evaluator = new RoyalFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.CLUBS), // Different suit
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.ACE, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "RoyalFlushEvaluator should return false when cards are not all of the same suit.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenNotRoyalCards() {
        // Arrange
        RoyalFlushEvaluator evaluator = new RoyalFlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.JACK, Suit.HEARTS),
                new Card(Rank.QUEEN, Suit.HEARTS),
                new Card(Rank.KING, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "RoyalFlushEvaluator should return false when hand does not contain royal cards.");
    }

    @Test
    void testGetLayout_ReturnsRoyalFlush() {
        // Arrange
        RoyalFlushEvaluator evaluator = new RoyalFlushEvaluator();

        // Act
        HandEvaluator.Layouts layout = evaluator.getLayout();

        // Assert
        assertEquals(HandEvaluator.Layouts.ROYAL_FLUSH, layout, "getLayout should return ROYAL_FLUSH.");
    }

}