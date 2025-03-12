package pl.project.commands;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.Game;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CreateGameCommandTest {


    private CreateGameCommand createGameCommand;
    private ServerData serverData;
    private SocketChannel mockClientChannel;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        createGameCommand = spy(new CreateGameCommand());
        serverData = new ServerData(3); // Example: max 3 players per game
        mockClientChannel = mock(SocketChannel.class);
        mockPlayer = mock(Player.class);

        // Initialize server data
        serverData.players = new HashMap<>();
        serverData.games = new HashMap<>();
        serverData.newGameID = 1; // Start game IDs at 1
    }

    @Test
    void testExecute_CreatesGameSuccessfully() throws IOException {
        // Given: A valid player in server data
        int playerID = 42;
        serverData.players.put(playerID, mockPlayer);

        // Arguments for creating a game
        String[] args = {"createGame", String.valueOf(playerID)};

        // When: Command is executed
        createGameCommand.execute(mockClientChannel, args, serverData);

        // Then: Verify a new game is created
        assertEquals(1, serverData.games.size());
        Game createdGame = serverData.games.get(1);
        assertEquals(1, createdGame.getGameID());
        assertEquals(mockPlayer, createdGame.getGameFounder());

        // Verify the correct token is sent to the client
        verify(createGameCommand, times(1)).sendToken(mockClientChannel, "acceptedCreate 1");

        // Verify the new game ID is incremented
        assertEquals(2, serverData.newGameID);
    }





}