package org.thepun.recycler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

final class TypeContext<T extends RecyclableObject> {

    private final int index;
    private final Queue<T> freeObjects;
    private final RecyclableObjectFactory<T> factory;

    TypeContext(int index, RecyclableObjectFactory<T> factory) {
        this.index = index;
        this.factory = factory;

        freeObjects = new ConcurrentLinkedQueue<>();
    }

    int getIndex() {
        return index;
    }

    RecyclableObjectFactory<T> getFactory() {
        return factory;
    }

    T tryGetFreeObject() {
        return freeObjects.poll();
    }

}
