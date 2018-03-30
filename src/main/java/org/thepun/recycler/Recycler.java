package org.thepun.recycler;

public final class Recycler<T extends RecyclableObject> {

    private final int type;

    public Recycler(RecycledFactory<T> factory) {
        TypeContext<T> typeContext = RecycleAwareThread.registerNewType(factory);
        type = typeContext.getIndex();
    }

    public T getOrCreate() {
        return ThreadContext.<T>locate(type).getOrCreate();
    }

    public T create() {
        return ThreadContext.<T>locate(type).create();
    }

    public T get() {
        return ThreadContext.<T>locate(type).get();
    }

}
