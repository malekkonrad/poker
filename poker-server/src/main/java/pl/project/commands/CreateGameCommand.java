package pl.project.commands;

import pl.project.Game;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * One of implementation of Command Interface
 */
public class CreateGameCommand implements Command {

    /**
     * Executes the "create game" command
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param args          an array of command arguments
     * @param serverData    the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {

        // get playerID from token
        int playerID = Integer.parseInt(args[1]);

        Player player = serverData.players.get(playerID);

        // create new game
        Game newGame = new Game(serverData.newGameID, player, serverData.maxNumberOfPlayers);

        // added new game to map of games
        serverData.games.put(serverData.newGameID, newGame);

        // send information with id of game
        sendToken(clientChannel, "acceptedCreate "+ serverData.newGameID);

        // update games number
        serverData.newGameID++;
    }


}
