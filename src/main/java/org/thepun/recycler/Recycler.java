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

import java.lang.reflect.Modifier;
import java.util.concurrent.locks.ReentrantLock;

public final class Recycler {

    static final ReentrantLock GLOBAL_LOCK = new ReentrantLock();

    public static <T  extends RecyclableObject> int registerType(Class<T> type, RecyclableObjectFactory<T> factory) {
        if (!Modifier.isFinal(type.getModifiers())) {
            throw new IllegalArgumentException("Only final classes supported: " + type.getName());
        }

        if (type.getSuperclass() != RecyclableObject.class) {
            throw new IllegalArgumentException("Only direct children of RecyclableObject supported: " + type.getName());
        }

        TypeContext typeContext = TypeContext.registerNewType(factory);
        ThreadContext.registerNewTypeToAll(typeContext);
        return typeContext.getIndex();
    }

    public static <T extends RecyclableObject> T get(int registeredType) {
        return (T) RecycleAwareThread.current().getContext(registeredType).get();
    }

    public static <T extends RecyclableObject> T create(int registeredType) {
        return (T) RecycleAwareThread.current().getContext(registeredType).create();
    }


    private Recycler() {
    }
}
