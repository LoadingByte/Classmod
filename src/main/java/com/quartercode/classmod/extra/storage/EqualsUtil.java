/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://www.quartercode.com/>
 *
 * Classmod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Classmod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Classmod. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.classmod.extra.storage;

import java.util.Arrays;
import java.util.Objects;

/**
 * A small utility which implements the {@link #equalsConsiderArrays(Object, Object)} algorithm.
 */
public class EqualsUtil {

    /**
     * Checks whether the given objects are equal to each other and considers arrays.
     * That means that this method returns {@code true} if both objects are arrays and {@link Arrays#equals(Object[], Object[])} outputs {@code true}.
     * 
     * @param object1 The first object which should be compared.
     * @param object2 The second object which should be compared.
     * @return Whether both objects are equal.
     */
    public static boolean equalsConsiderArrays(Object object1, Object object2) {

        if (object1 == null && object2 == null) {
            return true;
        } else if (object1 == null != (object2 == null)) {
            return false;
        } else if (Objects.equals(object1, object2)) {
            return true;
        } else if (object1.getClass().isArray() && object2.getClass().isArray() && Arrays.equals((Object[]) object1, (Object[]) object2)) {
            return true;
        } else {
            return false;
        }
    }

    private EqualsUtil() {

    }

}
