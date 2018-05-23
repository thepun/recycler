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
