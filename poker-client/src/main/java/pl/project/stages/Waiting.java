package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;

import java.io.IOException;

/**
 * Represents the waiting stage of the poker game, where players wait for others to join before the game starts.
 */
public class Waiting implements Stage {

    /**
     * Executes the waiting stage for the client.
     * Displays information to the user and waits for server messages to proceed to the next stage.
     *
     * @param client the {@link Client} object representing the player
     */
    @Override
    public void execute(Client client) {
        info();

        try {

            String response = getResponse(client);

            handleResponse(client, response);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Displays information about the waiting stage to the user.
     */
    void info(){
        System.out.println("You are waiting for other players to join the game...");
    }

    /**
     * Transitions the client to the poker game stage and informs them that the game is starting.
     *
     * @param client the {@link Client} object representing the player
     */
    void startGame(Client client){
        System.out.println("\n---------------");
        System.out.println("Game");
        System.out.println("---------------");
        System.out.println("Game is starting!");
        client.setStage(new PokerGame());
    }

    /**
     * Processes the server response during the waiting stage.
     * Handles messages for game start or other players joining.
     *
     * @param client   the {@link Client} object representing the player
     * @param response the server's response as a string
     */
    void handleResponse(Client client, String response){
        String[] parts = response.split(" ");
        if (parts[0].equals("startGame")) {
            startGame(client);
        } else if (parts[0].equals("playerJoin")) {
            System.out.println("Player : [" + parts[1] + "] joined the game!");
        } else {
            System.out.println("Unrecognised message from server");
        }
    }

    /**
     * Reads the server response for the client.
     *
     * @param client the {@link Client} object representing the player
     * @return the server's response as a string
     * @throws IOException if an I/O error occurs during communication
     */
    String getResponse(Client client) throws IOException {
        return  ReadToken.read(client);
    }

}
