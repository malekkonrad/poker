package pl.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.cards.Card;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("JohnDoe", 1);
    }

    @Test
    void testPlayerInitialization() {
        assertEquals("JohnDoe", player.getUserName(), "Username should match the initialized value.");
        assertEquals(1, player.getPlayerId(), "Player ID should match the initialized value.");
        assertEquals(10000, player.getCash(), "Default cash should be 10000.");
        assertNotNull(player.getHand(), "Player's hand should be initialized as an empty list.");
    }

    @Test
    void testSetGameId() {
        player.setGameId(42);
        assertEquals(42, player.getGameId(), "Game ID should be updated correctly.");
    }

    @Test
    void testSetAndGetHand() {
        List<Card> newHand = List.of(
                new Card(Card.Rank.ACE, Card.Suit.SPADES),
                new Card(Card.Rank.KING, Card.Suit.HEARTS)
        );
        player.setHand(newHand);

        assertEquals(2, player.getHand().size(), "Hand should contain 2 cards.");
        assertEquals(newHand, player.getHand(), "Hand should match the newly set cards.");
    }

    @Test
    void testSetAndGetExchangeCounter() {
        player.setExchangeCounter(3);
        assertEquals(3, player.getExchangeCounter(), "Exchange counter should be updated to 3.");
    }

    @Test
    void testSetAndGetCash() {
        player.setCash(5000);
        assertEquals(5000, player.getCash(), "Cash should be updated to 5000.");
    }

    @Test
    void testFoldStatus() {
        assertFalse(player.isFold(), "Player should not be folded by default.");
        player.setFold(true);
        assertTrue(player.isFold(), "Player's fold status should be updated to true.");
    }

    @Test
    void testAllInStatus() {
        assertFalse(player.isAllIn(), "Player should not be all-in by default.");
        player.setAllIn(true);
        assertTrue(player.isAllIn(), "Player's all-in status should be updated to true.");
    }

    @Test
    void testWinnerStatus() {
        assertFalse(player.isWinner(), "Player should not be a winner by default.");
        player.setWinner(true);
        assertTrue(player.isWinner(), "Player's winner status should be updated to true.");
    }

    @Test
    void testEqualsSamePlayerId() {
        Player anotherPlayer = new Player("JaneDoe", 1); // Same ID but different name
        assertEquals(player, anotherPlayer, "Players with the same ID should be equal.");
    }

    @Test
    void testEqualsDifferentPlayerId() {
        Player anotherPlayer = new Player("JaneDoe", 2);
        assertNotEquals(player, anotherPlayer, "Players with different IDs should not be equal.");
    }

    @Test
    void testHashCode_EqualsPlayerId() {
        // Given
        int player1Id = 42;
        Player player1 = new Player("Konrad", player1Id);

        // When
        int hashCode = player1.hashCode();

        // Then
        assertEquals(player1Id, hashCode, "hashCode powinno być równe playerId");
    }
    @Test
    void testHashCode_EqualObjects() {
        // Given
        Player player1 = new Player("Konrad",42);
        Player player2 = new Player("Konrad", 42);

        // When
        int hashCode1 = player1.hashCode();
        int hashCode2 = player2.hashCode();

        // Then
        assertEquals(hashCode1, hashCode2, "hashCode dwóch równych obiektów powinno być takie samo");
    }
    @Test
    void testHashCode_NotEqualObjects() {
        // Given
        Player player1 = new Player("Konrad",42);
        Player player2 = new Player("Jan",24);

        // When
        int hashCode1 = player1.hashCode();
        int hashCode2 = player2.hashCode();

        // Then
        assertTrue(hashCode1 != hashCode2, "hashCode dwóch różnych obiektów powinno być różne");
    }
}