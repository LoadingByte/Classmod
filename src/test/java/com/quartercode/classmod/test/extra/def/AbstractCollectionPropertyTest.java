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
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.def.ObjectCollectionProperty;

public class AbstractCollectionPropertyTest {

    private Set<String>                             collection;
    private CollectionProperty<String, Set<String>> property;

    @Before
    public void setUp() {

        collection = new HashSet<>();
        collection.add("entry1");
        collection.add("entry2");

        // Use object collection property as storage implementation
        CollectionPropertyDefinition<String, Set<String>> definition = ObjectCollectionProperty.createDefinition("property", collection);
        property = new DefaultFeatureHolder().get(definition);
    }

    @Test
    public void testGet() {

        Object currentCollection = property.get();
        assertEquals("Initially set property collection", collection, currentCollection);
    }

    @Test
    public void testAdd() {

        property.add("entry3");
        assertTrue("Collection property doesn't contain newly added entry", property.get().contains("entry3"));
    }

    @Test
    public void testRemove() {

        property.remove("entry2");
        assertTrue("Collection property contains removed entry", !property.get().contains("entry2"));
    }

    @Test
    public void testIgnoreEquals() {

        FeatureHolder holder = new DefaultFeatureHolder();
        CollectionProperty<String, Set<String>> property1 = holder.get(createDefinition("property1", false));
        CollectionProperty<String, Set<String>> property2 = holder.get(createDefinition("property2", false));
        CollectionProperty<String, Set<String>> property3 = holder.get(createDefinition("property3", true));
        CollectionProperty<String, Set<String>> property4 = holder.get(createDefinition("property4", true));

        assertNotEquals("Hash code of collection property with ignoreEquals=false should not be 0", 0, property1.hashCode());
        assertNotEquals("Hash code of collection property with ignoreEquals=false should not be 0", 0, property2.hashCode());
        assertEquals("Hash code of collection property with ignoreEquals=false", 0, property3.hashCode());
        assertEquals("Hash code of collection property with ignoreEquals=false", 0, property4.hashCode());

        assertFalse("Two different collection properties with ignoreEquals=false on both do equal", property1.equals(property2));
        assertTrue("Two different collection properties with ignoreEquals=true on one don't equal", property1.equals(property3));
        assertTrue("Two different collection properties with ignoreEquals=true on one don't equal", property1.equals(property4));

        assertTrue("Two different collection properties with ignoreEquals=true on one don't equal", property2.equals(property3));
        assertTrue("Two different collection properties with ignoreEquals=true on one don't equal", property2.equals(property4));

        assertTrue("Two different collection properties with ignoreEquals=true on both don't equal", property3.equals(property4));
    }

    private CollectionPropertyDefinition<String, Set<String>> createDefinition(String name, boolean ignoreEquals) {

        return ObjectCollectionProperty.createDefinition(name, new HashSet<String>(), ignoreEquals);
    }

}
