package org.thepun.recycler;

import java.util.concurrent.ConcurrentLinkedQueue;

final class TypeContext {

    private static final int MAX_TYPES = 16;
    private static final TypeContext[] TYPE_CONTEXTS = new TypeContext[MAX_TYPES];

    private static int TYPES = 0;

    static int getMaxPossibleRegisteredType() {
        return MAX_TYPES;
    }

    static TypeContext get(int registeredType) {
        return TYPE_CONTEXTS[registeredType];
    }

    static TypeContext registerNewType(RecyclableObjectFactory<?> factory) {
        synchronized (TYPE_CONTEXTS) {
            int newRegisteredType = TYPES++;
            if (newRegisteredType >= MAX_TYPES) {
                throw new IllegalStateException("Maximum amount of recyclable types reached");
            }

            TypeContext typeContext = new TypeContext(newRegisteredType, factory);
            TYPE_CONTEXTS[newRegisteredType] = typeContext;
            return typeContext;
        }
    }


    private final int index;
    private final RecyclableObjectFactory<?> factory;
    private final ConcurrentLinkedQueue<? extends RecyclableObject> free;

    private TypeContext(int index, RecyclableObjectFactory<?> factory) {
        this.index = index;
        this.factory = factory;

        free = new ConcurrentLinkedQueue<>();
    }

    int getIndex() {
        return index;
    }

    RecyclableObjectFactory<?> getFactory() {
        return factory;
    }

    RecyclableObject tryGetFreeGlobalObject() {
        return free.poll();
    }

    void addFreeObjectForGlobalUse(RecyclableObject object) {
        //free.add(object);
    }

}
