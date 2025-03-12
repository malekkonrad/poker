package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.project.Game;
import pl.project.Player;
import pl.project.cards.Card;
import pl.project.check.CheckEngine;
import pl.project.check.evaluators.HandEvaluator;
import pl.project.check.hand.EvaluatedHand;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class SummaryCommandTest {

    private SummaryCommand summaryCommand;
    private ServerData serverData;
    private Game mockGame;
    private SocketChannel mockClientChannel;

    @BeforeEach
    void setUp() {
        summaryCommand = spy(new SummaryCommand());
        serverData = new ServerData(500); // Initial stake value

        mockGame = mock(Game.class);
        mockClientChannel = mock(SocketChannel.class);

        serverData.games = new HashMap<>();
        serverData.players = new HashMap<>();
        serverData.clients = new HashMap<>();
    }

    @Test
    void testExecute_FoldWin() throws IOException {
        // Given
        int gameID = 1;
        int winnerID = 42;
        String winnerUsername = "Winner";

        Player winner = mock(Player.class);
        when(winner.getUserName()).thenReturn(winnerUsername);
        when(winner.getCash()).thenReturn(500);

        when(mockGame.getNumActivePlayers()).thenReturn(1);
        when(mockGame.foldedWinner()).thenReturn(winnerID);
        when(mockGame.getStake()).thenReturn(1000);
        when(mockGame.getPlayers()).thenReturn(Map.of(winnerID, winner));
        when(mockGame.getPlayerIDs()).thenReturn(Set.of(winnerID));

        serverData.games.put(gameID, mockGame);
        serverData.players.put(winnerID, winner);
        serverData.clients.put(winnerID, mockClientChannel);

        String[] args = {"summary", String.valueOf(gameID)};

        // When
        summaryCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(summaryCommand).handleFoldWin(mockGame, serverData); // Ensure handleFoldWin is invoked
        verify(summaryCommand).handleClearAfterGame(mockGame, serverData); // Ensure cleanup happens
        verify(summaryCommand).sendToken(
                mockClientChannel,
                "foldWinner " + winnerUsername + " " + winnerID + " 1000 500"
        ); // Check the correct token is sent
    }



    @Test
    void testHandleClearAfterGame() {
        // Given
        int gameID = 1;
        int playerID = 42;
        Player player = new Player("TestPlayer", playerID);

        player.setCash(0);
        player.setWinner(false);
        player.setHand(new ArrayList<>(List.of(mock(Card.class), mock(Card.class))));
        player.setExchangeCounter(3);
        player.setGameId(gameID);
        player.setFold(true);

        when(mockGame.getGameID()).thenReturn(gameID);
        when(mockGame.getPlayers()).thenReturn(Map.of(playerID, player));
        when(mockGame.getPlayerIDs()).thenReturn(Set.of(playerID));

        serverData.games.put(gameID, mockGame);

        // When
        summaryCommand.handleClearAfterGame(mockGame, serverData);

        // Then
        assertEquals(500, player.getCash());
        assertEquals(0, player.getExchangeCounter());
        assertEquals(-1, player.getGameId());
        assertEquals(0, player.getHand().size());
        assertEquals(false, player.isFold());
        assertEquals(false, player.isWinner());
        assertEquals(false, player.isAllIn());
        verify(mockGame, times(1)).getGameID();
        assertEquals(false, serverData.games.containsKey(gameID));
    }

    @Test
    void testFindWinningHand() {
        // Given
        int player1ID = 42;
        int player2ID = 43;

        // Create mutable lists of cards
        List<Card> player1Cards = new ArrayList<>(List.of(mock(Card.class), mock(Card.class)));
        List<Card> player2Cards = new ArrayList<>(List.of(mock(Card.class), mock(Card.class)));

        // Mocking card comparisons for sorting
        Card player1MaxCard = mock(Card.class);
        Card player2MaxCard = mock(Card.class);
        when(player1MaxCard.compareTo(any())).thenReturn(-1); // Mock comparison for sorting
        when(player2MaxCard.compareTo(any())).thenReturn(1);

        // Prepare EvaluatedHand instances
        EvaluatedHand hand1 = new EvaluatedHand(player1Cards, player1MaxCard, HandEvaluator.Layouts.HIGH_CARD, player1ID, player1Cards);
        EvaluatedHand hand2 = new EvaluatedHand(player2Cards, player2MaxCard, HandEvaluator.Layouts.PAIR, player2ID, player2Cards);

        List<EvaluatedHand> evaluatedHands = List.of(hand1, hand2);

        // Mock game with players
        when(mockGame.getPlayers()).thenReturn(Map.of(
                player1ID, mock(Player.class),
                player2ID, mock(Player.class)
        ));
        when(mockGame.getPlayers().get(player1ID).isFold()).thenReturn(false);
        when(mockGame.getPlayers().get(player2ID).isFold()).thenReturn(false);

        // When
        EvaluatedHand winningHand = summaryCommand.findWinningHand(mockGame, evaluatedHands);

        // Then
        assertEquals(hand2, winningHand); // Verify the hand with the better layout is returned
    }

}