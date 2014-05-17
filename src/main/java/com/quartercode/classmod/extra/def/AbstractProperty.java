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
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * An abstract property is an implementation of the {@link Property} interface.<br>
 * <br>
 * The setter of every abstract property keeps track of {@link ChildFeatureHolder}s.
 * That means that the parent of a {@link ChildFeatureHolder} value is set to the holder of the property.
 * If an old {@link ChildFeatureHolder} value is replaced by another object, the parent of the old value is set to null.
 * 
 * @param <T> The type of object which can be stored inside the abstract property.
 * @see Property
 */
public abstract class AbstractProperty<T> extends AbstractFeature implements Property<T> {

    private static final List<Class<?>> GETTER_PARAMETERS = new ArrayList<>();
    private static final List<Class<?>> SETTER_PARAMETERS = new ArrayList<>();

    static {

        SETTER_PARAMETERS.add(Object.class);

    }

    private boolean                     ignoreEquals;

    private boolean                     intialized;
    private Function<T>                 getter;
    private Function<Void>              setter;

    /**
     * Creates a new empty abstract property.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected AbstractProperty() {

    }

    /**
     * Creates a new abstract property with the given name and {@link FeatureHolder}.
     * 
     * @param name The name of the abstract property.
     * @param holder The feature holder which has and uses the new abstract property.
     */
    public AbstractProperty(String name, FeatureHolder holder) {

        super(name, holder);
    }

    /**
     * Creates a new abstract property with the given name and {@link FeatureHolder}.
     * Also sets the initially stored object.
     * 
     * @param name The name of the abstract property.
     * @param holder The feature holder which has and uses the new abstract property.
     * @param initialValue The value the new abstract property has directly after creation.
     */
    public AbstractProperty(String name, FeatureHolder holder, T initialValue) {

        super(name, holder);

        if (initialValue != null) {
            if (initialValue instanceof ChildFeatureHolder && TypeUtils.isInstance(getHolder(), ((ChildFeatureHolder<?>) initialValue).getParentType())) {
                // This cast is always true because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
                @SuppressWarnings ("unchecked")
                ChildFeatureHolder<FeatureHolder> childFeatureHolder = (ChildFeatureHolder<FeatureHolder>) initialValue;
                childFeatureHolder.setParent(getHolder());
            }

            setInternal(initialValue);
        }
    }

    @Override
    public void initialize(PropertyDefinition<T> definition) {

        intialized = true;

        ignoreEquals = definition.isIgnoreEquals();

        List<FunctionExecutorContext<T>> getterExecutors = new ArrayList<>();
        List<FunctionExecutorContext<Void>> setterExecutors = new ArrayList<>();

        // Add the custom getter/setter executors
        for (Entry<String, FunctionExecutor<T>> executor : definition.getGetterExecutorsForVariant(getHolder().getClass()).entrySet()) {
            getterExecutors.add(new DefaultFunctionExecutorContext<>(executor.getKey(), executor.getValue()));
        }
        for (Entry<String, FunctionExecutor<Void>> executor : definition.getSetterExecutorsForVariant(getHolder().getClass()).entrySet()) {
            setterExecutors.add(new DefaultFunctionExecutorContext<>(executor.getKey(), executor.getValue()));
        }

        // Add getter executor
        getterExecutors.add(new DefaultFunctionExecutorContext<>("getInternal", new FunctionExecutor<T>() {

            @Override
            public T invoke(FunctionInvocation<T> invocation, Object... arguments) {

                T value = getInternal();
                invocation.next(arguments);
                return value;
            }

        }));

        // Add setter executor
        setterExecutors.add(new DefaultFunctionExecutorContext<>("setInternal", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                T oldValue = getInternal();
                if (oldValue instanceof ChildFeatureHolder) {
                    Object parent = ((ChildFeatureHolder<?>) oldValue).getParent();
                    if (parent != null && parent.equals(getHolder())) {
                        ((ChildFeatureHolder<?>) oldValue).setParent(null);
                    }
                }

                // The only caller (set()) verified the type by a compiler-safe generic parameter
                @SuppressWarnings ("unchecked")
                T value = (T) arguments[0];

                setInternal(value);

                if (value instanceof ChildFeatureHolder && TypeUtils.isInstance(getHolder(), ((ChildFeatureHolder<?>) value).getParentType())) {
                    // This cast is always true because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
                    @SuppressWarnings ("unchecked")
                    ChildFeatureHolder<FeatureHolder> childFeatureHolder = (ChildFeatureHolder<FeatureHolder>) value;
                    childFeatureHolder.setParent(getHolder());
                }

                return invocation.next(arguments);
            }

        }));

        /*
         * Create the dummy getter/setter functions
         */
        getter = new DummyFunction<>("get", getHolder(), GETTER_PARAMETERS, getterExecutors);
        setter = new DummyFunction<>("set", getHolder(), SETTER_PARAMETERS, setterExecutors);
    }

    @Override
    public boolean isInitialized() {

        return intialized;
    }

    @Override
    public T get() {

        return getter.invoke();
    }

    @Override
    public void set(T value) {

        setter.invoke(value);
    }

    /**
     * Returns the stored object without invoking the getter {@link FunctionExecutor}s.
     * This method is used at the end of the {@link #get()} {@link FunctionExecutor} chain in order to perform the actual get operation.
     * 
     * @return The object that is stored by the property.
     */
    protected abstract T getInternal();

    /**
     * Changes the stored object without invoking the setter {@link FunctionExecutor}s.
     * This method is used at the end of the {@link #set(Object)} {@link FunctionExecutor} chain in order to perform the actual set operation.
     * 
     * @param value The new value that should be stored by the property.
     */
    protected abstract void setInternal(T value);

    @Override
    public int hashCode() {

        if (ignoreEquals) {
            return 0;
        }

        final int prime = 31;
        int result = super.hashCode();
        Object content = getInternal();
        result = prime * result + (content == null ? 0 : content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (ignoreEquals) {
            return true;
        }

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractProperty<?> other = (AbstractProperty<?>) obj;
        if (this.getInternal() == null) {
            if (other.getInternal() != null) {
                return false;
            }
        } else if (!valueEquals(this.getInternal(), other.getInternal())) {
            return false;
        }
        return true;
    }

    private boolean valueEquals(Object value1, Object value2) {

        if (value1.getClass().isArray() && value2.getClass().isArray()) {
            return Arrays.equals((Object[]) value1, (Object[]) value2);
        } else {
            return value1.equals(value2);
        }
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + getName() + ", content=" + getInternal() + "]";
    }

}
