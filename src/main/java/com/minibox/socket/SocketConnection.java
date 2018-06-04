package com.minibox.socket;

import com.minibox.constants.HardwareMessage;
import com.minibox.exception.ServerException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author May
 */

@Slf4j
public class SocketConnection {
    public static Map<Integer, SocketChannel> map = new ConcurrentHashMap<>();
    private static final int PORT_NUMBER = 1234;

    public static void waitToConnect() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        Selector selector = Selector.open();
        serverSocket.bind(new InetSocketAddress(PORT_NUMBER));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int n = selector.select();
            if (n == 0) {
                continue;
            }
            java.util.Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientSocket = serverChannel.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ |
                            SelectionKey.OP_WRITE);
                    map.put(1, clientSocket);
                    log.trace("客户端连接成功");
                } else if (key.isReadable()) {
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    clientSocket.close();
                    map.remove(1);
                    log.trace("客户端断开连接");
                }
            }
        }
    }

    private static void sendHexData(SocketChannel socketChannel, String data) {
        byte[] bytes = hexString2Bytes(data);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            throw new ServerException();
        }
    }

    public static void sendDataToHardware(int groupId, int boxId) {
        SocketChannel clientChanel = SocketConnection.map.get(groupId);
        sendHexData(clientChanel, intToNeedHexStringValue(HardwareMessage.START_MESSGE));
        sendHexData(clientChanel, intToNeedHexStringValue(groupId));
        sendHexData(clientChanel, intToNeedHexStringValue(HardwareMessage.OPERATION_CODE));
        sendHexData(clientChanel, intToNeedHexStringValue(boxId));
        sendHexData(clientChanel, intToNeedHexStringValue(13));
        sendHexData(clientChanel, intToNeedHexStringValue(10));
        sendHexData(clientChanel, intToNeedHexStringValue(1));
        sendHexData(clientChanel, intToNeedHexStringValue(1));
    }

    private static String intToNeedHexStringValue(int data) {
        switch (data) {
            case 1:
                return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "05";
            case 6:
                return "06";
            case 7:
                return "07";
            case 8:
                return "08";
            case 9:
                return "09";
            case 10:
                return "0a";
            case 11:
                return "0b";
            case 12:
                return "0c";
            case 13:
                return "0d";
            case 14:
                return "0e";
            case 15:
                return "0f";
            default:
                return Integer.toHexString(data);
        }
    }

    public static byte[] hexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }
}
