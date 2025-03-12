package pl.project.stages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.client.Client;
import static org.mockito.Mockito.*;



class JoinServerTest {

    private JoinServer joinServer;
    private Client client;

    @BeforeEach
    void setUp() {
        joinServer = new JoinServer();
        client = mock(Client.class);
    }

    @Test
    void testHandleResponseAccepted() {
        // Arrange
        String[] words = {"accepted", "123"};

        // Act
        joinServer.handleResponse(client, words);

        // Assert
        verify(client).setPlayerID(123);
        verify(client).setStage(any(PreGame.class));
    }

    @Test
    void testHandleResponseRejected() {
        // Arrange
        String[] words = {"rejected"};

        // Act
        joinServer.handleResponse(client, words);

        // Assert
        verify(client, never()).setPlayerID(anyInt());
        verify(client).setStage(any(JoinServer.class));
    }
}