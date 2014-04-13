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

package com.quartercode.classmod.test.extra.def;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.def.ObjectProperty;

public class AbstractPropertyTest {

    private Property<String> property;

    @Before
    public void setUp() {

        // Use object property as storage implementation
        property = new DefaultFeatureHolder().get(ObjectProperty.createDefinition("property", "initialString", true));
    }

    @Test
    public void testGet() throws ExecutorInvocationException {

        Object value = property.get();
        Assert.assertEquals("Initially set property value", "initialString", value);
    }

    @Test
    public void testSet() throws ExecutorInvocationException {

        property.set("secondString");
        Object value = property.get();
        Assert.assertEquals("Newly set property value", "secondString", value);
    }

}
