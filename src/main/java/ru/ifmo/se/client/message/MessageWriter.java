package ru.ifmo.se.client.message;

import ru.ifmo.se.commands.ClassCommand;
import ru.ifmo.se.commands.CommandName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class MessageWriter {
    private DatagramChannel channel;
    private SocketAddress address;

    public MessageWriter(DatagramChannel channel, SocketAddress address) throws IOException {
        this.channel = channel;
        this.address = address;
    }

    public void writeRequest(Object object) {
        try {
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            Throwable var3 = null;
            ClassCommand classCommand = null;
            if (object instanceof ClassCommand) {
                classCommand = (ClassCommand)object;
            }

            try {
                ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream);
                Throwable var5 = null;

                try {
                    if(classCommand != null && classCommand.getCommandName() == CommandName.EXIT){
                        channel.close();
                        System.exit(0);
                    }
                    objectStream.writeObject(object);
                    ByteBuffer objectBuffer = ByteBuffer.wrap(byteArrayStream.toByteArray());
                    this.sendDatagram(objectBuffer);
                } catch (Throwable var30) {
                    var5 = var30;
                    throw var30;
                } finally {
                    if (objectStream != null) {
                        if (var5 != null) {
                            try {
                                objectStream.close();
                            } catch (Throwable var29) {
                                var5.addSuppressed(var29);
                            }
                        } else {
                            objectStream.close();
                        }
                    }

                }
            } catch (Throwable var32) {
                var3 = var32;
                throw var32;
            } finally {
                if (byteArrayStream != null) {
                    if (var3 != null) {
                        try {
                            byteArrayStream.close();
                        } catch (Throwable var28) {
                            var3.addSuppressed(var28);
                        }
                    } else {
                        byteArrayStream.close();
                    }
                }

            }
        } catch (IOException var34) {
            System.err.println(var34.getMessage());
        }

    }

    public void sendDatagram(ByteBuffer content) throws IOException {
        this.channel.send(content, this.address);
    }
}
