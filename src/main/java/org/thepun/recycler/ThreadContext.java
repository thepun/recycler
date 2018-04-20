package org.thepun.recycler;

final class ThreadContext {

    private static final int MAX_LOCAL_FREE = 1024;


    private final int registeredType;
    private final RecyclableObject[] freeLocal;
    private final RecyclableObject[] freeOther;
    private final TypeContext typeContext;
    private final RecyclableObjectFactory<?> factory;

    private int freeLocalCount;
    private int freeOtherCount;

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

    RecyclableObject get() {
        if (freeLocalCount > 0) {
            int freeIndex = --freeLocalCount;
            RecyclableObject object = freeLocal[freeIndex];
            freeLocal[freeIndex] = null;
            return object;
        }

        RecyclableObject object = typeContext.tryGetFreeGlobalObject();

        if (object == null) {
            object = factory.createNew(registeredType);
        }

        return object;
    }

    void addFreeObjectBackToOrigin(RecyclableObject object) {

        //free.add(object);
    }

    void addFreeObjectForLocalUse(RecyclableObject object) {
        //free.add(object);
    }


}
