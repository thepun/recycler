package org.thepun.recycler;

public abstract class RecyclableObject {

    private final int type;

    private int lastGlobalCursor;
    private ThreadContext origin;

    public RecyclableObject(int type) {
        this.type = type;
    }

    public final void recycleBack() {
        ThreadContext threadContext = markFreed();

        RecycleAwareThread current = RecycleAwareThread.current();
        ThreadContext currentThreadContext = current.getContext(type);
        if (currentThreadContext == threadContext) {
            currentThreadContext.addFreeObjectForLocalUse(this);
        } else {
            threadContext.addFreeObjectBackToOrigin(this);
        }
    }

    public final void recycleLocal() {
        markFreed();

        RecycleAwareThread.current().getContext(type).addFreeObjectForLocalUse(this);
    }

    ThreadContext markFreed() {
        ThreadContext originLocal = origin;
        origin = null;

        if (originLocal == null) {
            throw new IllegalStateException("Object is already freed: " + this);
        }

        return originLocal;
    }

    void markUsed(ThreadContext thread) {
        origin = thread;
    }

    int getLastGlobalCursor() {
        return lastGlobalCursor;
    }

    void setLastGlobalCursor(int lastGlobalCursor) {
        this.lastGlobalCursor = lastGlobalCursor;
    }
}
