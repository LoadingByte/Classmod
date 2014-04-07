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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;

/**
 * An abstract function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a list of those {@link FunctionExecutor}s.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s. The function returns a {@link List} with these values.
 * @see FunctionExecutor
 * @see Function
 */
public class AbstractFunction<R> extends AbstractFeature implements Function<R> {

    private boolean                          initialized;
    private List<Class<?>>                   parameters;
    private List<FunctionExecutorContext<R>> executors;
    private int                              invocations;

    /**
     * Creates a new abstract function with the given name and parent {@link FeatureHolder}.
     * 
     * @param name The name of the abstract function.
     * @param holder The {@link FeatureHolder} which has and uses the new abstract function.
     */
    public AbstractFunction(String name, FeatureHolder holder) {

        super(name, holder);
    }

    @Override
    public void initialize(FunctionDefinition<R> definition) {

        initialized = true;

        parameters = definition.getParameters();
        for (Class<?> parameter : parameters) {
            Validate.notNull(parameter, "Null parameters are not allowed");
        }

        executors = new ArrayList<FunctionExecutorContext<R>>();
        for (Entry<String, FunctionExecutor<R>> executor : definition.getExecutorsForVariant(getHolder().getClass()).entrySet()) {
            executors.add(new DefaultFunctionExecutorContext<R>(executor.getKey(), executor.getValue()));
        }
    }

    @Override
    public boolean isInitialized() {

        return initialized;
    }

    @Override
    public int getInvocations() {

        return invocations;
    }

    @Override
    public List<Class<?>> getParameters() {

        return Collections.unmodifiableList(parameters);
    }

    @Override
    public List<FunctionExecutorContext<R>> getExecutors() {

        return Collections.unmodifiableList(executors);
    }

    @Override
    public FunctionExecutorContext<R> getExecutor(String name) {

        for (FunctionExecutorContext<R> executor : executors) {
            if (executor.getName().equals(name)) {
                return executor;
            }
        }

        return null;
    }

    @Override
    public R invoke(Object... arguments) throws ExecutorInvocationException {

        invocations++;
        FunctionInvocation<R> invocation = new DefaultFunctionInvocation<R>(this);
        return invocation.next(arguments);
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + getName() + ", " + getExecutors().size() + " executors]";
    }

}
