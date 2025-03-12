package pl.project.communication;

import pl.project.client.Client;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * The {@code SendToken} class provides a method for sending tokens to a server
 * through a client's {@code SocketChannel}. It handles writing the token to a
 * {@code ByteBuffer} and sending it over the channel.
 */
public class SendToken {


    private SendToken() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Sends a token to the server using the provided {@code Client}.
     * <p>
     * This method converts the given token string into bytes, writes them
     * into a {@code ByteBuffer}, and sends the buffer's contents via the
     * client's {@code SocketChannel}.
     * </p>
     *
     * @param client the client through which the token will be sent.
     *               The client must have a properly initialized
     *               {@code SocketChannel}.
     * @param token  the token string to be sent to the server.
     * @throws IOException if an I/O error occurs during the operation,
     *                     such as when the {@code SocketChannel} fails
     *                     to send data.
     * @throws NullPointerException if the {@code client} or {@code token} is null.
     */
    public static void send(Client client, String token) throws IOException {
        // write to buffer
        ByteBuffer buffer = ByteBuffer.wrap(token.getBytes());

        // send
        client.getSocketChannel().write(buffer);

        // clear
        buffer.clear();
    }
}
