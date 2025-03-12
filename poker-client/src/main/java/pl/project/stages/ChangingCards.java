package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.IOException;
import java.util.Scanner;

/**
 * Represents the stage of the game where players can change their cards.
 * Handles player actions, server communication, and transitions to the next game stage.
 */
public class ChangingCards implements Stage {


    private final Scanner scanner = new Scanner(System.in);

    /**
     * Executes the "changing cards" stage for the client.
     * Handles server responses and player actions.
     *
     * @param client the {@link Client} object representing the player
     */
    @Override
    public void execute(Client client) {
        try {
            String response = getResponse(client);

            handleRequest(client, response);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    /**
     * Reads the response from the server.
     *
     * @param client the {@link Client} object representing the player
     * @return the server's response as a string
     * @throws IOException if an I/O error occurs
     */
    String getResponse(Client client) throws IOException {
        return ReadToken.read(client);
    }

    /**
     * Handles the initial process of changing cards for the player.
     * Prompts the player to change their cards and sends the corresponding request to the server.
     *
     * @param client the {@link Client} object representing the player
     * @throws IOException if an I/O error occurs
     */
    void handleChangeCards(Client client) throws IOException {
        // pierwsze użycie
        System.out.println("It's your turn, do you want to change cards");
        System.out.println("Your current hand: ");
        displayCards(client);

        changeCards(client);
    }

    /**
     * Handles the transition to the next stage after all players have changed their cards.
     *
     * @param client the {@link Client} object representing the player
     * @throws IOException if an I/O error occurs
     */
    void handleNextStage(Client client) throws IOException {
        System.out.println("Every player changed theirs cards, we are going to next stage - second auction cards");
        System.out.println("\n-----------------");
        System.out.println("SECOND AUCTION");
        System.out.println("-----------------");
        client.setStage(new Auction());
    }

    /**
     * Handles the server response indicating that a card was successfully changed.
     * Updates the player's hand and allows further card changes.
     *
     * @param client the {@link Client} object representing the player
     * @param parts  the server response split into parts
     * @throws IOException if an I/O error occurs
     */
    void handleAcceptedChange(Client client, String[] parts) throws IOException {
        System.out.println("You correctly changed your card.");
        int position = Integer.parseInt(parts[1]);
        String card = parts[2];
        client.getHand().set(position, card);
        displayCards(client);

        changeCards(client);
    }

    /**
     * Processes the server's response and determines the appropriate action to take.
     *
     * @param client   the {@link Client} object representing the player
     * @param response the server's response as a string
     * @throws IOException if an I/O error occurs
     */
    void handleRequest(Client client, String response) throws IOException {
        String[] parts = response.split(" ");


        if (parts[0].equals("changeCards")) {
            handleChangeCards(client);

        } else if (parts[0].equals("acceptedChange")) {

            handleAcceptedChange(client, parts);

        } else if (parts[0].equals("acceptedEndChanging")) {

            System.out.println("Wait for other players to exchange cards");

        } else if (parts[0].equals("nextStage")) {

            handleNextStage(client);

        } else if(parts[0].equals("deniedChange")) {

            System.out.println("You changed maximum of cards (only 4 changes are allowed).");
        }

        else {
            System.out.println("Unrecognised message");
        }
    }

    /**
     * Allows the player to change cards based on their input and sends the corresponding request to the server.
     *
     * @param client the {@link Client} object representing the player
     * @throws IOException if an I/O error occurs
     */
    void changeCards(Client client) throws IOException {

        String response = getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");

        if (response.equals("yes")) {
            boolean flag = false;

            while (!flag) {
                String cardInput = getUserInput("Please enter your card number (on the left) you want to exchange: ");
                try {
                    int cardNumber = Integer.parseInt(cardInput);

                    if (cardNumber >= 0 && cardNumber <= 4) {
                        SendToken.send(client, "exchange " + client.getGameID() + " " + client.getPlayerID() + " yes " + cardNumber);
                        flag = true;
                    } else {
                        System.out.println("Please enter an number between 0 and 4");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number, from 0 to 4");
                }
            }
        } else {
            SendToken.send(client, "exchange " + client.getGameID() + " " + client.getPlayerID() + " no ");
        }
    }

    /**
     * Prompts the user for input and returns the entered value.
     *
     * @param prompt the message to display to the user
     * @return the user's input as a string
     */
    String getUserInput(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    /**
     * Displays the player's current hand of cards.
     *
     * @param client the {@link Client} object representing the player
     */
    void displayCards(Client client) {
        System.out.println("---------------");
        System.out.println("Your cards:");
        System.out.println("---------------");

        // printing cards
        for(int i=0; i< client.getHand().size(); i++){
            System.out.println(i + ": " + client.getHand().get(i));
        }
    }


}