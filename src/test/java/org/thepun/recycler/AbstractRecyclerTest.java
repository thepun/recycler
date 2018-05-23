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

    protected void asyncInRecyclerThread1(Runnable runnable) {
        executor1.submit(runnable);
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

    protected void asyncInRecyclerThread2(Runnable runnable) {
        executor2.submit(runnable);
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
