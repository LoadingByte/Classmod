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

import java.util.Collection;
import java.util.Map;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeatureDefinition;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.util.FunctionDefinitionFactory;

/**
 * An abstract collection property definition is used to retrieve a {@link CollectionProperty} from a {@link FeatureHolder}.
 * It's an implementation of the {@link CollectionPropertyDefinition} interface.<br>
 * <br>
 * Every definition contains the name of the {@link CollectionProperty}, as well as the getter, adder and remover {@link FunctionExecutor}s that are used.
 * You can use an abstract collection property definition to construct a new instance of the defined {@link CollectionProperty} through {@link #create(FeatureHolder)}.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} of the {@link Property}.
 * @param <C> The type of the {@link Collection} the defined {@link Property} stores.
 * @see CollectionPropertyDefinition
 * @see CollectionProperty
 * @see FunctionExecutor
 */
public abstract class AbstractCollectionPropertyDefinition<E, C extends Collection<E>> extends AbstractFeatureDefinition<CollectionProperty<E, C>> implements CollectionPropertyDefinition<E, C> {

    private final FunctionDefinition<C>    getter;
    private final FunctionDefinition<Void> adder;
    private final FunctionDefinition<Void> remover;

    /**
     * Creates a new abstract collection property definition for defining a {@link CollectionProperty} with the given name.
     * 
     * @param name The name of the defined {@link CollectionProperty}.
     */
    public AbstractCollectionPropertyDefinition(String name) {

        super(name);

        getter = FunctionDefinitionFactory.create(name);
        adder = FunctionDefinitionFactory.create(name, Object.class);
        remover = FunctionDefinitionFactory.create(name, Object.class);
    }

    @Override
    public Map<String, FunctionExecutor<C>> getGetterExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return getter.getExecutorsForVariant(variant);
    }

    @Override
    public void addGetterExecutor(Class<? extends FeatureHolder> variant, String name, FunctionExecutor<C> executor) {

        getter.addExecutor(variant, name, executor);
    }

    @Override
    public void removeGetterExecutor(Class<? extends FeatureHolder> variant, String name) {

        getter.removeExecutor(variant, name);
    }

    @Override
    public Map<String, FunctionExecutor<Void>> getAdderExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return adder.getExecutorsForVariant(variant);
    }

    @Override
    public void addAdderExecutor(Class<? extends FeatureHolder> variant, String name, FunctionExecutor<Void> executor) {

        adder.addExecutor(variant, name, executor);
    }

    @Override
    public void removeAdderExecutor(Class<? extends FeatureHolder> variant, String name) {

        adder.removeExecutor(variant, name);
    }

    @Override
    public Map<String, FunctionExecutor<Void>> getRemoverExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        return remover.getExecutorsForVariant(variant);
    }

    @Override
    public void addRemoverExecutor(Class<? extends FeatureHolder> variant, String name, FunctionExecutor<Void> executor) {

        remover.addExecutor(variant, name, executor);
    }

    @Override
    public void removeRemoverExecutor(Class<? extends FeatureHolder> variant, String name) {

        remover.removeExecutor(variant, name);
    }

}
