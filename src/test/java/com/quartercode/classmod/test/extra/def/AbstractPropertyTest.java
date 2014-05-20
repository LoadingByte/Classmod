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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
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
    public void testGet() {

        Object value = property.get();
        assertEquals("Initially set property value", "initialString", value);
    }

    @Test
    public void testSet() {

        property.set("secondString");
        Object value = property.get();
        assertEquals("Newly set property value", "secondString", value);

        property.set(null);
        value = property.get();
        assertEquals("Newly set property value", null, value);
    }

    @Test
    public void testIgnoreEquals() {

        FeatureHolder holder = new DefaultFeatureHolder();
        Property<String> property1 = holder.get(ObjectProperty.<String> createDefinition("property1", false));
        Property<String> property2 = holder.get(ObjectProperty.<String> createDefinition("property2", false));
        Property<String> property3 = holder.get(ObjectProperty.<String> createDefinition("property3", true));
        Property<String> property4 = holder.get(ObjectProperty.<String> createDefinition("property4", true));

        assertNotEquals("Hash code of property with ignoreEquals=false should not be 0", 0, property1.hashCode());
        assertNotEquals("Hash code of property with ignoreEquals=false should not be 0", 0, property2.hashCode());
        assertEquals("Hash code of property with ignoreEquals=false", 0, property3.hashCode());
        assertEquals("Hash code of property with ignoreEquals=false", 0, property4.hashCode());

        assertFalse("Two different properties with ignoreEquals=false on both do equal", property1.equals(property2));
        assertTrue("Two different properties with ignoreEquals=true on one don't equal", property1.equals(property3));
        assertTrue("Two different properties with ignoreEquals=true on one don't equal", property1.equals(property4));

        assertTrue("Two different properties with ignoreEquals=true on one don't equal", property2.equals(property3));
        assertTrue("Two different properties with ignoreEquals=true on one don't equal", property2.equals(property4));

        assertTrue("Two different properties with ignoreEquals=true on both don't equal", property3.equals(property4));
    }

}
