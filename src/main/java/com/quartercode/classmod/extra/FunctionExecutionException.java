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

/**
 * The function execution exception is thrown if an {@link Throwable} occurres during the invokation of a {@link FunctionExecutor}.
 * It is a wrapper which brings the wrapped {@link Throwable} through the {@link Function#invoke(Object...)} method.
 * 
 * @see Function
 */
public class FunctionExecutionException extends ExecutorInvokationException {

    private static final long serialVersionUID = -5515825410776845247L;

    /**
     * Creates a new function execution exception with the given wrapped {@link Throwable}.
     * 
     * @param wrapped The {@link Throwable} the exception wraps around.
     */
    public FunctionExecutionException(Throwable wrapped) {

        super(wrapped);
    }

    /**
     * Creates a new function execution exception with the given wrapped {@link Throwable} and an explanation.
     * 
     * @param message A message which describes why the exception occurres.
     * @param wrapped The {@link Throwable} the exception wraps around.
     */
    public FunctionExecutionException(String message, Throwable wrapped) {

        super(message, wrapped);
    }

}
