package pl.project;

import pl.project.commands.*;
import pl.project.data.ServerData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


/**
 * Main class that starts Server
 */
public class Server {

    static ServerData serverData;
    static final Map<String, Command> commands = new HashMap<>();
    static {
        commands.put("login", new LoginCommand());
        commands.put("create", new CreateGameCommand());
        commands.put("join", new JoinGameCommand());
        commands.put("handCards", new HandCardsCommand());
        commands.put("bet", new BetCommand());
        commands.put("exchange", new ChangeCardCommand());
        commands.put("summary", new SummaryCommand());
    }


    /**
     * Entry point for the server application.
     * @param args optional command-line arguments for server configuration.
     */
    public static void main( String[] args ) {

        try {
            // setting max players
            int maxNumberOfPlayers = 4;
            if (args.length != 0) {
                maxNumberOfPlayers = Integer.parseInt(args[0]);
            }
            serverData = new ServerData(maxNumberOfPlayers);
            System.out.println("Max number of players has been set to: " + maxNumberOfPlayers);


            // Utowrzenie selektora
            Selector selector = Selector.open();

            // Utworzenie kanału do nasłuchiwania
            ServerSocketChannel serverSocketChannel= ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8081));
            serverSocketChannel.configureBlocking(false);  // Ustaw kanał w tryb nieblokujący

            // Rejestracja kanału w selektorze
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("\n" + "The server is listening on port 8081...");

            while (true) {
                // Czekaj na zdarzenia (np. nowe połączenia)
                selector.select();

                // Pobierz klucze, które mają zdarzenia do obsłużenia
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();


                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        // Accepting new connection
                        handleAccept(selector, key);

                    } else if (key.isReadable()) {
                        // handling reading ale sending back answer
                        handleRead(key);
                    }
                }
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Handles accepting new client connections.
     * @param selector the Selector managing all channel registrations.
     * @param key the SelectionKey representing the current accept event.
     * @throws IOException if an I/O error occurs during client connection.
     */
    public static void handleAccept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);

        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }


    /**
     * Handles reading client data and executing corresponding commands.
     * @param key the SelectionKey representing the current read event.
     * @throws IOException if an I/O error occurs while reading data.
     */
    static void handleRead(SelectionKey key) throws IOException {


        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            System.out.println("Error reading from client, disconnecting: " + e.getMessage());
            disconnectClient(clientChannel, key);
            return;
        }

        if (bytesRead == -1) {
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            disconnectClient(clientChannel, key);
            return;
        }

        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();
        System.out.println("Received: " + message);

        // Create String list to store
        String[] parts = message.split(" ");
        String commandName = parts[0];

        // get command
        Command command = commands.get(commandName);



        if (command != null) {
            command.execute(clientChannel, parts, serverData);
        } else {
            // sending error information
            clientChannel.write(ByteBuffer.wrap("Unknown command.\n".getBytes()));
        }


    }


    /**
     * Disconnects a client safely, cleaning up resources and notifying other components.
     * @param clientChannel the channel of the client to disconnect.
     * @param key the selection key of the client to clean up.
     * @throws IOException if an I/O error occurs during the process.
     */
    static void disconnectClient(SocketChannel clientChannel, SelectionKey key) throws IOException {
        try {
            System.out.println("Cleaning up client resources: " + clientChannel.getRemoteAddress());

            int clientID = serverData.reverseUserMap.get(clientChannel);
            serverData.clients.remove(clientID);
            serverData.players.remove(clientID);
            serverData.reverseUserMap.remove(clientChannel);

            System.out.println(serverData.players.size());

            key.cancel(); // Anuluj klucz selektora
            clientChannel.close(); // Zamknij kanał
        } catch (IOException e) {
            System.err.println("Error during client disconnection: " + e.getMessage());
        }
    }

}
