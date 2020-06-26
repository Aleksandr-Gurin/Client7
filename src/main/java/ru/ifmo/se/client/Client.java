package ru.ifmo.se.client;

import ru.ifmo.se.client.message.MessageReader;
import ru.ifmo.se.client.message.MessageWriter;
import ru.ifmo.se.commands.ClassCommand;
import ru.ifmo.se.model.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {
    private MessageReader messageReader;
    private SocketAddress address;
    private DatagramChannel channel = DatagramChannel.open();
    private boolean connect = false;
    private Reader reader = new Reader();

    public Client(InetSocketAddress inetSocketAddress) throws IOException {
        address = inetSocketAddress;
        channel.configureBlocking(false);
    }

    //Создает сокет, ридер райтер и запускает логику
    public void start() throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
        MessageWriter messageWriter = new MessageWriter(channel, address);
        this.messageReader = new MessageReader(channel);
        String answer;
        User user = reader.readUser(new Scanner(System.in, "UTF-8"));
        while (true) {
            metka: while (true) {
                if (connect) {
                    ClassCommand classCommand = reader.readCommand(new Scanner(System.in, "UTF-8"));
                    classCommand.setUser(user);
                    messageWriter.writeRequest(classCommand);
                    messageReader.setAnswerWasRead(true);
                    long start = System.currentTimeMillis();
                    while (messageReader.isAnswerWasRead()) {
                        answer = messageReader.readAnswer();
                        if (answer != null) {
                            System.out.println(answer);
                            messageReader.setAnswerWasRead(false);
                        }
                        if (messageReader.isAnswerWasRead()&&System.currentTimeMillis()-start>3000L){
                            System.out.println("Ожидается получение ответа на запрос");
                            Thread.sleep(1000);
                        }
                        if (messageReader.isAnswerWasRead()&&System.currentTimeMillis()-start>10000L){
                            connect = false;
                            continue metka;
                        }
                    }
                } else {
                    System.out.println("Идет подключение к серверу");
                    messageWriter.writeRequest(user);
                    Thread.sleep(1000);
                    String answer1 = messageReader.readAnswer();
                    if (answer1 != null && answer1.equals("connect")) {
                        System.out.println("Connect successful");
                        connect = true;
                    }else if(answer1 != null){
                        System.out.println(answer1);
                        System.out.println("Повторите попытку");
                        user = reader.readUser(new Scanner(System.in, "UTF-8"));
                    }
                }
            }
        }
    }
}
