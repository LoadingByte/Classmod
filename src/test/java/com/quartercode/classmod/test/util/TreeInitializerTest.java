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

package com.quartercode.classmod.test.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.prop.ValueSupplier;
import com.quartercode.classmod.util.TreeInitializer;

public class TreeInitializerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private FeatureDefinition<?> prepareFeature(final Feature feature, final String name, DummyFeatureHolder holder) {

        final FeatureDefinition<?> definition = context.mock(FeatureDefinition.class, name + "Definition");

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(definition).getName();
                will(returnValue(name));
            allowing(feature).getName();
                will(returnValue(name));

        }});
        // @formatter:on

        if (holder != null) {
            holder.features.put(definition, feature);
        }

        return definition;
    }

    private Pair<FeatureDefinition<?>, Feature> createFeature(final String name, DummyFeatureHolder holder) {

        Feature feature = context.mock(Feature.class, name);
        return Pair.<FeatureDefinition<?>, Feature> of(prepareFeature(feature, name, holder), feature);
    }

    private Pair<FeatureDefinition<?>, ValueSupplier<?>> createValueSupplier(final String name, DummyFeatureHolder holder) {

        ValueSupplier<?> feature = context.mock(ValueSupplier.class, name);
        return Pair.<FeatureDefinition<?>, ValueSupplier<?>> of(prepareFeature(feature, name, holder), feature);
    }

    @Test
    public void testInitializeOne() {

        TestFeatureHolder1 holder = new TestFeatureHolder1();
        Pair<FeatureDefinition<?>, Feature> feature = createFeature("feature", holder);

        holder.expectedGetCalls.add(feature.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature.getLeft());
        initializer.apply(holder);

        holder.checkExpected();
    }

    @Test
    public void testInitializeIgnoreFeature() {

        TestFeatureHolder1 holder = new TestFeatureHolder1();
        Pair<FeatureDefinition<?>, Feature> feature1 = createFeature("feature1", holder);
        createFeature("feature2", holder);

        holder.expectedGetCalls.add(feature1.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        // Only add feature1
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.apply(holder);

        holder.checkExpected();
    }

    @Test
    public void testInitializeIgnoreFeatureDefinition() {

        TestFeatureHolder1 holder = new TestFeatureHolder1();
        Pair<FeatureDefinition<?>, Feature> feature1 = createFeature("feature1", holder);
        // Don't add feature2 to the holder
        Pair<FeatureDefinition<?>, Feature> feature2 = createFeature("feature2", null);

        holder.expectedGetCalls.add(feature1.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature2.getLeft());
        initializer.apply(holder);

        holder.checkExpected();
    }

    @Test
    public void testInitializeMultipleHolders() {

        TestFeatureHolder1 holder1 = new TestFeatureHolder1();
        Pair<FeatureDefinition<?>, Feature> feature1 = createFeature("feature1", holder1);
        final Pair<FeatureDefinition<?>, ValueSupplier<?>> feature2 = createValueSupplier("feature2", holder1);

        final TestFeatureHolder2 holder2 = new TestFeatureHolder2();
        Pair<FeatureDefinition<?>, Feature> feature3 = createFeature("feature3", holder2);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(feature2.getRight()).get();
                will(returnValue(holder2));

        }});
        // @formatter:on

        holder1.expectedGetCalls.add(feature1.getLeft());
        holder1.expectedGetCalls.add(feature2.getLeft());
        holder2.expectedGetCalls.add(feature3.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature2.getLeft());
        initializer.addInitializationDefinition(TestFeatureHolder2.class, feature3.getLeft());
        initializer.apply(holder1);

        holder1.checkExpected();
        holder2.checkExpected();
    }

    @Test
    public void testInitializeMultipleHoldersValueSupplierList() {

        TestFeatureHolder1 holder1 = new TestFeatureHolder1();
        final Pair<FeatureDefinition<?>, ValueSupplier<?>> feature1 = createValueSupplier("feature1", holder1);

        final TestFeatureHolder2 holder2 = new TestFeatureHolder2();
        Pair<FeatureDefinition<?>, Feature> feature2 = createFeature("feature2", holder2);

        final TestFeatureHolder2 holder3 = new TestFeatureHolder2();
        Pair<FeatureDefinition<?>, Feature> feature3 = createFeature("feature3", holder3);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(feature1.getRight()).get();
                will(returnValue(Arrays.asList(holder2, holder3)));

        }});
        // @formatter:on

        holder1.expectedGetCalls.add(feature1.getLeft());
        holder2.expectedGetCalls.add(feature2.getLeft());
        holder3.expectedGetCalls.add(feature3.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.addInitializationDefinition(TestFeatureHolder2.class, feature2.getLeft());
        initializer.addInitializationDefinition(TestFeatureHolder2.class, feature3.getLeft());
        initializer.apply(holder1);

        holder1.checkExpected();
        holder2.checkExpected();
        holder3.checkExpected();
    }

    @Test
    public void testInitializeMultipleHoldersCycle() {

        final TestFeatureHolder1 holder1 = new TestFeatureHolder1();
        final Pair<FeatureDefinition<?>, ValueSupplier<?>> feature1 = createValueSupplier("feature1", holder1);

        final TestFeatureHolder2 holder2 = new TestFeatureHolder2();
        final Pair<FeatureDefinition<?>, ValueSupplier<?>> feature2 = createValueSupplier("feature2", holder2);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(feature1.getRight()).get();
                will(returnValue(holder2));
            allowing(feature2.getRight()).get();
                will(returnValue(holder1));

        }});
        // @formatter:on

        holder1.expectedGetCalls.add(feature1.getLeft());
        holder2.expectedGetCalls.add(feature2.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.addInitializationDefinition(TestFeatureHolder2.class, feature2.getLeft());
        initializer.apply(holder1);

        holder1.checkExpected();
        holder2.checkExpected();
    }

    @Test
    public void testInitializeMultipleHoldersIgnoreOne() {

        TestFeatureHolder1 holder1 = new TestFeatureHolder1();
        final Pair<FeatureDefinition<?>, ValueSupplier<?>> feature1 = createValueSupplier("feature1", holder1);

        final TestFeatureHolder2 holder2 = new TestFeatureHolder2();
        createFeature("feature2", holder2);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(feature1.getRight()).get();
                will(returnValue(holder2));

        }});
        // @formatter:on

        holder1.expectedGetCalls.add(feature1.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        // There isn't any mapping for TestFeatureHolder2
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.apply(holder1);

        holder1.checkExpected();
        holder2.checkExpected();
    }

    @Test
    public void testInitializeIgnoreValueSupplier() {

        TestFeatureHolder1 holder = new TestFeatureHolder1();
        Pair<FeatureDefinition<?>, Feature> feature1 = createFeature("feature1", holder);
        final Pair<FeatureDefinition<?>, ValueSupplier<?>> feature2 = createValueSupplier("feature2", holder);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(feature2.getRight()).get();
                will(returnValue("unimportantString"));

        }});
        // @formatter:on

        holder.expectedGetCalls.add(feature1.getLeft());

        TreeInitializer initializer = new TreeInitializer();
        initializer.addInitializationDefinition(TestFeatureHolder1.class, feature1.getLeft());
        initializer.apply(holder);

        holder.checkExpected();
    }

    private static class DummyFeatureHolder implements FeatureHolder {

        private final UUID                                 uuid             = UUID.randomUUID();

        protected final Map<FeatureDefinition<?>, Feature> features         = new HashMap<>();
        protected final List<FeatureDefinition<?>>         expectedGetCalls = new ArrayList<>();

        protected void checkExpected() {

            assertTrue("Not all expected " + getClass().getSimpleName() + ".get() calls were made; remaining: " + expectedGetCalls, expectedGetCalls.isEmpty());
        }

        @Override
        public UUID getUUID() {

            return uuid;
        }

        @SuppressWarnings ("unchecked")
        @Override
        public <F extends Feature> F get(FeatureDefinition<F> definition) {

            if (expectedGetCalls.contains(definition)) {
                expectedGetCalls.remove(definition);
            } else {
                fail("Unexpected invocation of " + getClass().getSimpleName() + ".get() with definition '" + definition + "'");
            }

            return (F) features.get(definition);
        }

        @Override
        public Iterator<Feature> iterator() {

            return features.values().iterator();
        }

    }

    private static class TestFeatureHolder1 extends DummyFeatureHolder {

    }

    private static class TestFeatureHolder2 extends DummyFeatureHolder {

    }

}
