package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Full house layout
 */
public class FullHouseEvaluator extends HandEvaluator{

    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    @Override
    public boolean evaluate(List<Card> hand) {
        Collections.sort(hand);
        List<Card> handCopy = new ArrayList<>(hand);
        HandEvaluator threeSome = new ThreeSomeEvaluator();
        HandEvaluator onePair = new OnePairEvaluator();

        if (threeSome.evaluate(handCopy)) {
            position.addAll(threeSome.getPosition());
            handCopy.removeAll(threeSome.getPosition());
            if (onePair.evaluate(handCopy)) {
                position.addAll(onePair.getPosition());
                return true;
            }

        }
        position.clear();
        return false;
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    public Layouts getLayout() {
        return Layouts.FULL_HOUSE;
    }
}
