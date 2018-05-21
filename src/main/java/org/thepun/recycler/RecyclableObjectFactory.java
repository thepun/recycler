package org.thepun.recycler;

public interface RecyclableObjectFactory<T extends RecyclableObject> {

    T createNew(int type);

}
