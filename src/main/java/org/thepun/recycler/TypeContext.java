package org.thepun.recycler;

import io.github.thepun.unsafe.ArrayMemory;

// TODO: implement more predictable latency
final class TypeContext {

    static final Object GLOBAL_LOCK = new Object();

    private static final int MAX_TYPES = 16;
    private static final int MAX_FREE = 1024;
    private static final int MAX_FREE_MASK = MAX_FREE - 1;
    private static final TypeContext[] TYPE_CONTEXTS = new TypeContext[MAX_TYPES];

    private static int TYPES = 0;

    static int getCurrentlyRegisteredTypes() {
        return TYPES;
    }

    static int getMaxPossibleRegisteredTypes() {
        return MAX_TYPES;
    }

    static TypeContext get(int registeredType) {
        return TYPE_CONTEXTS[registeredType];
    }

    static TypeContext registerNewType(RecyclableObjectFactory<?> factory) {
        synchronized (GLOBAL_LOCK) {
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
    private final RecyclableObject[] free;
    private final RecyclableObjectFactory<?> factory;

    private TypeContext(int index, RecyclableObjectFactory<?> factory) {
        this.index = index;
        this.factory = factory;

        free = new RecyclableObject[MAX_FREE];
    }

    int getIndex() {
        return index;
    }

    RecyclableObjectFactory<?> getFactory() {
        return factory;
    }

    RecyclableObject tryGetFreeGlobalObject(int cursor) {
        int maxCursor = cursor + MAX_FREE;

        cursor++;

        RecyclableObject object;
        for (;;) {
            object = free[cursor & MAX_FREE_MASK];
            if (object != null) {
                if (ArrayMemory.compareAndSwapObject(free, cursor, object, null)) {
                    object.setLastGlobalCursor(cursor);
                    return object;
                } else {
                    cursor += 16;
                    continue;
                }
            }

            cursor++;
            if (cursor > maxCursor) {
                return null;
            }
        }
    }

    int addFreeObjectForGlobalUse(int cursor, RecyclableObject object) {
        int maxCursor = cursor + MAX_FREE;

        cursor++;

        RecyclableObject place;
        for (;;) {
            place = free[cursor & MAX_FREE_MASK];
            if (place == null) {
                if (ArrayMemory.compareAndSwapObject(free, cursor, null, object)) {
                    return cursor;
                } else {
                    cursor += 16;
                    continue;
                }
            }

            cursor++;
            if (cursor > maxCursor) {
                return cursor;
            }
        }
    }
}
