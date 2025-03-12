package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.Game;
import pl.project.Player;
import pl.project.commands.JoinGameCommand;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import static org.mockito.Mockito.*;

class JoinGameCommandTest {

    private JoinGameCommand joinGameCommand;
    private ServerData serverData;
    private SocketChannel clientChannel;
    private Game mockGame;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        joinGameCommand = spy(new JoinGameCommand());
        serverData = new ServerData(2);
        clientChannel = mock(SocketChannel.class);
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);


        // Initialize server data maps
        serverData.games = new HashMap<>();
        serverData.players = new HashMap<>();
        serverData.clients = new HashMap<>();
    }

    @Test
    void testExecute_GameNotFound() throws IOException {
        // Given
        String[] args = {"join", "1", "42"};
        serverData.games = new HashMap<>(); // No games in the map

        // When
        joinGameCommand.execute(clientChannel, args, serverData);

        // Then
        verify(joinGameCommand, times(1)).sendToken(clientChannel, "rejectedJoin 1");
    }


    @Test
    void testExecute_PendingGame() throws IOException {
        // Given
        String[] args = {"join", "1", "42"};
        serverData.games.put(1, mockGame);
        serverData.players.put(42, mockPlayer);

        // Mock game behavior
        when(mockGame.addPlayer(mockPlayer)).thenReturn(0);
        when(mockGame.getGameID()).thenReturn(1);
        when(mockGame.getPlayerIDs()).thenReturn(new HashSet<>(Arrays.asList(42, 43)));

        // Mock additional player
        SocketChannel otherPlayerChannel = mock(SocketChannel.class);
        serverData.clients.put(43, otherPlayerChannel);
        when(mockPlayer.getUserName()).thenReturn("Player42");

        // When
        joinGameCommand.execute(clientChannel, args, serverData);

        // Then
        verify(joinGameCommand, times(1)).sendToken(clientChannel, "acceptedJoin 1");
        verify(joinGameCommand, times(1)).sendToken(otherPlayerChannel, "playerJoin Player42");
    }

    @Test
    void testExecute_StartGame() throws IOException {
        // Given: Test arguments
        String[] args = {"join", "1", "42"};

        // Mock data setup
        serverData.games.put(1, mockGame);
        serverData.players.put(42, mockPlayer);

        // Mock game behavior
        when(mockGame.addPlayer(mockPlayer)).thenReturn(1); // Indicate the game is ready to start
        when(mockGame.getGameID()).thenReturn(1);
        when(mockGame.getPlayerIDs()).thenReturn(new HashSet<>(Arrays.asList(42, 43))); // Simulate player IDs in the game

        // Mock additional player
        SocketChannel otherPlayerChannel = mock(SocketChannel.class); // Mock a second client channel
        serverData.clients.put(43, otherPlayerChannel);
        serverData.clients.put(42, clientChannel); // Ensure the primary client channel is also present
        when(mockPlayer.getUserName()).thenReturn("Player42");

        // When: Execute the command
        joinGameCommand.execute(clientChannel, args, serverData);

        // Then: Verify expected token interactions
        verify(joinGameCommand, times(1)).sendToken(clientChannel, "acceptedJoin 1");
        verify(joinGameCommand, times(1)).sendToken(otherPlayerChannel, "playerJoin Player42");
        verify(joinGameCommand, times(1)).sendToken(otherPlayerChannel, "startGame 1");
    }

    @Test
    void testExecute_GameAlreadyStarted() throws IOException {
        // Given
        String[] args = {"join", "1", "42"};
        serverData.games.put(1, mockGame);
        serverData.players.put(42, mockPlayer);

        // Mock game behavior
        when(mockGame.addPlayer(mockPlayer)).thenReturn(2);

        // When
        joinGameCommand.execute(clientChannel, args, serverData);

        // Then
        verify(joinGameCommand, times(1)).sendToken(clientChannel, "rejectedJoin 1");
    }



    @Test
    void testHandleReject() throws IOException {
        // When
        joinGameCommand.handleReject(clientChannel, 1);

        // Then
        verify(joinGameCommand, times(1)).sendToken(clientChannel, "rejectedJoin 1");
    }





}