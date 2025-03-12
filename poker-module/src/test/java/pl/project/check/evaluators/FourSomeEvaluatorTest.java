package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.project.cards.Card.Rank.*;
import static pl.project.cards.Card.Suit.*;

class FourSomeEvaluatorTest {

    private FourSomeEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new FourSomeEvaluator();
    }

    @Test
    void testEvaluate_FindsFourOfAKind() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(ACE, CLUBS),
                new Card(ACE, SPADES),
                new Card(KING, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(4, evaluator.getPosition().size());
        assertEquals(new Card(ACE, CLUBS), evaluator.getPosition().get(0));
        assertEquals(new Card(ACE, DIAMONDS), evaluator.getPosition().get(1));
        assertEquals(new Card(ACE, HEARTS), evaluator.getPosition().get(2));
        assertEquals(new Card(ACE, SPADES), evaluator.getPosition().get(3));
    }

    @Test
    void testEvaluate_NoFourOfAKind() {
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
    void testGetLayout_ReturnsFourOfAKind() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(ACE, CLUBS),
                new Card(ACE, SPADES),
                new Card(KING, HEARTS)
        ));

        // When
        evaluator.evaluate(hand);

        // Then
        assertEquals(HandEvaluator.Layouts.FOUR_OF_A_KIND, evaluator.getLayout());
    }

    @Test
    void testEvaluate_MultipleFourOfAKind_FindsOne() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(ACE, CLUBS),
                new Card(ACE, SPADES),
                new Card(KING, HEARTS),
                new Card(KING, DIAMONDS),
                new Card(KING, CLUBS),
                new Card(KING, SPADES)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(4, evaluator.getPosition().size());
        assertEquals(new Card(KING, CLUBS), evaluator.getPosition().get(0));
        assertEquals(new Card(KING, DIAMONDS), evaluator.getPosition().get(1));
        assertEquals(new Card(KING, HEARTS), evaluator.getPosition().get(2));
        assertEquals(new Card(KING, SPADES), evaluator.getPosition().get(3));
    }





}