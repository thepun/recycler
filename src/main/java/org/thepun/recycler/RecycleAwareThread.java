package org.thepun.recycler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecycleAwareThread extends Thread {

    private static final List<WeakReference<RecycleAwareThread>> ALL_THREADS = new ArrayList<>(Runtime.getRuntime().availableProcessors() * 2);

    static void registerNewTypeToAll(TypeContext typeContext) {
        synchronized (ALL_THREADS) {
            // add new type to all threads
            Iterator<WeakReference<RecycleAwareThread>> iterator = ALL_THREADS.iterator();
            while (iterator.hasNext()) {
                WeakReference<RecycleAwareThread> ref = iterator.next();

                RecycleAwareThread thread = ref.get();
                if (thread == null) {
                    iterator.remove();
                    continue;
                }

                thread.contexts[typeContext.getIndex()] = new ThreadContext(typeContext);
            }
        }
    }

    static RecycleAwareThread current() {
        return (RecycleAwareThread) Thread.currentThread();
    }


    private final ThreadContext[] contexts;

    public RecycleAwareThread() {
        this(null);
    }

    public RecycleAwareThread(Runnable r) {
        super(r);

        // global lock
        synchronized (ALL_THREADS) {
            // fill contexts with default values
            contexts = new ThreadContext[TypeContext.getMaxPossibleRegisteredType()];
            for (int i = 0; i < contexts.length; i++) {
                contexts[i] = new ThreadContext(TypeContext.get(i));
            }

            // add self to list of all threads
            ALL_THREADS.add(new WeakReference<>(this));
        }
    }

    final ThreadContext getContext(int type) {
        return contexts[type];
    }
}
