package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Two pair layout
 */
public class TwoPairEvaluator extends HandEvaluator{


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
        HandEvaluator pair = new OnePairEvaluator();
        if (pair.evaluate(handCopy)){
            position.addAll(pair.getPosition());
            handCopy.removeAll(pair.getPosition());
            HandEvaluator secondPair = new OnePairEvaluator();
            if (secondPair.evaluate(handCopy)){
                position.addAll(secondPair.getPosition());
                return true;
            }
        }
        return false;
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    public Layouts getLayout() {
        return Layouts.TWO_PAIRS;
    }


}
