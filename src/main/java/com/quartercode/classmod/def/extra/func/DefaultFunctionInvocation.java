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

package com.quartercode.classmod.def.extra.func;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.conv.CFeatureHolder;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;

/**
 * A default implementation of the {@link FunctionInvocation} interface for executing a {@link Function}.
 * 
 * @param <R> The return type of the function invocation.
 * @see FunctionInvocation
 * @see Function
 */
public class DefaultFunctionInvocation<R> implements FunctionInvocation<R> {

    private final Function<R>                       source;
    private final Queue<FunctionExecutorWrapper<R>> remainingExecutors;

    /**
     * Creates a new default function invocation for the given {@link Function}.
     * The required data, like the {@link FunctionExecutor} collection, is taken from the function object.
     * 
     * @param source The function the default function invocation is used by.
     */
    public DefaultFunctionInvocation(Function<R> source) {

        this.source = source;

        // Fill the remaining executors queue with the function executors of the provided function
        remainingExecutors = new LinkedList<>(source.getExecutors());
    }

    @Override
    public FeatureHolder getHolder() {

        return source.getHolder();
    }

    @Override
    public CFeatureHolder getCHolder() {

        return (CFeatureHolder) getHolder();
    }

    @Override
    public R next(Object... arguments) {

        if (remainingExecutors.isEmpty()) {
            // Stop because all executors have already been invoked
            return null;
        } else {
            // Use a clone of the argument array in order to prevent it from outside modification
            Object[] actualArguments = arguments.clone();

            // Validate the arguments and transform varargs into arrays
            actualArguments = checkArguments(actualArguments);

            // Poll the next executor and invoke it with the checked arguments
            return remainingExecutors.poll().getExecutor().invoke(this, actualArguments);
        }
    }

    private Object[] checkArguments(Object[] arguments) {

        List<Class<?>> parameters = source.getParameters();

        // Abort check if there are no parameters
        if (parameters.isEmpty()) {
            // Throw an error if there are arguments while there are no parameters
            if (arguments.length != 0) {
                throw new IllegalArgumentException(generateParameterErrorMessage(parameters));
            }
            // Skip argument validation if there are no arguments and no parameters
            else {
                return arguments;
            }
        }

        // Determine whether the last parameter could be a vararg and the last arguments verify that assumption
        boolean hasVararg = true;
        if (!parameters.get(parameters.size() - 1).isArray()) {
            hasVararg = false;
        } else if (arguments.length == parameters.size() && arguments[arguments.length - 1].getClass().isArray()) {
            hasVararg = false;
        }
        // Check whether there is a correct argument count (if there is a vararg, we don't need to check)
        if (!hasVararg && parameters.size() != arguments.length) {
            throw new IllegalArgumentException(generateParameterErrorMessage(parameters));
        }
        // Check all parameters; if there is a vararg, don't check the last one
        for (int index = 0; index < parameters.size() - (hasVararg ? 1 : 0); index++) {
            Class<?> parameter = parameters.get(index);
            // Check whether the argument matches the parameter
            if (arguments[index] != null && !TypeUtils.isInstance(arguments[index], parameter)) {
                throw new IllegalArgumentException(generateParameterErrorMessage(parameters));
            }
        }

        // Vararg validation
        if (hasVararg) {
            // Calculate the amount of vararg arguments
            int varargArgumentCount = arguments.length - parameters.size() + 1;
            // Determine the type of the vararg
            Class<?> varargType = parameters.get(parameters.size() - 1).getComponentType();

            // Fill a new array with the vararg arguments
            Object[] varargArguments = (Object[]) Array.newInstance(varargType, varargArgumentCount);
            for (int index = 0; index < varargArguments.length; index++) {
                try {
                    varargArguments[index] = arguments[parameters.size() + index - 1];
                } catch (ArrayStoreException e) {
                    // The vararg argument has no the same type as the vararg
                    throw new IllegalArgumentException(generateParameterErrorMessage(parameters));
                }
            }

            // Check whether all vararg arguments are of the actual vararg type
            for (Object varargArgument : varargArguments) {
                if (!varargType.isInstance(varargArgument)) {
                    throw new IllegalArgumentException(generateParameterErrorMessage(parameters));
                }
            }

            // Create a new array with an array containing the vararg arguments at the last index
            // Transforms [ "val1", 2, 3, 4 ] to [ "val1", [ 2, 3, 4] ]
            Object[] newArguments = new Object[parameters.size()];
            System.arraycopy(arguments, 0, newArguments, 0, newArguments.length - 1);
            newArguments[newArguments.length - 1] = varargArguments;
            return newArguments;
        } else {
            return arguments;
        }
    }

    private String generateParameterErrorMessage(List<Class<?>> parameters) {

        StringBuilder errorStringBuilder = new StringBuilder();
        for (Class<?> parameter : parameters) {
            errorStringBuilder.append(", ").append(parameter.getSimpleName());
        }
        return "Wrong arguments: '" + (errorStringBuilder.length() == 0 ? "" : errorStringBuilder.substring(2)) + "' required";
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (remainingExecutors == null ? 0 : remainingExecutors.hashCode());
        result = prime * result + (source == null ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultFunctionInvocation)) {
            return false;
        } else {
            DefaultFunctionInvocation<?> other = (DefaultFunctionInvocation<?>) obj;
            return Objects.equals(remainingExecutors, other.remainingExecutors)
                    && Objects.equals(source, other.source);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toString(this);
    }

}
