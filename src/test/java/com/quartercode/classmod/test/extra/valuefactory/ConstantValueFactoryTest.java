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

package com.quartercode.classmod.test.extra.valuefactory;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.quartercode.classmod.extra.valuefactory.ConstantValueFactory;

public class ConstantValueFactoryTest {

    @Test
    public void test() {

        ConstantValueFactory<String> constantValueFactory = new ConstantValueFactory<>("test");

        for (int counter = 0; counter < 5; counter++) {
            assertEquals("The value returned by the constant value factory on the get() call " + (counter + 1), "test", constantValueFactory.get());
        }
    }

}
