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
