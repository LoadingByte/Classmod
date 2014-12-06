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

import java.util.Collection;
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
import com.quartercode.classmod.extra.prop.CollectionProperty;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;
import com.quartercode.classmod.extra.valuefactory.ValueFactory;

/**
 * An abstract collection property definition is used to retrieve a {@link CollectionProperty} from a {@link FeatureHolder}.
 * The class is the default implementation of the {@link CollectionPropertyDefinition} interface.<br>
 * <br>
 * Every definition contains the name of the collection property, as well as the getter, adder and remover {@link FunctionExecutor}s that are used.
 * You can use an abstract collection property definition to construct a new instance of the defined collection property through {@link #create(FeatureHolder)}.
 * 
 * @param <E> The type of object that can be stored inside the defined collection property's {@link Collection}.
 * @param <C> The type of collection that can be stored inside the defined collection property.
 * @see CollectionPropertyDefinition
 * @see CollectionProperty
 * @see FunctionExecutor
 */
public abstract class AbstractCollectionPropertyDefinition<E, C extends Collection<E>> extends AbstractFeatureDefinition<CollectionProperty<E, C>> implements CollectionPropertyDefinition<E, C> {

    private Storage<C>                     storageTemplate;
    private ValueFactory<C>                collectionFactory;
    private boolean                        hidden;

    private final FunctionDefinition<C>    getter;
    private final FunctionDefinition<Void> adder;
    private final FunctionDefinition<Void> remover;

    /**
     * Creates a new abstract collection property definition for defining a {@link CollectionProperty} with the given name and {@link Storage} implementation.
     * Also sets a template {@link Collection} whose clones are used by collection property instances.
     * 
     * @param name The name of the defined collection property.
     * @param storageTemplate A storage implementation that should be reproduced and used by every created collection property for storing collections.
     * @param collectionFactory A {@link ValueFactory} that returns new collections for all created collection properties.
     */
    public AbstractCollectionPropertyDefinition(String name, Storage<C> storageTemplate, ValueFactory<C> collectionFactory) {

        super(name);

        Validate.notNull(storageTemplate, "The storage template of a default collection property definition cannot be null");
        Validate.notNull(collectionFactory, "The collection factory of a default collection property definition cannot be null");

        this.storageTemplate = storageTemplate;
        this.collectionFactory = collectionFactory;

        getter = new InternalDefaultFunctionDefinition<>(name);
        adder = new InternalDefaultFunctionDefinition<>(name, Object.class);
        remover = new InternalDefaultFunctionDefinition<>(name, Object.class);
    }

    /**
     * Creates a new abstract collection property definition for defining a {@link CollectionProperty} with the given name, {@link Storage} implementation, and hiding flag.
     * Also sets a template {@link Collection} whose clones are used by collection property instances.
     * 
     * @param name The name of the defined collection property.
     * @param storageTemplate A storage implementation that should be reproduced and used by every created collection property for storing collections.
     * @param collectionFactory A {@link ValueFactory} that returns new collections for all created collection properties.
     * @param hidden Whether the value of the defined collection property should be excluded from equality checks of its feature holder.
     *        See {@link Hideable} for more information.
     */
    public AbstractCollectionPropertyDefinition(String name, Storage<C> storageTemplate, ValueFactory<C> collectionFactory, boolean hidden) {

        this(name, storageTemplate, collectionFactory);

        this.hidden = hidden;
    }

    @Override
    public C newCollection() {

        return collectionFactory.get();
    }

    @Override
    public boolean isHidden() {

        return hidden;
    }

    @Override
    public Map<String, FunctionExecutorWrapper<C>> getGetterExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return getter.getExecutorsForVariant(variant);
    }

    @Override
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor) {

        addGetterExecutor(name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor, int priority) {

        getter.addExecutor(name, variant, executor, priority);
    }

    @Override
    public void removeGetterExecutor(String name, Class<? extends FeatureHolder> variant) {

        getter.removeExecutor(name, variant);
    }

    @Override
    public Map<String, FunctionExecutorWrapper<Void>> getAdderExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return adder.getExecutorsForVariant(variant);
    }

    @Override
    public void addAdderExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        addAdderExecutor(name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addAdderExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        adder.addExecutor(name, variant, executor, priority);
    }

    @Override
    public void removeAdderExecutor(String name, Class<? extends FeatureHolder> variant) {

        adder.removeExecutor(name, variant);
    }

    @Override
    public Map<String, FunctionExecutorWrapper<Void>> getRemoverExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return remover.getExecutorsForVariant(variant);
    }

    @Override
    public void addRemoverExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        addRemoverExecutor(name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addRemoverExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        remover.addExecutor(name, variant, executor, priority);
    }

    @Override
    public void removeRemoverExecutor(String name, Class<? extends FeatureHolder> variant) {

        remover.removeExecutor(name, variant);
    }

    /**
     * Creates a new {@link Storage} instance from the stored storage template.
     * This method should be only used by subclasses.
     * 
     * @return A new storage instance.
     */
    protected Storage<C> newStorage() {

        return storageTemplate.reproduce();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (collectionFactory == null ? 0 : collectionFactory.hashCode());
        result = prime * result + (hidden ? 1231 : 1237);
        result = prime * result + (storageTemplate == null ? 0 : storageTemplate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof AbstractCollectionPropertyDefinition) || !super.equals(obj)) {
            return false;
        } else {
            AbstractCollectionPropertyDefinition<?, ?> other = (AbstractCollectionPropertyDefinition<?, ?>) obj;
            return hidden == other.hidden && Objects.equals(collectionFactory, other.collectionFactory) && Objects.equals(storageTemplate, other.storageTemplate);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "getter", "adder", "remover");
    }

}
