package pl.project.communication;

import pl.project.client.Client;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Class responsible for reading data from the client's {@code ByteBuffer}.
 * <p>
 * This class provides functionality to read data from the server
 * through the client's {@code SocketChannel} and retrieve it as a string.
 * </p>
 */
public class ReadToken {

    private ReadToken() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Reads a response from the server using the provided {@code Client}.
     *
     * This method performs the following steps:
     * <ul>
     *   <li>Clears the client's {@code ByteBuffer} to prepare it for reading.</li>
     *   <li>Reads data from the client's {@code SocketChannel} into the buffer.</li>
     *   <li>Flips the buffer to switch it to read mode.</li>
     *   <li>Extracts the response as a string from the buffer's contents.</li>
     *   <li>Clears the buffer after reading.</li>
     * </ul>
     *
     *
     * @param client the client whose {@code SocketChannel} and {@code ByteBuffer}
     *               will be used to read the response. The {@code Client} must
     *               have a properly initialized {@code SocketChannel} and
     *               {@code ByteBuffer}.
     * @return the response read from the server as a string.
     * @throws IOException if an I/O error occurs during the read operation,
     *                     such as when the {@code SocketChannel} fails to
     *                     retrieve data.
     * @throws NullPointerException if the {@code client}, its {@code SocketChannel},
     *                              or its {@code ByteBuffer} is null.
     */
    public static String read(Client client) throws IOException {
        // clear buffer before reading
        client.getBuffer().clear();

        // read
        client.getSocketChannel().read(client.getBuffer());

        // flip
        client.getBuffer().flip();

        // save response
        String response = new String(client.getBuffer().array(), 0, client.getBuffer().limit());

        // clear buffer after reading
        client.getBuffer().clear();

        // return response
        return response;
    }
}
