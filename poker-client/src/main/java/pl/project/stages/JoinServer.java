package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;


/**
 * Represents the stage where a client attempts to join the server.
 * <p>
 * This stage handles the process of logging in by asking for a username,
 * sending a login message to the server, and handling the server's response.
 * </p>
 */
public class JoinServer implements Stage{


    /**
     * Executes the join server stage for the provided client.
     * <p>
     * This method interacts with the user to collect their username,
     * sends the login message to the server, reads the response,
     * and processes the server's feedback to determine the next stage
     * for the client.
     * </p>
     *
     * @param client the client attempting to join the server. The client must have
     *               a properly initialized {@code SocketChannel} for communication.
     */
    @Override
    public void execute(Client client) {
        try {
            Scanner scanner = new Scanner(System.in);
            //get data
            String username = getUsername(scanner);

            // create message
            String joinMessage = "login " + username;

            // send
            SendToken.send(client, joinMessage);

            // read
            String response = ReadToken.read(client);

            String[] words = response.split(" ");

            handleResponse(client, words);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prompts the user to enter their username.
     *
     * @param scanner the scanner used to read user input.
     * @return the username entered by the user.
     */
    public String getUsername(Scanner scanner) {
        //get data
        System.out.print("Enter your username: ");
        return scanner.nextLine();
    }

    /**
     * Processes the server's response to the login attempt.
     * <p>
     * If the response indicates that the login was accepted,
     * the player's ID is set, and the client proceeds to the {@code PreGame} stage.
     * Otherwise, the client is informed that the username is already in use and
     * is returned to the {@code JoinServer} stage to try again.
     * </p>
     *
     * @param client the client whose login response is being handled.
     * @param words  the server's response split into individual words.
     */
    public void handleResponse(Client client, String[] words){
        if (words[0].equals("accepted")) {
            client.setPlayerID(Integer.parseInt(words[1]));
            // Go further
            client.setStage(new PreGame());
        }
        else{
            System.out.println("Given username is occupied, try with different username.");
            client.setStage(new JoinServer());
        }
    }

}
