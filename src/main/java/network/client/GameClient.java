package network.client;

import com.google.gson.Gson;
import network.helper.NetworkHelper;
import network.packet.GameData;
import network.protocol.MessageType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public class GameClient {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final Consumer<GameData> onDataReceived;
    private final NetworkHelper networkHelper;
    private final int port;
    private int playerId = -1;

    public GameClient(String ip, int port, Consumer<GameData> onDataReceived) throws Exception {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ip);
        this.port = port;
        this.onDataReceived = onDataReceived;

        // Создаем NetworkHelper
        networkHelper = new NetworkHelper(socket);

        // Запрашиваем ID у сервера
        send(new GameData(MessageType.CONNECT, -1, 0, 0, null, 1));
    }

    public void send(GameData data) {
        networkHelper.send(data, address, port);
    }

    public void receive() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());

                    GameData gameData = new Gson().fromJson(message, GameData.class);
                    if (gameData.getMessageType() == MessageType.CONNECT) {
                        playerId = gameData.getPlayerId();
                        System.out.println("Received player ID: " + playerId);
                    } else if (gameData.getPlayerId() != playerId) {
                        onDataReceived.accept(gameData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int getPlayerId() {
        return playerId;
    }
}