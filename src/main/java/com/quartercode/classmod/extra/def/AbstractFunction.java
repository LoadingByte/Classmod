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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.LockableClass;

/**
 * An abstract function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a set of those {@link FunctionExecutor}s.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s. The function returns a {@link List} with these values.
 * @see FunctionExecutor
 * @see Function
 * @see LockableClass
 */
public class AbstractFunction<R> extends AbstractFeature implements Function<R> {

    private final List<Class<?>>                  parameters;
    private final Set<FunctionExecutorContext<R>> executors;
    private boolean                               locked;
    private int                                   invocationCounter;

    /**
     * Creates a new abstract function with the given name, parent {@link FeatureHolder}, parameters and {@link FunctionExecutor}s.
     * 
     * @param name The name of the abstract function.
     * @param holder The {@link FeatureHolder} which has and uses the new abstract function.
     * @param parameters The argument types an {@link #invoke(Object...)} call must have (see {@link FunctionDefinition#setParameter(int, Class)} for further explanation).
     * @param executors The {@link FunctionExecutor}s which will be executing the function calls for this particular function.
     */
    public AbstractFunction(String name, FeatureHolder holder, List<Class<?>> parameters, Map<String, FunctionExecutor<R>> executors) {

        super(name, holder);

        for (Class<?> parameter : parameters) {
            Validate.isTrue(parameter != null, "Null parameters are not allowed");
        }
        this.parameters = parameters;

        this.executors = new HashSet<FunctionExecutorContext<R>>();
        for (Entry<String, FunctionExecutor<R>> executor : executors.entrySet()) {
            this.executors.add(new DefaultFunctionExecutorContext<R>(executor.getKey(), executor.getValue()));
        }

        locked = true;
    }

    @Override
    public boolean isLocked() {

        return locked;
    }

    @Override
    public void setLocked(boolean locked) {

        this.locked = locked;
    }

    /**
     * Returns the amount of times the {@link #invoke(Object...)} method was called on the function.
     * 
     * @return The amount of times the function was invoked.
     */
    public int getInvocationCounter() {

        return invocationCounter;
    }

    @Override
    public List<Class<?>> getParameters() {

        return Collections.unmodifiableList(parameters);
    }

    @Override
    public Set<FunctionExecutorContext<R>> getExecutors() {

        return Collections.unmodifiableSet(new HashSet<FunctionExecutorContext<R>>(this.executors));
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

        invocationCounter++;
        FunctionInvocation<R> invocation = new DefaultFunctionInvocation<R>(this);
        return invocation.next(arguments);
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + getName() + ", " + getExecutors().size() + " executors, locked=" + locked + "]";
    }

}
