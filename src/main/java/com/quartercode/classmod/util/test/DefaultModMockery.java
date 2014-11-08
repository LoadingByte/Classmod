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

package com.quartercode.classmod.util.test;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * The default implementation of the {@link ModMockery} interface.
 * 
 * @see ModMockery
 */
public class DefaultModMockery implements ModMockery {

    private final List<Pair<FunctionDefinition<?>, Pair<String, Class<? extends FeatureHolder>>>> funcExecutors = new ArrayList<>();

    private final List<Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>>> propGetters   = new ArrayList<>();
    private final List<Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>>> propSetters   = new ArrayList<>();

    @Override
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor) {

        // Store the mock function executor
        Pair<String, Class<? extends FeatureHolder>> params = Pair.<String, Class<? extends FeatureHolder>> of(name, variant);
        funcExecutors.add(Pair.<FunctionDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> of(definition, params));

        // Register the mock function executor
        definition.addExecutor(name, variant, executor);
    }

    @Override
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor) {

        // Store the mock property getter
        Pair<String, Class<? extends FeatureHolder>> params = Pair.<String, Class<? extends FeatureHolder>> of(name, variant);
        propGetters.add(Pair.<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> of(definition, params));

        // Register the mock property getter
        definition.addGetterExecutor(name, variant, executor);
    }

    @Override
    public <T> void addPropSetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        // Store the mock property setter
        Pair<String, Class<? extends FeatureHolder>> params = Pair.<String, Class<? extends FeatureHolder>> of(name, variant);
        propSetters.add(Pair.<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> of(definition, params));

        // Register the mock property setter
        definition.addSetterExecutor(name, variant, executor);
    }

    @Override
    public void close() {

        // Unregister all mock function executors
        for (Pair<FunctionDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> funcExecutorMapping : funcExecutors) {
            Pair<String, Class<? extends FeatureHolder>> params = funcExecutorMapping.getRight();
            funcExecutorMapping.getLeft().removeExecutor(params.getLeft(), params.getRight());
        }

        // Unregister all mock property getters
        for (Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> propGetterMapping : propGetters) {
            Pair<String, Class<? extends FeatureHolder>> params = propGetterMapping.getRight();
            propGetterMapping.getLeft().removeGetterExecutor(params.getLeft(), params.getRight());
        }

        // Unregister all mock property setters
        for (Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> propSetterMapping : propSetters) {
            Pair<String, Class<? extends FeatureHolder>> params = propSetterMapping.getRight();
            propSetterMapping.getLeft().removeSetterExecutor(params.getLeft(), params.getRight());
        }
    }

}
