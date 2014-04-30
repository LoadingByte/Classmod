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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;

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

    private static final List<Class<?>> GETTER_PARAMETERS        = new ArrayList<Class<?>>();
    private static final List<Class<?>> ADDER_REMOVER_PARAMETERS = new ArrayList<Class<?>>();

    static {

        ADDER_REMOVER_PARAMETERS.add(Object.class);

    }

    private boolean                     intialized;
    private Function<C>                 getter;
    private Function<Void>              adder;
    private Function<Void>              remover;

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

        Validate.notNull(collection, "A collection property must be supplied with a collection implementation to use");
        setInternal(collection);
    }

    @Override
    public void initialize(CollectionPropertyDefinition<E, C> definition) {

        intialized = true;

        List<FunctionExecutorContext<C>> getterExecutors = new ArrayList<FunctionExecutorContext<C>>();
        List<FunctionExecutorContext<Void>> adderExecutors = new ArrayList<FunctionExecutorContext<Void>>();
        List<FunctionExecutorContext<Void>> removerExecutors = new ArrayList<FunctionExecutorContext<Void>>();

        // Add the custom getter/adder/remover executors
        for (Entry<String, FunctionExecutor<C>> executor : definition.getGetterExecutorsForVariant(getHolder().getClass()).entrySet()) {
            getterExecutors.add(new DefaultFunctionExecutorContext<C>(executor.getKey(), executor.getValue()));
        }
        for (Entry<String, FunctionExecutor<Void>> executor : definition.getAdderExecutorsForVariant(getHolder().getClass()).entrySet()) {
            adderExecutors.add(new DefaultFunctionExecutorContext<Void>(executor.getKey(), executor.getValue()));
        }
        for (Entry<String, FunctionExecutor<Void>> executor : definition.getRemoverExecutorsForVariant(getHolder().getClass()).entrySet()) {
            removerExecutors.add(new DefaultFunctionExecutorContext<Void>(executor.getKey(), executor.getValue()));
        }

        // Add getter executor
        getterExecutors.add(new DefaultFunctionExecutorContext<C>("getInternal", new FunctionExecutor<C>() {

            @Override
            public C invoke(FunctionInvocation<C> invocation, Object... arguments) {

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

        }));

        // Add adder executor
        adderExecutors.add(new DefaultFunctionExecutorContext<Void>("addInternal", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                C collection = getInternal();
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
                setInternal(collection);

                return invocation.next(arguments);
            }

        }));

        // Add remover executor
        removerExecutors.add(new DefaultFunctionExecutorContext<Void>("removeInternal", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                C collection = getInternal();
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
                    setInternal(collection);
                }

                return invocation.next(arguments);
            }

        }));

        /*
         * Create the dummy getter/adder/remover functions
         */
        getter = new DummyFunction<C>("get", getHolder(), GETTER_PARAMETERS, getterExecutors);
        adder = new DummyFunction<Void>("add", getHolder(), ADDER_REMOVER_PARAMETERS, adderExecutors);
        remover = new DummyFunction<Void>("remove", getHolder(), ADDER_REMOVER_PARAMETERS, removerExecutors);
    }

    @Override
    public boolean isInitialized() {

        return intialized;
    }

    @Override
    public C get() {

        return getter.invoke();
    }

    @Override
    public void add(E element) {

        adder.invoke(element);
    }

    @Override
    public void remove(E element) {

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
