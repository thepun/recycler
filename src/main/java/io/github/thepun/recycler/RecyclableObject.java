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

public abstract class RecyclableObject {

    private final int type;

    private int lastGlobalCursor;
    private ThreadContext origin;

    public RecyclableObject(int type) {
        this.type = type;
    }

    public final void recycleBack() {
        ThreadContext threadContext = markFreed();

        RecycleAwareThread current = RecycleAwareThread.current();
        ThreadContext currentThreadContext = current.getContext(type);
        if (currentThreadContext == threadContext) {
            currentThreadContext.addFreeObjectForLocalUse(this);
        } else {
            threadContext.addFreeObjectBackToOrigin(this);
        }
    }

    public final void recycleLocal() {
        markFreed();

        RecycleAwareThread.current().getContext(type).addFreeObjectForLocalUse(this);
    }

    ThreadContext markFreed() {
        ThreadContext originLocal = origin;
        origin = null;

        if (originLocal == null) {
            throw new IllegalStateException("Object is already freed: " + this);
        }

        return originLocal;
    }

    void markUsed(ThreadContext thread) {
        origin = thread;
    }

    int getLastGlobalCursor() {
        return lastGlobalCursor;
    }

    void setLastGlobalCursor(int lastGlobalCursor) {
        this.lastGlobalCursor = lastGlobalCursor;
    }
}
