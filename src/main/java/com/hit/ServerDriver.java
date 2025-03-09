package com.hit;

import com.hit.server.Server;

public class ServerDriver {
    public static void main(String[] args) {
        Server server = new Server(34567);
        new Thread(server).start();
    }
}
