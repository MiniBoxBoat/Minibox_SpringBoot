package com.minibox.dao.redisDao;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class Util {
    public static List<String> getClassAttrubutesName(Class clazz) {
        List<String> attributes = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            attributes.add(field.getName());
        }
        return attributes;
    }

}
