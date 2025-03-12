package pl.project.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.project.client.Client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadTokenTest {

    private Client mockClient;
    private SocketChannel mockSocketChannel;
    private ByteBuffer mockBuffer;

    @BeforeEach
    void setUp() {
        // Mockowanie Client i jego zależności
        mockClient = mock(Client.class);
        mockSocketChannel = mock(SocketChannel.class);
        mockBuffer = ByteBuffer.allocate(256);

        // Mockowanie zachowania
        when(mockClient.getSocketChannel()).thenReturn(mockSocketChannel);
        when(mockClient.getBuffer()).thenReturn(mockBuffer);
    }

    @Test
    void testRead_WithValidData() throws IOException {

        // Symulowana odpowiedź serwera
        String simulatedResponse = "TestResponse";
        mockBuffer.put(simulatedResponse.getBytes()); // Umieszczamy dane w bufferze
        mockBuffer.flip(); // Ustawiamy buffer w tryb odczytu

        // Mockowanie odczytu z SocketChannel
        when(mockSocketChannel.read(mockBuffer)).thenAnswer(invocation -> {
            mockBuffer.position(mockBuffer.limit());
            return simulatedResponse.length(); // Zwracamy liczbę bajtów odczytanych
        });

        // Wywołanie metody
        String result = ReadToken.read(mockClient);

        // Sprawdzenie wyniku
        assertTrue(result.contains(simulatedResponse));

    }

    @Test
    void testRead_WithEmptyData() throws IOException {
        // Mockowanie odczytu z SocketChannel
        when(mockSocketChannel.read(mockBuffer)).thenReturn(0); // Symulujemy brak danych

        // Wywołanie metody
        String result = ReadToken.read(mockClient);

        // Sprawdzenie wyniku
        assertEquals("", result, "Oczekiwany wynik to pusty ciąg znaków");

        // Weryfikacja interakcji z bufferem i SocketChannel
        verify(mockClient, times(6)).getBuffer();
        verify(mockSocketChannel, times(1)).read(mockBuffer);
    }

    @Test
    void testConstructorThrowsException() {
        // Uzyskanie konstruktora klasy ReadToken
        Constructor<ReadToken> constructor;
        try {
            constructor = ReadToken.class.getDeclaredConstructor();
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