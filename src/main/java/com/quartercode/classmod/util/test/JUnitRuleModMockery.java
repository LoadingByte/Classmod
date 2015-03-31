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

package com.quartercode.classmod.util.test;

import java.util.Collection;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.func.FunctionDefinition;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.prop.PropertyDefinition;

/**
 * A JUnit {@link Rule} that provides methods for a {@link ModMockery} and automatically closes the underlying mockery after the test.
 * 
 * @see ModMockery
 */
public class JUnitRuleModMockery extends ExternalResource implements ModMockery {

    private final ModMockery modMockery = new DefaultModMockery();

    @Override
    protected void after() {

        modMockery.close();
    }

    @Override
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor) {

        modMockery.addFuncExec(definition, name, variant, executor);
    }

    @Override
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor, int priority) {

        modMockery.addFuncExec(definition, name, variant, executor, priority);
    }

    @Override
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor) {

        modMockery.addPropGetter(definition, name, variant, executor);
    }

    @Override
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor, int priority) {

        modMockery.addPropGetter(definition, name, variant, executor, priority);
    }

    @Override
    public void addPropSetter(PropertyDefinition<?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        modMockery.addPropSetter(definition, name, variant, executor);
    }

    @Override
    public void addPropSetter(PropertyDefinition<?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        modMockery.addPropSetter(definition, name, variant, executor, priority);
    }

    @Override
    public <C extends Collection<?>> void addCollPropGetter(CollectionPropertyDefinition<?, C> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor) {

        modMockery.addCollPropGetter(definition, name, variant, executor);
    }

    @Override
    public <C extends Collection<?>> void addCollPropGetter(CollectionPropertyDefinition<?, C> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor, int priority) {

        modMockery.addCollPropGetter(definition, name, variant, executor, priority);
    }

    @Override
    public void addCollPropAdder(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        modMockery.addCollPropAdder(definition, name, variant, executor);
    }

    @Override
    public void addCollPropAdder(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        modMockery.addCollPropAdder(definition, name, variant, executor, priority);
    }

    @Override
    public void addCollPropRemover(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor) {

        modMockery.addCollPropRemover(definition, name, variant, executor);
    }

    @Override
    public void addCollPropRemover(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority) {

        modMockery.addCollPropRemover(definition, name, variant, executor, priority);
    }

    @Override
    public void close() {

        throw new UnsupportedOperationException("Cannot close rule mod mockery manually");
    }

}
