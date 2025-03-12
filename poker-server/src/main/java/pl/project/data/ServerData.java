package pl.project.data;

import lombok.Getter;
import pl.project.Game;
import pl.project.Player;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data storage for server
 */
@Getter
public class ServerData {
    public int maxNumberOfPlayers = 4;
    public int newPlayerID = 0;
    public int newGameID = 0;

    public Map<Integer, SocketChannel> clients = new HashMap<>();
    public Map<Integer, Player> players = new HashMap<>();
    public Map<Integer, Game> games = new HashMap<>();
    public Set<String> userNames = new HashSet<>();
    public Map<SocketChannel, Integer> reverseUserMap = new HashMap<>();

    public ServerData(int maxNumberOfPlayers) {
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }
}
