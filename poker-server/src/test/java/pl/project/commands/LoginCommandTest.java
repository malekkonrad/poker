package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.Player;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



public class LoginCommandTest {

    private LoginCommand loginCommand;
    private SocketChannel mockClientChannel;
    private ServerData mockServerData;

    @BeforeEach
    void setUp() {
        loginCommand = new LoginCommand();
        mockClientChannel = mock(SocketChannel.class);
        mockServerData = new ServerData(2);

        // Initialize ServerData
        mockServerData.userNames = new HashSet<>();
        mockServerData.players = new HashMap<>();
        mockServerData.clients = new HashMap<>();
        mockServerData.reverseUserMap = new HashMap<>();
        mockServerData.newPlayerID = 1;
    }

    @Test
    void testExecute_InvalidArguments() throws IOException {
        // Given: Invalid arguments
        String[] args = {"login"};

        // When: Command is executed
        loginCommand.execute(mockClientChannel, args, mockServerData);

        // Then: Verify error message is sent
        verify(mockClientChannel, times(1)).write(ByteBuffer.wrap("Invalid arguments for login.".getBytes()));
        assertTrue(mockServerData.userNames.isEmpty());
    }

    @Test
    void testExecute_UsernameAlreadyTaken() throws IOException {
        // Given: Username already exists
        String username = "existingUser";
        mockServerData.userNames.add(username);
        String[] args = {"login", username};

        // When: Command is executed
        loginCommand.execute(mockClientChannel, args, mockServerData);

        // Then: Verify username taken message is sent
        verify(mockClientChannel, times(1)).write(ByteBuffer.wrap("Username already in use.".getBytes()));
        assertTrue(mockServerData.players.isEmpty());
    }

    @Test
    void testExecute_SuccessfulLogin() throws IOException {
        // Given: Valid username and no conflicts
        String username = "newUser";
        String[] args = {"login", username};

        // When: Command is executed
        loginCommand.execute(mockClientChannel, args, mockServerData);

        // Then: Verify player is created and added to server data
        assertEquals(1, mockServerData.players.size());
        assertEquals(1, mockServerData.clients.size());
        assertTrue(mockServerData.userNames.contains(username));

        // Verify player details
        Player player = mockServerData.players.get(1);
        assertNotNull(player);
        assertEquals(username, player.getUserName());

        // Verify token sent to client
        verify(mockClientChannel, times(1)).write(ByteBuffer.wrap("accepted 1".getBytes()));
    }

    @Test
    void testIsUsernameTaken_True() throws IOException {
        // Given: Username already exists
        String username = "existingUser";
        mockServerData.userNames.add(username);

        // When: Check if username is taken
        boolean result = loginCommand.isUsernameTaken(mockClientChannel, username, mockServerData);

        // Then: Verify result and token sent
        assertTrue(result);
        verify(mockClientChannel, times(1)).write(ByteBuffer.wrap("Username already in use.".getBytes()));
    }

    @Test
    void testIsUsernameTaken_False() throws IOException {
        // Given: Username does not exist
        String username = "newUser";

        // When: Check if username is taken
        boolean result = loginCommand.isUsernameTaken(mockClientChannel, username, mockServerData);

        // Then: Verify result
        assertFalse(result);
        verify(mockClientChannel, never()).write(any(ByteBuffer.class));
    }

    @Test
    void testValidateArguments_Valid() throws IOException {
        // Given: Valid arguments
        String[] args = {"login", "username"};

        // When: Validate arguments
        boolean result = loginCommand.validateArguments(mockClientChannel, args);

        // Then: Verify result
        assertTrue(result);
        verify(mockClientChannel, never()).write(any(ByteBuffer.class));
    }

    @Test
    void testValidateArguments_Invalid() throws IOException {
        // Given: Invalid arguments
        String[] args = {"login"};

        // When: Validate arguments
        boolean result = loginCommand.validateArguments(mockClientChannel, args);

        // Then: Verify result and token sent
        assertFalse(result);
        verify(mockClientChannel, times(1)).write(ByteBuffer.wrap("Invalid arguments for login.".getBytes()));
    }

    @Test
    void testCreateNewPlayer() throws IOException {
        // Given: Valid username
        String username = "newUser";

        // When: Create new player
        loginCommand.createNewPlayer(mockClientChannel, username, mockServerData);

        // Then: Verify player is created and added to server data
        assertEquals(1, mockServerData.players.size());
        assertEquals(1, mockServerData.clients.size());
        assertTrue(mockServerData.userNames.contains(username));

        // Verify player details
        Player player = mockServerData.players.get(1);
        assertNotNull(player);
        assertEquals(username, player.getUserName());

        // Verify token sent to client
        verify(mockClientChannel, times(1)).write(ByteBuffer.wrap("accepted 1".getBytes()));
    }
    
}