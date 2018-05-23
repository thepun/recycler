/**
 * Copyright (C)2011 - Marat Gariev <thepun599@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.thepun.recycler;

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
