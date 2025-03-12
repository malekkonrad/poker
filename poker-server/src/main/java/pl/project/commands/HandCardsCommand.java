package pl.project.commands;

import pl.project.Game;
import pl.project.Player;
import pl.project.cards.Card;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * One of implementation of Command Interface
 */
public class HandCardsCommand implements Command {

    /**
     * Executes the "handing card" command
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param args          an array of command arguments
     * @param serverData    the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {

        int gameID = Integer.parseInt(args[1]);
        int playerID = Integer.parseInt(args[2]);
        String typeOfRequest = args[3];

        // get game player want to join
        Game game = serverData.games.get(gameID);

        if (typeOfRequest.equals("request")){

            handleRequestToken(clientChannel, game, playerID);

        }else if(typeOfRequest.equals("accepted")){

            handleAcceptedToken(game, playerID, serverData);

        }

    }





    void handleRequestToken(SocketChannel clientChannel, Game game, int playerID) throws IOException {
        String token = makeCardsToken(game, playerID);
        clientChannel.write(ByteBuffer.wrap(token.getBytes(StandardCharsets.UTF_8)));
    }


    String makeCardsToken(Game game, int playerID){
        List<String> handToSend = game.handCards(playerID);
        StringBuilder token = new StringBuilder("cards ");
        for (String card : handToSend){
            token.append(card).append(" ");
        }
        return token.toString();
    }




    void handleAcceptedToken(Game game, int playerID, ServerData serverData) throws IOException {

        int pos = game.getOrderedPlayersIDs().indexOf(playerID);

        if (pos != game.getOrderedPlayersIDs().size() - 1){

            // to jest pozycja a nie id playera!!!!!!!!!!!!!!!!!!!!!!!
            pos++;

            // we get next player
            int newPlayerID = game.getOrderedPlayersIDs().get(pos);

            String token = makeCardsToken(game, newPlayerID);

            // zmienic wysłanie do innego gracza
            SocketChannel clientCH = serverData.clients.get(newPlayerID);

            sendToken(clientCH, token);

        }
        else{

            int gameFounderID = game.getQueueOfPlayers();

            SocketChannel clientCH = serverData.clients.get(gameFounderID);

            String token = "startAuction " + game.getPlayers().get(gameFounderID).getCash() + " " + game.getMinimumBet() + " " + game.getStake();

            clientCH.write(ByteBuffer.wrap(token.getBytes(StandardCharsets.UTF_8)));

        }



    }






}
