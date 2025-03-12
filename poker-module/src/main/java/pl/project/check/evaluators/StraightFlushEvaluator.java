package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.Collections;
import java.util.List;

public class StraightFlushEvaluator extends HandEvaluator{

    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    @Override
    public boolean evaluate(List<Card> hand) {
        Collections.sort(hand);
        HandEvaluator straight = new StraightEvaluator();
        HandEvaluator flush = new FlushEvaluator();
        return straight.evaluate(hand) && flush.evaluate(hand);
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    public Layouts getLayout() {
        return Layouts.STRAIGHT_FLUSH;
    }
}
