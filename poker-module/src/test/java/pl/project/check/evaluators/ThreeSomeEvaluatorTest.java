package pl.project.check.evaluators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.project.cards.Card.Rank.*;
import static pl.project.cards.Card.Suit.*;

class ThreeSomeEvaluatorTest {


    private ThreeSomeEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ThreeSomeEvaluator();
    }

    @Test
    void testEvaluate_FindsThreeOfAKind() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(ACE, CLUBS),
                new Card(KING, SPADES),
                new Card(TWO, HEARTS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(3, evaluator.getPosition().size());
        assertEquals(new Card(ACE, CLUBS), evaluator.getPosition().get(0));
        assertEquals(new Card(ACE, DIAMONDS), evaluator.getPosition().get(1));
        assertEquals(new Card(ACE, HEARTS), evaluator.getPosition().get(2));
    }

    @Test
    void testEvaluate_NoThreeOfAKind() {
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
    void testGetLayout_ReturnsThreeOfAKind() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(ACE, CLUBS),
                new Card(KING, SPADES),
                new Card(TWO, HEARTS)
        ));

        // When
        evaluator.evaluate(hand);

        // Then
        assertEquals(HandEvaluator.Layouts.THREE_OF_A_KIND, evaluator.getLayout());
    }

    @Test
    void testEvaluate_MultipleThreeOfAKind_FindsOne() {
        // Given
        List<Card> hand = new ArrayList<>(List.of(
                new Card(ACE, HEARTS),
                new Card(ACE, DIAMONDS),
                new Card(ACE, CLUBS),
                new Card(KING, SPADES),
                new Card(KING, CLUBS)
        ));

        // When
        boolean result = evaluator.evaluate(hand);

        // Then
        assertTrue(result);
        assertEquals(3, evaluator.getPosition().size());
        assertEquals(new Card(ACE, CLUBS), evaluator.getPosition().get(0));
        assertEquals(new Card(ACE, DIAMONDS), evaluator.getPosition().get(1));
        assertEquals(new Card(ACE, HEARTS), evaluator.getPosition().get(2));
    }


}