package ru.ifmo.se.client.message;

import ru.ifmo.se.musicians.MusicBand;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;
import java.util.List;

public class MessageReader {
    private DatagramChannel channel;
    private boolean answerWasRead = true;

    public MessageReader(DatagramChannel channel) {
        this.channel = channel;
    }


    public String readAnswer() throws IOException, ClassNotFoundException {
        ByteBuffer buf = ByteBuffer.allocate(1000000);
        this.receiveDatagram(buf);
        ((Buffer)buf).flip();
        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);
        if (bytes.length < 1) {
            return null;
        } else {
            if (bytes.length < 1000000) {
                answerWasRead = false;
                return this.readObj(this.processResponse(bytes));
            } else {
                return "Файл слишком большой";
            }
        }
    }

    private Object processResponse(byte[] petitionBytes) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(petitionBytes));
        Throwable var3 = null;

        Object var5;
        try {
            Object obj = stream.readObject();
            if (obj == null) {
                throw new ClassNotFoundException();
            }

            var5 = obj;
        } catch (Throwable var14) {
            var3 = var14;
            throw var14;
        } finally {
            if (stream != null) {
                if (var3 != null) {
                    try {
                        stream.close();
                    } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                    }
                } else {
                    stream.close();
                }
            }

        }
        return var5;
    }

    public void receiveDatagram(ByteBuffer buffer) throws IOException {
        SocketAddress ret = this.channel.receive(buffer);
    }

    public String readObj(Object obj) throws ClassNotFoundException {
        if (obj instanceof StringBuilder) {
            return obj.toString();
        }else if (obj instanceof String){
            return (String) obj;
        }
        else if (obj instanceof MusicBand){
            return obj.toString();
        }
        else {
            if (!(obj instanceof List)) {
                throw new ClassNotFoundException();
            }

            if (((List) obj).size() == 0) {
                return "Элементов не найдено";
            }

            if (((List) obj).get(0) instanceof MusicBand) {
                StringBuilder result = new StringBuilder();
                ((List) obj).forEach((e) -> {
                    result.append(e.toString()).append("\n");
                });
                return result.toString();
            } else {
                Iterator var2 = ((List) obj).iterator();
                StringBuilder result = new StringBuilder();

                while (var2.hasNext()) {
                    Object objFromScript = var2.next();
                    if (objFromScript instanceof String) {
                        result.append(objFromScript).append("\n");
                    } else if (objFromScript instanceof List) {
                        ((List) objFromScript).stream().forEach((e) -> {
                            result.append(e.toString()).append("\n");
                        });
                    }
                }
                return result.toString();
            }
        }
    }

    public void setAnswerWasRead(boolean answerWasRead) {
        this.answerWasRead = answerWasRead;
    }

    public boolean isAnswerWasRead() {
        return answerWasRead;
    }
}
