package org.thepun.recycler;

public interface RecycledFactory<T extends RecyclableObject> {

    T createNew(int type);


}
