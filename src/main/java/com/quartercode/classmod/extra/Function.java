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
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.Initializable;

/**
 * A function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a collection of {@link FunctionExecutorContext}s which wrap around the actual {@link FunctionExecutor}s.
 * For invoking a function, the object creates a new {@link FunctionInvocation} which takes care of calling all the executors.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s.
 * @see FunctionInvocation
 * @see FunctionExecutorContext
 */
public interface Function<R> extends Feature, Initializable<FunctionDefinition<R>> {

    /**
     * Returns a list of all parameters which are used by the {@link FunctionExecutor}s.
     * See {@link FunctionDefinition#setParameter(int, Class)} for further explanation.
     * 
     * @return All parameters which are used by the function.
     */
    public List<Class<?>> getParameters();

    /**
     * Returns a {@link List} of all {@link FunctionExecutorContext}s which are used by the function.
     * They store {@link FunctionExecutor}s which are used for actually handling a function call.
     * 
     * @return All {@link FunctionExecutorContext}s which are used by the function.
     */
    public List<FunctionExecutorContext<R>> getExecutors();

    /**
     * Invokes the defined function with the given arguments on all {@link FunctionExecutor}s.
     * This returns the return value the {@link FunctionExecutor} with the highest priority returns on the end of the invocation chain.
     * 
     * @param arguments Some arguments for the {@link FunctionExecutor}s.
     * @return The return value on the end of the invocation chain. Can be {@code null}.
     * @throws IllegalArgumentException The supplied arguments are not valid and do not match the parameters that are provided by {@link #getParameters()}.
     * @throws RuntimeException A function executor throws a custom function-related exception.
     */
    public R invoke(Object... arguments);

}
