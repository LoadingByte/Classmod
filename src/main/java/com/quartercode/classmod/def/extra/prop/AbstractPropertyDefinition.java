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

import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.def.base.AbstractFeatureDefinition;
import com.quartercode.classmod.extra.func.FunctionDefinition;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.Priorities;
import com.quartercode.classmod.extra.prop.Property;
import com.quartercode.classmod.extra.prop.PropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;
import com.quartercode.classmod.extra.valuefactory.ValueFactory;

/**
 * An abstract property definition is used to retrieve a {@link Property} from a {@link FeatureHolder}.
 * The class is the default implementation of the {@link PropertyDefinition} interface.<br>
 * <br>
 * Every definition contains the name of the property, as well as the getter and setter {@link FunctionExecutor}s that are used.
 * You can use an abstract property definition to construct a new instance of the defined property through {@link #create(FeatureHolder)}.
 * 
 * @param <T> The type of object which can be stored inside the defined property.
 * @see PropertyDefinition
 * @see Property
 * @see FunctionExecutor
 */
public abstract class AbstractPropertyDefinition<T> extends AbstractFeatureDefinition<Property<T>> implements PropertyDefinition<T> {

    private Storage<T>                     storageTemplate;
    private ValueFactory<T>                initialValueFactory;
    private boolean                        hidden;

    private final FunctionDefinition<T>    getter;
    private final FunctionDefinition<Void> setter;

    /**
     * Creates a new abstract property definition for defining a {@link Property} with the given name.
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     */
    public AbstractPropertyDefinition(String name, Storage<T> storageTemplate) {

        super(name);

        Validate.notNull(storageTemplate, "The storage template of an abstract property definition cannot be null");

        this.storageTemplate = storageTemplate;

        getter = new InternalDefaultFunctionDefinition<>(name);
        setter = new InternalDefaultFunctionDefinition<>(name, Object.class);
    }

    /**
     * Creates a new abstract property definition for defining a {@link Property} with the given name and initial value.
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     * @param initialValueFactory A {@link ValueFactory} that returns initial value objects for all created properties.
     */
    public AbstractPropertyDefinition(String name, Storage<T> storageTemplate, ValueFactory<T> initialValueFactory) {

        this(name, storageTemplate);

        this.initialValueFactory = initialValueFactory;
    }

    /**
     * Creates a new abstract property definition for defining a {@link Property} with the given name and hiding flag.
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     * @param hidden Whether the value of the defined property should be excluded from equality checks of its feature holder.
     *        See {@link Hideable} for more information.
     */
    public AbstractPropertyDefinition(String name, Storage<T> storageTemplate, boolean hidden) {

        this(name, storageTemplate);

        this.hidden = hidden;
    }

    /**
     * Creates a new abstract property definition for defining a {@link Property} with the given name, initial value, and hiding flag.
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     * @param initialValueFactory A {@link ValueFactory} that returns initial value objects for all created properties.
     * @param hidden Whether the value of the defined collection property should be excluded from equality checks of its feature holder.
     *        See {@link Hideable} for more information.
     */
    public AbstractPropertyDefinition(String name, Storage<T> storageTemplate, ValueFactory<T> initialValueFactory, boolean hidden) {

        this(name, storageTemplate);

        this.initialValueFactory = initialValueFactory;
        this.hidden = hidden;
    }

    @Override
    public boolean isHidden() {

        return hidden;
    }

    @Override
    public Map<String, FunctionExecutorWrapper<T>> getGetterExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return getter.getExecutorsForVariant(variant);
    }

    @Override
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor) {

        addGetterExecutor(name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor, int priority) {

        getter.addExecutor(name, variant, executor, priority);
    }

    @Override
    public void removeGetterExecutor(String name, Class<? extends FeatureHolder> variant) {

        getter.removeExecutor(name, variant);
    }

    @Override
    public Map<String, FunctionExecutorWrapper<Void>> getSetterExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return setter.getExecutorsForVariant(variant);
    }

    @Override
    public void addSetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        addSetterExecutor(name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addSetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        setter.addExecutor(name, variant, executor, priority);
    }

    @Override
    public void removeSetterExecutor(String name, Class<? extends FeatureHolder> variant) {

        setter.removeExecutor(name, variant);
    }

    /**
     * Creates a new {@link Storage} instance from the stored storage template.
     * This method should be only used by subclasses.
     * 
     * @return A new storage instance.
     */
    protected Storage<T> newStorage() {

        return storageTemplate.reproduce();
    }

    /**
     * Returns an initial value object that can be immediately used for a new {@link Property} instance.
     * The returned object may be {@code null} if no initial value factory is supplied or the factory returns {@code null}.
     * It should be only used by subclasses.
     * 
     * @return A ready-for-use initial value object.
     */
    protected T newInitialValue() {

        return initialValueFactory == null ? null : initialValueFactory.get();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (hidden ? 1231 : 1237);
        result = prime * result + (initialValueFactory == null ? 0 : initialValueFactory.hashCode());
        result = prime * result + (storageTemplate == null ? 0 : storageTemplate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof AbstractPropertyDefinition) || !super.equals(obj)) {
            return false;
        } else {
            AbstractPropertyDefinition<?> other = (AbstractPropertyDefinition<?>) obj;
            return hidden == other.hidden && Objects.equals(initialValueFactory, other.initialValueFactory) && Objects.equals(storageTemplate, other.storageTemplate);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "getter", "setter");
    }

}
