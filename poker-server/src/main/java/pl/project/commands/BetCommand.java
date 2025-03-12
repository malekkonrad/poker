package pl.project.commands;

import pl.project.Game;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * One of implementation of Command Interface
 */
public class BetCommand implements Command {

    /**
     * Executes the "bet" command, processing a player's action in the current game state.
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param args          an array of command arguments containing game ID, player ID, action type, and amount
     * @param serverData    the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {

        int gameID = Integer.parseInt(args[1]);
        int playerID = Integer.parseInt(args[2]);
        String type = args[3];
        int sum = Integer.parseInt(args[4]);

        Game game = serverData.games.get(gameID);
        Player currentPLayer = game.getPlayers().get(playerID);

        handleRequest(game, playerID, serverData, type, currentPLayer, sum);

        sendToken(serverData.clients.get(playerID), "acceptedBet");

        sendInfoAboutBet(game, playerID, type, sum, serverData);

        handleCurrentState(game, serverData);

    }

    /**
     * Processes the player's specific action during the betting stage.
     *
     * @param game         the {@link Game} object representing the current game
     * @param playerID     the ID of the player performing the action
     * @param serverData   the {@link ServerData} object containing game and player state
     * @param type         the type of action ("fold", "call", "raise", or "allIn")
     * @param currentPLayer the {@link Player} object representing the player performing the action
     * @param sum          the amount of the bet or raise
     * @throws IOException if an I/O error occurs
     */
    void handleRequest(Game game, int playerID, ServerData serverData, String type, Player currentPLayer, int sum) throws IOException {
        if (type.equals("fold")){

            handleFoldToken(game, playerID, serverData);

        }else if(type.equals("call")){

            handleCallToken(game, currentPLayer, sum);

        }else if(type.equals("raise")){

            handleRaiseToken(game, currentPLayer, playerID, sum);

        }else if (type.equals("allIn")){
            handleAllInToken(game, currentPLayer, playerID, sum);
        }

        else{
            System.out.println("Unknown command");
        }
    }

    /**
     * Handles the game's state after a betting action, determining the next player or transitioning to the next stage.
     *
     * @param game       the {@link Game} object representing the current game
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void handleCurrentState(Game game, ServerData serverData) throws IOException {
        if (!game.getCurrentAuctionQueue().isEmpty()){
            int nextPlayerID = game.nextPlayerIDFromQueue();
            String token = "startAuction " + game.getPlayers().get(nextPlayerID).getCash() + " "+ game.getMinimumBet() + " " + game.getStake();
            sendToken(serverData.clients.get(nextPlayerID), token);

        }
        else{

            if (game.getNumberOfAuction() == 0){



                int gameFounderID = game.getQueueOfPlayers();
                System.out.println(gameFounderID);
                if (gameFounderID == -1){
                    handleSendTokenToEveryOne(game, serverData, "lastStage ");

                }
                else{
                    // send to everyone info
                    handleSendTokenToEveryOne(game, serverData, "nextStage ");

                    SocketChannel clientCH = serverData.clients.get(gameFounderID);

                    String token = "changeCards";
                    clientCH.write(ByteBuffer.wrap(token.getBytes(StandardCharsets.UTF_8)));
                    game.setNumberOfAuction(game.getNumberOfAuction() + 1);
                }
            }
            else{
                handleSendTokenToEveryOne(game, serverData, "lastStage ");

            }
        }
    }




    /**
     * Sends a message to all players in the game.
     *
     * @param game       the {@link Game} object representing the current game
     * @param serverData the {@link ServerData} object containing game and player state
     * @param token      the message to be sent
     * @throws IOException if an I/O error occurs
     */
    void handleSendTokenToEveryOne(Game game, ServerData serverData, String token) throws IOException {
        for (Integer gamePlayerID : game.getPlayerIDs()) {
            sendToken(serverData.clients.get(gamePlayerID), token);
        }
    }



    /**
     * Handles the "fold" action for a player, checking for a winner if only one active player remains.
     *
     * @param game       the {@link Game} object representing the current game
     * @param playerID   the ID of the player who folded
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void handleFoldToken(Game game, int playerID, ServerData serverData) throws IOException {

        // change player state to fold and check whether is more than 1 active player
        int winnerID = game.playerFold(playerID);

        // if winnner == -1 -> there more players so do nothing
        // else
        if (winnerID != -1){

            // send them to summary
            for (Integer gamePlayerID : game.getPlayerIDs()) {
                sendToken(serverData.clients.get(gamePlayerID), "winner " + winnerID + " "+ serverData.players.get(winnerID).getUserName() + " " + game.getStake());
            }
        }
    }

    /**
     * Handles the "call" action, deducting the called amount from the player's balance and adding it to the stake.
     *
     * @param game         the {@link Game} object representing the current game
     * @param currentPLayer the {@link Player} performing the call action
     * @param sum          the amount called
     * @throws IOException if an I/O error occurs
     */
    void handleCallToken(Game game, Player currentPLayer, int sum) throws IOException {
        currentPLayer.setCash(currentPLayer.getCash() - sum);
        game.setStake(game.getStake() + sum);
    }


    /**
     * Handles the "raise" action, deducting the raise amount from the player's balance, updating the stake, and notifying other players.
     *
     * @param game         the {@link Game} object representing the current game
     * @param currentPLayer the {@link Player} performing the raise action
     * @param playerID     the ID of the player raising
     * @param sum          the raise amount
     */
    void handleRaiseToken(Game game, Player currentPLayer, int playerID, int sum){
        currentPLayer.setCash(currentPLayer.getCash() - sum);
        game.setStake(game.getStake() + sum);
        game.setMinimumBet(sum);

        List<Integer> ids = game.getPlayersIDsBeforeNotFold(playerID);

        // dodanie graczy którzy muszą ponowanie scallować bo podniesiono zakład
        game.getCurrentAuctionQueue().addAll(ids);
    }

    /**
     * Handles the "all-in" action, setting the player's balance to zero and updating the stake.
     *
     * @param game         the {@link Game} object representing the current game
     * @param currentPLayer the {@link Player} performing the all-in action
     * @param playerID     the ID of the player going all-in
     * @param sum          the all-in amount
     */
    void handleAllInToken(Game game, Player currentPLayer, int playerID, int sum){
        currentPLayer.setCash(0);
        game.setStake(game.getStake() + sum);
        game.setMinimumBet(sum);

        currentPLayer.setAllIn(true);

        System.out.println("tutaj problem?");
        List<Integer> ids = game.getPlayersIDsBeforeNotFold(playerID);
        System.out.println(ids);
        System.out.println("nie tutaj");
        // dodanie graczy którzy muszą ponowanie scallować bo podniesiono zakład
        game.getCurrentAuctionQueue().addAll(ids);
    }


    /**
     * Sends information about a player's betting action to all other players.
     *
     * @param game       the {@link Game} object representing the current game
     * @param playerID   the ID of the player who performed the action
     * @param type       the type of action performed ("fold", "call", "raise", or "allIn")
     * @param sum        the amount involved in the action
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void sendInfoAboutBet(Game game, int playerID, String type, int sum, ServerData serverData) throws IOException {
        // sending info to other players that one player bet
        for (Integer gamePlayerID : game.getPlayerIDs()) {
            if (gamePlayerID != playerID) {
                sendToken(serverData.clients.get(gamePlayerID), "playerBet " + serverData.players.get(playerID).getUserName() + " " + type + " " + sum);
            }
        }
    }


}
