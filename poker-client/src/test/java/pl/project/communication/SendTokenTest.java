package pl.project.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.project.client.Client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendTokenTest {

    private Client mockClient;
    private SocketChannel mockSocketChannel;

    @BeforeEach
    void setUp() {
        mockClient = mock(Client.class);
        mockSocketChannel = mock(SocketChannel.class);

        // Mockowanie SocketChannel w kliencie
        when(mockClient.getSocketChannel()).thenReturn(mockSocketChannel);
    }

    @Test
    void testSend_WithValidToken() throws IOException {
        // Testowany token
        String token = "TestToken";

        // Przechwycenie bufora podczas wywołania write
        doAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0);
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes); // Pobieramy dane z bufora
            assertEquals(token, new String(bytes), "Token wysłany przez metodę send powinien być zgodny z wejściowym tokenem");
            return null;
        }).when(mockSocketChannel).write(any(ByteBuffer.class));

        // Wywołanie metody
        SendToken.send(mockClient, token);

        // Weryfikacja, czy metoda write została wywołana
        verify(mockSocketChannel, times(1)).write(any(ByteBuffer.class));
    }

    @Test
    void testSend_WithIOException() throws IOException {
        // Testowany token
        String token = "TestToken";

        // Symulowanie wyjątku IOException podczas zapisu do SocketChannel
        doThrow(new IOException("Test exception")).when(mockSocketChannel).write(any(ByteBuffer.class));

        // Sprawdzenie, czy metoda rzuca IOException
        IOException exception = assertThrows(IOException.class, () -> SendToken.send(mockClient, token));

        // Sprawdzenie komunikatu wyjątku
        assertEquals("Test exception", exception.getMessage());

        // Weryfikacja, czy write zostało wywołane
        verify(mockSocketChannel, times(1)).write(any(ByteBuffer.class));
    }

    @Test
    void testConstructorThrowsException() {
        // Uzyskanie konstruktora klasy ReadToken
        Constructor<SendToken> constructor;
        try {
            constructor = SendToken.class.getDeclaredConstructor();
            constructor.setAccessible(true); // Udostępniamy prywatny konstruktor

            // Próba utworzenia instancji
            Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

            // Sprawdzenie przyczyny wyjątku
            assertTrue(exception.getCause() instanceof IllegalStateException);
            assertEquals("Utility class", exception.getCause().getMessage());

        } catch (NoSuchMethodException e) {
            fail("Constructor not found");
        }
    }
}