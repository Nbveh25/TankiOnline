package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private DatagramSocket socket;
    private Map<Integer, ClientInfo> clients;
    private int nextPlayerId = 1;

    public GameServer(int port) throws Exception {
        socket = new DatagramSocket(port);
        clients = new HashMap<>();
        System.out.println("Server started on port " + port);
    }

    public void start() {
        byte[] buffer = new byte[1024];

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Новый клиент подключается
                String message = new String(packet.getData(), 0, packet.getLength());
                if (message.equals("connect")) {
                    int playerId = nextPlayerId++;
                    clients.put(playerId, new ClientInfo(packet.getAddress(), packet.getPort()));

                    // Отправляем ID игроку
                    String response = "id:" + playerId;
                    socket.send(new DatagramPacket(
                            response.getBytes(),
                            response.length(),
                            packet.getAddress(),
                            packet.getPort()
                    ));
                    continue;
                }

                // Пересылаем данные всем клиентам
                for (ClientInfo client : clients.values()) {
                    socket.send(new DatagramPacket(
                            packet.getData(),
                            packet.getLength(),
                            client.address,
                            client.port
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientInfo {
        java.net.InetAddress address;
        int port;

        ClientInfo(java.net.InetAddress address, int port) {
            this.address = address;
            this.port = port;
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