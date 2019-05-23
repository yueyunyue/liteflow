package cn.lite.flow.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by luya on 2019/5/23.
 */
public class ReflectUtils {

    private static Logger LOG = LoggerFactory.getLogger(ReflectUtils.class);


    /**
     * Returns the value by name, on the specified object. The value is
     * automatically wrapped in an object if it has a primitive type.
     *
     * @param o    the specified object
     * @param name the name of the represented field in object
     * @return the value of the represented field in object
     * @throws IllegalStateException
     */
    public static Object getValue(Object o, String name) {
        try {
            Field field = getField(o.getClass(), name);
            makeAccessible(field);
            return field.get(o);
        } catch (Exception e) {
            LOG.error("get value {} of class {} error!", name, o.getClass().getName(), e);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Returns the static value by name, on the specified {@code Class}. The value is
     * automatically wrapped in an object if it has a primitive type.
     *
     * @param clazz the specified class
     * @param name  the name of the represented field in class
     * @return the value of the represented field in class
     * @throws IllegalStateException
     */
    public static Object getStaticValue(Class<?> clazz, String name) {
        try {
            Field field = getField(clazz, name);
            makeAccessible(field);
            return field.get(null);
        } catch (Exception e) {
            LOG.error("get static value {} of class {} error!", name, clazz.getName(), e);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
     * semantics, the returned value is automatically wrapped if the underlying field
     * has a primitive type.
     * @param field the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     * @throws IllegalStateException
     */
    public static Object getField(Field field, Object target) {

        try {
            makeAccessible(field);
            return field.get(target);
        } catch (Exception e) {
            LOG.error("get field {} of class {} error!", field.getName(), target.getClass().getName(), e);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Returns a {@code Field} object that reflects the specified declared field
     * of the {@code Class} or interface represented by this {@code Class} object.
     * The {@code name} parameter is a {@code String} that specifies the
     * simple name of the desired field.
     *
     * @param clazz class
     * @param name  field name
     * @return the {@code Field} object for the specified field in this class
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        for (Class<?> cls = checkNotNull(clazz, "class"); cls != null; cls = cls.getSuperclass()) {
            try {
                Field field = cls.getDeclaredField(name);
                if (field!=null) {
                    field.setAccessible(true);
                }
                return field;
            } catch (Throwable ignored) {
            }
        }
        throw new NoSuchFieldException(clazz.getName() + "#" + name);
    }



    /**
     * Make the given field accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will
     *                     be converted to a string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
