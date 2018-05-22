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
