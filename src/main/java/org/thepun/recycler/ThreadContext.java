package org.thepun.recycler;

final class ThreadContext {

    private static final int MAX_LOCAL_FREE = 1024;
    private static final int MAX_LOCAL_FREE_MASK = MAX_LOCAL_FREE - 1;


    private final int registeredType;
    private final TypeContext typeContext;
    private final RecyclableObject[] freeLocal;
    private final RecyclableObject[] freeOther;
    private final RecyclableObjectFactory<?> factory;

    private int freeLocalCount;
    private long freeOtherReader;

    // TODO: offset value
    private long freeOtherWriters;

    ThreadContext(TypeContext typeContext) {
        this.typeContext = typeContext;

        registeredType = typeContext.getIndex();
        factory = typeContext.getFactory();

        freeLocal = new RecyclableObject[MAX_LOCAL_FREE];
        freeOther = new RecyclableObject[MAX_LOCAL_FREE];
    }

    RecyclableObject create() {
        return factory.createNew(registeredType);
    }

    // TODO: add barrier
    RecyclableObject get() {
        if (freeLocalCount > 0) {
            int freeIndex = --freeLocalCount;
            RecyclableObject object = freeLocal[freeIndex];
            freeLocal[freeIndex] = null;
            return object;
        }

        long localFreeOtherReader = freeOtherReader;
        long localFreeOtherWriters = freeOtherWriters;
        if (localFreeOtherWriters > localFreeOtherReader) {
            int index = (int) (localFreeOtherReader & MAX_LOCAL_FREE_MASK);
            freeOther[index] = null;
            localFreeOtherReader++;
            freeOtherReader = localFreeOtherReader;
        }

        RecyclableObject object = typeContext.tryGetFreeGlobalObject();

        if (object == null) {
            object = factory.createNew(registeredType);
        }

        return object;
    }

    // TODO: add barrier
    void addFreeObjectBackToOrigin(RecyclableObject object) {


    }

    // TODO: add barrier
    void addFreeObjectForLocalUse(RecyclableObject object) {
        if (freeLocalCount < MAX_LOCAL_FREE) {
            freeLocal[freeLocalCount++] = object;
        } else {
            typeContext.addFreeObjectForGlobalUse(object);
        }
    }
}
