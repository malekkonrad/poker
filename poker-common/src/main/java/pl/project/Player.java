package pl.project;

import lombok.Getter;
import lombok.Setter;
import pl.project.cards.Card;

import java.util.ArrayList;
import java.util.List;


/**
 * The {@code Player} class represents a player in the game with attributes such as
 * username, hand of cards, cash, and game status (e.g., fold, all-in, or winner).
 */
@Getter
public class Player {

    final String userName;
    @Setter
    private int gameId;
    final int playerId;
    @Setter
    private List<Card> hand = new ArrayList<>();
    @Setter
    private int exchangeCounter = 0;
    @Setter
    private int cash = 10000;
    @Getter
    @Setter
    private boolean fold = false;
    @Getter
    @Setter
    private boolean allIn = false;
    @Getter
    @Setter
    private boolean winner = false;

    /**
     * Constructs a new player with the specified username and player ID.
     *
     * @param userName the username of the player
     * @param playerId the unique ID of the player
     */
    public Player(String userName, int playerId) {
        this.userName = userName;
        this.playerId = playerId;
    }

    /**
     * Compares this player to another object for equality based on their player IDs.
     *
     * @param obj the object to compare with this player
     * @return {@code true} if the specified object is a {@code Player} with the same player ID,
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = (Player) obj;
            return getPlayerId() == p.getPlayerId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getPlayerId();
    }
}
