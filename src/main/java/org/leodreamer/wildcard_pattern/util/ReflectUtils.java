package org.leodreamer.wildcard_pattern.util;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ReflectUtils {

    public static <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(obj));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field value: " + fieldName, e);
        }
    }

    public static List<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        List<Class<?>> classes = new ArrayList<>();
        Type annoType = Type.getType(annotation);
        for (var data : ModList.get().getAllScanData()) {
            for (var annoData : data.getAnnotations()) {
                Type a = annoData.annotationType();
                if (Objects.equals(a, annoType)) {
                    try {
                        classes.add(Class.forName(annoData.memberName()));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return classes;
    }

    public static <A extends Annotation,
        F> Map<A, F> getStaticFieldsWithAnnotation(Class<?> clazz, Class<A> annotationType, Class<F> fieldType) {
        Map<A, F> result = new HashMap<>();
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationType)) {
                A anno = field.getAnnotation(annotationType);
                field.setAccessible(true);
                try {
                    F key = fieldType.cast(field.get(null));
                    if (key == null) {
                        throw new RuntimeException(
                            "Field " + field.getName() + " in class " + clazz.getName() +
                                " is null. Probably not initialized yet as it is not static."
                        );
                    }
                    result.put(anno, key);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}
