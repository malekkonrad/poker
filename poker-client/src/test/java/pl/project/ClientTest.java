package pl.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.client.Client;
import pl.project.stages.Stage;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientTest {


    private Client client;
    private SocketChannel mockSocketChannel;

    @BeforeEach
    void setUp() {
        // Mockowanie SocketChannel
        mockSocketChannel = mock(SocketChannel.class);
        client = new Client(mockSocketChannel);
    }

    @Test
    void testConstructor() {
        // Then: Weryfikacja wartości początkowych
        assertNotNull(client.getSocketChannel());
        assertNotNull(client.getBuffer());
        assertEquals(256, client.getBuffer().capacity());
        assertFalse(client.isGameFounder());
        assertTrue(client.getHand().isEmpty());
        assertEquals(-1, client.getPlayerID());
        assertEquals(-1, client.getGameID());
    }


    @Test
    void testSetAndGetGameFounder() {
        // When: Ustawienie wartości
        client.setGameFounder(true);

        // Then: Weryfikacja
        assertTrue(client.isGameFounder());
    }

    @Test
    void testSetAndGetHand() {
        // Given: Nowa lista kart
        List<String> hand = new ArrayList<>();
        hand.add("Card1");
        hand.add("Card2");

        // When: Ustawienie listy kart
        client.setHand(hand);

        // Then: Weryfikacja
        assertEquals(hand, client.getHand());
    }

    @Test
    void testSetAndGetPlayerID() {
        // When: Ustawienie ID gracza
        client.setPlayerID(42);

        // Then: Weryfikacja
        assertEquals(42, client.getPlayerID());
    }

    @Test
    void testSetAndGetGameID() {
        // When: Ustawienie ID gry
        client.setGameID(100);

        // Then: Weryfikacja
        assertEquals(100, client.getGameID());
    }

    @Test
    void testExecuteStage_WhenStageIsSet() {
        // Given: Mockowanie Stage
        Stage mockStage = mock(Stage.class);
        client.setStage(mockStage);

        // When: Wywołanie metody executeStage
        client.executeStage();

        // Then: Weryfikacja, czy metoda execute została wywołana
        verify(mockStage, times(1)).execute(client);
    }

    @Test
    void testExecuteStage_WhenStageIsNull() {
        // When: Wywołanie metody executeStage
        client.executeStage();

        // Then: Weryfikacja, że nic się nie dzieje (brak wyjątku)
        assertDoesNotThrow(() -> client.executeStage());
    }


    @Test
    void testBufferInitialization() {
        // Then: Weryfikacja, że bufor jest poprawnie zainicjalizowany
        ByteBuffer buffer = client.getBuffer();
        assertNotNull(buffer);
        assertEquals(256, buffer.capacity());
    }

    @Test
    void testSocketChannelInitialization() {
        // Then: Weryfikacja, że SocketChannel jest poprawnie zainicjalizowany
        assertEquals(mockSocketChannel, client.getSocketChannel());
    }
}