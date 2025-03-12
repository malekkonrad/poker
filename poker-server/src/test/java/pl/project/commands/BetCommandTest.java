package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.project.Game;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BetCommandTest {

    private BetCommand betCommand;
    private ServerData serverData;
    private Game mockGame;
    private Player mockPlayer;
    private SocketChannel mockClientChannel;

    @BeforeEach
    void setUp() {
        betCommand = spy(new BetCommand());
        serverData = new ServerData(3); // Example max players
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
        mockClientChannel = mock(SocketChannel.class);

        // Initialize server data
        serverData.games = new HashMap<>();
        serverData.clients = new HashMap<>();
        serverData.players = new HashMap<>();

        int gameID = 1;
        int playerID = 42;
        int gameFounderID = 99;

        serverData.games.put(gameID, mockGame);
        serverData.players.put(playerID, mockPlayer);
        serverData.clients.put(playerID, mockClientChannel);
        serverData.clients.put(gameFounderID, mockClientChannel); // Ensure game founder is mapped

        Map<Integer, Player> gamePlayers = new HashMap<>();
        gamePlayers.put(playerID, mockPlayer);
        gamePlayers.put(gameFounderID, mock(Player.class)); // Add game founder to game players

        when(mockGame.getPlayers()).thenReturn(gamePlayers);
        when(mockGame.getQueueOfPlayers()).thenReturn(gameFounderID); // Mock game founder
    }

    @Test
    void testExecute_Call() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        String[] args = {"bet", String.valueOf(gameID), String.valueOf(playerID), "call", "100"};

        when(mockPlayer.getCash()).thenReturn(500);

        // When
        betCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(mockPlayer, times(1)).setCash(400); // 500 - 100
        verify(mockGame, times(1)).setStake(100); // Add 100 to stake
        verify(betCommand, times(1)).sendToken(mockClientChannel, "acceptedBet");
    }

    @Test
    void testExecute_Raise() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        String[] args = {"bet", String.valueOf(gameID), String.valueOf(playerID), "raise", "200"};
        serverData.games.put(gameID, mockGame);
        serverData.players.put(playerID, mockPlayer);
        serverData.clients.put(playerID, mockClientChannel);

        when(mockPlayer.getCash()).thenReturn(500);

        // When
        betCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(mockPlayer, times(1)).setCash(300); // 500 - 200
        verify(mockGame, times(1)).setStake(200); // Add 200 to stake
        verify(mockGame, times(1)).setMinimumBet(200); // Update minimum bet
        verify(betCommand, times(1)).sendToken(mockClientChannel, "acceptedBet");
    }

    @Test
    void testExecute_Fold() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        String[] args = {"bet", String.valueOf(gameID), String.valueOf(playerID), "fold", "0"};
        serverData.games.put(gameID, mockGame);
        serverData.players.put(playerID, mockPlayer);
        serverData.clients.put(playerID, mockClientChannel);

        when(mockGame.playerFold(playerID)).thenReturn(-1); // No winner yet

        // When
        betCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(mockGame, times(1)).playerFold(playerID);
        verify(betCommand, times(1)).sendToken(mockClientChannel, "acceptedBet");
        verify(betCommand, times(1)).handleCurrentState(mockGame, serverData);
    }

    @Test
    void testExecute_AllIn() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        String[] args = {"bet", String.valueOf(gameID), String.valueOf(playerID), "allIn", "500"};
        serverData.games.put(gameID, mockGame);
        serverData.players.put(playerID, mockPlayer);
        serverData.clients.put(playerID, mockClientChannel);

        when(mockPlayer.getCash()).thenReturn(500);

        // When
        betCommand.execute(mockClientChannel, args, serverData);

        // Then
        verify(mockPlayer, times(1)).setCash(0); // All-in sets cash to 0
        verify(mockGame, times(1)).setStake(500); // Add all-in amount to stake
        verify(mockGame, times(1)).setMinimumBet(500); // Update minimum bet
        verify(betCommand, times(1)).sendToken(mockClientChannel, "acceptedBet");
    }

    @Test
    void testHandleCurrentState_WithQueue() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;

        // Mock Player
        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.getCash()).thenReturn(1000); // Mock cash

        // Add mock player to server data
        serverData.players.put(playerID, mockPlayer1);

        // Mock Game
        Game mockGame1 = mock(Game.class);
        when(mockGame1.getCurrentAuctionQueue()).thenReturn(new LinkedList<>(List.of(playerID)));
        when(mockGame1.nextPlayerIDFromQueue()).thenReturn(playerID);
        when(mockGame1.getPlayers()).thenReturn(serverData.players);
        when(mockGame1.getMinimumBet()).thenReturn(100);
        when(mockGame1.getStake()).thenReturn(500);

        serverData.games.put(gameID, mockGame1);

        // Mock ClientChannel
        SocketChannel mockClientChannel1 = mock(SocketChannel.class);
        serverData.clients.put(playerID, mockClientChannel1);

        // When
        betCommand.handleCurrentState(mockGame1, serverData);

        // Then
        verify(mockClientChannel1, times(1))
                .write(ByteBuffer.wrap("startAuction 1000 100 500".getBytes()));
    }

    @Test
    void testHandleCurrentState_NoQueue() throws IOException {
        // Given
        when(mockGame.getCurrentAuctionQueue()).thenReturn(Collections.emptyList());
        when(mockGame.getNumberOfAuction()).thenReturn(0);
        when(mockGame.getQueueOfPlayers()).thenReturn(-1);

        // When
        betCommand.handleCurrentState(mockGame, serverData);

        // Then
        verify(betCommand, times(1)).handleSendTokenToEveryOne(mockGame, serverData, "lastStage ");
    }

    @Test
    void testSendInfoAboutBet() throws IOException {
        // Given
        int gameID = 1;
        int playerID = 42;
        int otherPlayerID = 43;
        serverData.games.put(gameID, mockGame);
        serverData.players.put(playerID, mockPlayer);
        serverData.players.put(otherPlayerID, mock(Player.class));
        serverData.clients.put(otherPlayerID, mock(SocketChannel.class));

        when(mockPlayer.getUserName()).thenReturn("Player1");
        when(mockGame.getPlayerIDs()).thenReturn(Set.of(playerID, otherPlayerID));

        // When
        betCommand.sendInfoAboutBet(mockGame, playerID, "call", 100, serverData);

        // Then
        verify(serverData.clients.get(otherPlayerID), times(1))
                .write(ByteBuffer.wrap("playerBet Player1 call 100".getBytes()));
    }

    @Test
    void testHandleFoldToken_Winner() throws IOException {
        // Given
        int gameID = 1;
        int playerID1 = 42;
        int playerID2 = 43;
        int winnerID = playerID1;

        // Mock data
        SocketChannel mockClientChannel1 = mock(SocketChannel.class);
        SocketChannel mockClientChannel2 = mock(SocketChannel.class);

        serverData.clients.put(playerID1, mockClientChannel1);
        serverData.clients.put(playerID2, mockClientChannel2);

        Player mockWinner = mock(Player.class);
        when(mockWinner.getUserName()).thenReturn("Winner");

        serverData.players.put(playerID1, mockWinner);
        serverData.players.put(playerID2, mock(Player.class));

        Game mockGame1 = mock(Game.class);
        when(mockGame1.getPlayerIDs()).thenReturn(Set.of(playerID1, playerID2));
        when(mockGame1.playerFold(playerID2)).thenReturn(winnerID);
        when(mockGame1.getStake()).thenReturn(1000);

        serverData.games.put(gameID, mockGame1);

        // When
        betCommand.handleFoldToken(mockGame1, playerID2, serverData);

        // Then
        verify(mockClientChannel1, times(1))
                .write(ByteBuffer.wrap("winner 42 Winner 1000".getBytes()));
        verify(mockClientChannel2, times(1))
                .write(ByteBuffer.wrap("winner 42 Winner 1000".getBytes()));
    }



}