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

}
