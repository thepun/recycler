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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonOperations extends AbstractRecyclerTest {

    @Test
    void createObject() {
        Object recyclableObject = executeInRecyclerThread1(() -> Recycler.create(getType()));
        assertNotNull(recyclableObject);
        assertTrue(recyclableObject instanceof TestRecyclableObject);
    }

    @Test
    void getObject() {
        Object recyclableObject = executeInRecyclerThread1(() -> Recycler.get(getType()));
        assertNotNull(recyclableObject);
        assertTrue(recyclableObject instanceof TestRecyclableObject);
    }

    @Test
    void recycleLocally() {
        TestRecyclableObject recyclableObject = executeInRecyclerThread1(() -> Recycler.create(getType()));
        executeInRecyclerThread1(recyclableObject::recycleLocal);

        TestRecyclableObject anotherRecyclableObject = executeInRecyclerThread1(() -> Recycler.get(getType()));
        assertSame(recyclableObject, anotherRecyclableObject);
    }

    @Test
    void recycleBack() {
        TestRecyclableObject recyclableObject = executeInRecyclerThread1(() -> Recycler.create(getType()));
        executeInRecyclerThread2(recyclableObject::recycleBack);

        TestRecyclableObject anotherRecyclableObject = executeInRecyclerThread1(() -> Recycler.get(getType()));
        assertSame(recyclableObject, anotherRecyclableObject);
    }
}
