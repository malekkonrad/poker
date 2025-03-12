package pl.project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommandTest {

    private Command command;
    private SocketChannel mockClientChannel;


    @BeforeEach
    void setUp() {
        // Tworzymy klasę anonimową implementującą Command
        command = new Command() {
            @Override
            public void execute(SocketChannel clientChannel, String[] args, ServerData serverData) {
                // Pusta implementacja, nie jest potrzebna do testów sendToken
            }
        };
        mockClientChannel = mock(SocketChannel.class);
    }

    @Test
    void testSendToken() throws IOException {
        // Given
        String token = "testToken";
        ArgumentCaptor<ByteBuffer> bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);

        // When
        command.sendToken(mockClientChannel, token);

        // Then
        verify(mockClientChannel, times(1)).write(bufferCaptor.capture());
        ByteBuffer capturedBuffer = bufferCaptor.getValue();

        // Sprawdzenie zawartości bufora
        assertEquals(token, new String(capturedBuffer.array(), 0, capturedBuffer.limit()));
    }

    @Test
    void testSendToken_IOException() throws IOException {
        // Given
        String token = "testToken";
        doThrow(new IOException("Write failed")).when(mockClientChannel).write(any(ByteBuffer.class));

        // When & Then
        IOException exception = assertThrows(IOException.class, () -> command.sendToken(mockClientChannel, token));
        assertEquals("Write failed", exception.getMessage());
    }
}