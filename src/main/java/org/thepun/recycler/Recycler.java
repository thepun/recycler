package org.thepun.recycler;

import java.lang.reflect.Modifier;

public final class Recycler {

    public static <T  extends RecyclableObject> int registerType(Class<T> type, RecyclableObjectFactory<T> factory) {
        if (!Modifier.isFinal(type.getModifiers())) {
            throw new IllegalArgumentException("Only final classes supported: " + type.getName());
        }

        if (type.getSuperclass() != RecyclableObject.class) {
            throw new IllegalArgumentException("Only direct children of RecyclableObject supported: " + type.getName());
        }

        TypeContext typeContext = TypeContext.registerNewType(factory);
        RecycleAwareThread.registerNewTypeToAll(typeContext);
        return typeContext.getIndex();
    }

    public static <T extends RecyclableObject> T get(int registeredType) {
        return (T) RecycleAwareThread.current().getContext(registeredType).get();
    }

    public static <T extends RecyclableObject> T create(int registeredType) {
        return (T) RecycleAwareThread.current().getContext(registeredType).create();
    }


    private Recycler() {
    }
}
