package org.thepun.recycler;

public abstract class RecyclableObject {

    private final int type;

    private RecycleAwareThread origin;

    public RecyclableObject(int type) {
        this.type = type;
    }

    public final void recycleBack() {
        RecycleAwareThread thread = markFreed();

        thread.getContext(type).addFreeObjectBackToOrigin(this);
    }

    public final void recycleLocal() {
        markFreed();

        RecycleAwareThread.current().getContext(type).addFreeObjectForLocalUse(this);
    }

    public final void recycleGlobal() {
        markFreed();

        TypeContext.get(type).addFreeObjectForGlobalUse(this);
    }

    RecycleAwareThread markFreed() {
        RecycleAwareThread originLocal = origin;
        origin = null;

        if (originLocal == null) {
            throw new IllegalStateException("Object is already freed: " + this);
        }

        return originLocal;
    }

    void markUsed(RecycleAwareThread thread) {
        origin = thread;
    }
}
