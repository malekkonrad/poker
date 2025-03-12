package pl.project.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {


    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testDefaultDeckSize() {
        assertEquals(52, deck.getCardList().size(), "Default deck should contain 52 cards.");
    }

    @Test
    void testCustomDeckInitialization() {
        List<Card> customCards = List.of(
                new Card(Card.Rank.ACE, Card.Suit.SPADES),
                new Card(Card.Rank.KING, Card.Suit.HEARTS)
        );
        Deck customDeck = new Deck(customCards);

        assertEquals(2, customDeck.getCardList().size(), "Custom deck size should match the given list.");
    }

    @Test
    void testShuffle() {
        List<Card> originalDeck = List.copyOf(deck.getCardList());
        deck.shuffle();
        assertNotEquals(originalDeck, deck.getCardList(), "Deck should be shuffled and different from the original.");
    }

    @Test
    void testDealHandValid() {
        List<Card> dealtCards = deck.dealHand(5);
        assertEquals(5, dealtCards.size(), "Dealt hand should contain 5 cards.");
        assertEquals(47, deck.getCardList().size(), "Deck should have 47 cards after dealing 5 cards.");
    }

    @Test
    void testDealHandInvalid() {
        assertThrows(IndexOutOfBoundsException.class, () -> deck.dealHand(53),
                "Dealing more cards than the deck contains should throw an exception.");
        assertThrows(IndexOutOfBoundsException.class, () -> deck.dealHand(0),
                "Dealing 0 cards should throw an exception.");
    }

    @Test
    void testSortDeck() {
        deck.shuffle();
        deck.sort();

        Deck sortedDeck = new Deck();
        sortedDeck.sort();
        assertEquals(sortedDeck.getCardList(), deck.getCardList(), "Deck should be sorted to its default state.");
    }

    @Test
    void testGetCardFromDeck() {
        int initialSize = deck.getCardList().size();
        Card card = deck.getCardFromDeck();

        assertNotNull(card, "Retrieved card should not be null.");
        assertEquals(initialSize - 1, deck.getCardList().size(), "Deck size should decrease by 1.");
    }

    @Test
    void testAddCardToDeck() {
        Card newCard = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        deck.addCardToDeck(newCard);

        assertEquals(newCard, deck.getCardList().get(0), "Added card should be at the top of the deck.");
        assertEquals(53, deck.getCardList().size(), "Deck size should increase to 53 after adding a card.");
    }
}