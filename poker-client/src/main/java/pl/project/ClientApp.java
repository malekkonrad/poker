package pl.project;

import pl.project.client.Client;
import pl.project.stages.JoinServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


/**
 * The {@code ClientApp} class represents a client-side application
 * that connects to a server, initializes the client state, and executes
 * various stages of communication.
 * <p>
 * The main purpose of this class is to provide an entry point for the client application
 * and manage the lifecycle of the client connection.
 */
public class ClientApp {

    /**
     * The main entry point for the client application.
     * <p>
     * It initializes a new {@code ClientApp} instance and starts the client
     * by connecting to the specified server address and port.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new ClientApp().runClient("localhost", 8081);
    }

    /**
     * Starts the client application and connects to the specified server.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Opens a connection to the server at the given host and port.</li>
     *     <li>Initializes a {@link Client} instance and sets its initial stage to {@link JoinServer}.</li>
     *     <li>Continuously executes the client's stages until the connection is closed.</li>
     * </ul>
     * If an exception occurs during execution, it is caught and logged.
     * The connection is closed in the {@code finally} block to ensure proper resource cleanup.
     *
     * @param host the hostname or IP address of the server
     * @param port the port number of the server
     */
    public void runClient(String host, int port) {
        Client client = null;
        try {
            // Połączenie z serwerem
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));

            // Tworzenie klienta i ustawienie początkowego stanu
            client = new Client(socketChannel);

            client.setStage(new JoinServer());

            // Wykonanie etapów klienta
            while (socketChannel.isOpen()) {
                client.executeStage();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // Zamknięcie połączenia w bloku finally
            if (client != null && client.getSocketChannel() != null && client.getSocketChannel().isOpen()) {
                try {
                    client.getSocketChannel().close();
                    System.out.println("Połączenie zostało zamknięte.");
                } catch (IOException e) {
                    System.err.println("Błąd podczas zamykania połączenia: " + e.getMessage());
                }
            }
        }
    }

}
