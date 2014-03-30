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

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.def.ObjectCollectionProperty;

public class AbstractCollectionPropertyTest {

    private Set<String>                             collection;
    private CollectionProperty<String, Set<String>> property;

    @Before
    public void setUp() {

        collection = new HashSet<String>();
        collection.add("entry1");
        collection.add("entry2");

        // Use object collection property as storage implementation
        CollectionPropertyDefinition<String, Set<String>> definition = ObjectCollectionProperty.createDefinition("property", collection);
        property = new DefaultFeatureHolder().get(definition);
    }

    @Test
    public void testGet() throws ExecutorInvocationException {

        Object currentCollection = property.get();
        Assert.assertEquals("Initially set property collection", collection, currentCollection);
    }

    @Test
    public void testAdd() throws ExecutorInvocationException {

        property.add("entry3");
        Assert.assertTrue("Collection property doesn't contain newly added entry", property.get().contains("entry3"));
    }

    @Test
    public void testRemove() throws ExecutorInvocationException {

        property.remove("entry2");
        Assert.assertTrue("Collection property contains removed entry", !property.get().contains("entry2"));
    }
}
