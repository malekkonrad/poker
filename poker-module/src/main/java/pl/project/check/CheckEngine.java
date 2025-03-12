package pl.project.check;

import pl.project.Player;
import pl.project.cards.Card;
import pl.project.check.evaluators.*;
import pl.project.check.hand.EvaluatedHand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Class that handles checking different layouts of cards that players have
 */
public class CheckEngine {

    private final List<HandEvaluator> evaluators = List.of(
            new RoyalFlushEvaluator(),
            new StraightFlushEvaluator(),
            new FourSomeEvaluator(),
            new FullHouseEvaluator(),
            new FlushEvaluator(),
            new StraightEvaluator(),
            new ThreeSomeEvaluator(),
            new TwoPairEvaluator(),
            new OnePairEvaluator(),
            new HighestCardEvaluator()
    );

    /**
     * All magic happens here
     * @param players List of players
     * @return list of marked hands
     */
    public List<EvaluatedHand> check(List<Player> players) {

        List<EvaluatedHand> evaluatedHands = new ArrayList<>();

        List<HandEvaluator.Layouts> playersLayouts = new ArrayList<>(Collections.nCopies(players.size(), HandEvaluator.Layouts.HIGH_CARD));
        List<List<Card>> hands = new ArrayList<>(Collections.nCopies(players.size(), null));
        int playerIndex = 0;
        for (Player player : players) {
            for (HandEvaluator evaluator : evaluators) {
                evaluator.getPosition().clear();
                if (evaluator.evaluate(player.getHand())){
                    playersLayouts.set(playerIndex, evaluator.getLayout());
                    hands.set(playerIndex, evaluator.getPosition());
                    System.out.println(evaluator.getPosition());
                    EvaluatedHand current = new EvaluatedHand(player.getHand(), evaluator.highestCard(player.getHand()), evaluator.getLayout(), player.getPlayerId(), evaluator.getPosition());

                    evaluatedHands.add(current);
                    break;
                }
                evaluator.getPosition().clear();
            }
            playerIndex++;
        }
        Collections.sort(evaluatedHands);
        return evaluatedHands;

    }


}
