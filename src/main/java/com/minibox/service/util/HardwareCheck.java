package com.minibox.service.util;

import com.minibox.constants.ExceptionMessage;
import com.minibox.exception.HardwareException;
import com.minibox.socket.SocketConnection;

public class HardwareCheck {
    public static void checkHardwareIsOnline(int groupId){
        System.out.println(SocketConnection.map.size());
        SocketConnection.map.entrySet().forEach(System.out::println);
        if (SocketConnection.map.get(groupId) == null) {
            throw new HardwareException(ExceptionMessage.HARDWARE_NOT_ONLINE);
        }
    }
}
