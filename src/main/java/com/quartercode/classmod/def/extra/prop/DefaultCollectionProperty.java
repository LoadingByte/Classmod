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

    private static final List<Class<?>> GETTER_PARAMETERS        = new ArrayList<>();
    private static final List<Class<?>> ADDER_REMOVER_PARAMETERS = new ArrayList<>();

    static {

        ADDER_REMOVER_PARAMETERS.add(Object.class);

    }

    @XmlAnyElement (lax = true)
    private Storage<C>                  storage;

    private boolean                     intialized;
    private boolean                     hidden;
    private boolean                     persistent;
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

        List<FunctionExecutorWrapper<C>> getterExecutors = new ArrayList<>();
        List<FunctionExecutorWrapper<Void>> adderExecutors = new ArrayList<>();
        List<FunctionExecutorWrapper<Void>> removerExecutors = new ArrayList<>();

        // Add the custom getter/adder/remover executors
        getterExecutors.addAll(definition.getGetterExecutorsForVariant(getHolder().getClass()).values());
        adderExecutors.addAll(definition.getAdderExecutorsForVariant(getHolder().getClass()).values());
        removerExecutors.addAll(definition.getRemoverExecutorsForVariant(getHolder().getClass()).values());

        // Add default executors
        getterExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultGetterFunctionExecutor(), Priorities.DEFAULT));
        adderExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultAdderFunctionExecutor(), Priorities.DEFAULT));
        removerExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultRemoverFunctionExecutor(), Priorities.DEFAULT));

        /*
         * Create the dummy getter/adder/remover functions
         */
        getter = new DummyFunction<>("get", getHolder(), GETTER_PARAMETERS, getterExecutors);
        adder = new DummyFunction<>("add", getHolder(), ADDER_REMOVER_PARAMETERS, adderExecutors);
        remover = new DummyFunction<>("remove", getHolder(), ADDER_REMOVER_PARAMETERS, removerExecutors);
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
            // Allow to retrieve the stored value even if the feature wasn't initialized yet
            // That is needed for FeatureHolder tree walkers to be able to retrieve children from ValueSupplier objects
            return storage.get();
        }
    }

    @Override
    public void add(E element) {

        adder.invoke(element);
    }

    @Override
    public void remove(E element) {

        remover.invoke(element);
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

            C collection = unmodifiable(storage.get());
            invocation.next(arguments);
            return collection;
        }

        // The casts always return the right value if C is no implementation (e.g. ArrayList instead of just List)
        @SuppressWarnings ("unchecked")
        private C unmodifiable(C collection) {

            if (collection instanceof List) {
                return (C) Collections.unmodifiableList(new ArrayList<>(collection));
            } else if (collection instanceof Set) {
                return (C) Collections.unmodifiableSet(new HashSet<>(collection));
            } else if (collection instanceof SortedSet) {
                return (C) Collections.unmodifiableSortedSet(new TreeSet<>(collection));
            } else {
                return (C) Collections.unmodifiableCollection(collection);
            }
        }

    }

    private class DefaultAdderFunctionExecutor implements FunctionExecutor<Void> {

        @Override
        public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

            C collection = storage.get();
            // The only caller (add()) verified the type by a compiler-safe generic parameter
            @SuppressWarnings ("unchecked")
            E element = (E) arguments[0];

            if (element instanceof ChildFeatureHolder && TypeUtils.isInstance(getHolder(), ((ChildFeatureHolder<?>) element).getParentType())) {
                // This cast is always true because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
                @SuppressWarnings ("unchecked")
                ChildFeatureHolder<FeatureHolder> childFeatureHolder = (ChildFeatureHolder<FeatureHolder>) element;
                childFeatureHolder.setParent(getHolder());
            }

            collection.add(element);
            storage.set(collection);

            return invocation.next(arguments);
        }

    }

    private class DefaultRemoverFunctionExecutor implements FunctionExecutor<Void> {

        @Override
        public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

            C collection = storage.get();
            // The only caller (remove()) verified the type by a compiler-safe generic parameter
            @SuppressWarnings ("unchecked")
            E element = (E) arguments[0];

            if (collection.contains(element)) {
                if (element instanceof ChildFeatureHolder) {
                    Object parent = ((ChildFeatureHolder<?>) element).getParent();
                    if (parent != null && parent.equals(getHolder())) {
                        ((ChildFeatureHolder<?>) element).setParent(null);
                    }
                }

                collection.remove(element);
                storage.set(collection);
            }

            return invocation.next(arguments);
        }

    }

}
