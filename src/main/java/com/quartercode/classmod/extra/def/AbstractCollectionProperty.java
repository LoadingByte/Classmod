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

package com.quartercode.classmod.extra.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.util.FunctionDefinitionFactory;

/**
 * An abstract collection property is an implementation of the {@link CollectionProperty} interface.<br>
 * <br>
 * The adder and the remover of every abstract collection property keep track of {@link ChildFeatureHolder}s.
 * That means that the parent of a {@link ChildFeatureHolder} value is set to the holder of the property on add.
 * If an old {@link ChildFeatureHolder} value is removed, the parent of the old value is set to null.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} the abstract collection property holds.
 * @param <C> The type of {@link Collection} the abstract collection property stores.
 * @see CollectionProperty
 */
public abstract class AbstractCollectionProperty<E, C extends Collection<E>> extends AbstractFeature implements CollectionProperty<E, C> {

    private boolean        intialized;
    private Function<C>    getter;
    private Function<Void> adder;
    private Function<Void> remover;

    /**
     * Creates a new empty abstract collection property.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected AbstractCollectionProperty() {

    }

    /**
     * Creates a new abstract collection property with the given name, {@link FeatureHolder} and stored {@link Collection}.
     * 
     * @param name The name of the abstract collection property.
     * @param holder The feature holder which has and uses the new abstract collection property.
     * @param collection The {@link Collection} the new abstract collection property stores.
     */
    public AbstractCollectionProperty(String name, FeatureHolder holder, C collection) {

        super(name, holder);

        setInternal(collection);
    }

    @Override
    public void initialize(CollectionPropertyDefinition<E, C> definition) {

        intialized = true;

        // Create getter/adder/remover definitions for creating a function later on
        FunctionDefinition<C> getterDefinition = FunctionDefinitionFactory.create("get");
        // Using any object as parameter here; safe since the adder/remover is only called through the add()/remove() method that has the correct type as parameter
        FunctionDefinition<Void> adderDefinition = FunctionDefinitionFactory.create("add", Object.class);
        FunctionDefinition<Void> removerDefinition = FunctionDefinitionFactory.create("remove", Object.class);

        // Add the custom getter/adder/remover executors
        for (Entry<String, FunctionExecutor<C>> executor : definition.getGetterExecutorsForVariant(getHolder().getClass()).entrySet()) {
            getterDefinition.addExecutor(executor.getKey(), getHolder().getClass(), executor.getValue());
        }
        for (Entry<String, FunctionExecutor<Void>> executor : definition.getAdderExecutorsForVariant(getHolder().getClass()).entrySet()) {
            adderDefinition.addExecutor(executor.getKey(), getHolder().getClass(), executor.getValue());
        }
        for (Entry<String, FunctionExecutor<Void>> executor : definition.getRemoverExecutorsForVariant(getHolder().getClass()).entrySet()) {
            removerDefinition.addExecutor(executor.getKey(), getHolder().getClass(), executor.getValue());
        }

        // Use a random value as name for the internal executor so no one can override it
        String internalExecutorName = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));

        // Add getter executor
        getterDefinition.addExecutor(internalExecutorName, getHolder().getClass(), new FunctionExecutor<C>() {

            @Override
            public C invoke(FunctionInvocation<C> invocation, Object... arguments) throws ExecutorInvocationException {

                C collection = unmodifiable(getInternal());
                invocation.next(arguments);
                return collection;
            }

            // The casts always return the right value if C is no implementation (e.g. ArrayList instead of just List)
            @SuppressWarnings ("unchecked")
            private C unmodifiable(C collection) {

                if (collection instanceof List) {
                    return (C) Collections.unmodifiableList(new ArrayList<E>(collection));
                } else if (collection instanceof Set) {
                    return (C) Collections.unmodifiableSet(new HashSet<E>(collection));
                } else if (collection instanceof SortedSet) {
                    return (C) Collections.unmodifiableSortedSet(new TreeSet<E>(collection));
                } else {
                    return (C) Collections.unmodifiableCollection(collection);
                }
            }

        });

        // Add adder executor
        adderDefinition.addExecutor(internalExecutorName, getHolder().getClass(), new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                C collection = getInternal();
                // The only caller (add()) verified the type by a compiler-safe generic parameter
                @SuppressWarnings ("unchecked")
                E element = (E) arguments[0];

                if (element instanceof ChildFeatureHolder) {
                    // This cast is always true because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
                    @SuppressWarnings ("unchecked")
                    ChildFeatureHolder<FeatureHolder> childFeatureHolder = (ChildFeatureHolder<FeatureHolder>) element;
                    childFeatureHolder.setParent(getHolder());
                }

                collection.add(element);
                setInternal(collection);

                return invocation.next(arguments);
            }

        });

        // Add remover executor
        removerDefinition.addExecutor(internalExecutorName, getHolder().getClass(), new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                C collection = getInternal();
                // The only caller (remove()) verified the type by a compiler-safe generic parameter
                @SuppressWarnings ("unchecked")
                E element = (E) arguments[0];

                if (collection.contains(element)) {
                    if (element instanceof ChildFeatureHolder && ((ChildFeatureHolder<?>) element).getParent().equals(getHolder())) {
                        ((ChildFeatureHolder<?>) element).setParent(null);
                    }
                    collection.remove(element);
                    setInternal(collection);
                }

                return invocation.next(arguments);
            }

        });

        /*
         * Create the getter/adder/remover functions
         * We can't use FeatureHolder#get here because that method would add the new function to the feature holder.
         * We also can't use a new instance of that feature holder because the functions needs to believe that its holder is the property's one.
         */
        getter = getterDefinition.create(getHolder());
        getter.initialize(getterDefinition);
        adder = adderDefinition.create(getHolder());
        adder.initialize(adderDefinition);
        remover = removerDefinition.create(getHolder());
        remover.initialize(removerDefinition);
    }

    @Override
    public boolean isInitialized() {

        return intialized;
    }

    @Override
    public C get() throws ExecutorInvocationException {

        return getter.invoke();
    }

    @Override
    public void add(E element) throws ExecutorInvocationException {

        adder.invoke(element);
    }

    @Override
    public void remove(E element) throws ExecutorInvocationException {

        remover.invoke(element);
    }

    /**
     * Returns the stored {@link Collection} without invoking the getter {@link FunctionExecutor}s.
     * This method is used at the end of the {@link #get()} {@link FunctionExecutor} chain in order to perform the actual get operation.
     * 
     * @return The {@link Collection} that is stored by the collection property.
     */
    protected abstract C getInternal();

    /**
     * Changes the stored {@link Collection} to an entirely new object.
     * This method is used in the constructor and at the end of the adder/remover in order to set or change the stored {@link Collection}.
     * 
     * @param collection The new {@link Collection} that should be stored by the collection property.
     */
    protected abstract void setInternal(C collection);

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        Object content = getInternal();
        result = prime * result + (content == null ? 0 : content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractCollectionProperty<?, ?> other = (AbstractCollectionProperty<?, ?>) obj;
        if (this.getInternal() == null) {
            if (other.getInternal() != null) {
                return false;
            }
        } else if (!this.getInternal().equals(other.getInternal())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + getName() + ", collection=" + getInternal() + "]";
    }

}
