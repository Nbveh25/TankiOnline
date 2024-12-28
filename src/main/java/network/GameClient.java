package network;

import com.google.gson.Gson;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public class GameClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private int playerId = -1;
    private Consumer<GameData> onDataReceived;
    private final Gson gson = new Gson();

    public GameClient(String ip, int port, Consumer<GameData> onDataReceived) throws Exception {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ip);
        this.port = port;
        this.onDataReceived = onDataReceived;

        // Запрашиваем ID у сервера
        send("connect");
    }

    public void send(GameData data) {
        try {
            String json = gson.toJson(data);
            send(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(String message) throws Exception {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    public void receive() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (message.startsWith("id:")) {
                        playerId = Integer.parseInt(message.substring(3));
                        System.out.println("Received player ID: " + playerId);
                        continue;
                    }

                    GameData gameData = gson.fromJson(message, GameData.class);
                    if (gameData.getPlayerId() != playerId) {
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