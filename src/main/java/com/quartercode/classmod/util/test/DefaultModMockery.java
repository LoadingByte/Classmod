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
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Priorities;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * The default implementation of the {@link ModMockery} interface.
 * 
 * @see ModMockery
 */
public class DefaultModMockery implements ModMockery {

    private final List<Pair<FunctionDefinition<?>, Pair<String, Class<? extends FeatureHolder>>>>              funcExecutors    = new ArrayList<>();

    private final List<Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>>>              propGetters      = new ArrayList<>();
    private final List<Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>>>              propSetters      = new ArrayList<>();

    private final List<Pair<CollectionPropertyDefinition<?, ?>, Pair<String, Class<? extends FeatureHolder>>>> collPropGetters  = new ArrayList<>();
    private final List<Pair<CollectionPropertyDefinition<?, ?>, Pair<String, Class<? extends FeatureHolder>>>> collPropAdders   = new ArrayList<>();
    private final List<Pair<CollectionPropertyDefinition<?, ?>, Pair<String, Class<? extends FeatureHolder>>>> collPropRemovers = new ArrayList<>();

    private static <D> void storeFuncExec(List<Pair<D, Pair<String, Class<? extends FeatureHolder>>>> list, D definition, String name, Class<? extends FeatureHolder> variant) {

        list.add(Pair.of(definition, Pair.<String, Class<? extends FeatureHolder>> of(name, variant)));
    }

    @Override
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor) {

        addFuncExec(definition, name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor, int priority) {

        // Store the mock function executor
        storeFuncExec(funcExecutors, definition, name, variant);

        // Register the mock function executor
        definition.addExecutor(name, variant, executor, priority);
    }

    @Override
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor) {

        addPropGetter(definition, name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor, int priority) {

        // Store the mock property getter
        storeFuncExec(propGetters, definition, name, variant);

        // Register the mock property getter
        definition.addGetterExecutor(name, variant, executor, priority);
    }

    @Override
    public void addPropSetter(PropertyDefinition<?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        addPropSetter(definition, name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addPropSetter(PropertyDefinition<?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        // Store the mock property setter
        storeFuncExec(propSetters, definition, name, variant);

        // Register the mock property setter
        definition.addSetterExecutor(name, variant, executor, priority);
    }

    @Override
    public <C extends Collection<?>> void addCollPropGetter(CollectionPropertyDefinition<?, C> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor) {

        addCollPropGetter(definition, name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public <C extends Collection<?>> void addCollPropGetter(CollectionPropertyDefinition<?, C> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor, int priority) {

        // Store the mock collection property getter
        storeFuncExec(collPropGetters, definition, name, variant);

        // Register the mock collection property getter
        definition.addGetterExecutor(name, variant, executor, priority);
    }

    @Override
    public void addCollPropAdder(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        addCollPropAdder(definition, name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addCollPropAdder(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        // Store the mock collection property adder
        storeFuncExec(collPropAdders, definition, name, variant);

        // Register the mock collection property adder
        definition.addAdderExecutor(name, variant, executor, priority);
    }

    @Override
    public void addCollPropRemover(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        addCollPropRemover(definition, name, variant, executor, Priorities.DEFAULT);
    }

    @Override
    public void addCollPropRemover(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        // Store the mock collection property remover
        storeFuncExec(collPropRemovers, definition, name, variant);

        // Register the mock collection property remover
        definition.addRemoverExecutor(name, variant, executor, priority);
    }

    @Override
    public void close() {

        // Unregister all mock function executors
        for (Pair<FunctionDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> funcExecutorMapping : funcExecutors) {
            Pair<String, Class<? extends FeatureHolder>> params = funcExecutorMapping.getRight();
            funcExecutorMapping.getLeft().removeExecutor(params.getLeft(), params.getRight());
        }

        // Unregister all mock property getters/setters
        for (Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> propGetterMapping : propGetters) {
            Pair<String, Class<? extends FeatureHolder>> params = propGetterMapping.getRight();
            propGetterMapping.getLeft().removeGetterExecutor(params.getLeft(), params.getRight());
        }
        for (Pair<PropertyDefinition<?>, Pair<String, Class<? extends FeatureHolder>>> propSetterMapping : propSetters) {
            Pair<String, Class<? extends FeatureHolder>> params = propSetterMapping.getRight();
            propSetterMapping.getLeft().removeSetterExecutor(params.getLeft(), params.getRight());
        }

        // Unregister all mock collection property getters/adders/removers
        for (Pair<CollectionPropertyDefinition<?, ?>, Pair<String, Class<? extends FeatureHolder>>> collPropGetterMapping : collPropGetters) {
            Pair<String, Class<? extends FeatureHolder>> params = collPropGetterMapping.getRight();
            collPropGetterMapping.getLeft().removeGetterExecutor(params.getLeft(), params.getRight());
        }
        for (Pair<CollectionPropertyDefinition<?, ?>, Pair<String, Class<? extends FeatureHolder>>> collPropAdderMapping : collPropAdders) {
            Pair<String, Class<? extends FeatureHolder>> params = collPropAdderMapping.getRight();
            collPropAdderMapping.getLeft().removeAdderExecutor(params.getLeft(), params.getRight());
        }
        for (Pair<CollectionPropertyDefinition<?, ?>, Pair<String, Class<? extends FeatureHolder>>> collPropRemoverMapping : collPropRemovers) {
            Pair<String, Class<? extends FeatureHolder>> params = collPropRemoverMapping.getRight();
            collPropRemoverMapping.getLeft().removeRemoverExecutor(params.getLeft(), params.getRight());
        }
    }

}
