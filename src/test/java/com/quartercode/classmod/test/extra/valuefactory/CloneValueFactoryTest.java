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

package com.quartercode.classmod.test.extra.valuefactory;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.quartercode.classmod.extra.valuefactory.CloneValueFactory;

public class CloneValueFactoryTest {

    @Test
    public void test() {

        CloneValueFactory<TestObject> cloneValueFactory = new CloneValueFactory<>(new TestObject("test1", "test2"));

        TestObject clone = cloneValueFactory.get();
        assertEquals("Attribute value1 of the cloned object (should have been cloned)", "test1", clone.getValue1());
        assertEquals("Attribute value1 of the cloned object (should have not be cloned)", null, clone.getValue2());
    }

    // Must be protected for reflection to work without setAccessible().
    protected static class TestObject implements Cloneable {

        private final String value1;
        private final String value2;

        private TestObject(String value1, String value2) {

            this.value1 = value1;
            this.value2 = value2;
        }

        private String getValue1() {

            return value1;
        }

        private String getValue2() {

            return value2;
        }

        @Override
        public Object clone() {

            // Only copy value1
            return new TestObject(value1, null);
        }

    }

}
