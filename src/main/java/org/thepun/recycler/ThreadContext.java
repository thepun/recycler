package org.thepun.recycler;

import io.github.thepun.unsafe.MemoryFence;
import io.github.thepun.unsafe.ObjectMemory;
import sun.misc.Contended;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.thepun.recycler.Recycler.GLOBAL_LOCK;

public final class ThreadContext {

    private static final List<WeakReference<RecycleAwareThread>> ALL_THREADS = new ArrayList<>(Runtime.getRuntime().availableProcessors() * 2);

    private static final int GLOBAL_SPACE_OFFSET = PropUtil.getPositiveInt("org.thepun.recycler.globalSpaceOffset", 256);
    private static final int MAX_LOCAL_FREE = PropUtil.getPositiveInt("org.thepun.recycler.maxLocalFree", 16);
    private static final int MAX_OTHER_FREE = PropUtil.getPositiveInt("org.thepun.recycler.maxOtherFree", 1024);
    private static final int MAX_OTHER_FREE_MASK = MAX_OTHER_FREE - 1;
    private static final long FREE_OTHER_WRITERS_FIELD_OFFSET = ObjectMemory.fieldOffset(ThreadContext.class, "freeOtherWriters");

    static void registerNewTypeToAll(TypeContext typeContext) {
        GLOBAL_LOCK.lock();
        try {
            // add new type to all threads
            Iterator<WeakReference<RecycleAwareThread>> iterator = ALL_THREADS.iterator();
            while (iterator.hasNext()) {
                WeakReference<RecycleAwareThread> ref = iterator.next();

                RecycleAwareThread thread = ref.get();
                if (thread == null) {
                    iterator.remove();
                    continue;
                }

                thread.setContext(typeContext.getIndex(), new ThreadContext(typeContext));
            }

            MemoryFence.full();
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }

    static void registerThread(RecycleAwareThread recycleAwareThread) {
        GLOBAL_LOCK.lock();
        try {
            int currentlyRegisteredTypes = TypeContext.getCurrentlyRegisteredTypes();

            // fill contexts with default values
            ThreadContext[] newContexts = new ThreadContext[TypeContext.getMaxPossibleRegisteredTypes()];
            for (int i = 0; i < currentlyRegisteredTypes; i++) {
                newContexts[i] = new ThreadContext(TypeContext.get(i));
            }
            recycleAwareThread.initContexts(newContexts);

            // add self to list of all threads
            ALL_THREADS.add(new WeakReference<>(recycleAwareThread));

            MemoryFence.full();
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }


    private final int registeredType;
    private final TypeContext typeContext;
    private final RecyclableObject[] freeLocal;
    private final RecyclableObject[] freeOther;
    private final RecyclableObjectFactory<?> factory;

    @Contended("local")
    private int freeLocalCount;
    @Contended("local")
    private int globalReaderCursor;
    @Contended("local")
    private int globalWriterCursor;

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

        // TODO: implement local buffer fulfill from others array
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

        RecyclableObject object = typeContext.tryGetFreeGlobalObject(globalReaderCursor);
        if (object == null) {
            object = factory.createNew(registeredType);
            globalReaderCursor += GLOBAL_SPACE_OFFSET;
        } else {
            globalReaderCursor = object.getLastGlobalCursor();
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

        globalWriterCursor = typeContext.addFreeObjectForGlobalUse(globalWriterCursor, object);
    }

    void addFreeObjectForLocalUse(RecyclableObject object) {
        int localFreeLocalCount = freeLocalCount;
        if (localFreeLocalCount < MAX_LOCAL_FREE) {
            localFreeLocalCount++;
            freeLocal[localFreeLocalCount] = object;
            freeLocalCount = localFreeLocalCount;
        } else {
            globalWriterCursor = typeContext.addFreeObjectForGlobalUse(globalWriterCursor, object);
        }
    }
}
