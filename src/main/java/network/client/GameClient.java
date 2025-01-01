package network.client;

import com.google.gson.Gson;
import network.helper.NetworkHelper;
import network.packet.PlayerData;
import network.packet.BulletData;
import network.protocol.MessageType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public class GameClient {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final Consumer<Object> onDataReceived;
    private final NetworkHelper networkHelper;
    private final int port;
    private int playerId = -1;

    public GameClient(String ip, int port, Consumer<Object> onDataReceived) throws Exception {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ip);
        this.port = port;
        this.onDataReceived = onDataReceived;

        networkHelper = new NetworkHelper(socket);
        send(new PlayerData(MessageType.CONNECT, -1, 0, 0, null, 1));
    }

    public void send(Object data) {
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
                    Gson gson = new Gson();

                    // Пробуем определить тип данных
                    if (message.contains("BULLET_SHOT")) {
                        BulletData bulletData = gson.fromJson(message, BulletData.class);
                        onDataReceived.accept(bulletData);
                    } else {
                        PlayerData playerData = gson.fromJson(message, PlayerData.class);
                        if (playerData.getMessageType() == MessageType.CONNECT) {
                            playerId = playerData.getPlayerId();
                            System.out.println("Received player ID: " + playerId);
                        } else if (playerData.getPlayerId() != playerId) {
                            onDataReceived.accept(playerData);
                        }
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