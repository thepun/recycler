package org.thepun.recycler;

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
