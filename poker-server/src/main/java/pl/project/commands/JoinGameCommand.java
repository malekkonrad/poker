package pl.project.commands;

import pl.project.Game;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * One of implementation of Command Interface
 */
public class JoinGameCommand implements Command {
    /**
     * @param clientChannel
     * @param args
     * @param serverData
     * @throws IOException
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {

        int gameID = Integer.parseInt(args[1]);
        int playerID = Integer.parseInt(args[2]);

        // get game player want to join
        Game game = serverData.games.get(gameID);

        if (game == null) {
            handleReject(clientChannel, gameID);
            return;
        }
        handlePlayerJoin(clientChannel, game, playerID, gameID, serverData);

    }



    void handlePlayerJoin(SocketChannel clientChannel, Game game, int playerID, int gameID, ServerData serverData) throws IOException {
        // add player to game
        int status = game.addPlayer(serverData.players.get(playerID));

        // join, but not enough players to start game
        if (status == 0) {
            handlePendingGame(clientChannel, game, playerID, serverData);
        }

        // start game
        else if (status == 1) {
            handleStartGame(clientChannel, game, playerID, serverData);
        }

        else{
            handleReject(clientChannel, gameID);
        }
    }


    void handlePendingGame(SocketChannel clientChannel, Game game, int playerID, ServerData serverData) throws IOException {
        int gameID = game.getGameID();

        System.out.println("Player " + playerID + " joined the game."+ gameID);
        sendToken(clientChannel, "acceptedJoin " + gameID);

        for (Integer gamePlayerID : game.getPlayerIDs()) {
            if (gamePlayerID != playerID) {
                sendToken(serverData.clients.get(gamePlayerID), "playerJoin " + serverData.players.get(playerID).getUserName());
            }
        }
    }


    void handleStartGame(SocketChannel clientChannel, Game game, int playerID, ServerData serverData) throws IOException {
        int gameID = game.getGameID();

        System.out.println("Player " + playerID + " joined the game."+ gameID);

        System.out.println("Gracze ");
        for (Integer gamePlayerID : game.getPlayerIDs()) {
            System.out.println("id" + gamePlayerID);
        }

        sendToken(clientChannel, "acceptedJoin " + gameID);

        for (Integer gamePlayerID : game.getPlayerIDs()) {
            if (gamePlayerID != playerID) {
                sendToken(serverData.clients.get(gamePlayerID), "playerJoin " + serverData.players.get(playerID).getUserName());
            }

            sendToken(serverData.clients.get(gamePlayerID), "startGame " + gameID);
        }

    }


    void handleReject(SocketChannel clientChannel, int gameID) throws IOException {
        sendToken(clientChannel, "rejectedJoin " + gameID);
    }
}
