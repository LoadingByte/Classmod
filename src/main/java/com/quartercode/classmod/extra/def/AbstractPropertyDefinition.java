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

import java.util.HashMap;
import java.util.Map;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeatureDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * An abstract property definition is used to retrieve a {@link Property} from a {@link FeatureHolder}.
 * It's an implementation of the {@link PropertyDefinition} interface.<br>
 * <br>
 * Every definition contains the name of the {@link Property}, as well as the getter and setter {@link FunctionExecutor}s that are used.
 * You can use an abstract property definition to construct a new instance of the defined {@link Property} through {@link #create(FeatureHolder)}.
 * 
 * @param <T> The type of object which can be stored inside the defined {@link Property}.
 * @see PropertyDefinition
 * @see Property
 * @see FunctionExecutor
 */
public abstract class AbstractPropertyDefinition<T> extends AbstractFeatureDefinition<Property<T>> implements PropertyDefinition<T> {

    private final Map<String, FunctionExecutor<T>>    getterExecutors = new HashMap<String, FunctionExecutor<T>>();
    private final Map<String, FunctionExecutor<Void>> setterExecutors = new HashMap<String, FunctionExecutor<Void>>();

    /**
     * Creates a new abstract property definition for defining a {@link Property} with the given name.
     * 
     * @param name The name of the defined {@link Property}.
     */
    public AbstractPropertyDefinition(String name) {

        super(name);
    }

    @Override
    public Map<String, FunctionExecutor<T>> getGetterExecutors() {

        return new HashMap<String, FunctionExecutor<T>>(getterExecutors);
    }

    @Override
    public void addGetterExecutor(String name, FunctionExecutor<T> executor) {

        getterExecutors.put(name, executor);
    }

    @Override
    public void removeGetterExecutor(String name) {

        getterExecutors.remove(name);
    }

    @Override
    public Map<String, FunctionExecutor<Void>> getSetterExecutors() {

        return new HashMap<String, FunctionExecutor<Void>>(setterExecutors);
    }

    @Override
    public void addSetterExecutor(String name, FunctionExecutor<Void> executor) {

        setterExecutors.put(name, executor);
    }

    @Override
    public void removeSetterExecutor(String name) {

        setterExecutors.remove(name);
    }

}
