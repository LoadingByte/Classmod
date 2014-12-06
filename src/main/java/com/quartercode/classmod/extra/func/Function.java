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

package com.quartercode.classmod.extra.func;

import java.util.List;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.base.Initializable;

/**
 * A function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * For invoking a function, the object creates a new {@link FunctionInvocation} which takes care of calling all the executors.<br>
 * <br>
 * Please note that all function implementations must be hidden.
 * 
 * @param <R> The type of the return value of the used function executors.
 * @see FunctionExecutor
 * @see FunctionInvocation
 */
public interface Function<R> extends Feature, Hideable, Initializable<FunctionDefinition<R>> {

    /**
     * Returns a list of all parameters which are used by the {@link FunctionExecutor}s.
     * See {@link FunctionDefinition#setParameter(int, Class)} for further explanation.
     * 
     * @return All parameters which are used by the function.
     */
    public List<Class<?>> getParameters();

    /**
     * Returns all used {@link FunctionExecutorWrapper}s sorted by their priorities in descending order.
     * They are used for actually handling a function call.
     * Note that the returned wrapper objects contain real {@link FunctionExecutor}s.
     * 
     * @return All function executors which are used by the function.
     */
    public List<FunctionExecutorWrapper<R>> getExecutors();

    /**
     * Invokes the defined function by invoking the first {@link FunctionExecutor}s which starts the invocation chain.
     * The given arguments are passed into that first function executor.
     * At the end of the invocation chain, this method returns the return value the function executor with the highest priority returned.
     * 
     * @param arguments Some arguments for the first function executor.
     * @return The return value at the end of the invocation chain. Can be {@code null}.
     * @throws IllegalArgumentException The supplied arguments are not valid and do not match the parameters that are provided by {@link #getParameters()}.
     * @throws RuntimeException A function executor throws a custom function-related exception.
     */
    public R invoke(Object... arguments);

}
