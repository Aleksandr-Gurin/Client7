package ru.ifmo.se.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

public class ClientLauncher {
    public static void main(String[] args)  {
        InetSocketAddress address = null;
        Client client = null;
        String host = null;

        try {
            int port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                host = args[1];
                new InetSocketAddress(host, port);
            }if (host == null) {
                address = new InetSocketAddress("localhost", port);
            }else address = new InetSocketAddress(host, port);
        } catch (ArrayIndexOutOfBoundsException var10) {
            System.err.println("Необходимо указать порт(java -jar Client.jar port host)");
            System.exit(-1);
        } catch (IllegalArgumentException var11) {
            System.err.println("Порт введен неверно(java -jar Client.jar port host)");
            System.exit(-1);
        }
        try {
            client = new Client(address);
            client.start();
        } catch (InterruptedException | IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchElementException var13){
            System.out.println("Программа завершена");
        }
    }
}
