package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import pl.project.cards.Card.Rank;
import pl.project.cards.Card.Suit;

class FlushEvaluatorTest {
    @Test
    void testEvaluate_ReturnsTrue_WhenAllCardsAreSameSuit() {
        // Arrange
        FlushEvaluator evaluator = new FlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.HEARTS),
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.HEARTS),
                new Card(Rank.TEN, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertTrue(result, "FlushEvaluator should return true for a flush.");
        assertEquals(hand, evaluator.getPosition(), "The position should contain the original flush hand.");
    }

    @Test
    void testEvaluate_ReturnsFalse_WhenCardsAreDifferentSuits() {
        // Arrange
        FlushEvaluator evaluator = new FlushEvaluator();
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.FOUR, Suit.CLUBS),
                new Card(Rank.SIX, Suit.HEARTS),
                new Card(Rank.EIGHT, Suit.SPADES),
                new Card(Rank.TEN, Suit.HEARTS)
        ));

        // Act
        boolean result = evaluator.evaluate(hand);

        // Assert
        assertFalse(result, "FlushEvaluator should return false for a non-flush hand.");
        assertTrue(evaluator.getPosition().isEmpty(), "The position should remain empty when no flush is found.");
    }

    @Test
    void testGetLayout_ReturnsFlush() {
        // Arrange
        FlushEvaluator evaluator = new FlushEvaluator();

        // Act
        HandEvaluator.Layouts layout = evaluator.getLayout();

        // Assert
        assertEquals(HandEvaluator.Layouts.FLUSH, layout, "getLayout should return FLUSH.");
    }
}