package org.thepun.recycler;

public final class RecycleAwareThreadImpl extends Thread implements RecycleAwareThread {

    private ThreadContext[] contexts;

    public RecycleAwareThreadImpl() {
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(Runnable target) {
        super(target);
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(ThreadGroup group, Runnable target) {
        super(group, target);
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(String name) {
        super(name);
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(ThreadGroup group, String name) {
        super(group, name);
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(Runnable target, String name) {
        super(target, name);
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        ThreadContext.registerThread(this);
    }

    public RecycleAwareThreadImpl(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        ThreadContext.registerThread(this);
    }

    @Override
    public ThreadContext getContext(int type) {
        return contexts[type];
    }

    @Override
    public void setContext(int type, ThreadContext threadContext) {
        contexts[type] = threadContext;
    }

    @Override
    public void initContexts(ThreadContext[] threadContexts) {
        contexts = threadContexts;
    }
}
