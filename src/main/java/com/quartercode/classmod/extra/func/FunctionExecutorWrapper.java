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

/**
 * A wrapper class that stores a {@link FunctionExecutor} and some additional metadata.
 * 
 * @param <R> The type of the return value of the wrapped function executor.
 * @see FunctionExecutor
 */
public interface FunctionExecutorWrapper<R> {

    /**
     * Returns the wrapped {@link FunctionExecutor}.
     * All other stored data belongs to this executor.
     * 
     * @return The wrapped function executor.
     */
    public FunctionExecutor<R> getExecutor();

    /**
     * Returns the priority of the wrapped {@link FunctionExecutor}.
     * It is used to determine the order in which the available function executors are invoked.
     * Executors with a high priority are invoked before executors with a low priority.
     * 
     * @return The priority of the function executor.
     */
    public int getPriority();

}
