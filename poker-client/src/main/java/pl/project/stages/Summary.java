package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.IOException;
import java.util.Scanner;


/**
 * Represents the summary stage of the poker game, where the game results are processed,
 * displayed, and the client transitions back to the lobby.
 */
public class Summary implements Stage{

    /**
     * Executes the summary stage for the client.
     * Handles the game results based on whether the client is the game founder or a regular player.
     *
     * @param client the {@link Client} object representing the player
     */
    @Override
    public void execute(Client client) {

        try {

            if (client.isGameFounder()){
                handleSummaryRequest(client);

            }else{
                String response = getResponse(client);
                String[] parts = response.split(" ");

                if (parts[0].equals("score")){

                    handleScoreToken(client, parts);

                } else if(parts[0].equals("foldWinner")){

                    handleFoldWinnerToken(client, parts);
                }
                else{
                    System.out.println("Summary: Error message!");
                }
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Sends a summary request to the server if the client is the game founder.
     *
     * @param client the {@link Client} object representing the player
     * @throws IOException if an I/O error occurs during communication
     */
    void handleSummaryRequest(Client client) throws IOException {
        String token = "summary " + client.getGameID() + " " + client.getPlayerID() + " request";
        SendToken.send(client, token);
        client.setGameFounder(false);
    }

    /**
     * Processes the "score" token from the server, displaying the game's winner and results.
     *
     * @param client the {@link Client} object representing the player
     * @param parts  the server response split into parts
     */
    void handleScoreToken(Client client, String[] parts){
        String clientLayout = parts[1];
        Integer winnerID = Integer.parseInt(parts[2]);
        String playerName = parts[3];
        String winnerLayout = parts[4];
        String stake = parts[5];
        String cash = parts[6];


        if (winnerID.equals(client.getPlayerID())){
            handleWin(stake, cash);
            System.out.println("Your layout was: " + clientLayout);

        } else{
            handleLoss(stake, cash, playerName);
            System.out.println("Your layout was: " + clientLayout);
            System.out.println("Winner layout was: " + winnerLayout);
        }

        handleLeft(client);

    }

    /**
     * Handles the winning scenario for the client, displaying their updated balance and winnings.
     *
     * @param stake the amount won in the game
     * @param cash  the client's updated cash balance
     */
    void handleWin( String stake, String cash){
        System.out.println("You won the game");
        System.out.println("You have won: " + stake);
        System.out.println("You current balance: " + cash);
    }


    /**
     * Handles the losing scenario for the client, displaying the winner and their updated balance.
     *
     * @param stake      the amount won by the winner
     * @param cash       the client's updated cash balance
     * @param playerName the name of the winning player
     */
    void handleLoss(String stake, String cash, String playerName){
        System.out.println("You lost the game");
        System.out.println("Player [" + playerName + "] won the game");
        System.out.println("The prize was: " + stake);
        System.out.println("You current balance: " + cash);
    }

    /**
     * Handles the "foldWinner" token, which indicates that the game ended due to all but one player folding.
     *
     * @param client the {@link Client} object representing the player
     * @param parts  the server response split into parts
     */
    void handleFoldWinnerToken(Client client, String[] parts){
        String winnerUsername = parts[1];
        System.out.println("Game ended, because everyone except one player fold");

        Integer winnerID = Integer.parseInt(parts[2]);
        String stake = parts[3];
        String cash = parts[4];


        if (winnerID.equals(client.getPlayerID())){
            handleWin(stake, cash);

        } else{
            handleLoss(stake, cash, winnerUsername);

        }

        handleLeft(client);
    }

    /**
     * Reads the server response for the client.
     *
     * @param client the {@link Client} object representing the player
     * @return the server's response as a string
     * @throws IOException if an I/O error occurs during communication
     */
    String getResponse(Client client) throws IOException {
        return ReadToken.read(client);
    }


    /**
     * Handles the client's transition back to the lobby after the summary stage.
     * Clears the client's game data and sets the next stage to {@link PreGame}.
     *
     * @param client the {@link Client} object representing the player
     */
    void handleLeft(Client client){
        // musi nastapić wyczyszczenie danych klienta
        client.getHand().clear();
        client.setGameID(-1);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter to left the game and join the lobby");
        scanner.nextLine();
        client.setStage(new PreGame());
    }

}
