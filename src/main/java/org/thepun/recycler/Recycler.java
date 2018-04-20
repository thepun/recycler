package org.thepun.recycler;

public final class Recycler<T extends RecyclableObject> {

    private final int registeredType;

    public Recycler(Class<T> type, RecyclableObjectFactory<T> factory) {
        registeredType = registerType(type, factory);
    }

    public T get() {
        return get(registeredType);
    }

    public T create() {
        return create(registeredType);
    }

    public static <T  extends RecyclableObject> int registerType(Class<T> type, RecyclableObjectFactory<T> factory) {
        return  RecycleAwareThread.registerNewType(type, factory).getIndex();
    }

    public static <T extends RecyclableObject> T get(int registeredType) {
        return ThreadContext.<T>locate(registeredType).get();
    }

    public static <T extends RecyclableObject> T create(int registeredType) {
        return ThreadContext.<T>locate(registeredType).create();
    }
}
