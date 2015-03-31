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

package com.quartercode.classmod.def.extra.prop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.base.AbstractFeature;
import com.quartercode.classmod.def.extra.func.DefaultFunctionExecutorWrapper;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;
import com.quartercode.classmod.extra.func.Priorities;
import com.quartercode.classmod.extra.prop.Property;
import com.quartercode.classmod.extra.prop.PropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;

/**
 * The default implementation of the {@link Property} interface.<br>
 * <br>
 * The setter of every default property keeps track of {@link ChildFeatureHolder}s.
 * That means that the parent of a child feature holder value is set to the holder of the property.
 * If an old child feature holder value is replaced by another object, the parent of the old value is set to null.
 * 
 * @param <T> The type of object that can be stored inside the default property.
 * @see Property
 */
@XmlRootElement
public class DefaultProperty<T> extends AbstractFeature implements Property<T> {

    private static final List<Class<?>> GETTER_PARAMETERS = Collections.emptyList();
    private static final List<Class<?>> SETTER_PARAMETERS = Arrays.<Class<?>> asList(Object.class);

    @XmlAnyElement (lax = true)
    private Storage<T>                  storage;

    /*
     * Note that initialValue is only non-null if:
     * - The parameterized constructor of DefaultProperty was called.
     * - The set initialValue is not null.
     * 
     * The first condition implies that the property was created the first time
     * from a definition and not loaded by a serializer.
     */
    private T                           initialValue;

    private boolean                     intialized;
    private boolean                     hidden            = PropertyDefinition.HIDDEN_DEFAULT;
    private boolean                     persistent        = PropertyDefinition.PERSISTENT_DEFAULT;
    private Function<T>                 getter;
    private Function<Void>              setter;

    /**
     * Creates a new empty default property.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected DefaultProperty() {

    }

    /**
     * Creates a new default property with the given name, {@link FeatureHolder}, {@link Storage} implementation, and initial value.
     * 
     * @param name The name of the default property.
     * @param holder The feature holder which has and uses the new default property.
     * @param storage The {@link Storage} implementation that should be used by the property for storing its value.
     * @param initialValue The value the new default property has directly after creation.
     */
    public DefaultProperty(String name, FeatureHolder holder, Storage<T> storage, T initialValue) {

        super(name, holder);

        this.storage = storage;
        this.initialValue = initialValue;
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
    public void initialize(PropertyDefinition<T> definition) {

        intialized = true;

        hidden = definition.isHidden();
        persistent = definition.isPersistent();

        // Try to initialize the getter/setter functions; if no custom getter/setter executors are available, no function is created for that accessor
        initializeGetter(definition);
        initializeSetter(definition);

        // See the comment on the "initialValue" field for more information about why this works
        if (initialValue != null) {
            set(initialValue);
            initialValue = null;
        }
    }

    private void initializeGetter(PropertyDefinition<T> definition) {

        // Retrieve the custom getter executors
        Collection<FunctionExecutorWrapper<T>> definitionGetterExecutors = definition.getGetterExecutorsForVariant(getHolder().getClass()).values();

        if (!definitionGetterExecutors.isEmpty()) {
            // Add the custom getter executors
            List<FunctionExecutorWrapper<T>> getterExecutors = new ArrayList<>(definitionGetterExecutors);
            // Add the default getter executor
            getterExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultGetterFunctionExecutor(), Priorities.DEFAULT));
            // Create the dummy getter function
            getter = new DummyFunction<>("get", getHolder(), GETTER_PARAMETERS, getterExecutors);
        }
    }

    private void initializeSetter(PropertyDefinition<T> definition) {

        // Retrieve the custom setter executors
        Collection<FunctionExecutorWrapper<Void>> definitionSetterExecutors = definition.getSetterExecutorsForVariant(getHolder().getClass()).values();

        if (!definitionSetterExecutors.isEmpty()) {
            // Add the custom setter executors
            List<FunctionExecutorWrapper<Void>> setterExecutors = new ArrayList<>(definitionSetterExecutors);
            // Add the default setter executor
            setterExecutors.add(new DefaultFunctionExecutorWrapper<>(new DefaultSetterFunctionExecutor(), Priorities.DEFAULT));
            // Create the dummy setter function
            setter = new DummyFunction<>("set", getHolder(), SETTER_PARAMETERS, setterExecutors);
        }
    }

    @Override
    public boolean isInitialized() {

        return intialized;
    }

    @Override
    public T get() {

        // The getter function is null if no custom getter executors are available; in that case, the unnecessary function calling overhead is avoided
        if (getter != null) {
            return getter.invoke();
        } else {
            return getInternal();
        }
    }

    @Override
    public void set(T value) {

        // The setter function is null if no custom setter executors are available; in that case, the unnecessary function calling overhead is avoided
        if (setter != null) {
            setter.invoke(value);
        } else {
            setInternal(value);
        }
    }

    private T getInternal() {

        return storage.get();
    }

    private void setInternal(T value) {

        // Set the parent of any old stored ChildFeatureHolder if its parent is the holder of this property
        T oldValue = storage.get();
        if (oldValue instanceof ChildFeatureHolder) {
            Object oldValueParent = ((ChildFeatureHolder<?>) oldValue).getParent();

            if (oldValueParent != null && oldValueParent.equals(getHolder())) {
                ((ChildFeatureHolder<?>) oldValue).setParent(null);
            }
        }

        // Set the parent of any new ChildFeatureHolder to the holder of this property
        if (value instanceof ChildFeatureHolder && ((ChildFeatureHolder<?>) value).getParentType().isInstance(getHolder())) {
            // This cast is always valid because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
            @SuppressWarnings ("unchecked")
            ChildFeatureHolder<FeatureHolder> childFH = (ChildFeatureHolder<FeatureHolder>) value;
            childFH.setParent(getHolder());
        }

        storage.set(value);
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
        } else if (obj == null || ! (obj instanceof DefaultProperty) || !super.equals(obj)) {
            return false;
        } else {
            DefaultProperty<?> other = (DefaultProperty<?>) obj;
            return hidden == other.hidden
                    && persistent == other.persistent
                    && Objects.equals(storage, other.storage);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder", "initialValue", "intialized", "getter", "setter");
    }

    private class DefaultGetterFunctionExecutor implements FunctionExecutor<T> {

        @Override
        public T invoke(FunctionInvocation<T> invocation, Object... arguments) {

            T value = getInternal();

            invocation.next(arguments);
            return value;
        }

    }

    private class DefaultSetterFunctionExecutor implements FunctionExecutor<Void> {

        @Override
        public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

            // The only caller (set()) verified the type by the compiler-safe generic parameter <T>
            @SuppressWarnings ("unchecked")
            T value = (T) arguments[0];
            setInternal(value);

            return invocation.next(arguments);
        }

    }

}
