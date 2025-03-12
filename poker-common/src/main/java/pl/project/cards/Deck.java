package pl.project.cards;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The {@code Deck} class represents a deck containing objects of class {@link Card}.
 * Default constructor creates deck containing 52 cards.
 */
@Getter
public class Deck {

    private List<Card> cardList = new ArrayList<>();

    /**
     * Default constructor - creates deck of 52 cards
     */
    public Deck() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cardList.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Creates deck based on given List of cards. It will be used in hands
     * @param cardList List of cards, on which deck will be based
     */
    public Deck(List<Card> cardList) {
        this.cardList = cardList;
    }

    /**
     * Shuffles current deck
     */
    public void shuffle() {
        Collections.shuffle(cardList);
    }

    /**
     * Return last {@code numberOfCards} cards and deletes it from the deck and returns
     * @param numberOfCards int representing how many cards we want to give player
     * @return return {@code List} of {@link Card}
     */
    public List<Card> dealHand(int numberOfCards) {
        if (numberOfCards <= 0) {
            throw new IndexOutOfBoundsException("Number of Cards must be greater than 0");
        }
        else if (numberOfCards > cardList.size()) {
            throw new IndexOutOfBoundsException("Number of Cards must be less than the number of Cards");
        }

        List<Card> handView = this.cardList.subList(cardList.size() - numberOfCards, cardList.size());
        List<Card> hand = new ArrayList<>(handView);
        handView.clear();
        return hand;
    }

    /**
     * Method which sorts the {@code Deck}
     */
    public void sort(){
        Collections.sort(cardList);
    }

    /**
     * Returns Card from top of deck
     * @return Card
     */
    public Card getCardFromDeck() {
        return cardList.remove(cardList.size() - 1);
    }

    /**
     * Adds cart back to deck
     * @param card object of Card
     */
    public void addCardToDeck(Card card) {
        cardList.add(0, card);
    }

}
