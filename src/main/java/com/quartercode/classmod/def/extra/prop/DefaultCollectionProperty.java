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

package com.quartercode.classmod.def.extra.prop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.base.AbstractFeature;
import com.quartercode.classmod.def.extra.func.DefaultFunctionExecutorWrapper;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;
import com.quartercode.classmod.extra.func.Priorities;
import com.quartercode.classmod.extra.prop.CollectionProperty;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;

/**
 * The default implementation of the {@link CollectionProperty} interface.<br>
 * <br>
 * The adder and the remover of every default collection property keep track of {@link ChildFeatureHolder}s.
 * That means that the parent of a {@link ChildFeatureHolder} value is set to the holder of the property on add.
 * If an old {@link ChildFeatureHolder} value is removed, the parent of the old value is set to null.
 * 
 * @param <E> The type of object that can be stored inside the default collection property's {@link Collection}.
 * @param <C> The type of collection that can be stored inside the default collection property.
 * @see CollectionProperty
 */
@XmlRootElement
public class DefaultCollectionProperty<E, C extends Collection<E>> extends AbstractFeature implements CollectionProperty<E, C> {

    private static final List<Class<?>> GETTER_PARAMETERS            = Collections.emptyList();
    private static final List<Class<?>> ADDER_AND_REMOVER_PARAMETERS = Arrays.<Class<?>> asList(Object.class);

    @XmlAnyElement (lax = true)
    private Storage<C>                  storage;

    private boolean                     intialized;
    private boolean                     hidden                       = CollectionPropertyDefinition.HIDDEN_DEFAULT;
    private boolean                     persistent                   = CollectionPropertyDefinition.PERSISTENT_DEFAULT;
    private Function<C>                 getter;
    private Function<Void>              adder;
    private Function<Void>              remover;

    /**
     * Creates a new empty default collection property.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected DefaultCollectionProperty() {

    }

    /**
     * Creates a new default collection property with the given name, {@link FeatureHolder}, and {@link Storage} implementation.
     * 
     * @param name The name of the default collection property.
     * @param holder The feature holder which has and uses the new default collection property.
     * @param storage The {@link Storage} implementation that should be used by the default collection property for storing its {@link Collection}.
     */
    public DefaultCollectionProperty(String name, FeatureHolder holder, Storage<C> storage) {

        super(name, holder);

        this.storage = storage;
    }

    @Override
    public boolean isHidden() {

        return hidden;
    }

    @Override
    public boolean isPersistent() {

        return persistent;
    }

    @Override
    public void initialize(CollectionPropertyDefinition<E, C> definition) {

        intialized = true;

        hidden = definition.isHidden();
        persistent = definition.isPersistent();

        C newCollection = definition.newCollection();
        C oldCollection = storage.get();
        if (oldCollection != null) {
            newCollection.addAll(oldCollection);
        }
        storage.set(newCollection);

        // Try to initialize the getter/adder/remover functions; if no custom getter/adder/remover executors are available, no function is created for that accessor
        initializeGetter(definition);
        initializeAdder(definition);
        initializeRemover(definition);
    }

    private void initializeGetter(CollectionPropertyDefinition<E, C> definition) {

        // Retrieve the custom getter executors
        Collection<FunctionExecutorWrapper<C>> definitionGetterExecutors = definition.getGetterExecutorsForVariant(getHolder().getClass()).values();

        if (!definitionGetterExecutors.isEmpty()) {
            // Add the custom getter executors
            List<FunctionExecutorWrapper<C>> getterExecutors = new ArrayList<>(definitionGetterExecutors);
            // Add the default getter executor
            getterExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultGetterFunctionExecutor(), Priorities.DEFAULT));
            // Create the dummy getter function
            getter = new DummyFunction<>("get", getHolder(), GETTER_PARAMETERS, getterExecutors);
        }
    }

    private void initializeAdder(CollectionPropertyDefinition<E, C> definition) {

        // Retrieve the custom adder executors
        Collection<FunctionExecutorWrapper<Void>> definitionAdderExecutors = definition.getAdderExecutorsForVariant(getHolder().getClass()).values();

        if (!definitionAdderExecutors.isEmpty()) {
            // Add the custom adder executors
            List<FunctionExecutorWrapper<Void>> adderExecutors = new ArrayList<>(definitionAdderExecutors);
            // Add the default adder executor
            adderExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultAdderFunctionExecutor(), Priorities.DEFAULT));
            // Create the dummy adder function
            adder = new DummyFunction<>("add", getHolder(), ADDER_AND_REMOVER_PARAMETERS, adderExecutors);
        }
    }

    private void initializeRemover(CollectionPropertyDefinition<E, C> definition) {

        // Retrieve the custom remover executors
        Collection<FunctionExecutorWrapper<Void>> definitionRemoverExecutors = definition.getRemoverExecutorsForVariant(getHolder().getClass()).values();

        if (!definitionRemoverExecutors.isEmpty()) {
            // Add the custom remover executors
            List<FunctionExecutorWrapper<Void>> removerExecutors = new ArrayList<>(definitionRemoverExecutors);
            // Add the default remover executor
            removerExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultRemoverFunctionExecutor(), Priorities.DEFAULT));
            // Create the dummy remover function
            remover = new DummyFunction<>("remove", getHolder(), ADDER_AND_REMOVER_PARAMETERS, removerExecutors);
        }
    }

    @Override
    public boolean isInitialized() {

        return intialized;
    }

    @Override
    public C get() {

        if (getter != null) {
            return getter.invoke();
        } else {
            return getInternal();
        }
    }

    @Override
    public void add(E element) {

        if (adder != null) {
            adder.invoke(element);
        } else {
            addInternal(element);
        }
    }

    @Override
    public void remove(E element) {

        if (remover != null) {
            remover.invoke(element);
        } else {
            removeInternal(element);
        }
    }

    @SuppressWarnings ("unchecked")
    private C getInternal() {

        C collection = storage.get();

        /*
         * TODO: Decisions related to the returned unmodifiable collection.
         * 
         * 1) Decide whether an unmodifiable view on queues should be returned for queues.
         * It is important to note that a QueueProperty would be the only alternative for making the queue methods available.
         * However, that would result in the need for another DequeueProperty.
         * 
         * 2) Another thing to note is that classes which implement both List and Queue (e.g. LinkedList) are returned as a list by the first if-clause.
         * Therefore, the queue methods are not accessible on the return object.
         * Another thing to consider might be to return one big "UnmodifiableCollection" class which implements delegations for all methods of all collections.
         * It would implement all read methods from Collection, List, Set, SortedSet, Queue, Dequeue, ...
         * However, only calls on valid methods for the wrapped collection (e.g. a list) are allowed.
         * The advantage would be getting rid of this if-else-block and solving the problem mentioned above.
         */

        if (collection instanceof List) {
            return (C) Collections.unmodifiableList((List<?>) collection);
        } else if (collection instanceof Set) {
            return (C) Collections.unmodifiableSet((Set<?>) collection);
        } else if (collection instanceof SortedSet) {
            return (C) Collections.unmodifiableSortedSet((SortedSet<?>) collection);
        } else {
            return (C) Collections.unmodifiableCollection(collection);
        }
    }

    private void addInternal(E element) {

        C collection = storage.get();

        // Set the parent of any new ChildFeatureHolder to the holder of this property
        if (element instanceof ChildFeatureHolder && TypeUtils.isInstance(getHolder(), ((ChildFeatureHolder<?>) element).getParentType())) {
            // This cast is always true because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
            @SuppressWarnings ("unchecked")
            ChildFeatureHolder<FeatureHolder> childFeatureHolder = (ChildFeatureHolder<FeatureHolder>) element;
            childFeatureHolder.setParent(getHolder());
        }

        collection.add(element);
        storage.set(collection);
    }

    private void removeInternal(E element) {

        C collection = storage.get();

        if (collection.contains(element)) {
            // Set the parent of any old stored ChildFeatureHolder if its parent is the holder of this property
            if (element instanceof ChildFeatureHolder) {
                Object parent = ((ChildFeatureHolder<?>) element).getParent();

                if (parent != null && parent.equals(getHolder())) {
                    ((ChildFeatureHolder<?>) element).setParent(null);
                }
            }

            collection.remove(element);
            storage.set(collection);
        }
    }

    @Override
    public void clear() {

        // Cannot use one iterator because that would cause a ConcurrentModificationException
        while (!storage.get().isEmpty()) {
            remove(storage.get().iterator().next());
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (hidden ? 1231 : 1237);
        result = prime * result + (persistent ? 1231 : 1237);
        result = prime * result + (storage == null ? 0 : storage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultCollectionProperty) || !super.equals(obj)) {
            return false;
        } else {
            DefaultCollectionProperty<?, ?> other = (DefaultCollectionProperty<?, ?>) obj;
            return hidden == other.hidden
                    && persistent == other.persistent
                    && Objects.equals(storage, other.storage);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder", "intialized", "getter", "adder", "remover");
    }

    private class DefaultGetterFunctionExecutor implements FunctionExecutor<C> {

        @Override
        public C invoke(FunctionInvocation<C> invocation, Object... arguments) {

            C collection = getInternal();

            invocation.next(arguments);
            return collection;
        }

    }

    private class DefaultAdderFunctionExecutor implements FunctionExecutor<Void> {

        @Override
        public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

            // The only caller (add()) verified the type by the compiler-safe generic parameter <E>
            @SuppressWarnings ("unchecked")
            E element = (E) arguments[0];
            addInternal(element);

            return invocation.next(arguments);
        }

    }

    private class DefaultRemoverFunctionExecutor implements FunctionExecutor<Void> {

        @Override
        public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

            // The only caller (remove()) verified the type by the compiler-safe generic parameter <E>
            @SuppressWarnings ("unchecked")
            E element = (E) arguments[0];
            removeInternal(element);

            return invocation.next(arguments);
        }

    }

}
