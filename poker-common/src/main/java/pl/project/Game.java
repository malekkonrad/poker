package pl.project;

import lombok.Getter;
import lombok.Setter;
import pl.project.cards.Card;
import pl.project.cards.Deck;

import java.util.*;

/**
 * The {@code Game} class represents a game involving multiple players, a deck of cards,
 * and game state management, including player actions such as folding and card exchanges.
 */
public class Game {

    @Getter
    private int gameID;
    @Getter
    private final Map<Integer, Player> players = new HashMap<>();
    @Getter
    @Setter
    private Map<Integer, Player> activePlayers = new HashMap<>();
    @Getter
    private Player gameFounder;
    private final int maxNumberOfPlayers;
    @Getter
    private List<Integer> orderedPlayersIDs = new ArrayList<>();
    @Getter
    private List<Integer> orderedActivePlayersIDs = new ArrayList<>();
    @Getter
    private Deck deck = new Deck();
    @Getter
    @Setter
    private int numberOfAuction = 0;
    @Setter
    @Getter
    private int stake =0;
    @Getter
    @Setter
    private int minimumBet = 100;
    @Getter
    @Setter
    private List<Integer> currentAuctionQueue = new ArrayList<>();
    @Setter
    @Getter
    private int numActivePlayers = 0;




    /**
     * fucking magic!
     * @return
     */
    public int getQueueOfPlayers(){

        for (Integer playerID : orderedPlayersIDs) {
            Player player = players.get(playerID);
            if (!player.isFold() && !player.isAllIn()) {
                currentAuctionQueue.add(playerID);
            }
        }

        if (!currentAuctionQueue.isEmpty()){
            return currentAuctionQueue.remove(0);
        }
        return -1;
    }

    public int nextPlayerIDFromQueue(){
        if (!currentAuctionQueue.isEmpty()){
            return currentAuctionQueue.remove(0);
        }
        return -1;
    }


    // do zostanowienai
    public void addPlayerToQueue(Integer playerID){
        currentAuctionQueue.add(playerID);
    }


    public List<Integer> getPlayersIDsBeforeNotFold(Integer playerID){
        int currentPosition = orderedPlayersIDs.indexOf(playerID);
        List<Integer> playersIDs = orderedPlayersIDs.subList(0, currentPosition);
        List<Integer> notFoldPlayersIDs = new ArrayList<>();
        for (Integer playerID2 : playersIDs) {
            if (!players.get(playerID2).isFold() && !players.get(playerID2).isAllIn()) {
                notFoldPlayersIDs.add(playerID2);
            }
        }
        return notFoldPlayersIDs;
    }


    /**
     * Main constructor
     * @param gameID
     * @param gameFounder
     * @param maxNumberOfPlayers
     */
    public Game(int gameID, Player gameFounder, int maxNumberOfPlayers) {
        this.gameID = gameID;
        this.gameFounder = gameFounder;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.numActivePlayers = maxNumberOfPlayers;
        players.put(gameFounder.getPlayerId(), gameFounder);
        activePlayers.put(gameFounder.getPlayerId(), gameFounder);
        orderedPlayersIDs.add(gameFounder.getPlayerId());
        activePlayers.put(gameFounder.getPlayerId(), gameFounder);
    }




    public Set<Integer> getPlayerIDs() {return new HashSet<>(players.keySet());}


    /**
     * Adds a player to the game if there is space available.
     *
     * @param player the player to add.
     * @return 1 if the game is full after adding, 0 if successfully added, -1 if the game is already full.
     */
    public int addPlayer(Player player) {
        if (players.size() +1 == maxNumberOfPlayers){
            players.put(player.getPlayerId(), player);
            activePlayers.put(player.getPlayerId(), player);
            orderedPlayersIDs.add(player.getPlayerId());
            orderedActivePlayersIDs.add(player.getPlayerId());
            return 1;
        } else if (players.size() < maxNumberOfPlayers) {
            players.put(player.getPlayerId(), player);
            activePlayers.put(player.getPlayerId(), player);
            orderedPlayersIDs.add(player.getPlayerId());
            activePlayers.put(player.getPlayerId(), player);
            return 0;
        }else{
            return -1;
        }
    }

    /**
     * Deals a hand of 5 cards to the player with the specified ID.
     *
     * @param playerID the ID of the player.
     * @return a list of card descriptions.
     */
    public List<String> handCards(Integer playerID){
        deck.shuffle();
        List<Card> hand = deck.dealHand(5);
        players.get(playerID).setHand(hand);

        List<String> handCardsStrings = new ArrayList<>();
        for(Card card : hand){
            handCardsStrings.add(card.toString());
        }
        return handCardsStrings;
    }

    /**
     * Changes a card in the player's hand at the specified position.
     *
     * @param playerID the player's ID.
     * @param cardID   the index of the card to exchange.
     * @return the new card description.
     */
    public String changeCard(Integer playerID, Integer cardID){
        List<Card> hand = players.get(playerID).getHand();

        Card cardToExchange = hand.get(cardID);
        Card newCard = deck.getCardFromDeck();

        // nie usuwamy tylko podmieniamy
        hand.set(cardID, cardToExchange);

        // dodaje z powrotem do eck
        deck.addCardToDeck(newCard);

        return newCard.toString();
    }

    /**
     * Marks a player as folded. If only one player remains active, returns their ID.
     *
     * @param playerID the ID of the player folding.
     * @return the ID of the remaining player, or -1 if more players remain.
     */
    public int playerFold(Integer playerID){
        // ustawienie na true fold
        players.get(playerID).setFold(true);

        numActivePlayers--;

        if (numActivePlayers == 1){

            for(Player player : players.values()){
                if (!player.isFold()){
                    return player.getPlayerId();
                }
            }

        }
        return -1;

    }

    /**
     *
     * @return
     */
    public int foldedWinner(){
        if (numActivePlayers == 1){
            for(Player player : players.values()){
                if (!player.isFold()){
                    return player.getPlayerId();
                }
            }
        }
        return -1;
    }







}
