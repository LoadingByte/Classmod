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

package com.quartercode.classmod.test.def.base;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Persistent;
import com.quartercode.classmod.def.base.AbstractFeature;
import com.quartercode.classmod.def.base.AbstractFeatureDefinition;
import com.quartercode.classmod.def.base.DefaultFeatureHolder;

public class DefaultFeatureHolderPersistentFeaturesTest {

    private static FeatureDefinition<TestFeature1> TEST_FEATURE_1;
    private static FeatureDefinition<TestFeature2> TEST_FEATURE_2;

    @BeforeClass
    public static void setUpBeforeClass() {

        TEST_FEATURE_1 = new AbstractFeatureDefinition<TestFeature1>("testFeature1") {

            @Override
            public TestFeature1 create(FeatureHolder holder) {

                return new TestFeature1(getName(), holder);
            }

        };

        TEST_FEATURE_2 = new AbstractFeatureDefinition<TestFeature2>("testFeature2") {

            @Override
            public TestFeature2 create(FeatureHolder holder) {

                return new TestFeature2(getName(), holder);
            }

        };
    }

    private DefaultFeatureHolder featureHolder;

    @Before
    public void setUp() {

        featureHolder = new DefaultFeatureHolder();
    }

    @Test
    public void testGetPersistentFeatures() {

        // Add feature objects
        featureHolder.get(TEST_FEATURE_1);
        featureHolder.get(TEST_FEATURE_2);

        assertTrue("Persistent features list doesn't contain TEST_FEATURE_1", featureHolder.getPersistentFeatures().contains(featureHolder.get(TEST_FEATURE_1)));
        assertFalse("Persistent features list contains TEST_FEATURE_2", featureHolder.getPersistentFeatures().contains(featureHolder.get(TEST_FEATURE_2)));
    }

    @Test
    public void testGetPersistentFeaturesMod() {

        Feature testFeature = new AbstractFeature("testFeature", featureHolder);

        // Modify persistent feature set
        List<Feature> persistentFeatures = featureHolder.getPersistentFeatures();
        persistentFeatures.add(testFeature);

        List<Feature> actualFeatures = new ArrayList<>();
        for (Feature feature : featureHolder) {
            actualFeatures.add(feature);
        }
        List<Object> expectedFeatures = new ArrayList<>();
        expectedFeatures.add(testFeature);
        assertTrue("Persistent features list modification wasn't applied", expectedFeatures.equals(actualFeatures));
    }

    @Persistent
    private static class TestFeature1 extends AbstractFeature {

        private TestFeature1(String name, FeatureHolder holder) {

            super(name, holder);
        }

    }

    private static class TestFeature2 extends AbstractFeature {

        private TestFeature2(String name, FeatureHolder holder) {

            super(name, holder);
        }

    }

}
