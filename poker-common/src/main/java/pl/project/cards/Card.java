package pl.project.cards;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The {@code Card} class represents a card containing rand and suit.
 * Class has two public enums - {@code Suit} and {@code Rank}
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Card implements Comparable<Card>{

    public enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES
    }
    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }

    private final Rank rank;
    private final Suit suit;

    /**
     * Method to compare to cards - needed to sort deck
     * @param other the object to be compared.
     * @return negative number if actual card is lesser than compared, 0 if they are equal,
     * positive if actual card is greater than compared card
     */
    @Override
    public int compareTo(Card other) {
        if (getRank().ordinal() < other.getRank().ordinal()) return -1;

        else if (getRank().ordinal() > other.getRank().ordinal()) return 1;

        return getSuit().ordinal() - other.getSuit().ordinal();
    }


    public String toString(){
        return rank + "-" + suit;
    }

    /**
     * Special method to use in comparing card to check for pair
     * @param card
     * @return
     */
    public boolean rankEquals(Card card){
        return rank.equals(card.getRank());
    }

    /**
     * Special method used in ColourEvaluator
     * @param card
     * @return
     */
    public boolean suitEquals(Card card){
        return suit.equals(card.getSuit());
    }


}
