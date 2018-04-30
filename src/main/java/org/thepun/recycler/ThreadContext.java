package org.thepun.recycler;

import io.github.thepun.unsafe.MemoryFence;
import io.github.thepun.unsafe.ObjectMemory;
import sun.misc.Contended;

final class ThreadContext {

    private static final int MAX_LOCAL_FREE = 256;
    private static final int MAX_OTHER_FREE = 1024;
    private static final int MAX_OTHER_FREE_MASK = MAX_OTHER_FREE - 1;
    private static final long FREE_OTHER_WRITERS_FIELD_OFFSET = ObjectMemory.fieldOffset(ThreadContext.class, "freeOtherWriters");

    private final int registeredType;
    private final TypeContext typeContext;
    private final RecyclableObject[] freeLocal;
    private final RecyclableObject[] freeOther;
    private final RecyclableObjectFactory<?> factory;

    @Contended("local")
    private int freeLocalCount;

    @Contended("reader")
    private long freeOtherReader;

    @Contended("writer")
    private long freeOtherWriters;

    ThreadContext(TypeContext typeContext) {
        this.typeContext = typeContext;

        registeredType = typeContext.getIndex();
        factory = typeContext.getFactory();

        freeLocal = new RecyclableObject[MAX_LOCAL_FREE];
        freeOther = new RecyclableObject[MAX_OTHER_FREE];
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

        MemoryFence.load(); // do not reorder these operations before local objects check because it will require separate cache line load
        long localFreeOtherReader = freeOtherReader;
        long localFreeOtherWriters = freeOtherWriters;
        if (localFreeOtherWriters > localFreeOtherReader) {
            int index = (int) (localFreeOtherReader & MAX_OTHER_FREE_MASK);
            RecyclableObject object = freeOther[index];
            if (object != null) { // other thread finished writing to the cell
                freeOther[index] = null;
                localFreeOtherReader++;
                MemoryFence.store(); // do not reorder index increase before clear
                freeOtherReader = localFreeOtherReader;
                return object;
            }
        }

        RecyclableObject object = typeContext.tryGetFreeGlobalObject();
        if (object == null) {
            object = factory.createNew(registeredType);
        }

        return object;
    }

    void addFreeObjectBackToOrigin(RecyclableObject object) {
        MemoryFence.store(); // ensure we stored everything before putting the object back

        long localFreeOtherReader = freeOtherReader;
        long localFreeOtherWriters = freeOtherWriters;
        if (localFreeOtherWriters < localFreeOtherReader + MAX_OTHER_FREE) {
            // if we are not able to put object back to origin in one step then just send it to global pool
            if (ObjectMemory.compareAndSwapLong(this, FREE_OTHER_WRITERS_FIELD_OFFSET, localFreeOtherWriters, localFreeOtherWriters + 1)) {
                int index = (int) (localFreeOtherWriters & MAX_OTHER_FREE_MASK);
                freeOther[index] = object; // we write the object after the cursor increase so on a reader side we should check that the cell is not null
                return;
            }
        }

        typeContext.addFreeObjectForGlobalUse(object);
    }

    void addFreeObjectForLocalUse(RecyclableObject object) {
        int localFreeLocalCount = freeLocalCount;
        if (localFreeLocalCount < MAX_LOCAL_FREE) {
            localFreeLocalCount++;
            freeLocal[localFreeLocalCount] = object;
            freeLocalCount = localFreeLocalCount;
        } else {
            typeContext.addFreeObjectForGlobalUse(object);
        }
    }
}
