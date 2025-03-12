package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.project.cards.Card.Rank.*;
import static pl.project.cards.Card.Suit.*;

class TwoPairEvaluatorTest {


    private TwoPairEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new TwoPairEvaluator();
    }

    @Test
    void testEvaluate_FindsTwoPairs() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(KING, CLUBS),
                new Card(KING, SPADES),
                new Card(TWO, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(4, evaluator.getPosition().size());

        assertEquals(new Card(KING, CLUBS), evaluator.getPosition().get(0));
        assertEquals(new Card(KING, SPADES), evaluator.getPosition().get(1));
        assertEquals(new Card(ACE, DIAMONDS), evaluator.getPosition().get(2));
        assertEquals(new Card(ACE, HEARTS), evaluator.getPosition().get(3));
    }

    @Test
    void testEvaluate_NoTwoPairs() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(KING, DIAMONDS),
                new Card(THREE, CLUBS),
                new Card(EIGHT, SPADES),
                new Card(TWO, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertFalse(result);
        assertTrue(evaluator.getPosition().isEmpty());
    }

    @Test
    void testGetLayout_ReturnsTwoPairs() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(KING, CLUBS),
                new Card(KING, SPADES),
                new Card(TWO, HEARTS)
        ));

        // When
        evaluator.evaluate(hand);

        // Then
        assertEquals(HandEvaluator.Layouts.TWO_PAIRS, evaluator.getLayout());
    }

    @Test
    void testEvaluate_MultiplePairs_FindsTwoPairs() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(KING, CLUBS),
                new Card(KING, SPADES),
                new Card(QUEEN, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(4, evaluator.getPosition().size());
        assertEquals(new Card(ACE, DIAMONDS), evaluator.getPosition().get(2));
        assertEquals(new Card(ACE, HEARTS), evaluator.getPosition().get(3));
        assertEquals(new Card(KING, CLUBS), evaluator.getPosition().get(0));
        assertEquals(new Card(KING, SPADES), evaluator.getPosition().get(1));
    }

}