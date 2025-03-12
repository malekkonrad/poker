package pl.project.check.evaluators;

import pl.project.cards.Card;

import java.util.Collections;
import java.util.List;

/**
 * Four of kind
 */
public class FourSomeEvaluator extends HandEvaluator{

    /**
     * Evaluates the given hand to determine if it satisfies a specific layout (e.g., pair, flush).
     *
     * @param hand the player's hand to evaluate.
     * @return {@code true} if the hand matches the layout; {@code false} otherwise.
     */
    @Override
    public boolean evaluate(List<Card> hand) {
        Collections.sort(hand);
        for (int i = 0; i < hand.size() - 3; i++) {
            if (hand.get(i).rankEquals(hand.get(i + 1)) && hand.get(i).rankEquals(hand.get(i + 2)) && hand.get(i).rankEquals(hand.get(i + 3))) {
                position.add(hand.get(i));
                position.add(hand.get(i + 1));
                position.add(hand.get(i + 2));
                position.add(hand.get(i + 3));
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
        return Layouts.FOUR_OF_A_KIND;
    }
}
