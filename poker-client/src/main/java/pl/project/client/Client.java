package pl.project.client;

import lombok.Getter;
import pl.project.stages.Stage;

import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * The {@code Client} class represents a client connected to a server.
 * <p>
 * It maintains the state and data of the client, such as the current game state,
 * player information, and communication channel. The class provides methods to execute
 * the current stage of the client's interaction with the server.
 */
@Getter
public class Client {

    @Setter
    private Stage stage;
    final SocketChannel socketChannel;
    final ByteBuffer buffer;
    @Setter
    private boolean gameFounder = false;
    @Setter
    private List<String> hand = new ArrayList<>();
    @Setter
    int playerID = -1;
    @Setter
    int gameID = -1;


    /**
     * Constructs a new {@code Client} with the specified socket channel.
     * <p>
     * The buffer size is initialized to 256 bytes for network communication.
     *
     * @param socketChannel the {@link SocketChannel} for communication with the server
     */
    public Client(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.buffer = ByteBuffer.allocate(256);
    }

    /**
     * Executes the current stage of the client.
     * <p>
     * If the {@link Stage} is set, the method invokes the {@code execute} method
     * of the current stage, passing this client instance as the context.
     */
    public void executeStage() {
        if (stage != null) {
            stage.execute(this);
        }
    }

}
