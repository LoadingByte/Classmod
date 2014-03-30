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
import java.util.HashMap;
import java.util.Map;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeatureDefinition;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Property;

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

    private final Map<String, FunctionExecutor<C>>    getterExecutors  = new HashMap<String, FunctionExecutor<C>>();
    private final Map<String, FunctionExecutor<Void>> adderExecutors   = new HashMap<String, FunctionExecutor<Void>>();
    private final Map<String, FunctionExecutor<Void>> removerExecutors = new HashMap<String, FunctionExecutor<Void>>();

    /**
     * Creates a new abstract collection property definition for defining a {@link CollectionProperty} with the given name.
     * 
     * @param name The name of the defined {@link CollectionProperty}.
     */
    public AbstractCollectionPropertyDefinition(String name) {

        super(name);
    }

    @Override
    public Map<String, FunctionExecutor<C>> getGetterExecutors() {

        return new HashMap<String, FunctionExecutor<C>>(getterExecutors);
    }

    @Override
    public void addGetterExecutor(String name, FunctionExecutor<C> executor) {

        getterExecutors.put(name, executor);
    }

    @Override
    public void removeGetterExecutor(String name) {

        getterExecutors.remove(name);
    }

    @Override
    public Map<String, FunctionExecutor<Void>> getAdderExecutors() {

        return new HashMap<String, FunctionExecutor<Void>>(adderExecutors);
    }

    @Override
    public void addAdderExecutor(String name, FunctionExecutor<Void> executor) {

        adderExecutors.put(name, executor);
    }

    @Override
    public void removeAdderExecutor(String name) {

        adderExecutors.remove(name);
    }

    @Override
    public Map<String, FunctionExecutor<Void>> getRemoverExecutors() {

        return new HashMap<String, FunctionExecutor<Void>>(removerExecutors);
    }

    @Override
    public void addRemoverExecutor(String name, FunctionExecutor<Void> executor) {

        removerExecutors.put(name, executor);
    }

    @Override
    public void removeRemoverExecutor(String name) {

        removerExecutors.remove(name);
    }

}
