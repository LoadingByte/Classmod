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

import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * A mod mockery is able to temporarily change {@link FeatureDefinition}s for testing purposes.
 * For example, a {@link FunctionExecutor} could be added to a {@link FunctionDefinition} for simulating an interface implementation.
 * After the test, the {@link #close()} method automatically reverts everything that was modified by the mockery.
 */
public interface ModMockery extends AutoCloseable {

    /**
     * Temporarily adds the given {@link FunctionExecutor} to the given {@link FunctionDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The function definition the executor should be added to.
     * @param name The name the function executor should be registered under.
     * @param variant The variant that defines which classes use the function executor.
     *        A custom class, which extends the original function-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added function executor.
     * @param executor The function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor);

    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor);

    public <T> void addPropSetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Reverts everything that was modified by the mockery.
     * For example, any added {@link FunctionExecutor}s are removed.
     * This method should be called after the test.
     */
    @Override
    public void close();

}
