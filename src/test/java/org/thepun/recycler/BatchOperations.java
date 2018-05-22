package org.thepun.recycler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.fail;

class BatchOperations extends AbstractRecyclerTest {

    @Test
    void passObjects() throws InterruptedException {
        TestRecyclableObject.clearCounter();

        CountDownLatch allReceived = new CountDownLatch(1);

        BlockingQueue<TestRecyclableObject> queue = new ArrayBlockingQueue<>(1000000);

        asyncInRecyclerThread1(() -> {
            int type = getType();
            for (int i = 0; i < 100000000; i++) {
                queue.add(Recycler.get(type));
            }
        });

        asyncInRecyclerThread2(() -> {
            for (int i = 0; i < 100000000; i++) {
                TestRecyclableObject object;
                try {
                    object = queue.take();
                } catch (InterruptedException e) {
                    fail(e);
                    return;
                }

                object.recycleBack();
            }

            allReceived.countDown();
        });

        allReceived.await();

        System.out.println("Created instances: " + TestRecyclableObject.getCounter());
    }

}
