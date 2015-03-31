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

package com.quartercode.classmod.test.def.base;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.base.Initializable;
import com.quartercode.classmod.def.base.AbstractFeature;
import com.quartercode.classmod.def.base.AbstractFeatureDefinition;
import com.quartercode.classmod.def.base.DefaultFeatureHolder;

public class DefaultFeatureHolderTest {

    private static FeatureDefinition<TestFeature1> TEST_FEATURE_1;
    private static FeatureDefinition<TestFeature2> TEST_FEATURE_1_WRONG_TYPE;
    private static FeatureDefinition<TestFeature2> TEST_FEATURE_2;
    private static FeatureDefinition<TestFeature3> TEST_FEATURE_3;
    private static FeatureDefinition<TestFeature3> TEST_FEATURE_3_WRONG_DEFINITION_TYPE;
    private static FeatureDefinition<TestFeature4> TEST_FEATURE_4;

    @BeforeClass
    public static void setUpBeforeClass() {

        TEST_FEATURE_1 = new AbstractFeatureDefinition<TestFeature1>("testFeature1") {

            @Override
            public TestFeature1 create(FeatureHolder holder) {

                return new TestFeature1(getName(), holder);
            }

        };

        TEST_FEATURE_1_WRONG_TYPE = new AbstractFeatureDefinition<TestFeature2>("testFeature1") {

            @Override
            public TestFeature2 create(FeatureHolder holder) {

                // Something went wrong
                fail("Should not be called");
                return null;
            }

        };

        TEST_FEATURE_2 = new AbstractFeatureDefinition<TestFeature2>("testFeature2") {

            @Override
            public TestFeature2 create(FeatureHolder holder) {

                return new TestFeature2(getName(), holder);
            }

        };

        TEST_FEATURE_3 = new TestFeature3Definition();

        TEST_FEATURE_3_WRONG_DEFINITION_TYPE = new AbstractFeatureDefinition<TestFeature3>("testFeature3") {

            @Override
            public TestFeature3 create(FeatureHolder holder) {

                return new TestFeature3(getName(), holder);
            }

        };

        TEST_FEATURE_4 = new AbstractFeatureDefinition<TestFeature4>("testFeature4") {

            @Override
            public TestFeature4 create(FeatureHolder holder) {

                return new TestFeature4(getName(), holder);
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

        Feature feature1 = featureHolder.get(TEST_FEATURE_1);
        Feature feature2 = featureHolder.get(TEST_FEATURE_2);

        assertEquals("Type of feature from definition TEST_FEATURE_1", TestFeature1.class, feature1.getClass());
        assertEquals("Type of feature from definition TEST_FEATURE_2", TestFeature2.class, feature2.getClass());

        assertEquals("Name of feature from definition TEST_FEATURE_1", "testFeature1", feature1.getName());
        assertEquals("Name of feature from definition TEST_FEATURE_2", "testFeature2", feature2.getName());

        assertSame("Result of second call of get(TEST_FEATURE_1) (should be same as first call)", feature1, featureHolder.get(TEST_FEATURE_1));
        assertSame("Result of second call of get(TEST_FEATURE_2) (should be same as first call)", feature2, featureHolder.get(TEST_FEATURE_2));
    }

    @Test (expected = ClassCastException.class)
    public void testGetWrongType() {

        featureHolder.get(TEST_FEATURE_1);

        @SuppressWarnings ("unused")
        // Because of type erasure, the error occures here
        TestFeature2 feature = featureHolder.get(TEST_FEATURE_1_WRONG_TYPE);
    }

    @Test
    public void testGetInitialize() {

        TestFeature3 feature = featureHolder.get(TEST_FEATURE_3);
        assertTrue("Feature wasn't initialized properly", feature.initializeCalls == 1);

        feature = featureHolder.get(TEST_FEATURE_3);
        assertTrue("Feature was initialized more than once", feature.initializeCalls == 1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetInitializeWrongDefinitionType() {

        featureHolder.get(TEST_FEATURE_3_WRONG_DEFINITION_TYPE);
    }

    @Test
    public void testHashCode() {

        DefaultFeatureHolder featureHolder2 = new DefaultFeatureHolder();

        featureHolder.get(TEST_FEATURE_1);
        featureHolder2.get(TEST_FEATURE_1);
        assertTrue("Hash codes of the two feature holders are not the same", featureHolder.hashCode() == featureHolder2.hashCode());

        featureHolder.get(TEST_FEATURE_2);
        assertTrue("Hash codes of the two feature holders are the same", featureHolder.hashCode() != featureHolder2.hashCode());
    }

    @Test
    public void testEquals() {

        DefaultFeatureHolder featureHolder2 = new DefaultFeatureHolder();

        featureHolder.get(TEST_FEATURE_1);
        featureHolder2.get(TEST_FEATURE_1);
        assertTrue("The two feature holders do not equal", featureHolder.equals(featureHolder2) && featureHolder2.equals(featureHolder));

        featureHolder.get(TEST_FEATURE_2);
        assertTrue("The two feature holders do equal", !featureHolder.equals(featureHolder2) && !featureHolder2.equals(featureHolder));
    }

    @Test
    public void testHashCodeHidden() {

        DefaultFeatureHolder featureHolder2 = new DefaultFeatureHolder();

        // Only the first feature holder has testFeature4 (which is hidden)
        featureHolder.get(TEST_FEATURE_1);
        featureHolder.get(TEST_FEATURE_4);
        featureHolder2.get(TEST_FEATURE_1);

        assertTrue("Hash codes of the two feature holders are not the same", featureHolder.hashCode() == featureHolder2.hashCode());
    }

    @Test
    public void testEqualsHidden() {

        DefaultFeatureHolder featureHolder2 = new DefaultFeatureHolder();

        // Only the first feature holder has testFeature4 (which is hidden)
        featureHolder.get(TEST_FEATURE_1);
        featureHolder.get(TEST_FEATURE_4);
        featureHolder2.get(TEST_FEATURE_1);

        assertTrue("The two feature holders do not equal", featureHolder.equals(featureHolder2) && featureHolder2.equals(featureHolder));
    }

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

    private static class TestFeature3 extends AbstractFeature implements Initializable<TestFeature3Definition> {

        private boolean initialized;
        public int      initializeCalls;

        private TestFeature3(String name, FeatureHolder holder) {

            super(name, holder);
        }

        @Override
        public void initialize(TestFeature3Definition definition) {

            initialized = true;
            initializeCalls++;
        }

        @Override
        public boolean isInitialized() {

            return initialized;
        }

    }

    private static class TestFeature3Definition extends AbstractFeatureDefinition<TestFeature3> {

        private TestFeature3Definition() {

            super("testFeature3");
        }

        @Override
        public TestFeature3 create(FeatureHolder holder) {

            return new TestFeature3(getName(), holder);
        }

    }

    private static class TestFeature4 extends AbstractFeature implements Hideable {

        private TestFeature4(String name, FeatureHolder holder) {

            super(name, holder);
        }

        @Override
        public boolean isHidden() {

            return true;
        }

    }

}
