package pl.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {


    private Game game;
    private Player founder;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        founder = new Player("Founder", 1);
        player1 = new Player("Player1", 2);
        player2 = new Player("Player2", 3);
        game = new Game(100, founder, 3);
    }

    @Test
    void testAddPlayer() {
        assertEquals(0, game.addPlayer(player1), "Player1 should be added successfully.");
        assertEquals(1, game.addPlayer(player2), "Player2 fills the game.");
        assertEquals(-1, game.addPlayer(new Player("ExtraPlayer", 4)), "No more players should be added.");
    }

    @Test
    void testHandCards() {
        List<String> hand = game.handCards(founder.getPlayerId());
        assertEquals(5, hand.size(), "Founder should receive 5 cards.");
    }

    @Test
    void testChangeCard() {
        game.handCards(founder.getPlayerId());
        String oldCard = game.changeCard(founder.getPlayerId(), 0);
        assertNotNull(oldCard, "Old card description should not be null.");
    }

    @Test
    void testPlayerFold() {
        int result = game.playerFold(founder.getPlayerId());
        assertEquals(-1, result, "Folding one player should not declare a winner yet.");

        game.addPlayer(player1);
        int winnerID = game.playerFold(player1.getPlayerId());
        assertEquals(-1, winnerID, "Founder should be declared winner after the second player folds.");
    }

    @Test
    void testNextPlayerIDFromQueue() {
        game.addPlayerToQueue(player1.getPlayerId());
        game.addPlayerToQueue(player2.getPlayerId());

        assertEquals(player1.getPlayerId(), game.nextPlayerIDFromQueue(), "First player in queue should be returned.");
        assertEquals(player2.getPlayerId(), game.nextPlayerIDFromQueue(), "Second player in queue should be returned.");
        assertEquals(-1, game.nextPlayerIDFromQueue(), "Queue should return -1 when empty.");
    }

}