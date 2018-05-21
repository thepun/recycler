package org.thepun.recycler;

import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.fail;

abstract class AbstractRecyclerTest {

    private static int type;
    private static ExecutorService executor;

    @BeforeAll
    static void registerType() {
        executor = Executors.newSingleThreadExecutor(RecycleAwareThreadImpl::new);
        type = Recycler.registerType(TestRecyclableObject.class, TestRecyclableObject::new);
    }

    protected int getType() {
        return type;
    }

    protected <T> T executeInRecycleAwareThread(Callable<T> callable) {
        try {
            return executor.submit(callable).get();
        } catch (Exception e) {
            fail(e);
        }

        return null;
    }
}
