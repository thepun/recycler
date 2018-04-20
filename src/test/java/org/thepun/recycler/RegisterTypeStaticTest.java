package org.thepun.recycler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterTypeStaticTest extends AbstractRecyclerTest {

    @Test
    void createObject() {
        Object recyclableObject = executeInRecycleAwareThread(() -> Recycler.create(getType()));
        assertNotNull(recyclableObject);
        assertTrue(recyclableObject instanceof TestRecyclableObject);
    }

    @Test
    void getObject() {
        Object recyclableObject = executeInRecycleAwareThread(() -> Recycler.get(getType()));
        assertNotNull(recyclableObject);
        assertTrue(recyclableObject instanceof TestRecyclableObject);
    }
}
