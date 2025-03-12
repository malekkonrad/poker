package pl.project.cards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCompareTo() {
        Card card1 = new Card(Card.Rank.THREE, Card.Suit.CLUBS);
        Card card2 = new Card(Card.Rank.THREE, Card.Suit.HEARTS);
        Card card3 = new Card(Card.Rank.FIVE, Card.Suit.DIAMONDS);

        // card1 and card2 have the same rank, compare by suit
        assertTrue(card1.compareTo(card2) < 0); // CLUBS < HEARTS
        assertTrue(card2.compareTo(card1) > 0); // HEARTS > CLUBS

        // card3 has a higher rank than card1
        assertTrue(card3.compareTo(card1) > 0); // FIVE > THREE
    }

    @Test
    void testRankEquals() {
        Card card1 = new Card(Card.Rank.KING, Card.Suit.SPADES);
        Card card2 = new Card(Card.Rank.KING, Card.Suit.CLUBS);
        Card card3 = new Card(Card.Rank.QUEEN, Card.Suit.HEARTS);

        assertTrue(card1.rankEquals(card2)); // Same rank
        assertFalse(card1.rankEquals(card3)); // Different rank
    }

    @Test
    void testSuitEquals() {
        Card card1 = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        Card card2 = new Card(Card.Rank.TWO, Card.Suit.HEARTS);
        Card card3 = new Card(Card.Rank.THREE, Card.Suit.SPADES);

        assertTrue(card1.suitEquals(card2)); // Same suit
        assertFalse(card1.suitEquals(card3)); // Different suit
    }

    @Test
    void testEqualsAndHashCode() {
        Card card1 = new Card(Card.Rank.JACK, Card.Suit.DIAMONDS);
        Card card2 = new Card(Card.Rank.JACK, Card.Suit.DIAMONDS);
        Card card3 = new Card(Card.Rank.JACK, Card.Suit.CLUBS);
        Card card4 = new Card(Card.Rank.TEN, Card.Suit.HEARTS);

        assertEquals(card1, card2); // Same rank and suit
        assertNotEquals(card1, card3); // Different suit
        assertNotEquals(card1, card4);
        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }

    @Test
    void testToString() {
        Card card = new Card(Card.Rank.TEN, Card.Suit.SPADES);
        assertEquals("TEN-SPADES", card.toString());
    }
}