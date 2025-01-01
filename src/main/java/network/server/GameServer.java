package network.server;

import com.google.gson.Gson;
import network.packet.BulletData;
import network.packet.PlayerData;
import network.protocol.MessageType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private DatagramSocket socket;
    private ConcurrentHashMap<Integer, ClientInfo> clients;
    private int nextClientId = 1;
    private final Gson gson = new Gson();

    public GameServer(int port) throws Exception {
        socket = new DatagramSocket(port);
        clients = new ConcurrentHashMap<>();
        System.out.println("Server started on port " + port);
    }

    public void start() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                // Определяем тип сообщения
                if (message.contains("BULLET_SHOT")) {
                    BulletData bulletData = gson.fromJson(message, BulletData.class);
                    handleBulletData(bulletData, packet);
                } else {
                    PlayerData playerData = gson.fromJson(message, PlayerData.class);
                    handlePlayerData(playerData, packet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBulletData(BulletData bulletData, DatagramPacket packet) {
        // Отправляем данные о пуле всем клиентам
        String json = gson.toJson(bulletData);
        byte[] data = json.getBytes();
        clients.forEach((id, client) -> {
            if (id != bulletData.getPlayerId()) {
                try {
                    DatagramPacket outPacket = new DatagramPacket(
                        data, data.length, client.getAddress(), client.getPort()
                    );
                    socket.send(outPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handlePlayerData(PlayerData playerData, DatagramPacket packet) {
        if (playerData.getMessageType() == MessageType.CONNECT) {
            // Обработка нового подключения
            int clientId = nextClientId++;
            clients.put(clientId, new ClientInfo(packet.getAddress(), packet.getPort()));
            
            // Отправляем ID клиенту
            PlayerData response = new PlayerData(MessageType.CONNECT, clientId, 0, 0, null, 1);
            sendToClient(response, packet.getAddress(), packet.getPort());
        } else {
            // Пересылаем обновления позиции всем остальным клиентам
            String json = gson.toJson(playerData);
            byte[] data = json.getBytes();
            clients.forEach((id, client) -> {
                if (id != playerData.getPlayerId()) {
                    try {
                        DatagramPacket outPacket = new DatagramPacket(
                            data, data.length, client.getAddress(), client.getPort()
                        );
                        socket.send(outPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void sendToClient(PlayerData data, java.net.InetAddress address, int port) {
        try {
            String json = gson.toJson(data);
            byte[] buffer = json.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
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