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
        System.err.println("Connecting..."); // output message
        if (args.length < 2) { // if not anything args are given show message and exit
            System.err.println("Usage: java Client <address> <port number>");
            System.exit(1);
        }
        try {
            String address = args[0]; // get address, should be 127.0.0.1
            int port = Integer.parseInt(args[1]); // get port, should be 8005
            Client client = new Client(); // instantiate new client
            client.start(client, address, port); // call start on created client, and parse args
        } catch (NumberFormatException e) {
            // catch errors on incorrect port number
            System.err.println("Usage: invalid port number: " + args[0]);
            System.exit(1);
        }

    }

    public void start(Client client, String address, int port) throws Exception {
        System.err.println("Connected!\n");
        socket = new Socket(address, port); // create to end-point socket

        // input/output streams
        outObjStream = new ObjectOutputStream(socket.getOutputStream()); // create output stream from server
        inObjStream = new ObjectInputStream(socket.getInputStream()); // create input stream from client
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); // buffers input stream

        Packet recvPacket = (Packet) inObjStream.readObject(); // get packet from server
        System.out.println(recvPacket.message); // output message

        String userInput;
        Boolean exit = false;
        // (userInput = stdIn.readLine()) != null &&
        while ((userInput = stdIn.readLine()) != null) { // enter interaction loop for client, getting input from user
            if (userInput.equals("0")) { // if input 0, exit server
                exit = true;
            }
            Packet packet = new Packet(userInput); // put message into Packet
            outObjStream.writeObject(packet); // output Packet to server

            recvPacket = (Packet) inObjStream.readObject(); // get reply from server
            System.out.println(recvPacket.message); // output message from server
            if (exit == true) {
                System.out.println("Disconnecting...");
                break;
            }
        }

        this.stop();
    }

    public void stop() throws Exception {
        // close all streams
        inObjStream.close();
        outObjStream.close();
        socket.close();
        System.out.println("Disconnected");
    }
}
