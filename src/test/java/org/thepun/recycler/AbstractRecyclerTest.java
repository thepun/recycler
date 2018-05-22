package org.thepun.recycler;

import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.fail;

abstract class AbstractRecyclerTest {

    private static int type;
    private static ExecutorService executor1;
    private static ExecutorService executor2;

    @BeforeAll
    static void registerType() {
        executor1 = Executors.newSingleThreadExecutor(RecycleAwareThreadImpl::new);
        executor2 = Executors.newSingleThreadExecutor(RecycleAwareThreadImpl::new);
        type = Recycler.registerType(TestRecyclableObject.class, TestRecyclableObject::new);
    }

    protected int getType() {
        return type;
    }

    protected void executeInRecyclerThread1(Runnable runnable) {
        try {
            executor1.submit(runnable).get();
        } catch (Exception e) {
            fail(e);
        }
    }

    protected <T> T executeInRecyclerThread1(Callable<T> callable) {
        try {
            return executor1.submit(callable).get();
        } catch (Exception e) {
            fail(e);
        }

        return null;
    }

    protected void executeInRecyclerThread2(Runnable runnable) {
        try {
            executor2.submit(runnable).get();
        } catch (Exception e) {
            fail(e);
        }
    }

    protected <T> T executeInRecyclerThread2(Callable<T> callable) {
        try {
            return executor2.submit(callable).get();
        } catch (Exception e) {
            fail(e);
        }

        return null;
    }
}
