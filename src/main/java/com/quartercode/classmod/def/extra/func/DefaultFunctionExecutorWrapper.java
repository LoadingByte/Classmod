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

package com.quartercode.classmod.def.extra.func;

import java.util.Objects;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;

/**
 * The internal default implementation of the {@link FunctionExecutorWrapper} interface.
 * 
 * @param <R> The type of the return value of the wrapped function executor.
 * @see FunctionExecutorWrapper
 */
public class DefaultFunctionExecutorWrapper<R> implements FunctionExecutorWrapper<R> {

    private final FunctionExecutor<R> executor;
    private final int                 priority;

    /**
     * Creates a new default function executor wrapper.
     * 
     * @param executor The wrapped {@link FunctionExecutor}.
     * @param priority The priority of the wrapped function executor.
     */
    public DefaultFunctionExecutorWrapper(FunctionExecutor<R> executor, int priority) {

        this.executor = executor;
        this.priority = priority;
    }

    @Override
    public FunctionExecutor<R> getExecutor() {

        return executor;
    }

    @Override
    public int getPriority() {

        return priority;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (executor == null ? 0 : executor.hashCode());
        result = prime * result + priority;
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultFunctionExecutorWrapper)) {
            return false;
        } else {
            DefaultFunctionExecutorWrapper<?> other = (DefaultFunctionExecutorWrapper<?>) obj;
            return priority == other.priority && Objects.equals(executor, other.executor);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder", "intialized");
    }

}
