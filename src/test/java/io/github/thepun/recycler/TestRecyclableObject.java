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

import java.util.concurrent.atomic.LongAdder;

final class TestRecyclableObject extends RecyclableObject {

    private static final LongAdder created = new LongAdder();

    public static void clearCounter() {
        created.reset();
    }

    public static long getCounter() {
        return created.longValue();
    }


    TestRecyclableObject(int type) {
        super(type);

        created.add(1);
    }
}