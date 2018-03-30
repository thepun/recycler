package org.thepun.recycler;

import java.util.LinkedList;
import java.util.Queue;

final class ThreadContext<T extends RecyclableObject> {

    static <T extends RecyclableObject> ThreadContext<T> locate(int type) {
        return RecycleAwareThread.currentRecycleAwareThread().getContext(type);
    }


    private final int type;
    private final Queue<T> freeObjects;
    private final TypeContext<T> typeContext;
    private final RecycledFactory<T> factory;

    ThreadContext(TypeContext<T> typeContext) {
        this.typeContext = typeContext;

        type = typeContext.getIndex();
        factory = typeContext.getFactory();

        freeObjects = new LinkedList<>();
    }

    T create() {
        return factory.createNew(type);
    }

    T get() {
        T object = freeObjects.poll();

        if (object == null) {
            object = typeContext.tryGetFreeObject();
        }

        return object;
    }

    T getOrCreate() {
        T object = freeObjects.poll();

        if (object == null) {
            object = typeContext.tryGetFreeObject();
        }

        if (object == null) {
            object = factory.createNew(type);
        }

        return object;
    }

    void addFreeObject(T object) {
        freeObjects.add(object);
    }

}
