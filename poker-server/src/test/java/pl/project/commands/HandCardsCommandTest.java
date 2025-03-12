package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.Game;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

class HandCardsCommandTest {

    private HandCardsCommand handCardsCommand;
    private ServerData serverData;
    private Game mockGame;
    private SocketChannel mockClientChannel;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        handCardsCommand = spy(new HandCardsCommand());
        serverData = new ServerData(3); // Example max 3 players per game
        mockGame = mock(Game.class);
        mockClientChannel = mock(SocketChannel.class);
        mockPlayer = mock(Player.class);

        // Initialize server data
        serverData.games = new HashMap<>();
        serverData.clients = new HashMap<>();
        serverData.players = new HashMap<>();
    }

    @Test
    void testExecute_RequestToken() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        String[] args = {"handCards", String.valueOf(gameID), String.valueOf(playerID), "request"};
        serverData.games.put(gameID, mockGame);

        when(mockGame.handCards(playerID)).thenReturn(Arrays.asList("Card1", "Card2"));

        // When
        handCardsCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(handCardsCommand, times(1)).handleRequestToken(mockClientChannel, mockGame, playerID);
    }

    @Test
    void testExecute_AcceptedToken_NextPlayer() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        int nextPlayerID = 43;
        String[] args = {"handCards", String.valueOf(gameID), String.valueOf(playerID), "accepted"};
        serverData.games.put(gameID, mockGame);
        serverData.clients.put(nextPlayerID, mockClientChannel);

        when(mockGame.getOrderedPlayersIDs()).thenReturn(Arrays.asList(playerID, nextPlayerID));
        when(mockGame.handCards(nextPlayerID)).thenReturn(Arrays.asList("Card3", "Card4"));

        // When
        handCardsCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(handCardsCommand, times(1)).handleAcceptedToken(mockGame, playerID, serverData);
        verify(handCardsCommand, times(1)).sendToken(mockClientChannel, "cards Card3 Card4 ");
    }

    @Test
    void testExecute_AcceptedToken_GameFounder() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        int gameFounderID = 99;
        String[] args = {"handCards", String.valueOf(gameID), String.valueOf(playerID), "accepted"};
        serverData.games.put(gameID, mockGame);
        serverData.clients.put(gameFounderID, mockClientChannel);

        when(mockGame.getOrderedPlayersIDs()).thenReturn(Arrays.asList(playerID));
        when(mockGame.getQueueOfPlayers()).thenReturn(gameFounderID);
        when(mockGame.getPlayers()).thenReturn(serverData.players);
        when(mockGame.getMinimumBet()).thenReturn(100);
        when(mockGame.getStake()).thenReturn(500);
        when(mockPlayer.getCash()).thenReturn(1000);
        serverData.players.put(gameFounderID, mockPlayer);

        // When
        handCardsCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(handCardsCommand, times(1)).handleAcceptedToken(mockGame, playerID, serverData);
        verify(mockClientChannel, times(1))
                .write(ByteBuffer.wrap("startAuction 1000 100 500".getBytes()));
    }

    @Test
    void testHandleRequestToken() throws IOException {
        // Given
        int playerID = 42;
        when(mockGame.handCards(playerID)).thenReturn(Arrays.asList("Card1", "Card2"));

        // When
        handCardsCommand.handleRequestToken(mockClientChannel, mockGame, playerID);

        // Then
        verify(mockClientChannel, times(1))
                .write(ByteBuffer.wrap("cards Card1 Card2 ".getBytes()));
    }

    @Test
    void testHandleAcceptedToken_NextPlayer() throws IOException {
        // Given
        int playerID = 42;
        int nextPlayerID = 43;
        serverData.clients.put(nextPlayerID, mockClientChannel);
        when(mockGame.getOrderedPlayersIDs()).thenReturn(Arrays.asList(playerID, nextPlayerID));
        when(mockGame.handCards(nextPlayerID)).thenReturn(Arrays.asList("Card3", "Card4"));

        // When
        handCardsCommand.handleAcceptedToken(mockGame, playerID, serverData);

        // Then
        verify(handCardsCommand, times(1)).sendToken(mockClientChannel, "cards Card3 Card4 ");
    }

    @Test
    void testHandleAcceptedToken_GameFounder() throws IOException {
        // Given
        int playerID = 42;
        int gameFounderID = 99;
        serverData.clients.put(gameFounderID, mockClientChannel);
        serverData.players.put(gameFounderID, mockPlayer);

        when(mockGame.getOrderedPlayersIDs()).thenReturn(Arrays.asList(playerID));
        when(mockGame.getQueueOfPlayers()).thenReturn(gameFounderID);
        when(mockGame.getPlayers()).thenReturn(serverData.players);
        when(mockGame.getMinimumBet()).thenReturn(100);
        when(mockGame.getStake()).thenReturn(500);
        when(mockPlayer.getCash()).thenReturn(1000);

        // When
        handCardsCommand.handleAcceptedToken(mockGame, playerID, serverData);

        // Then
        verify(mockClientChannel, times(1))
                .write(ByteBuffer.wrap("startAuction 1000 100 500".getBytes()));
    }
}