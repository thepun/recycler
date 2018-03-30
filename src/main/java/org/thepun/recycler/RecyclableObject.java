package org.thepun.recycler;

public abstract class RecyclableObject {

    private final int type;

    public RecyclableObject(int type) {
        this.type = type;
    }

    public final void recycle() {
        ThreadContext.locate(type).addFreeObject(this);
    }
}
