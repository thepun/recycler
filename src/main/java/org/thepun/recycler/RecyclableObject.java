package org.thepun.recycler;

public abstract class RecyclableObject {

    private final int type;

    private int lastGlobalCursor;
    private RecycleAwareThread origin;

    public RecyclableObject(int type) {
        this.type = type;
    }

    public final void recycleBack() {
        RecycleAwareThread thread = markFreed();

        RecycleAwareThread current = RecycleAwareThread.current();
        if (current == thread) {
            current.getContext(type).addFreeObjectForLocalUse(this);
        } else {
            thread.getContext(type).addFreeObjectBackToOrigin(this);
        }
    }

    public final void recycleLocal() {
        markFreed();

        RecycleAwareThread.current().getContext(type).addFreeObjectForLocalUse(this);
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

    int getLastGlobalCursor() {
        return lastGlobalCursor;
    }

    void setLastGlobalCursor(int lastGlobalCursor) {
        this.lastGlobalCursor = lastGlobalCursor;
    }
}
