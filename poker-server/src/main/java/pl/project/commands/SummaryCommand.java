package pl.project.commands;

import pl.project.Game;
import pl.project.Player;
import pl.project.cards.Card;
import pl.project.check.CheckEngine;
import pl.project.check.hand.EvaluatedHand;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * One of implementation of Command Interface
 */
public class SummaryCommand implements Command {

    /**
     * Executes the "Summary section" command,
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param args          an array of command arguments
     * @param serverData    the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {

        int gameID = Integer.parseInt(args[1]);
        Game game = serverData.games.get(gameID);

        if (game.getNumActivePlayers() == 1){
            handleFoldWin(game, serverData);
        }
        else{
            handleNormalWin(game, serverData);
        }

        handleClearAfterGame(game, serverData);
    }





    void handleNormalWin(Game game, ServerData serverData) throws IOException {

        List<EvaluatedHand> allEvaluatedHands = evaluatePlayersHands(game);

        EvaluatedHand winnerHand = findWinningHand(game, allEvaluatedHands);

        Player winner = game.getPlayers().get(winnerHand.playerID);


        winner.setWinner(true);
        winner.setCash(winner.getCash() + game.getStake());
        String username = serverData.players.get(winnerHand.playerID).getUserName();

        for (EvaluatedHand hand : allEvaluatedHands) {
            int currentID = hand.playerID;
            int cash = serverData.players.get(currentID).getCash();
            sendToken(serverData.clients.get(currentID), "score " + hand.layout + " "+ winnerHand.playerID + " " + username + " " + winnerHand.layout  + " " + game.getStake() + " "+ cash);
        }

    }

    List<EvaluatedHand> evaluatePlayersHands(Game game){
        List<Player> quickListOfPlayers = new ArrayList<>(game.getPlayers().values());

        CheckEngine checkEngine = new CheckEngine();

        return checkEngine.check(quickListOfPlayers);
    }




    EvaluatedHand findWinningHand(Game game, List<EvaluatedHand> allEvaluatedHands){

        int i = 2;
        EvaluatedHand winnerHand = allEvaluatedHands.get(allEvaluatedHands.size()-1);

        while(game.getPlayers().get(winnerHand.playerID).isFold()){
            winnerHand = allEvaluatedHands.get(allEvaluatedHands.size()-i);
            i++;
        }
        return  winnerHand;
    }


    void handleFoldWin(Game game, ServerData serverData) throws IOException {
        // everyone folded!
        int winnerID = game.foldedWinner();

        Player winner = game.getPlayers().get(winnerID);
        String winnerUsername = winner.getUserName();
        winner.setWinner(true);
        winner.setCash(winner.getCash() + game.getStake());


        for (Integer playerID : game.getPlayerIDs()){
            int cash  = serverData.players.get(playerID).getCash();
            sendToken(serverData.clients.get(playerID), "foldWinner " + winnerUsername + " "+ winnerID + " " + game.getStake() + " " + cash);
        }
    }


    void handleClearAfterGame(Game game, ServerData serverData) {
        for (Integer gamePlayerID : game.getPlayerIDs()) {

            Player player = game.getPlayers().get(gamePlayerID);

            if(player.isWinner()){
                player.setCash(player.getCash() + game.getStake());
                player.setWinner(false);
            }else{
                if (player.getCash()==0){
                    player.setCash(500);
                }
            }

            player.getHand().clear();
            player.setExchangeCounter(0);
            player.setGameId(-1);
            player.setFold(false);
            player.setAllIn(false);

        }

        int gameID = game.getGameID();
        serverData.games.remove(gameID);
    }


}
