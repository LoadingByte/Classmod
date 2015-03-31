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

package com.quartercode.classmod.extra.func;

import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.conv.CFeatureHolder;

/**
 * A function invocation object takes care of invoking all {@link FunctionExecutor}s of a {@link Function} in a chain.
 * That means that the function invocation with the highest priority is called first.
 * That firstly called function executor then calls the {@link #next(Object...)} method on its function invocation in order to call the next function executor.
 * 
 * @param <R> The return type of the function invocation.
 */
public interface FunctionInvocation<R> {

    /**
     * Returns the {@link FeatureHolder} which holds the {@link Function} using the function invocation.
     * 
     * @return The feature holder the function invocation is used by.
     */
    public FeatureHolder getHolder();

    /**
     * Returns the {@link FeatureHolder} which holds the {@link Function} using the function invocation casted to a {@link CFeatureHolder}.
     * 
     * @return The convenient feature holder the function invocation is used by.
     */
    public CFeatureHolder getCHolder();

    /**
     * Invokes the next {@link FunctionExecutor} in the chain with the given arguments and returns its return value.
     * If there is no next function executor with a lower priority than the one invoked before, the method returns null.
     * The method should be used by function executors to keep the invocation chain going.
     * 
     * @param arguments The arguments for the next function executor. Most of the times, the same argument array will be carried through all of the function executors.
     * @return The return value the nextly invoked function executor returns.
     * @throws IllegalArgumentException The supplied arguments are not valid and do not match the parameters.
     * @throws RuntimeException The next function executor throws a custom function-related exception.
     */
    public R next(Object... arguments);

}
