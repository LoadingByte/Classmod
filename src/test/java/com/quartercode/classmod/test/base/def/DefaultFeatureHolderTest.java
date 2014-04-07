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

package com.quartercode.classmod.test.base.def;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Persistent;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.base.def.AbstractFeatureDefinition;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;

public class DefaultFeatureHolderTest {

    private static FeatureDefinition<TestFeature>     TEST_FEATURE_1;

    private static FeatureDefinition<AbstractFeature> TEST_FEATURE_2;

    @BeforeClass
    public static void setUpBeforeClass() {

        TEST_FEATURE_1 = new AbstractFeatureDefinition<TestFeature>("testFeature1") {

            @Override
            public TestFeature create(FeatureHolder holder) {

                return new TestFeature(getName(), holder);
            }

        };

        TEST_FEATURE_2 = new AbstractFeatureDefinition<AbstractFeature>("testFeature2") {

            @Override
            public AbstractFeature create(FeatureHolder holder) {

                return new AbstractFeature(getName(), holder);
            }

        };
    }

    private DefaultFeatureHolder featureHolder;

    @Before
    public void setUp() {

        featureHolder = new DefaultFeatureHolder();
    }

    @Test
    public void testGet() {

        Assert.assertEquals("Name of TEST_FEATURE_1", "testFeature1", featureHolder.get(TEST_FEATURE_1).getName());
        Assert.assertEquals("Name of TEST_FEATURE_2", "testFeature2", featureHolder.get(TEST_FEATURE_2).getName());
    }

    @Test
    public void testGetPersistentFeatures() {

        // Add feature objects
        featureHolder.get(TEST_FEATURE_1);
        featureHolder.get(TEST_FEATURE_2);

        Assert.assertTrue("Persistent features list doesn't contain TEST_FEATURE_1", featureHolder.getPersistentFeatures().contains(featureHolder.get(TEST_FEATURE_1)));
        Assert.assertFalse("Persistent features list contains TEST_FEATURE_2", featureHolder.getPersistentFeatures().contains(featureHolder.get(TEST_FEATURE_2)));
    }

    @Test
    public void testGetPersistentFeaturesMod() {

        Feature testFeature = new AbstractFeature("testFeature", featureHolder);

        // Modify persistent feature set
        List<Feature> persistentFeatures = featureHolder.getPersistentFeatures();
        persistentFeatures.add(testFeature);

        List<Feature> actualFeatures = new ArrayList<Feature>();
        for (Feature feature : featureHolder) {
            actualFeatures.add(feature);
        }
        List<Object> expectedFeatures = new ArrayList<Object>();
        expectedFeatures.add(testFeature);
        Assert.assertTrue("Persistent features list modification wasn't applied", expectedFeatures.equals(actualFeatures));
    }

    @Persistent
    private static class TestFeature extends AbstractFeature {

        private TestFeature(String name, FeatureHolder holder) {

            super(name, holder);
        }

    }

}
