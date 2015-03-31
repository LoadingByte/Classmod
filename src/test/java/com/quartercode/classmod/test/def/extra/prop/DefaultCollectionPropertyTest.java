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

package com.quartercode.classmod.test.def.extra.prop;

import static com.quartercode.classmod.extra.func.Priorities.LEVEL_8;
import static com.quartercode.classmod.test.ExtraAssert.assertListEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.extra.func.DefaultFunctionExecutorWrapper;
import com.quartercode.classmod.def.extra.prop.DefaultCollectionProperty;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;
import com.quartercode.classmod.extra.prop.CollectionProperty;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.test.def.extra.prop.StorageWrapper.StorageInterface;

@SuppressWarnings ("unchecked")
public class DefaultCollectionPropertyTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private FeatureHolder   featureHolder;

    private <E> void addInitializeExpectationsToStorage(final StorageInterface<List<E>> storage) {

        // @formatter:off
        context.checking(new Expectations() {{

            // The initial collection is null
            oneOf(storage).get();
                will(returnValue(null));
            // Expect a new collection, wich is a clone of the collection template, to be set
            oneOf(storage).set(with(any(ArrayList.class)));

        }});
        // @formatter:on
    }

    private <E, C extends Collection<E>> void initializeCollectionProperty(CollectionProperty<E, C> property, FunctionExecutor<C> getterExecutor, FunctionExecutor<Void> adderExecutor, FunctionExecutor<Void> removerExecutor) {

        final Map<String, FunctionExecutorWrapper<C>> getterExecutors = new HashMap<>();
        if (getterExecutor != null) {
            getterExecutors.put("default", new DefaultFunctionExecutorWrapper<>(getterExecutor, LEVEL_8));
        }
        final Map<String, FunctionExecutorWrapper<Void>> adderExecutors = new HashMap<>();
        if (adderExecutor != null) {
            adderExecutors.put("default", new DefaultFunctionExecutorWrapper<>(adderExecutor, LEVEL_8));
        }
        final Map<String, FunctionExecutorWrapper<Void>> removerExecutors = new HashMap<>();
        if (removerExecutor != null) {
            removerExecutors.put("default", new DefaultFunctionExecutorWrapper<>(removerExecutor, LEVEL_8));
        }

        final CollectionPropertyDefinition<E, C> definition = context.mock(CollectionPropertyDefinition.class, property.getName() + "Definition");

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(definition).newCollection();
                will(returnValue(new ArrayList<>()));

            allowing(definition).isHidden();
                will(returnValue(false));
            allowing(definition).isPersistent();
                will(returnValue(false));

            allowing(definition).getGetterExecutorsForVariant(with(any(Class.class)));
                will(returnValue(getterExecutors));
            allowing(definition).getAdderExecutorsForVariant(with(any(Class.class)));
                will(returnValue(adderExecutors));
            allowing(definition).getRemoverExecutorsForVariant(with(any(Class.class)));
                will(returnValue(removerExecutors));

        }});
        // @formatter:on

        property.initialize(definition);
    }

    @Test
    public void testInitialize() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(storage).get();
                will(returnValue(new LinkedList<>(Arrays.asList("teststring1", "teststring2"))));
            oneOf(storage).set(new ArrayList<>(Arrays.asList("teststring1", "teststring2")));

        }});
        // @formatter:on

        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);
    }

    @Test
    public void testGet() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1", "teststring2"))));

        }});
        // @formatter:on

        assertListEquals("Value which was returned by the default property", collectionProperty.get(), "teststring1", "teststring2");
    }

    @Test
    public void testGetterFunctionExecutor() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));

        final FunctionExecutor<List<String>> getterExecutor = context.mock(FunctionExecutor.class);
        initializeCollectionProperty(collectionProperty, new DummyFunctionExecutor<>(getterExecutor, false), null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence calls = context.sequence("calls");
            oneOf(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[0])); inSequence(calls);
            oneOf(storage).get(); inSequence(calls);
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1", "teststring2"))));

        }});
        // @formatter:on

        assertListEquals("List which was returned by the default collection property", collectionProperty.get(), "teststring1", "teststring2");
    }

    @Test
    public void testGetterFunctionExecutorOverrideReturnValue() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));

        final FunctionExecutor<List<String>> getterExecutor = context.mock(FunctionExecutor.class);
        initializeCollectionProperty(collectionProperty, new DummyFunctionExecutor<>(getterExecutor, true), null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            final Sequence calls = context.sequence("calls");
            oneOf(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[0])); inSequence(calls);
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1", "teststring2"))));
            oneOf(storage).get(); inSequence(calls);
                will(returnValue(new ArrayList<>(Arrays.asList("teststring3", "teststring4"))));

        }});
        // @formatter:on

        assertListEquals("List which was returned by the default collection property", collectionProperty.get(), "teststring1", "teststring2");
    }

    @Test
    public void testAdd() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1"))));

            oneOf(storage).set(new ArrayList<>(Arrays.asList("teststring1", "teststring2")));

        }});
        // @formatter:on

        collectionProperty.add("teststring2");
    }

    @Test
    public void testAdderFunctionExecutor() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));

        final FunctionExecutor<Void> adderExecutor = context.mock(FunctionExecutor.class);
        initializeCollectionProperty(collectionProperty, null, new DummyFunctionExecutor<>(adderExecutor, false), null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1"))));

            final Sequence calls = context.sequence("calls");
            oneOf(adderExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "teststring2" })); inSequence(calls);
            oneOf(storage).set(new ArrayList<>(Arrays.asList("teststring1", "teststring2"))); inSequence(calls);

        }});
        // @formatter:on

        collectionProperty.add("teststring2");
    }

    @Test
    public void testAddWithChildFeatureHolder() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);

        final StorageInterface<List<ChildFeatureHolder<FeatureHolder>>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<ChildFeatureHolder<FeatureHolder>, List<ChildFeatureHolder<FeatureHolder>>> collectionProperty;
        collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(childFeatureHolder).getParentType();
                will(returnValue(FeatureHolder.class));

            allowing(storage).get();
                will(returnValue(new ArrayList<>()));

            final Sequence calls = context.sequence("calls");
            oneOf(childFeatureHolder).setParent(featureHolder); inSequence(calls);
            oneOf(storage).set(new ArrayList<>(Arrays.asList(childFeatureHolder))); inSequence(calls);

        }});
        // @formatter:on

        collectionProperty.add(childFeatureHolder);
    }

    @Test
    public void testAddWithChildFeatureHolderWrongParentType() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);

        final StorageInterface<List<ChildFeatureHolder<FeatureHolder>>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<ChildFeatureHolder<FeatureHolder>, List<ChildFeatureHolder<FeatureHolder>>> collectionProperty;
        collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(childFeatureHolder).getParentType();
                will(returnValue(OtherFeatureHolder.class));

            allowing(storage).get();
                will(returnValue(new ArrayList<>()));

            final Sequence calls = context.sequence("calls");
            never(childFeatureHolder).setParent(featureHolder); inSequence(calls);
            oneOf(storage).set(new ArrayList<>(Arrays.asList(childFeatureHolder))); inSequence(calls);

        }});
        // @formatter:on

        collectionProperty.add(childFeatureHolder);
    }

    @Test
    public void testRemove() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1", "teststring2"))));

            oneOf(storage).set(new ArrayList<>(Arrays.asList("teststring1")));

        }});
        // @formatter:on

        collectionProperty.remove("teststring2");
    }

    @Test
    public void testRemoverFunctionExecutor() {

        final StorageInterface<List<String>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));

        final FunctionExecutor<Void> removerExecutor = context.mock(FunctionExecutor.class);
        initializeCollectionProperty(collectionProperty, null, null, new DummyFunctionExecutor<>(removerExecutor, false));

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList("teststring1", "teststring2"))));

            final Sequence calls = context.sequence("calls");
            oneOf(removerExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "teststring2" })); inSequence(calls);
            oneOf(storage).set(new ArrayList<>(Arrays.asList("teststring1"))); inSequence(calls);

        }});
        // @formatter:on

        collectionProperty.remove("teststring2");
    }

    @Test
    public void testRemoveWithChildFeatureHolder() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);

        final StorageInterface<List<ChildFeatureHolder<FeatureHolder>>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<ChildFeatureHolder<FeatureHolder>, List<ChildFeatureHolder<FeatureHolder>>> collectionProperty;
        collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{
            
            allowing(childFeatureHolder).getParentType();
                will(returnValue(FeatureHolder.class));

            // The collection property wants to retrieve the old state of the childFeatureHolder for removing its parent
            allowing(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList(childFeatureHolder))));
            allowing(childFeatureHolder).getParent();
                will(returnValue(featureHolder));

            final Sequence calls = context.sequence("calls");
            oneOf(childFeatureHolder).setParent(null); inSequence(calls);
            oneOf(storage).set(new ArrayList<ChildFeatureHolder<FeatureHolder>>()); inSequence(calls);

        }});
        // @formatter:on

        collectionProperty.remove(childFeatureHolder);
    }

    @Test
    public void testRemoveWithChildFeatureHolderWrongParentObject() {

        final ChildFeatureHolder<FeatureHolder> childFeatureHolder = context.mock(ChildFeatureHolder.class);
        final OtherFeatureHolder wrongParentObject = context.mock(OtherFeatureHolder.class);

        final StorageInterface<List<ChildFeatureHolder<FeatureHolder>>> storage = context.mock(StorageInterface.class);
        addInitializeExpectationsToStorage(storage);
        CollectionProperty<ChildFeatureHolder<FeatureHolder>, List<ChildFeatureHolder<FeatureHolder>>> collectionProperty;
        collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, new StorageWrapper<>(storage));
        initializeCollectionProperty(collectionProperty, null, null, null);

        // @formatter:off
        context.checking(new Expectations() {{
            
            allowing(childFeatureHolder).getParentType();
                will(returnValue(FeatureHolder.class));

            // The collection property wants to retrieve the old state of the childFeatureHolder for removing its parent
            allowing(storage).get();
                will(returnValue(new ArrayList<>(Arrays.asList(childFeatureHolder))));
            allowing(childFeatureHolder).getParent();
                will(returnValue(wrongParentObject));

            final Sequence calls = context.sequence("calls");
            never(childFeatureHolder).setParent(null); inSequence(calls);
            oneOf(storage).set(new ArrayList<ChildFeatureHolder<FeatureHolder>>()); inSequence(calls);

        }});
        // @formatter:on

        collectionProperty.remove(childFeatureHolder);
    }

    private static interface OtherFeatureHolder extends FeatureHolder {

    }

}
