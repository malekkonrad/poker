package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.IOException;
import java.util.Scanner;

/**
 * Represents the pre-game stage where a player can either create or join a game.
 * Handles the communication with the server and transitions to the appropriate next stage.
 */
public class PreGame implements Stage {

    /**
     * Executes the pre-game stage for the client.
     * Allows the player to either create or join a game and handles server communication.
     *
     * @param client the {@link Client} object representing the player
     */
    @Override
    public void execute(Client client) {

        preGameWelcome();
        // get data

        Scanner scanner = new Scanner(System.in);
        String command = "exit";

        try {

            while (true) {
                System.out.print("Type 'join' or 'create': ");
                command = scanner.nextLine().trim();

                if ("create".equals(command)) {
                    command = handleCreate(client);
                    break; // Kończy pętlę
                } else if ("join".equals(command)) {
                    command = handleJoin(client, scanner);
                    break; // Kończy pętlę
                } else {
                    System.out.println("Wrong command, try again:");
                }
            }
            // send
            SendToken.send(client, command);
            // read
            String response = ReadToken.read(client);
            // split
            String[] tokens = response.split(" ");

            handleResponse(client, tokens);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Displays a welcome message and instructions for the pre-game stage.
     */
    public void preGameWelcome() {
        System.out.println("\n----------------");
        System.out.println("Lobby");
        System.out.println("----------------");
        System.out.println("Welcome to the lobby! You can join or create a game.");
    }

    /**
     * Handles the response from the server after a player attempts to create or join a game.
     *
     * @param client the {@link Client} object representing the player
     * @param tokens the server response split into an array of tokens
     */
    public void handleResponse(Client client, String[] tokens) {
        if (tokens[0].equals("acceptedCreate")) {
            System.out.println("\nYou created a new game");
            System.out.println("GAME ID (for other players to join): " + tokens[1]);
            client.setGameID(Integer.parseInt(tokens[1]));
            client.setStage(new Waiting());
        } else if (tokens[0].equals("acceptedJoin")) {
            System.out.println("\n" + "You have joined the game. Moving to the waiting stage...");
            client.setGameID(Integer.parseInt(tokens[1]));
            client.setStage(new Waiting());
        } else if (tokens[0].equals("rejectedJoin")) {
            System.out.println("Game of ID you entered does not exist");
        }

        else {
            System.out.println("Unrecognised message from server.");
        }
    }

    /**
     * Prepares the command to create a game and sets the client as the game founder.
     *
     * @param client the {@link Client} object representing the player
     * @return a string command to send to the server for game creation
     */
    String handleCreate(Client client) {
        client.setGameFounder(true);
        return "create " + client.getPlayerID();
    }

    /**
     * Handles the input from the player to join an existing game.
     * Validates the game ID entered by the player.
     *
     * @param client  the {@link Client} object representing the player
     * @param scanner a {@link Scanner} for reading player input
     * @return a string command to send to the server to join a game
     */
    String handleJoin(Client client, Scanner scanner) {
        while (true) {
            System.out.print("Enter your game ID: ");
            if (scanner.hasNextInt()) {
                int gameId = scanner.nextInt();
                scanner.nextLine(); // Czyści bufor wejściowy
                return "join " + gameId + " " + client.getPlayerID();
            } else {
                System.out.println("You must enter a valid game ID - number");
                scanner.nextLine(); // Czyści nieprawidłowe wejście
            }
        }
    }
}
