package org.thepun.recycler;

import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class RecycleAwareThread extends Thread {

    private static final int MAX_TYPES = 16;
    private static final TypeContext<?>[] TYPE_CONTEXTS = new TypeContext[MAX_TYPES];
    private static final List<WeakReference<RecycleAwareThread>> ALL_THREADS = new ArrayList<>();

    private static int TYPES = 0;

    synchronized static <T extends RecyclableObject> TypeContext<T> registerNewType(Class<T> type, RecyclableObjectFactory<T> factory) {
        if (!Modifier.isFinal(type.getModifiers())) {
            throw new IllegalArgumentException("Only final classes supported");
        }

        if (type.getSuperclass() != RecyclableObject.class) {
            throw new IllegalArgumentException("Only direct children of RecyclableObject supported");
        }

        int newType = TYPES++;
        if (newType >= MAX_TYPES) {
            throw new IllegalStateException("Maximum amount of recyclable types reached");
        }

        // save type
        TypeContext<?> typeContext = new TypeContext<>(newType, factory);
        TYPE_CONTEXTS[newType] = typeContext;

        // add new type to all threads
        Iterator<WeakReference<RecycleAwareThread>> iterator = ALL_THREADS.iterator();
        while (iterator.hasNext()) {
            WeakReference<RecycleAwareThread> ref = iterator.next();

            RecycleAwareThread thread = ref.get();
            if (thread == null) {
                iterator.remove();
                continue;
            }

            thread.contexts[newType] = new ThreadContext<>(typeContext);
        }

        return (TypeContext<T>) typeContext;
    }

    static RecycleAwareThread currentRecycleAwareThread() {
        return (RecycleAwareThread) Thread.currentThread();
    }


    private final ThreadContext<?>[] contexts;

    public RecycleAwareThread() {
        this(null);
    }

    public RecycleAwareThread(Runnable r) {
        super(r);

        // global lock
        synchronized (RecycleAwareThread.class) {
            // fill contexts with default values
            contexts = new ThreadContext[MAX_TYPES];
            for (int i = 0; i < TYPES; i++) {
                contexts[i] = new ThreadContext<>(TYPE_CONTEXTS[i]);
            }

            // add self to list of all threads
            ALL_THREADS.add(new WeakReference<>(this));
        }
    }

    final <T extends RecyclableObject> ThreadContext<T> getContext(int type) {
        return (ThreadContext<T>) contexts[type];
    }
}
