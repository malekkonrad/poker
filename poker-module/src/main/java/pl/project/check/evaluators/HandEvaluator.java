package pl.project.check.evaluators;


import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Abstract class representing a hand evaluator for poker games.
 * It provides the structure for evaluating a player's hand and determining
 * its layout (e.g., pair, straight, flush).
 */
public abstract class HandEvaluator {

    /**
     * Enumeration representing the possible poker hand layouts.
     */
    public enum Layouts{
        HIGH_CARD, PAIR, TWO_PAIRS, THREE_OF_A_KIND, STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH
    }


    List<Card> position = new ArrayList<>();


    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    public abstract boolean evaluate(List<Card> hand);


    /**
     * Retrieves the list of cards contributing to the evaluated hand.
     *
     * @return a list of {@code Card} objects that form the layout.
     */
    public List<Card> getPosition(){
        return position;
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    public abstract Layouts getLayout();


    /**
     * Determines the highest card in the given hand without modifying the original list.
     *
     * @param hand the player's hand to evaluate.
     * @return the {@code Card} object representing the highest-ranked card.
     */
    public Card highestCard(List<Card> hand){
        Collections.sort(hand);
        return hand.get(hand.size()-1);
    }
}
