package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream outObjStream;
    private ObjectInputStream inObjStream;

    public static void main(String[] args) throws Exception {
        System.err.println("Connecting...");
        if (args.length < 2) {
            System.err.println("Usage: java Client <address> <port number>");
            System.exit(1);
        }
        try {
            String address = args[0];
            int port = Integer.parseInt(args[1]);
            Client client = new Client();
            client.start(client, address, port);
        } catch (NumberFormatException e) {
            System.err.println("Usage: invalid port number: " + args[0]);
            System.exit(1);
        }

    }

    public void start(Client client, String address, int port) throws Exception {
        System.err.println("Connected!\n");
        socket = new Socket(address, port);

        // input/output streams
        outObjStream = new ObjectOutputStream(socket.getOutputStream());
        inObjStream = new ObjectInputStream(socket.getInputStream());
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        Packet recvPacket = (Packet) inObjStream.readObject();
        System.out.println(recvPacket.message);

        String userInput;
        // (userInput = stdIn.readLine()) != null &&
        while ((userInput = stdIn.readLine()) != null) {

            if (userInput.equals("0")) {
                System.out.println("Disconnecting...");
                break;
            }
            Packet packet = new Packet(userInput);
            outObjStream.writeObject(packet);

            recvPacket = (Packet) inObjStream.readObject();
            System.out.println(recvPacket.message);
        }

        this.stop();
    }

    public void stop() throws Exception {

        inObjStream.close();
        outObjStream.close();
        socket.close();
        System.out.println("Disconnected");
    }
}
