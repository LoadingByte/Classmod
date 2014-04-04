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

import java.lang.annotation.Annotation;
import com.quartercode.classmod.base.Named;

/**
 * The function executor context wraps around a {@link FunctionExecutor} for storing data values along with the executor.
 * The data isn't stored in the actual {@link FunctionExecutor} object because it should only do the execution and nothing else.
 * 
 * @param <R> The type of the value the stored {@link FunctionExecutor} returns.
 * @see FunctionExecutor
 */
public interface FunctionExecutorContext<R> extends Named {

    /**
     * Returns the name of the {@link FunctionExecutor} which is stored by the context.
     * 
     * @return The name of the stored {@link FunctionExecutor}.
     */
    @Override
    public String getName();

    /**
     * Returns the actual {@link FunctionExecutor} which is stored in the context
     * 
     * @return The stored {@link FunctionExecutor}.
     */
    public FunctionExecutor<R> getExecutor();

    /**
     * Returns a value of the given annotation type at the {@link FunctionExecutor#invoke(FunctionInvocation, Object...)} method.
     * 
     * @param type The annotation type whose value should be retrieved.
     * @param name The name of the value stored in the annotation which should be retrieved.
     * @return The value stored in the defined variable.
     */
    public <A extends Annotation> Object getValue(Class<A> type, String name);

    /**
     * Sets a value of the given annotation type at the {@link FunctionExecutor#invoke(FunctionInvocation, Object...)} method to the given one.
     * That allows to modify annotation values at runtime.
     * 
     * @param type The annotation type whose value should be changed.
     * @param name The name of the value stored in the annotation which should be changed.
     * @param value The new value for the defined variable.
     */
    public <A extends Annotation> void setValue(Class<A> type, String name, Object value);

    /**
     * Invokes the stored {@link FunctionExecutor} inside the given {@link FunctionInvocation} with the given arguments.
     * 
     * @param invocation The {@link FunctionInvocation} which called the function executor.
     * @param arguments Some arguments for the stored {@link FunctionExecutor}.
     * @return The value the invoked {@link FunctionExecutor} returns. Can be null.
     * @throws ExecutorInvocationException Something goes wrong while invoking the stored {@link FunctionExecutor}.
     */
    public R invoke(FunctionInvocation<R> invocation, Object... arguments) throws ExecutorInvocationException;

}
