package com.hit.server;

import com.hit.controller.ControllerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server class listens on a specified port and spawns a new thread (via HandleRequest)
 * for each incoming client connection.
 */
public class Server implements Runnable {
    private final ServerSocket serverSocket;
    private final ControllerFactory controllerFactory;

    /**
     * Constructs the Server with the given port.
     *
     * @param port the port to listen on
     */
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            controllerFactory = new ControllerFactory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the server: initializes all components and continuously listens for incoming connections.
     */
    @Override
    public void run() {
        System.out.println("Server is running on port " + serverSocket.getLocalPort());
        while (true) {
            try {
                // Wait for a client to connect.
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                // Create a new thread to handle the request.
                new Thread(new HandleRequest(clientSocket, controllerFactory)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
