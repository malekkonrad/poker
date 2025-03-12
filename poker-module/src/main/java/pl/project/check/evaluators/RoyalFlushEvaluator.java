package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.Collections;
import java.util.List;

/**
 * Royal Flush layout
 */
public class RoyalFlushEvaluator extends HandEvaluator{

    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    @Override
    public boolean evaluate(List<Card> hand) {
        Collections.sort(hand);
        HandEvaluator straightFlush = new StraightFlushEvaluator();
        return straightFlush.evaluate(hand) && hand.get(0).getRank() == Card.Rank.TEN && hand.get(4).getRank() == Card.Rank.ACE;
    }

    /**
     * Method with returns value of enum, that describes with layout of cards player has.
     * @return value of enum class
     */
    public Layouts getLayout() {
        return Layouts.ROYAL_FLUSH;
    }
}
