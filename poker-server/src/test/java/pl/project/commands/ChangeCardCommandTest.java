package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.Game;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import static org.mockito.Mockito.*;

class ChangeCardCommandTest {


    private ChangeCardCommand changeCardCommand;
    private ServerData serverData;
    private Game mockGame;
    private Player mockPlayer;
    private SocketChannel mockClientChannel;

    @BeforeEach
    void setUp() {
        changeCardCommand = spy(new ChangeCardCommand());
        serverData = new ServerData(3); // Example max players
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
        mockClientChannel = mock(SocketChannel.class);

        // Initialize server data
        serverData.games = new HashMap<>();
        serverData.clients = new HashMap<>();
        serverData.players = new HashMap<>();
    }

    @Test
    void testExecute_ChangeCard() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        int cardID = 3;
        String[] args = {"changeCard", String.valueOf(gameID), String.valueOf(playerID), "yes", String.valueOf(cardID)};
        serverData.games.put(gameID, mockGame);
        serverData.players.put(playerID, mockPlayer);

        when(mockPlayer.getExchangeCounter()).thenReturn(2);
        when(mockGame.changeCard(playerID, cardID)).thenReturn("NewCard");

        // When
        changeCardCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(changeCardCommand, times(1))
                .handleChangeCardToken(mockClientChannel, mockGame, playerID, serverData, cardID);
        verify(mockClientChannel, times(1))
                .write(ByteBuffer.wrap("acceptedChange 3 NewCard".getBytes()));
        verify(mockPlayer, times(1)).setExchangeCounter(3);
    }

    @Test
    void testExecute_NoChange() throws IOException {
        // Given
        int playerID = 42;
        int gameID = 1;

        SocketChannel clientChannel = mock(SocketChannel.class);
        serverData.clients.put(playerID, clientChannel);

        Player mockPlayer1 = mock(Player.class);
        serverData.players.put(playerID, mockPlayer1);

        Game mockGame1 = mock(Game.class);
        serverData.games.put(gameID, mockGame1);

        // Mock Player and Game behavior
        when(mockPlayer1.getCash()).thenReturn(1000); // Ensure no NPE on getCash()
        when(mockGame1.getPlayers()).thenReturn(serverData.players); // Game returns players map
        when(mockGame1.getCurrentAuctionQueue()).thenReturn(List.of(playerID));
        when(mockGame1.nextPlayerIDFromQueue()).thenReturn(playerID);

        // Args
        String[] args = {"change", String.valueOf(gameID), String.valueOf(playerID), "no"};

        // When
        changeCardCommand.execute(clientChannel, args, serverData);

        // Then
        verify(changeCardCommand, times(1)).sendToken(clientChannel, "acceptedEndChanging");
        verify(changeCardCommand, times(1)).handleAuctionOrChange(mockGame1, serverData);
    }

    @Test
    void testHandleChangeCardToken_LimitNotReached() throws IOException {
        // Given
        int playerID = 42;
        int cardID = 3;
        serverData.players.put(playerID, mockPlayer);

        when(mockPlayer.getExchangeCounter()).thenReturn(1);
        when(mockGame.changeCard(playerID, cardID)).thenReturn("NewCard");

        // When
        changeCardCommand.handleChangeCardToken(mockClientChannel, mockGame, playerID, serverData, cardID);

        // Then
        verify(mockClientChannel, times(1))
                .write(ByteBuffer.wrap("acceptedChange 3 NewCard".getBytes()));
        verify(mockPlayer, times(1)).setExchangeCounter(2);
    }

    @Test
    void testHandleChangeCardToken_LimitReached() throws IOException {
        // Given
        int playerID = 42;
        int gameID = 1;
        int cardID = 3;

        SocketChannel clientChannel = mock(SocketChannel.class);
        serverData.clients.put(playerID, clientChannel);

        Player mockPlayer1 = mock(Player.class);
        serverData.players.put(playerID, mockPlayer1);

        Game mockGame1 = mock(Game.class);
        serverData.games.put(gameID, mockGame1);

        // Mock Player behavior
        when(mockPlayer1.getExchangeCounter()).thenReturn(4); // Limit reached
        when(mockPlayer1.getCash()).thenReturn(1000); // Avoid NullPointerException
        when(mockGame1.getCurrentAuctionQueue()).thenReturn(List.of(playerID));
        when(mockGame1.nextPlayerIDFromQueue()).thenReturn(playerID);

        // Mock game behavior
        when(mockGame1.getPlayers()).thenReturn(serverData.players);

        // When
        changeCardCommand.handleChangeCardToken(clientChannel, mockGame1, playerID, serverData, cardID);

        // Then
        verify(changeCardCommand, times(1)).sendToken(clientChannel, "deniedChange ");
        verify(changeCardCommand, times(1)).handleAuctionOrChange(mockGame1, serverData);
    }

    @Test
    void testHandleNoChangeToken() throws IOException {
        // Given
        int playerID = 42;
        int gameID = 1;

        SocketChannel clientChannel = mock(SocketChannel.class);
        serverData.clients.put(playerID, clientChannel);

        Player mockPlayer1 = mock(Player.class);
        serverData.players.put(playerID, mockPlayer1);

        Game mockGame1 = mock(Game.class);
        serverData.games.put(gameID, mockGame1);

        // Mock game behavior
        when(mockGame1.getPlayerIDs()).thenReturn(Set.of(playerID)); // Use Set instead of List
        when(mockGame1.getQueueOfPlayers()).thenReturn(playerID);
        when(mockGame1.getPlayers()).thenReturn(serverData.players);

        // Mock Player behavior
        when(mockPlayer1.getCash()).thenReturn(1000); // Avoid NullPointerException
        when(mockGame1.getMinimumBet()).thenReturn(100);
        when(mockGame1.getStake()).thenReturn(500);

        // When
        changeCardCommand.handleNoChangeToken(mockGame1, playerID, serverData);

        // Then
        verify(clientChannel, times(1)).write(ByteBuffer.wrap("acceptedEndChanging".getBytes()));
        verify(changeCardCommand, times(1)).handleAuctionOrChange(mockGame1, serverData);
    }

    @Test
    void testHandleAuctionOrChange_WithQueue() throws IOException {
        // Given
        int nextPlayerID = 43;
        SocketChannel nextPlayerChannel = mock(SocketChannel.class);
        serverData.clients.put(nextPlayerID, nextPlayerChannel);

        when(mockGame.getCurrentAuctionQueue()).thenReturn(Arrays.asList(nextPlayerID));
        when(mockGame.nextPlayerIDFromQueue()).thenReturn(nextPlayerID);

        // When
        changeCardCommand.handleAuctionOrChange(mockGame, serverData);

        // Then
        verify(nextPlayerChannel, times(1))
                .write(ByteBuffer.wrap("changeCards".getBytes()));
    }

    @Test
    void testHandleAuctionOrChange_NoQueue() throws IOException {
        // Given
        int gameFounderID = 99;
        SocketChannel founderChannel = mock(SocketChannel.class);
        serverData.clients.put(gameFounderID, founderChannel);
        serverData.players.put(gameFounderID, mockPlayer);

        when(mockGame.getCurrentAuctionQueue()).thenReturn(Collections.emptyList());
        when(mockGame.getQueueOfPlayers()).thenReturn(gameFounderID);
        when(mockGame.getPlayers()).thenReturn(serverData.players);
        when(mockGame.getMinimumBet()).thenReturn(100);
        when(mockGame.getStake()).thenReturn(500);
        when(mockPlayer.getCash()).thenReturn(1000);

        // When
        changeCardCommand.handleAuctionOrChange(mockGame, serverData);

        // Then
        verify(changeCardCommand, times(1))
                .sendRequestNextStage(mockGame, serverData);
        verify(changeCardCommand, times(1))
                .sendTokenToStarAuction(mockGame, serverData);
    }


    @Test
    void testSendTokenToStartAuction() throws IOException {
        // Given
        int gameFounderID = 99;
        SocketChannel founderChannel = mock(SocketChannel.class);
        serverData.clients.put(gameFounderID, founderChannel);
        serverData.players.put(gameFounderID, mockPlayer);

        when(mockGame.getQueueOfPlayers()).thenReturn(gameFounderID);
        when(mockGame.getPlayers()).thenReturn(serverData.players);
        when(mockGame.getMinimumBet()).thenReturn(100);
        when(mockGame.getStake()).thenReturn(500);
        when(mockPlayer.getCash()).thenReturn(1000);

        // When
        changeCardCommand.sendTokenToStarAuction(mockGame, serverData);

        // Then
        verify(founderChannel, times(1))
                .write(ByteBuffer.wrap("startAuction 1000 100 500".getBytes()));
    }




}