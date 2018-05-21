package org.thepun.recycler;

public interface RecycleAwareThread {

    static RecycleAwareThread current() {
        return (RecycleAwareThread) Thread.currentThread();
    }

    static void register(RecycleAwareThread recycleAwareThread) {
        ThreadContext.registerThread(recycleAwareThread);
    }


    ThreadContext getContext(int type);

    void setContext(int type, ThreadContext threadContext);

    void initContexts(ThreadContext[] threadContexts);

}
