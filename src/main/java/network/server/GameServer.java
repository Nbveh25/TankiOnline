package network.server;

import com.google.gson.Gson;
import network.helper.NetworkHelper;
import network.packet.GameData;
import network.protocol.MessageType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private final DatagramSocket socket;
    private final ConcurrentHashMap<Integer, ClientInfo> clients;
    private final NetworkHelper networkHelper;
    private int nextPlayerId = 1;

    public GameServer(int port) throws Exception {
        socket = new DatagramSocket(port);
        clients = new ConcurrentHashMap<>();
        networkHelper = new NetworkHelper(socket);

        System.out.println("Server started on port " + port);
    }

    public void start() {
        byte[] buffer = new byte[1024];

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                handlePacket(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePacket(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength());
        GameData data = new Gson().fromJson(message, GameData.class);

        switch (data.getMessageType()) {
            case CONNECT:
                handleConnect(packet);
                break;
            case PLAYER_POSITION:
                handlePlayerPosition(data);
                break;
            case DISCONNECT:
                handleDisconnect(data.getPlayerId());
                break;
        }
    }

    private void handleConnect(DatagramPacket packet) {
        int playerId = nextPlayerId++;
        clients.put(playerId, new ClientInfo(packet.getAddress(), packet.getPort()));

        // Отправляем ответ с ID игрока и начальным спрайтом
        GameData response = new GameData(MessageType.CONNECT, playerId, 0, 0, null, 1);
        networkHelper.send(response, packet.getAddress(), packet.getPort());
        System.out.println("Player connected with ID: " + playerId);
    }

    private void handlePlayerPosition(GameData data) {
        // Обновляем позицию игрока и отправляем данные всем клиентам
        for (ClientInfo client : clients.values()) {
            if (client.playerId != data.getPlayerId()) {
                networkHelper.send(data, client.address, client.port);
            }
        }
    }

    private void handleDisconnect(int playerId) {
        clients.remove(playerId);
        System.out.println("Player disconnected: " + playerId);
    }

    public static void main(String[] args) {
        try {
            GameServer server = new GameServer(12345);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}