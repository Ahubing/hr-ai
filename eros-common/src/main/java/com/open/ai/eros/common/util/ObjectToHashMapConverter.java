package com.open.ai.eros.common.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ObjectToHashMapConverter {

    public static Map<String, String> convertObjectToHashMap(Object obj) {
        Map<String, String> map = new HashMap<>();

        // 获取对象的所有属性
        Field[] fields = obj.getClass().getDeclaredFields();

        try {
            // 遍历属性并将其添加到HashMap中
            for (Field field : fields) {
                field.setAccessible(true); // 设置属性的可访问性
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);
                if(fieldValue==null){
                    continue;
                }
                map.put(fieldName, fieldValue.toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static <T> void setValuesToObject(Map<String, String> map, T obj) {
        Class<?> objClass = obj.getClass();

        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                String value = map.get(field.getName());
                if(value!=null){
                    field.setAccessible(true);
                    Object convertedValue = convertValueToFieldType(field, value);
                    field.set(obj, convertedValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Object convertValueToFieldType(Field field, String fieldValue) {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            return fieldValue;
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(fieldValue);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(fieldValue);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(fieldValue);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(fieldValue);
        } else if (fieldType == float.class || fieldType == Float.class) {
            return Float.parseFloat(fieldValue);
        } else if (fieldType == byte.class || fieldType == Byte.class) {
            return Byte.parseByte(fieldValue);
        } else if (fieldType == short.class || fieldType == Short.class) {
            return Short.parseShort(fieldValue);
        } else if (fieldType == char.class || fieldType == Character.class) {
            if (fieldValue.length() == 1) {
                return fieldValue.charAt(0);
            }
        } else if (fieldType == List.class || fieldType == ArrayList.class) {
            String[] values = fieldValue.split(",");
            return new ArrayList<>(Arrays.asList(values));
        } else if (fieldType == Set.class || fieldType == HashSet.class) {
            String[] values = fieldValue.split(",");
            Set<String> set = new HashSet<>(Arrays.asList(values));
            return set;
        } else if (fieldType.isEnum()) {
            return Enum.valueOf((Class<Enum>) fieldType, fieldValue);
        } else if (fieldType == LocalDate.class) {
            return LocalDate.parse(fieldValue);
        } else if (fieldType == LocalDateTime.class) {
            return LocalDateTime.parse(fieldValue);
        }
        return fieldValue;
    }


}
