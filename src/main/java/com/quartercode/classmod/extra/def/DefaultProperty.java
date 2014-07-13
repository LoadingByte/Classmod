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
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Persistent;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.Storage;

/**
 * The {@link Persistent} default implementation of the {@link Property} interface.<br>
 * <br>
 * The setter of every default property keeps track of {@link ChildFeatureHolder}s.
 * That means that the parent of a child feature holder value is set to the holder of the property.
 * If an old child feature holder value is replaced by another object, the parent of the old value is set to null.
 * 
 * @param <T> The type of object that can be stored inside the default property.
 * @see Property
 */
@Persistent
@XmlRootElement
public class DefaultProperty<T> extends AbstractFeature implements Property<T> {

    private static final List<Class<?>> GETTER_PARAMETERS = new ArrayList<>();
    private static final List<Class<?>> SETTER_PARAMETERS = new ArrayList<>();

    static {

        SETTER_PARAMETERS.add(Object.class);

    }

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
    private boolean                     ignoreEquals;
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
    public void initialize(PropertyDefinition<T> definition) {

        intialized = true;

        ignoreEquals = definition.isIgnoreEquals();

        List<FunctionExecutor<T>> getterExecutors = new ArrayList<>();
        List<FunctionExecutor<Void>> setterExecutors = new ArrayList<>();

        // Add the custom getter/setter executors
        getterExecutors.addAll(definition.getGetterExecutorsForVariant(getHolder().getClass()).values());
        setterExecutors.addAll(definition.getSetterExecutorsForVariant(getHolder().getClass()).values());

        // Add default executor
        getterExecutors.add(new DefaultGetterFunctionExecutor());
        setterExecutors.add(new DefaultSetterFunctionExecutor());

        /*
         * Create the dummy getter/setter functions
         */
        getter = new DummyFunction<>("get", getHolder(), GETTER_PARAMETERS, getterExecutors);
        setter = new DummyFunction<>("set", getHolder(), SETTER_PARAMETERS, setterExecutors);

        // See the comment on the "initialValue" field for more information on why this works
        if (initialValue != null) {
            set(initialValue);
            initialValue = null;
        }
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

    @Override
    public int hashCode() {

        return ignoreEquals ? 0 : realHashCode();
    }

    private int realHashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (ignoreEquals ? 1231 : 1237);
        result = prime * result + (storage == null ? 0 : storage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        boolean doIgnoreEquals = ignoreEquals || obj instanceof DefaultProperty && ((DefaultProperty<?>) obj).ignoreEquals;
        return doIgnoreEquals ? true : realEquals(obj);
    }

    private boolean realEquals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultProperty)) {
            return false;
        } else {
            DefaultProperty<?> other = (DefaultProperty<?>) obj;
            if (ignoreEquals != other.ignoreEquals || !Objects.equals(storage, other.storage)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder", "initialValue", "intialized", "getter", "setter");
    }

    private class DefaultGetterFunctionExecutor implements FunctionExecutor<T> {

        @Override
        public T invoke(FunctionInvocation<T> invocation, Object... arguments) {

            T value = storage.get();
            invocation.next(arguments);
            return value;
        }

    }

    private class DefaultSetterFunctionExecutor implements FunctionExecutor<Void> {

        @Override
        public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

            T oldValue = storage.get();
            if (oldValue instanceof ChildFeatureHolder) {
                Object parent = ((ChildFeatureHolder<?>) oldValue).getParent();
                if (parent != null && parent.equals(getHolder())) {
                    ((ChildFeatureHolder<?>) oldValue).setParent(null);
                }
            }

            // The only caller (set()) verified the type by a compiler-safe generic parameter
            @SuppressWarnings ("unchecked")
            T value = (T) arguments[0];

            if (value instanceof ChildFeatureHolder && TypeUtils.isInstance(getHolder(), ((ChildFeatureHolder<?>) value).getParentType())) {
                // This cast is always true because the generic type parameter of ChildFeatureHolder must extend FeatureHolder
                @SuppressWarnings ("unchecked")
                ChildFeatureHolder<FeatureHolder> childFeatureHolder = (ChildFeatureHolder<FeatureHolder>) value;
                childFeatureHolder.setParent(getHolder());
            }

            storage.set(value);

            return invocation.next(arguments);
        }

    }

}
