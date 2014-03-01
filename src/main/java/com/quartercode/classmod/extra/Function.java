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

package com.quartercode.classmod.extra;

import java.util.List;
import java.util.Set;
import com.quartercode.classmod.base.Feature;

/**
 * A function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a set of {@link FunctionExecutorContext}s which wrap around the actual {@link FunctionExecutor}s.
 * For invoking a function, the object creates a new {@link FunctionInvocation} which takes care of calling all the executors.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s.
 * @see FunctionInvocation
 * @see FunctionExecutorContext
 */
public interface Function<R> extends Feature, LockableClass {

    /**
     * Returns a list of all parameters which are used by the {@link FunctionExecutor}s.
     * See {@link FunctionDefinition#setParameter(int, Class)} for further explanation.
     * 
     * @return All parameters which are used by the function.
     */
    public List<Class<?>> getParameters();

    /**
     * Returns a {@link Set} of all {@link FunctionExecutorContext}s which are used by the function.
     * They store {@link FunctionExecutor}s which are used for actually handling a function call.
     * 
     * @return All {@link FunctionExecutorContext}s which are used by the function.
     */
    public Set<FunctionExecutorContext<R>> getExecutors();

    /**
     * Returns the {@link FunctionExecutorContext} which is used by the function and has the given name.
     * 
     * @param name The name the returned {@link FunctionExecutorContext} must have.
     * @return The {@link FunctionExecutorContext} which has the given name.
     */
    public FunctionExecutorContext<R> getExecutor(String name);

    /**
     * Returns the amount of times the {@link #invoke(Object...)} method was called on the function.
     * 
     * @return How many times the function was invoked.
     */
    public int getInvocations();

    /**
     * Invokes the defined function with the given arguments on all {@link FunctionExecutor}s.
     * This returns the return value the {@link FunctionExecutor} with the highest priority returns on the end of the invocation chain.
     * 
     * @param arguments Some arguments for the {@link FunctionExecutor}s.
     * @return The return value on the end of the invocation chain. Can be null.
     * @throws ExecutorInvocationException Something goes wrong during the invocation of a {@link FunctionExecutor}.
     */
    public R invoke(Object... arguments) throws ExecutorInvocationException;

}
