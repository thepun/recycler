package org.thepun.recycler;

import java.lang.reflect.Modifier;
import java.util.concurrent.locks.ReentrantLock;

public final class Recycler {

    static final ReentrantLock GLOBAL_LOCK = new ReentrantLock();

    public static <T  extends RecyclableObject> int registerType(Class<T> type, RecyclableObjectFactory<T> factory) {
        if (!Modifier.isFinal(type.getModifiers())) {
            throw new IllegalArgumentException("Only final classes supported: " + type.getName());
        }

        if (type.getSuperclass() != RecyclableObject.class) {
            throw new IllegalArgumentException("Only direct children of RecyclableObject supported: " + type.getName());
        }

        TypeContext typeContext = TypeContext.registerNewType(factory);
        ThreadContext.registerNewTypeToAll(typeContext);
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
