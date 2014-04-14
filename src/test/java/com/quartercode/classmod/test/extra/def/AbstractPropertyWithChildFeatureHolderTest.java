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
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.def.DefaultChildFeatureHolder;
import com.quartercode.classmod.extra.def.ObjectProperty;

public class AbstractPropertyWithChildFeatureHolderTest {

    private FeatureHolder                   propertyHolder;
    private Property<ChildFeatureHolder<?>> property;

    @Before
    public void setUp() {

        propertyHolder = new DefaultFeatureHolder();
        // Use object property as storage implementation
        PropertyDefinition<ChildFeatureHolder<?>> propertyDefinition = ObjectProperty.createDefinition("property");
        property = propertyHolder.get(propertyDefinition);
    }

    @Test
    public void testSet() {

        property.set(new TestFeatureHolder());
        ChildFeatureHolder<?> value1 = property.get();

        property.set(new TestFeatureHolder());
        ChildFeatureHolder<?> value2 = property.get();

        Assert.assertEquals("Parent of the newly set property value 1 (child feature holder)", null, value1.getParent());
        Assert.assertEquals("Parent of the newly set property value 2 (child feature holder)", propertyHolder, value2.getParent());
    }

    private static class TestFeatureHolder extends DefaultChildFeatureHolder<FeatureHolder> {

        public TestFeatureHolder() {

            setParentType(FeatureHolder.class);
        }

    }

}
