package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.Collections;
import java.util.List;

/**
 * Straight layout
 */
public class StraightEvaluator extends HandEvaluator {

    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    @Override
    public boolean evaluate(List<Card> hand) {
        Collections.sort(hand);
        for (int i = 1; i < hand.size() ; i++) {
            if (  (   hand.get(i).getRank().ordinal()-1 != (hand.get(i - 1).getRank().ordinal())   )   ) {
                return false;
            }
        }
        position.addAll(hand);
        return true;
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    public Layouts getLayout() {
        return Layouts.STRAIGHT;
    }
}

