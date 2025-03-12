package pl.project.check.hand;

import pl.project.cards.Card;
import pl.project.check.evaluators.HandEvaluator;

import java.util.Collections;
import java.util.List;

public class EvaluatedHand implements Comparable<EvaluatedHand>{

    public List<Card> hand;
    public List<Card> cardLayout;
    public Card maxCard;
    public HandEvaluator.Layouts layout;
    public Card maxCardOfLayout;
    public int playerID;

    /**
     * Default constructor - one magic trick with finding {@code maxCardOfLayout}
     * @param hand Player hand -
     * @param maxCard Card
     * @param layout just a list
     * @param playerID int
     * @param cardLayout just a list
     */
    public EvaluatedHand(List<Card> hand, Card maxCard, HandEvaluator.Layouts layout, int playerID, List<Card> cardLayout) {
        this.hand = hand;
        this.maxCard = maxCard;
        this.layout = layout;
        this.playerID = playerID;
        this.cardLayout = cardLayout;
        Collections.sort(cardLayout);
        this.maxCardOfLayout = cardLayout.get(cardLayout.size()-1);
    }


    /**
     * Compares object between themselves
     * @param o the object to be compared.
     * @return difference between objects
     */
    @Override
    public int compareTo(EvaluatedHand o) {
        if (layout.equals(o.layout)) {
            return maxCardOfLayout.compareTo(o.maxCardOfLayout);
        }
        return layout.compareTo(o.layout);
    }
}
