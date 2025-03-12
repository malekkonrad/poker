package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.IOException;

/**
 * Represents the main stage of a poker game where players are dealt cards and the game progresses
 * through the initial stages into the auction.
 */
public class PokerGame implements Stage{

    /**
     * Executes the poker game stage for the client.
     * Handles card dealing, player turns, and transitions to the auction stage.
     *
     * @param client the {@link Client} object representing the player
     */
    @Override
    public void execute(Client client) {
        System.out.println("You are in the game. Wait for more instructions.");
        System.out.println("Wait for your turn.");

        try {
            // blok wysyłania requestów
            if (client.isGameFounder()){

                // sending request
                String joinMessage = createHandCardsToken(client);
                SendToken.send(client, joinMessage);

                // response
                String response = getResponse(client);

                String[] parts = response.split(" ");
                saveCards(parts, client);

                // sending info to server that we get cards, and it needs to send it to next player
                String token = createAcceptedHandCardsToken(client);
                SendToken.send(client, token);

                goToAuction(client);
            }
            else{
                // zachowanie pozostałych graczy
                System.out.println("Wait for your turn.");

                String response = getResponse(client);

                handleResponse(client, response);
            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Saves the dealt cards to the client's hand and displays them.
     *
     * @param parts  the server's response containing the dealt cards
     * @param client the {@link Client} object representing the player
     */
    void saveCards(String[] parts, Client client) {
        // saving cards
        for(int i=1;i<parts.length;i++){
            client.getHand().add(parts[i]);
        }

        // printing cards
        for(int i=0; i< client.getHand().size(); i++){
            System.out.println(i + ": " + client.getHand().get(i));
        }
    }

    /**
     * Reads the server's response for the client.
     *
     * @param client the {@link Client} object representing the player
     * @return the server's response as a string
     * @throws IOException if an I/O error occurs while reading the response
     */
    String getResponse(Client client) throws IOException {
        return ReadToken.read(client);
    }

    /**
     * Creates a token to request cards for the client.
     *
     * @param client the {@link Client} object representing the player
     * @return a string token to request cards
     */
    String createHandCardsToken(Client client) {
        return "handCards " + client.getGameID() + " " + client.getPlayerID() + " request";
    }

    /**
     * Creates a token to acknowledge the receipt of cards for the client.
     *
     * @param client the {@link Client} object representing the player
     * @return a string token to acknowledge receipt of cards
     */
    String createAcceptedHandCardsToken(Client client) {
        return "handCards " + client.getGameID()  + " " +client.getPlayerID() + " accepted";
    }

    /**
     * Transitions the client to the auction stage of the game.
     *
     * @param client the {@link Client} object representing the player
     */
    void goToAuction(Client client) {
        // print information because I've fucked up
        System.out.println("\n-----------------");
        System.out.println("AUCTION");
        System.out.println("-----------------");
        client.setStage(new Auction());
    }

    /**
     * Handles the server's response to the client, processing cards or error messages.
     *
     * @param client   the {@link Client} object representing the player
     * @param response the server's response as a string
     * @throws IOException if an I/O error occurs while sending or processing the response
     */
    void handleResponse(Client client, String response) throws IOException {
        String[] parts = response.split(" ");
        if (parts[0].equals("exit")){
            System.out.println("something went wrong");
        } else if (parts[0].equals("cards")) {

            System.out.println("\n" + "You've been dealt the cards");

            saveCards(parts, client);

            String token = createAcceptedHandCardsToken(client);

            SendToken.send(client, token);

            goToAuction(client);
        } else{
            System.out.println("Unrecognized message from server");
        }
    }


}



