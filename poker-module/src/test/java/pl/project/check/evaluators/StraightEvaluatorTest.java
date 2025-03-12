package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.project.cards.Card.Rank.*;
import static pl.project.cards.Card.Suit.*;


class StraightEvaluatorTest {

    private StraightEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new StraightEvaluator();
    }

    @Test
    void testEvaluate_FindsStraight() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(THREE, HEARTS),
                new Card(FOUR, DIAMONDS),
                new Card(FIVE, CLUBS),
                new Card(SIX, SPADES),
                new Card(SEVEN, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(5, evaluator.getPosition().size());
        assertEquals(new Card(THREE, HEARTS), evaluator.getPosition().get(0));
        assertEquals(new Card(FOUR, DIAMONDS), evaluator.getPosition().get(1));
        assertEquals(new Card(FIVE, CLUBS), evaluator.getPosition().get(2));
        assertEquals(new Card(SIX, SPADES), evaluator.getPosition().get(3));
        assertEquals(new Card(SEVEN, HEARTS), evaluator.getPosition().get(4));
    }

    @Test
    void testEvaluate_NoStraight() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(KING, DIAMONDS),
                new Card(TEN, CLUBS),
                new Card(SIX, SPADES),
                new Card(QUEEN, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertFalse(result);
        assertTrue(evaluator.getPosition().isEmpty());
    }



    @Test
    void testGetLayout_ReturnsStraight() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(THREE, HEARTS),
                new Card(FOUR, DIAMONDS),
                new Card(FIVE, CLUBS),
                new Card(SIX, SPADES),
                new Card(SEVEN, HEARTS)
        ));

        // When
        evaluator.evaluate(hand);

        // Then
        assertEquals(HandEvaluator.Layouts.STRAIGHT, evaluator.getLayout());
    }




}