package pl.project.commands;

import pl.project.data.ServerData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Interface!
 */
public interface Command {
    void execute(SocketChannel clientChannel, String[] args, ServerData serverData) throws IOException;

    /**
     * Helps with sending tokens to client
     * @param clientChannel socketChannel
     * @param token string
     * @throws IOException exception
     */
    default void sendToken(SocketChannel clientChannel, String token) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(token.getBytes());
        clientChannel.write(buffer);
        buffer.clear();
    }

}
