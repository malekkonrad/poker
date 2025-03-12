package pl.project.commands;

import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * One of implementation of Command Interface
 */
public class LoginCommand implements Command {

    /**
     * Executes the "login to the game " command,
     *
     * @param clientChannel the {@link SocketChannel} representing the client's connection
     * @param args          an array of command arguments
     * @param serverData    the {@link ServerData} object containing game and player state
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException {

        if (!validateArguments(clientChannel, args)) {
            return;
        }

        String username = args[1];
        if (isUsernameTaken(clientChannel, username, serverData)) {
            return;
        }

        createNewPlayer(clientChannel, username, serverData);

    }



    /**
     * Checks if the username is already taken.
     * @param clientChannel the client's socket channel
     * @param username the username to check
     * @param serverData the server's data
     * @return true if the username is taken, false otherwise
     * @throws IOException if an error occurs while writing to the client channel
     */
    boolean isUsernameTaken(SocketChannel clientChannel, String username, ServerData serverData) throws IOException {
        if (serverData.userNames.contains(username)) {
            sendToken(clientChannel, "Username already in use.");
            return true;
        }
        return false;
    }


    /**
     * Creates a new player and updates server data.
     * @param clientChannel the client's socket channel
     * @param username the username of the new player
     * @param serverData the server's data
     * @throws IOException if an error occurs while writing to the client channel
     */
    void createNewPlayer(SocketChannel clientChannel, String username, ServerData serverData) throws IOException {
        Player newPlayer = new Player(username, serverData.newPlayerID);
        serverData.players.put(serverData.newPlayerID, newPlayer);
        serverData.clients.put(serverData.newPlayerID, clientChannel);
        serverData.reverseUserMap.put(clientChannel, serverData.newPlayerID);
        serverData.userNames.add(username);

        sendToken(clientChannel, "accepted " + serverData.newPlayerID);

        serverData.newPlayerID++;
    }

    /**
     * Validates the arguments provided by the client.
     * @param clientChannel the client's socket channel
     * @param args the arguments passed to the command
     * @return true if arguments are valid, false otherwise
     * @throws IOException if an error occurs while writing to the client channel
     */
    boolean validateArguments(SocketChannel clientChannel, String[] args) throws IOException {
        if (args.length != 2) {
            sendToken(clientChannel, "Invalid arguments for login.");
            return false;
        }
        return true;
    }


}
