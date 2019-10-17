/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shashki1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Spike
 */
public class Connection implements Runnable {

    private String IPAddress;
    private int port;
    private boolean connected = false;
    private Socket link;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private volatile Queue<Object> fronta = new LinkedList<>();

    public Connection(int portNumber, int seconds) throws SocketException, IOException {

        port = portNumber;

            ServerSocket socket = new ServerSocket(port, 1);
            socket.setSoTimeout(seconds * 1000);
            link = socket.accept();
            output = new ObjectOutputStream(link.getOutputStream());
            output.flush();

            input = new ObjectInputStream(link.getInputStream());

            IPAddress = link.getInetAddress().toString();
            connected = true;
            Thread go = new Thread(this, "ObjectQueue");
            go.setDaemon(true);
            go.start();

    }

    public Connection(int portNumber) throws SocketException, IOException {
        this(portNumber, 0);

    }

    public Connection(String address, int portNumber) throws UnknownHostException, IOException {

        IPAddress = address;
        port = portNumber;

            link = new Socket(IPAddress, port);
            output = new ObjectOutputStream(link.getOutputStream());
            output.flush();

            input = new ObjectInputStream(link.getInputStream());

            connected = true;
            Thread go = new Thread(this, "ObjectQueue");
            go.setDaemon(true);
            go.start();
    }


    
   
    @Override
    public void run() {
        while (connected) {
            try {
                Object obj = input.readObject();
                if (obj != null) {
                    fronta.offer(obj);
                }
            } catch (IOException | ClassNotFoundException e) {
                connected = false;
            }
        }
    }

    public boolean established() {
        return connected;
    }

    public Object getObject() {
        return fronta.poll();
    }

    public void sendObject(Object obj) throws IOException {
        if (connected) {
            try {
                output.reset();
                output.writeObject(obj);
                output.flush();
            } catch (IOException e) {
                connected = false;
                throw new IOException();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public String getOtherIP() {
        return (connected) ? IPAddress : "Not Connected";
    }

    static public String getMyIP() throws UnknownHostException {
            return InetAddress.getLocalHost().toString();
    }

 
    @Override
    protected void finalize() {
        try {
            output.close();
            link.close();
            super.finalize();
        } catch (Throwable t) {
        }
    }
}
