package pl.project.check.hand;

import org.junit.jupiter.api.Test;
import pl.project.cards.Card;
import pl.project.cards.Card.Rank;
import pl.project.cards.Card.Suit;
import pl.project.check.evaluators.HandEvaluator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatedHandTest {

    @Test
    void testConstructor_SortsCardLayoutAndSetsMaxCardOfLayout() {
        // Arrange
        List<Card> hand = new ArrayList<>(List.of(
                new Card(Rank.THREE, Suit.HEARTS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.TWO, Suit.HEARTS)
        ));
        List<Card> cardLayout = new ArrayList<>(List.of(
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.TWO, Suit.HEARTS),
                new Card(Rank.THREE, Suit.HEARTS)
        ));

        // Act
        EvaluatedHand evaluatedHand = new EvaluatedHand(
                hand, new Card(Rank.FIVE, Suit.HEARTS), HandEvaluator.Layouts.FLUSH, 1, cardLayout);

        // Assert
        assertEquals(new Card(Rank.FIVE, Suit.HEARTS), evaluatedHand.maxCardOfLayout,
                "maxCardOfLayout should be the highest card after sorting cardLayout.");
    }

    @Test
    void testCompareTo_DifferentLayouts() {
        // Arrange
        EvaluatedHand hand1 = new EvaluatedHand(
                createHand(Rank.FIVE, Rank.FIVE, Rank.FIVE, Rank.THREE, Rank.THREE),
                new Card(Rank.FIVE, Suit.HEARTS),
                HandEvaluator.Layouts.FULL_HOUSE,
                1,
                createHand(Rank.FIVE, Rank.FIVE, Rank.FIVE, Rank.THREE, Rank.THREE)
        );

        EvaluatedHand hand2 = new EvaluatedHand(
                createHand(Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX),
                new Card(Rank.SIX, Suit.SPADES),
                HandEvaluator.Layouts.STRAIGHT,
                2,
                createHand(Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX)
        );

        // Act & Assert
        assertTrue(hand1.compareTo(hand2) > 0, "Full House should be stronger than Straight.");
    }

    @Test
    void testCompareTo_SameLayouts_DifferentMaxCardOfLayout() {
        // Arrange
        EvaluatedHand hand1 = new EvaluatedHand(
                createHand(Rank.TWO, Rank.TWO, Rank.TWO, Rank.THREE, Rank.THREE),
                new Card(Rank.THREE, Suit.CLUBS),
                HandEvaluator.Layouts.FULL_HOUSE,
                1,
                createHand(Rank.TWO, Rank.TWO, Rank.TWO, Rank.THREE, Rank.THREE)
        );

        EvaluatedHand hand2 = new EvaluatedHand(
                createHand(Rank.THREE, Rank.THREE, Rank.THREE, Rank.TWO, Rank.TWO),
                new Card(Rank.THREE, Suit.HEARTS),
                HandEvaluator.Layouts.FULL_HOUSE,
                2,
                createHand(Rank.TWO, Rank.TWO, Rank.THREE, Rank.THREE, Rank.THREE)
        );

        // Act & Assert
        assertTrue(hand2.compareTo(hand1) >= 0, "Higher maxCardOfLayout should win when layouts are the same.");
    }

    @Test
    void testCompareTo_SameLayouts_SameMaxCardOfLayout() {
        // Arrange
        EvaluatedHand hand1 = new EvaluatedHand(
                createHand(Rank.THREE, Rank.THREE, Rank.THREE, Rank.FIVE, Rank.FIVE),
                new Card(Rank.THREE, Suit.HEARTS),
                HandEvaluator.Layouts.FULL_HOUSE,
                1,
                createHand(Rank.THREE, Rank.THREE, Rank.THREE, Rank.FIVE, Rank.FIVE)
        );

        EvaluatedHand hand2 = new EvaluatedHand(
                createHand(Rank.THREE, Rank.THREE, Rank.THREE, Rank.FIVE, Rank.FIVE),
                new Card(Rank.THREE, Suit.DIAMONDS),
                HandEvaluator.Layouts.FULL_HOUSE,
                2,
                createHand(Rank.THREE, Rank.THREE, Rank.THREE, Rank.FIVE, Rank.FIVE)
        );

        // Act & Assert
        assertEquals(0, hand1.compareTo(hand2), "Hands with the same layout and maxCardOfLayout should be equal.");
    }

    // Helper method to create hands quickly
    private List<Card> createHand(Rank... ranks) {
        List<Card> hand = new ArrayList<>();
        for (Rank rank : ranks) {
            hand.add(new Card(rank, Suit.HEARTS));
        }
        return hand;
    }

}