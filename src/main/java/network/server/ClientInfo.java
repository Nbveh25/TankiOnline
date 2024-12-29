package network.server;

import java.net.InetAddress;

public class ClientInfo {
    InetAddress address;
    int port;
    int playerId;

    ClientInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
}