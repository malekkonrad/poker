package pl.project.stages;

import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.IOException;
import java.util.Scanner;


/**
 * The {@code Auction} class represents the auction stage in a game where players can bet, fold, or take other betting actions.
 * <p>
 * This class handles the client's interaction during the auction stage, including receiving server responses, processing player actions,
 * and sending appropriate requests to the server.
 */
public class Auction implements Stage{

    /**
     * Executes the auction stage for the given client.
     * <p>
     * This method continuously processes server responses and handles client actions based on the current state of the auction.
     *
     * @param client the {@link Client} object representing the player in the auction stage
     */
    @Override
    public void execute(Client client) {

        try {
            // wczytujemy odpowiedz od serwera
            String response = getResponse(client);

            handleResponse(client, response);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    /**
     * Handles the server's response during the auction stage.
     *
     * @param client   the {@link Client} object representing the player
     * @param response the server's response as a string
     * @throws IOException if an I/O error occurs
     */
     void handleResponse(Client client, String response) throws IOException {
        String[] parts = response.split(" ");
        switch (parts[0]) {
            case "startAuction":
                handleStartAuction(client, parts);
                break;
            case "acceptedBet":
                handleAcceptedBetToken();
                break;
            case "playerBet":
                handlePlayerBetToken(parts);
                break;
            case "nextStage":
                handleNextStageToken(client);
                break;
            case "lastStage":
                handleLastStageToken(client);
                break;
            case "winner":
                handleWinnerToken(client);
                break;
            default:
                System.out.println("Unrecognized message: " + response);
        }
    }


    /**
     * Handles the start of the auction by prompting the player for an action.
     *
     * @param client the {@link Client} object representing the player
     * @param parts  the response tokens containing auction details (e.g., cash, stake, minimum bet)
     * @throws IOException if an I/O error occurs
     */
     void handleStartAuction(Client client, String[] parts) throws IOException {
        int playerCash = Integer.parseInt(parts[1]);
        int minimumBet = Integer.parseInt(parts[2]);
        int stake = Integer.parseInt(parts[3]);

        printCards(client);
        printCashState(playerCash, stake, minimumBet);

        Scanner scanner = new Scanner(System.in);
        boolean actionTaken = false;
        while (!actionTaken) {
            actionTaken = getAction(client, scanner, playerCash, minimumBet, stake);
        }
    }


    /**
     * Reads a response from the server.
     *
     * @param client the {@link Client} object representing the player
     * @return the server's response as a string
     * @throws IOException if an I/O error occurs
     */
    String getResponse(Client client) throws IOException {
        return ReadToken.read(client);
    }

    /**
     * Prints the player's current cards.
     *
     * @param client the {@link Client} object representing the player
     */
    void printCards(Client client) {
        System.out.println("\n---------------");
        System.out.println("Your cards:");
        System.out.println("---------------");
        for(int i=0; i< client.getHand().size(); i++){
            System.out.println(i + ": " + client.getHand().get(i));
        }
    }

    /**
     * Handles the "nextStage" token by transitioning the player to the card-changing stage.
     *
     * @param client the {@link Client} object representing the player
     */
    void handleNextStageToken(Client client) {
        System.out.println("Every player bet, we are going to next stage - changing cards");
        System.out.println("\n-----------------");
        System.out.println("Changing card stage");
        System.out.println("-----------------");
        client.setStage(new ChangingCards());
    }

    /**
     * Handles the "lastStage" token by transitioning the player to the summary stage.
     *
     * @param client the {@link Client} object representing the player
     */
    void handleLastStageToken(Client client) {
        System.out.println("Every player bet, we are going to the last stage - Summary");
        System.out.println("\n-----------------");
        System.out.println("SUMMARY");
        System.out.println("-----------------");
        client.setStage(new Summary());
    }

    /**
     * Handles the "winner" token by transitioning the player to the summary stage.
     *
     * @param client the {@link Client} object representing the player
     */
    void handleWinnerToken(Client client) {
        System.out.println("\n-----------------");
        System.out.println("SUMMARY");
        System.out.println("-----------------");
        client.setStage(new Summary());
    }

    /**
     * Handles the "playerBet" token to display information about another player's bet.
     *
     * @param parts the response tokens containing player bet details
     */
    void handlePlayerBetToken(String[] parts) {
        if (parts[2].equals("fold")){
            System.out.println("player: [" + parts[1] + "]  " + parts[2]);
        }else{
            System.out.println("player: [" + parts[1] + "]  " + parts[2] + ": " + parts[3]);
        }
    }

    /**
     * Handles the "acceptedBet" token to inform the player that their bet was accepted.
     */
    void handleAcceptedBetToken() {
        System.out.println("Your bet was submitted, wait for others players to bet");
    }

    /**
     * Prints the player's cash state, including the current balance, stake, and minimum bet.
     *
     * @param playerCash  the player's current cash balance
     * @param stake       the current stake in the game
     * @param minimumBet  the minimum bet amount
     */
    void printCashState(int playerCash, int stake, int minimumBet) {
        System.out.println("Your current balance: " + playerCash);
        System.out.println("Current stake: " + stake);
        System.out.println("Minimum bet: " + minimumBet);
    }


    /**
     * Handles the player's action during their turn in the auction.
     *
     * @param client      the {@link Client} object representing the player
     * @param scanner     the {@link Scanner} for user input
     * @param playerCash  the player's current cash balance
     * @param minimumBet  the minimum bet amount
     * @param stake       the current stake in the game
     * @return {@code true} if the action was successfully performed; otherwise, {@code false}
     * @throws IOException if an I/O error occurs
     */
    boolean getAction(Client client, Scanner scanner, int playerCash, int minimumBet, int stake) throws IOException {

        if (playerCash <= minimumBet){
            System.out.println("You can only 'fold' or play all in stay at the game (type 'allIn') ");

            String command = scanner.nextLine();

            if (command.equals("fold")){
                handleFold(client);
                return true;

            }else if(command.equals("allIn")){
                return handleAllIn(client, 0, (stake + playerCash), playerCash );

            }else{
                System.out.println("Invalid command");
                return false;
            }

        }else{
            System.out.println("It's your turn, you can 'fold', 'call', 'raise' or 'allIn', type one of this options: ");

            String command = scanner.nextLine();

            if (command.equals("fold")){
                handleFold(client);
                return true;
            }else if (command.equals("call")){
                handleCall(client, playerCash, stake, minimumBet);
                return true;
            }else if (command.equals("raise")){
                return handleRaise(client, scanner, playerCash,  stake, minimumBet);
            }else if(command.equals("allIn")){
                return handleAllIn(client, 0, (stake + playerCash), playerCash );
            } else{
                System.out.println("Invalid command");
                return false;
            }
        }

    }

    /**
     * Sends a request to the server with the given token.
     *
     * @param client the {@link Client} object representing the player
     * @param token  the request token to be sent
     * @return {@code true} if the request was successfully sent
     * @throws IOException if an I/O error occurs
     */
    boolean sendRequest(Client client, String token) throws IOException {
        SendToken.send(client, token);
        return true;
    }

    /**
     * Creates a token for an "all in" bet.
     *
     * @param client     the {@link Client} object representing the player
     * @param playerCash the player's current cash balance
     * @return the "all in" token string
     */
    String createBetAllInToken(Client client, int playerCash) {
        return "bet "+ client.getGameID() + " " + client.getPlayerID() + " allIn " + playerCash;
    }

    /**
     * Creates a token for a "raise" action.
     *
     * @param client     the {@link Client} object representing the player
     * @param minimumBet the current minimum bet
     * @param sum        the amount by which the player is raising
     * @return the "raise" token string
     */
    String createRaiseToken(Client client, int minimumBet, int sum) {
        return "bet "+ client.getGameID() + " " + client.getPlayerID() + " raise " + (minimumBet + sum);
    }

    /**
     * Creates a token for a "call" action.
     *
     * @param client     the {@link Client} object representing the player
     * @param minimumBet the current minimum bet
     * @return the "call" token string
     */
    String createCallToken(Client client, int minimumBet) {
        return "bet "+ client.getGameID() + " " + client.getPlayerID() + " call " + minimumBet;
    }

    /**
     * Creates a token for a "fold" action.
     *
     * @param client the {@link Client} object representing the player
     * @return the "fold" token string
     */
    String createFoldToken(Client client) {
        return "bet "+ client.getGameID() + " " + client.getPlayerID() + " fold " + 0;
    }

    /**
     * Prepares a token for an "all in" action and displays the updated cash state.
     *
     * @param client     the {@link Client} object representing the player
     * @param playerCash the player's current cash balance
     * @param stake      the current stake in the game
     * @param minimumBet the current minimum bet
     * @return the "all in" token string
     */
    String prepareAllIn(Client client, int playerCash, int stake, int minimumBet) {
        printCashState(playerCash, playerCash + stake, minimumBet);
        return  createBetAllInToken(client, minimumBet);
    }

    /**
     * Handles an "all in" action by preparing the token and sending it to the server.
     *
     * @param client     the {@link Client} object representing the player
     * @param playerCash the player's current cash balance
     * @param stake      the current stake in the game
     * @param minimumBet the current minimum bet
     * @return {@code true} if the action was successfully performed
     * @throws IOException if an I/O error occurs
     */
    boolean handleAllIn(Client client, int playerCash, int stake, int minimumBet ) throws IOException {
        String token = prepareAllIn(client, playerCash,stake,  minimumBet);
        return sendRequest(client,token);
    }

    /**
     * Handles a "fold" action by sending the appropriate token to the server.
     *
     * @param client the {@link Client} object representing the player
     * @throws IOException if an I/O error occurs
     */
    void handleFold(Client client) throws IOException {
        System.out.println("You have folded.");
        String token = createFoldToken(client);
        SendToken.send(client, token);

    }

    /**
     * Handles a "call" action by updating the player's cash state and sending the appropriate token to the server.
     *
     * @param client     the {@link Client} object representing the player
     * @param playerCash the player's current cash balance
     * @param stake      the current stake in the game
     * @param minimumBet the current minimum bet
     * @throws IOException if an I/O error occurs
     */
    void handleCall(Client client, int playerCash,  int stake, int minimumBet) throws IOException {
        System.out.println("You have called.");
        printCashState(playerCash - minimumBet, stake + minimumBet, minimumBet);
        String token = createCallToken(client, minimumBet);
        SendToken.send(client, token);
    }

    /**
     * Handles a "raise" action by prompting the player for the raise amount and sending the appropriate token to the server.
     *
     * @param client     the {@link Client} object representing the player
     * @param scanner    the {@link Scanner} for user input
     * @param playerCash the player's current cash balance
     * @param stake      the current stake in the game
     * @param minimumBet the current minimum bet
     * @return {@code true} if the raise action was successfully performed; otherwise, {@code false}
     * @throws IOException if an I/O error occurs
     */
    boolean handleRaise(Client client, Scanner scanner, int playerCash, int stake, int minimumBet) throws IOException {
        System.out.println("How much do you want to raise?");

        if (scanner.hasNextInt()) {
            int sum = scanner.nextInt();
            if (sum+minimumBet>playerCash){
                System.out.println("You don't have enough money to raise that much.");
                return false;
            }
            printCashState(( playerCash-(minimumBet+sum) ), (stake + minimumBet + sum), (minimumBet + sum));

            String token = createRaiseToken(client, minimumBet, sum);
            SendToken.send(client, token);
            return true;
        }else{
            System.out.println("It's not a number!");
            return false;
        }
    }


}
