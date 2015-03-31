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

import static org.junit.Assert.fail;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.prop.ValueSupplier;
import com.quartercode.classmod.util.FeatureHolderVisitor;
import com.quartercode.classmod.util.FeatureHolderVisitor.VisitResult;
import com.quartercode.classmod.util.TreeWalker;

public class TreeWalkerTest {

    @Rule
    public JUnitRuleMockery      context     = new JUnitRuleMockery();
    private final Sequence       visits      = context.sequence("visits");

    @Mock
    private FeatureHolderVisitor visitor;

    private int                  nameCounter = 0;

    // ----- Creation -----

    private String newName(String prefix) {

        String name = prefix + nameCounter;
        nameCounter++;
        return name;
    }

    private FeatureHolder featureHolder(Feature... features) {

        // Cannot use mock because FeatureHolder.iterator() needs to return a new instance on each call
        return new DummyFeatureHolder(Arrays.asList(features));
    }

    private Feature otherFeature() {

        return context.mock(Feature.class, newName("otherFeature"));
    }

    private ValueSupplier<?> valueSupplier(Object... suppliedValues) {

        final Object suppliedValue = suppliedValues.length == 1 ? suppliedValues[0] : Arrays.asList(suppliedValues);
        final ValueSupplier<?> feature = context.mock(ValueSupplier.class, newName("valueSupplier"));

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(feature).get();
                will(returnValue(suppliedValue));

        }});
        // @formatter:on

        return feature;
    }

    // ----- Checking -----

    private void assertPreVisit(final FeatureHolder holder, final VisitResult result) {

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(visitor).preVisit(holder); inSequence(visits);
                will(returnValue(result));

        }});
        // @formatter:on
    }

    private void assertPostVisit(final FeatureHolder holder, final VisitResult result) {

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(visitor).postVisit(holder); inSequence(visits);
                will(returnValue(result));

        }});
        // @formatter:on
    }

    // ----- Tests -----

    @Test
    public void testVisitOne() {

        FeatureHolder start;

        start = featureHolder();

        assertPreVisit(start, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitOneWithoutPost() {

        FeatureHolder start;

        start = featureHolder();

        assertPreVisit(start, VisitResult.CONTINUE);

        TreeWalker.walk(start, visitor, false);
    }

    @Test
    public void testVisitOneChild() {

        FeatureHolder start;
        FeatureHolder child1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder())
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoChildren() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder())
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
            assertPostVisit(child2, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoChildrenOfOneValueSupplier() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder(), child2 = featureHolder())
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
            assertPostVisit(child2, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitOneChildAndOtherFeature() {

        FeatureHolder start;
        FeatureHolder child1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            otherFeature()
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitOneChildAndOtherValue() {

        FeatureHolder start;
        FeatureHolder child1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier("otherValue")
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitOneChildAndOtherValueOfOneValueSupplier() {

        FeatureHolder start;
        FeatureHolder child1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder(), "otherValue")
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevels() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child1_2;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder(
                valueSupplier(child1_2 = featureHolder())
            ))
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
                assertPreVisit(child1_2, VisitResult.CONTINUE);
                assertPostVisit(child1_2, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallel() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder(
                valueSupplier(child2_1 = featureHolder())
            ))
        );
        // @formatter:on

        assertTwoLevelsAndParallelFull(start, child1, child2, child2_1);

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelOfOneValueSupplier() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder(), child2 = featureHolder(
                valueSupplier(child2_1 = featureHolder())
            ))
        );
        // @formatter:on

        assertTwoLevelsAndParallelFull(start, child1, child2, child2_1);

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelAndSeveralOtherValues() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier("otherValue1"),
            valueSupplier(child1 = featureHolder(
                valueSupplier("otherValue2"),
                valueSupplier("otherValue3")
            )),
            valueSupplier("otherValue4"),
            valueSupplier(child2 = featureHolder(
                valueSupplier("otherValue5"),
                valueSupplier(child2_1 = featureHolder()),
                valueSupplier("otherValue6")
            )),
            valueSupplier("otherValue7")
        );
        // @formatter:on

        assertTwoLevelsAndParallelFull(start, child1, child2, child2_1);

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelAndSeveralOtherValuesOfOneValueSupplier() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier("otherValue1", child1 = featureHolder(
                valueSupplier("otherValue2", "otherValue3")
            ), "otherValue4", child2 = featureHolder(
                valueSupplier("otherValue5", child2_1 = featureHolder(), "otherValue6")
            ), "otherValue7")
        );
        // @formatter:on

        assertTwoLevelsAndParallelFull(start, child1, child2, child2_1);

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelAndSeveralOtherFeatures() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            otherFeature(),
            valueSupplier(child1 = featureHolder(
                otherFeature(),
                otherFeature()
            )),
            otherFeature(),
            valueSupplier(child2 = featureHolder(
                otherFeature(),
                valueSupplier(child2_1 = featureHolder()),
                otherFeature()
            )),
            otherFeature()
        );
        // @formatter:on

        assertTwoLevelsAndParallelFull(start, child1, child2, child2_1);

        TreeWalker.walk(start, visitor, true);
    }

    private void assertTwoLevelsAndParallelFull(FeatureHolder start, FeatureHolder child1, FeatureHolder child2, FeatureHolder child2_1) {

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
                assertPreVisit(child2_1, VisitResult.CONTINUE);
                assertPostVisit(child2_1, VisitResult.CONTINUE);
            assertPostVisit(child2, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on
    }

    @Test
    public void testVisitTwoLevelsWithCycle() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder(
                valueSupplier(child1) // Cycle (reference back to child1)
            ))
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
                // Child 1 has already been visited
            assertPostVisit(child2, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelTerminateInPre() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder(
                valueSupplier(child2_1 = featureHolder())
            ))
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
                assertPreVisit(child2_1, VisitResult.TERMINATE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelTerminateInPost() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder(
                valueSupplier(child2_1 = featureHolder())
            ))
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
                assertPreVisit(child2_1, VisitResult.CONTINUE);
                assertPostVisit(child2_1, VisitResult.TERMINATE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelSkipSubtreeInPre() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder(
                valueSupplier(featureHolder())
            ))
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.SKIP_SUBTREE);
            assertPostVisit(child2, VisitResult.CONTINUE);
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    @Test
    public void testVisitTwoLevelsAndParallelSkipSubtreeInPost() {

        FeatureHolder start;
        FeatureHolder child1;
        FeatureHolder child2;
        FeatureHolder child2_1;

        // @formatter:off
        start = featureHolder(
            valueSupplier(child1 = featureHolder()),
            valueSupplier(child2 = featureHolder(
                valueSupplier(child2_1 = featureHolder())
            ))
        );
        // @formatter:on

        // @formatter:off
        assertPreVisit(start, VisitResult.CONTINUE);
            assertPreVisit(child1, VisitResult.CONTINUE);
            assertPostVisit(child1, VisitResult.CONTINUE);

            assertPreVisit(child2, VisitResult.CONTINUE);
                assertPreVisit(child2_1, VisitResult.CONTINUE);
                assertPostVisit(child2_1, VisitResult.CONTINUE);
            assertPostVisit(child2, VisitResult.SKIP_SUBTREE); // Shouldn't do anything
        assertPostVisit(start, VisitResult.CONTINUE);
        // @formatter:on

        TreeWalker.walk(start, visitor, true);
    }

    private static class DummyFeatureHolder implements FeatureHolder {

        private final UUID          uuid = UUID.randomUUID();
        private final List<Feature> features;

        public DummyFeatureHolder(List<Feature> features) {

            this.features = features;
        }

        @Override
        public UUID getUUID() {

            return uuid;
        }

        @Override
        public <F extends Feature> F get(FeatureDefinition<F> definition) {

            fail("Tree walker shouldn't call FeatureHolder.get()");
            return null;
        }

        @Override
        public Iterator<Feature> iterator() {

            return features.iterator();
        }

    }

}
