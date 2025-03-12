package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.Collections;
import java.util.List;

import static pl.project.check.evaluators.HandEvaluator.Layouts.*;

/**
 * Highest card layout
 */
public class HighestCardEvaluator extends HandEvaluator{

    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    @Override
    public boolean evaluate(List<Card> hand) {
        Collections.sort(hand);
        position.add(hand.get(hand.size()-1));
        return true;
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    @Override
    public Layouts getLayout() {
        return HIGH_CARD;
    }
}