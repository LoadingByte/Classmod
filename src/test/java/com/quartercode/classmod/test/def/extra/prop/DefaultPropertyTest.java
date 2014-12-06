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

package com.quartercode.classmod.test.def.extra.prop;

import static com.quartercode.classmod.extra.func.Priorities.LEVEL_8;
import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.extra.func.DefaultFunctionExecutorWrapper;
import com.quartercode.classmod.def.extra.prop.DefaultProperty;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;
import com.quartercode.classmod.extra.prop.Property;
import com.quartercode.classmod.extra.prop.PropertyDefinition;
import com.quartercode.classmod.test.def.extra.prop.StorageWrapper.StorageInterface;

@SuppressWarnings ("unchecked")
public class DefaultPropertyTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private FeatureHolder   featureHolder;

    private <T> void initializeProperty(Property<T> property, FunctionExecutor<T> getterExecutor, FunctionExecutor<Void> setterExecutor) {

        final Map<String, FunctionExecutorWrapper<T>> getterExecutors = new HashMap<>();
        if (getterExecutor != null) {
            getterExecutors.put("default", new DefaultFunctionExecutorWrapper<>(getterExecutor, LEVEL_8));
        }
        final Map<String, FunctionExecutorWrapper<Void>> setterExecutors = new HashMap<>();
        if (setterExecutor != null) {
            setterExecutors.put("default", new DefaultFunctionExecutorWrapper<>(setterExecutor, LEVEL_8));
        }

        final PropertyDefinition<T> definition = context.mock(PropertyDefinition.class, property.getName() + "Definition");

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(definition).isHidden();
                will(returnValue(false));
            allowing(definition).isPersistent();
                will(returnValue(false));

            allowing(definition).getGetterExecutorsForVariant(with(any(Class.class)));
                will(returnValue(getterExecutors));
            allowing(definition).getSetterExecutorsForVariant(with(any(Class.class)));
                will(returnValue(setterExecutors));

        }});
        // @formatter:on

        property.initialize(definition);
    }

    @Test
    public void testGet() {

        final StorageInterface<String> storage = context.mock(StorageInterface.class);
        Property<String> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);
        initializeProperty(property, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(storage).get();
                will(returnValue("teststring"));

        }});
        // @formatter:on

        assertEquals("Value which was returned by the default property", "teststring", property.get());
    }

    @Test
    public void testGetterFunctionExecutor() {

        final StorageInterface<String> storage = context.mock(StorageInterface.class);
        Property<String> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);

        final FunctionExecutor<String> getterExecutor = context.mock(FunctionExecutor.class);
        initializeProperty(property, new DummyFunctionExecutor<>(getterExecutor, false), null);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence calls = context.sequence("calls");
            oneOf(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[0])); inSequence(calls);
            oneOf(storage).get(); inSequence(calls);
                will(returnValue("teststring"));

        }});
        // @formatter:on

        assertEquals("Value which was returned by the default property", "teststring", property.get());
    }

    @Test
    public void testGetterFunctionExecutorOverrideReturnValue() {

        final StorageInterface<String> storage = context.mock(StorageInterface.class);
        Property<String> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);

        final FunctionExecutor<String> getterExecutor = context.mock(FunctionExecutor.class);
        initializeProperty(property, new DummyFunctionExecutor<>(getterExecutor, true), null);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence calls = context.sequence("calls");
            oneOf(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[0])); inSequence(calls);
                will(returnValue("teststring1"));
            oneOf(storage).get(); inSequence(calls);
                will(returnValue("teststring2"));

        }});
        // @formatter:on

        assertEquals("Value which was returned by the default property", "teststring1", property.get());
    }

    @Test
    public void testSet() {

        final StorageInterface<String> storage = context.mock(StorageInterface.class);
        Property<String> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);
        initializeProperty(property, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            // The property might retrieve the old value for some checks
            allowing(storage).get();
                will(returnValue(null));

            oneOf(storage).set("teststring");
            oneOf(storage).set(null);

        }});
        // @formatter:on

        property.set("teststring");
        property.set(null);
    }

    @Test
    public void testSetterFunctionExecutor() {

        final StorageInterface<String> storage = context.mock(StorageInterface.class);
        Property<String> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);

        final FunctionExecutor<Void> setterExecutor = context.mock(FunctionExecutor.class);
        initializeProperty(property, null, new DummyFunctionExecutor<>(setterExecutor, false));

        // @formatter:off
        context.checking(new Expectations() {{

            // The property might retrieve the old value for some checks
            allowing(storage).get();
                will(returnValue(null));

            final Sequence calls = context.sequence("calls");
            oneOf(setterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "teststring" })); inSequence(calls);
            oneOf(storage).set("teststring"); inSequence(calls);

        }});
        // @formatter:on

        property.set("teststring");
    }

    @Test
    public void testSetWithChildFeatureHolder() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);

        final StorageInterface<ChildFeatureHolder<FeatureHolder>> storage = context.mock(StorageInterface.class);
        Property<ChildFeatureHolder<FeatureHolder>> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);
        initializeProperty(property, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(childFeatureHolder).getParentType();
                will(returnValue(FeatureHolder.class));

            // The property might retrieve the old value for some checks
            allowing(storage).get();
                will(returnValue(null));

            final Sequence calls = context.sequence("calls");
            oneOf(childFeatureHolder).setParent(featureHolder); inSequence(calls);
            oneOf(storage).set(childFeatureHolder); inSequence(calls);

        }});
        // @formatter:on

        property.set(childFeatureHolder);
    }

    @Test
    public void testSetWithChildFeatureHolderWrongParentType() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);

        final StorageInterface<ChildFeatureHolder<FeatureHolder>> storage = context.mock(StorageInterface.class);
        Property<ChildFeatureHolder<FeatureHolder>> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);
        initializeProperty(property, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(childFeatureHolder).getParentType();
                will(returnValue(OtherFeatureHolder.class));

            // The property might retrieve the old value for some checks
            allowing(storage).get();
                will(returnValue(null));

            final Sequence calls = context.sequence("calls");
            never(childFeatureHolder).setParent(featureHolder); inSequence(calls);
            oneOf(storage).set(childFeatureHolder); inSequence(calls);

        }});
        // @formatter:on

        property.set(childFeatureHolder);
    }

    @Test
    public void testSetWithChildFeatureHolderToNull() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);

        final StorageInterface<ChildFeatureHolder<FeatureHolder>> storage = context.mock(StorageInterface.class);
        Property<ChildFeatureHolder<FeatureHolder>> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);
        initializeProperty(property, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(childFeatureHolder).getParentType();
                will(returnValue(FeatureHolder.class));

            // The property wants to retrieve the old state of the childFeatureHolder for removing its parent
            allowing(storage).get();
                will(returnValue(childFeatureHolder));
            allowing(childFeatureHolder).getParent();
                will(returnValue(featureHolder));

            final Sequence calls = context.sequence("calls");
            oneOf(childFeatureHolder).setParent(null); inSequence(calls);
            oneOf(storage).set(null); inSequence(calls);

        }});
        // @formatter:on

        property.set(null);
    }

    @Test
    public void testSetWithChildFeatureHolderToNullWrongParentObject() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);
        final OtherFeatureHolder wrongParentObject = context.mock(OtherFeatureHolder.class);

        final StorageInterface<ChildFeatureHolder<FeatureHolder>> storage = context.mock(StorageInterface.class);
        Property<ChildFeatureHolder<FeatureHolder>> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), null);
        initializeProperty(property, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(childFeatureHolder).getParentType();
                will(returnValue(FeatureHolder.class));

            // The property wants to retrieve the old state of the childFeatureHolder for removing its parent
            allowing(storage).get();
                will(returnValue(childFeatureHolder));
            allowing(childFeatureHolder).getParent();
                will(returnValue(wrongParentObject));

            final Sequence calls = context.sequence("calls");
            never(childFeatureHolder).setParent(null); inSequence(calls);
            oneOf(storage).set(null); inSequence(calls);

        }});
        // @formatter:on

        property.set(null);
    }

    @Test
    public void testInitialValue() {

        final StorageInterface<String> storage = context.mock(StorageInterface.class);

        // @formatter:off
        context.checking(new Expectations() {{

            // The property might retrieve the old value for some checks
            allowing(storage).get();
                will(returnValue(null));

            oneOf(storage).set("initialValue");

        }});
        // @formatter:on

        Property<String> property = new DefaultProperty<>("property", featureHolder, new StorageWrapper<>(storage), "initialValue");
        initializeProperty(property, null, null);
    }

    private static interface OtherFeatureHolder extends FeatureHolder {

    }

}
