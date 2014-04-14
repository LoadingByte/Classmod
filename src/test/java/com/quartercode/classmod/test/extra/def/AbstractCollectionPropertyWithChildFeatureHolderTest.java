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

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.def.DefaultChildFeatureHolder;
import com.quartercode.classmod.extra.def.ObjectCollectionProperty;

public class AbstractCollectionPropertyWithChildFeatureHolderTest {

    private FeatureHolder                                                          propertyHolder;
    private CollectionProperty<ChildFeatureHolder<?>, List<ChildFeatureHolder<?>>> property;

    @Before
    public void setUp() {

        propertyHolder = new DefaultFeatureHolder();
        // Use object collection property as storage implementation
        CollectionPropertyDefinition<ChildFeatureHolder<?>, List<ChildFeatureHolder<?>>> propertyDefinition = ObjectCollectionProperty.createDefinition("property", new ArrayList<ChildFeatureHolder<?>>(), true);
        property = propertyHolder.get(propertyDefinition);
    }

    @Test
    public void testAdd() {

        property.add(new TestFeatureHolder());
        ChildFeatureHolder<?> element1 = property.get().iterator().next();
        property.remove(element1);

        property.add(new TestFeatureHolder());
        ChildFeatureHolder<?> element2 = property.get().iterator().next();

        Assert.assertEquals("Parent of the newly added and then removed collection property element 1 (child feature holder)", null, element1.getParent());
        Assert.assertEquals("Parent of the newly added and not removed collection property element 2 (child feature holder)", propertyHolder, element2.getParent());
    }

    private static class TestFeatureHolder extends DefaultChildFeatureHolder<FeatureHolder> {

        public TestFeatureHolder() {

            setParentType(FeatureHolder.class);
        }

    }

}
