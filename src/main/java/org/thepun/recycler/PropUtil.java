package org.thepun.recycler;

final class PropUtil {

    static int getPositiveInt(String name, int defaultValue) {
        String property = System.getProperty(name);
        if (property == null) {
            return defaultValue;
        }

        int i = Integer.parseInt(property);
        if (i <= 0) {
            throw new IllegalStateException("Property '" + name + "' is not positive integer: " + i);
        }

        return i;
    }

    static int getPositiveIntPowOf2(String name, int defaultValue) {
        int value = getPositiveInt(name, defaultValue);
        return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }

}
