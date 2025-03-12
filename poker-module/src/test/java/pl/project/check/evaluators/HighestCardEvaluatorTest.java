package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.project.cards.Card.Rank.*;
import static pl.project.cards.Card.Suit.*;


class HighestCardEvaluatorTest {

    private HighestCardEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new HighestCardEvaluator();
    }

    @Test
    void testEvaluate_ReturnsHighestCard() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(FIVE, HEARTS),
                new Card(QUEEN, CLUBS),
                new Card(ACE, SPADES),
                new Card(TWO, DIAMONDS),
                new Card(TEN, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(1, evaluator.getPosition().size());
        assertEquals(new Card(ACE, SPADES), evaluator.getPosition().get(0));
    }

    @Test
    void testEvaluate_WhenMultipleHighCards_ReturnsCorrectCard() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(KING, HEARTS),
                new Card(JACK, CLUBS),
                new Card(KING, DIAMONDS),
                new Card(NINE, SPADES),
                new Card(SIX, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(1, evaluator.getPosition().size());
        assertEquals(new Card(KING, HEARTS), evaluator.getPosition().get(0));
    }

    @Test
    void testGetLayout_ReturnsHighCard() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(TWO, CLUBS),
                new Card(THREE, HEARTS),
                new Card(FOUR, DIAMONDS),
                new Card(SIX, SPADES),
                new Card(SEVEN, HEARTS)
        ));

        // When
        evaluator.evaluate(hand);

        // Then
        assertEquals(HandEvaluator.Layouts.HIGH_CARD, evaluator.getLayout());
    }

    @Test
    void testEvaluate_EmptyHand_ThrowsException() {
        // Given
        List<Card> emptyHand = new ArrayList<>(List.of());

        // When & Then
        assertThrows(IndexOutOfBoundsException.class, () -> evaluator.evaluate(emptyHand));
    }


}