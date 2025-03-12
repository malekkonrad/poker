package pl.project.commands;

import pl.project.Game;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * One of implementation of Command Interface
 */
public class ChangeCardCommand implements Command {

    /**
     * Executes the "change card" command, handling whether a player wants to change a card or not.
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param args          an array of command arguments containing game ID, player ID, type ("yes"/"no"), and optionally card ID
     * @param serverData    the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {
        // checking if the token is correct

        int gameID = Integer.parseInt(args[1]);
        int playerID = Integer.parseInt(args[2]);
        String type = args[3];


        // game
        Game game = serverData.games.get(gameID);

        if (type.equals("yes")){
            int cardID = Integer.parseInt(args[4]);
            handleChangeCardToken(clientChannel, game, playerID, serverData, cardID);

        }
        else{
            handleNoChangeToken(game, playerID, serverData);
        }

    }


    /**
     * Handles the card change request for a player. If the player hasn't exceeded the allowed number of exchanges,
     * a new card is provided; otherwise, the request is denied.
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param game          the {@link Game} object representing the current game
     * @param playerID      the ID of the player requesting the card change
     * @param serverData    the {@link ServerData} object containing game and player state
     * @param cardID        the ID of the card to be exchanged
     * @throws IOException if an I/O error occurs
     */
    void handleChangeCardToken(SocketChannel clientChannel, Game game, int playerID, ServerData serverData, int cardID) throws IOException {
        int count = serverData.getPlayers().get(playerID).getExchangeCounter();

        if (count < 4) {
            String newCard = game.changeCard(playerID, cardID);
            sendToken(clientChannel, "acceptedChange " + cardID + " " + newCard);
            serverData.getPlayers().get(playerID).setExchangeCounter(count + 1);
        }
        else{
            // odmowa wymiany
            sendToken(clientChannel, "deniedChange ");

            handleAuctionOrChange(game, serverData);

        }
    }


    /**
     * Handles the scenario where a player opts not to change cards, sending confirmation and progressing the game state.
     *
     * @param game       the {@link Game} object representing the current game
     * @param playerID   the ID of the player who declined to change cards
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void handleNoChangeToken(Game game, int playerID, ServerData serverData) throws IOException {

        sendToken(serverData.clients.get(playerID), "acceptedEndChanging");

        handleAuctionOrChange(game, serverData);

    }


    /**
     * Determines whether to continue with the card change phase or proceed to the next stage (auction).
     *
     * @param game       the {@link Game} object representing the current game
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void handleAuctionOrChange(Game game, ServerData serverData) throws IOException {
        if (!game.getCurrentAuctionQueue().isEmpty()){
            int nextPlayerID = game.nextPlayerIDFromQueue();

            sendToken(serverData.clients.get(nextPlayerID), "changeCards");
        }
        else{
            sendRequestNextStage(game, serverData);
            game.setMinimumBet(100);
            sendTokenToStarAuction(game, serverData);
        }
    }


    /**
     * Sends a token to all players indicating the transition to the next stage.
     *
     * @param game       the {@link Game} object representing the current game
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void sendRequestNextStage(Game game, ServerData serverData) throws IOException {

        for (Integer gamePlayerID : game.getPlayerIDs()) {
            sendToken(serverData.clients.get(gamePlayerID), "nextStage ");
        }

    }

    /**
     * Sends the "start auction" token to the game founder, signaling the start of the auction phase.
     *
     * @param game       the {@link Game} object representing the current game
     * @param serverData the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    void sendTokenToStarAuction(Game game, ServerData serverData) throws IOException {

        int gameFounderID = game.getQueueOfPlayers();
        SocketChannel clientCH = serverData.clients.get(gameFounderID);
        String token = "startAuction " + game.getPlayers().get(gameFounderID).getCash() + " "+ game.getMinimumBet() + " " + game.getStake();
        clientCH.write(ByteBuffer.wrap(token.getBytes(StandardCharsets.UTF_8)));

    }



}
