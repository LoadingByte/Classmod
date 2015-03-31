/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
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

package com.quartercode.classmod.test;

import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Objects;

public class ExtraAssert {

    public static void assertListEquals(String message, List<?> actualList, Object... expectedElements) {

        assertTrue(message, actualList.size() == expectedElements.length);

        for (int index = 0; index < actualList.size(); index++) {
            assertTrue(message, Objects.equals(expectedElements[index], actualList.get(index)));
        }
    }

    private ExtraAssert() {

    }

}
