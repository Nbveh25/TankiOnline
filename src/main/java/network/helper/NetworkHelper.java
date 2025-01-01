package network.helper;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkHelper {
    private final DatagramSocket socket;
    private final Gson gson;

    public NetworkHelper(DatagramSocket socket) {
        this.socket = socket;
        this.gson = new Gson();
    }

    public void send(Object data, InetAddress address, int port) {
        try {
            String json = gson.toJson(data);
            byte[] buffer = json.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}