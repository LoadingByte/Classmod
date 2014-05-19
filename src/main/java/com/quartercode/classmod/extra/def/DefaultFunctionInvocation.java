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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Prioritized;

/**
 * A default implementation of the {@link FunctionInvocation} interface for executing a {@link Function}.
 * 
 * @param <R> The return type of the function invocation.
 * @see FunctionInvocation
 * @see Function
 */
public class DefaultFunctionInvocation<R> implements FunctionInvocation<R> {

    private final Function<R>                       source;
    private final Queue<FunctionExecutorContext<R>> remainingExecutors;

    /**
     * Creates a new default function invocation for the given {@link Function}.
     * The required data is taken from the given {@link Function} object.
     * This constructor also takes the available {@link FunctionExecutor}s and sorts them so they
     * 
     * @param source The {@link Function} the default function invocation is used by.
     */
    public DefaultFunctionInvocation(Function<R> source) {

        this.source = source;

        // Specify the list type for using this as a queue later on
        // We need a list here for sorting the executors
        LinkedList<FunctionExecutorContext<R>> executors = new LinkedList<>();
        executors.addAll(source.getExecutors());

        Collections.sort(executors, new Comparator<FunctionExecutorContext<R>>() {

            @Override
            public int compare(FunctionExecutorContext<R> o1, FunctionExecutorContext<R> o2) {

                return ((Integer) o2.getValue(Prioritized.class, "value")).compareTo((Integer) o1.getValue(Prioritized.class, "value"));
            }

        });

        remainingExecutors = executors;
    }

    @Override
    public FeatureHolder getHolder() {

        return source.getHolder();
    }

    @Override
    public R next(Object... arguments) {

        // Use a clone of the argument array in order to prevent it from outside modification
        Object[] actualArguments = arguments.clone();

        // Performance: Skip argument validation if there are no arguments and no parameters
        if (!source.getParameters().isEmpty() || actualArguments.length > 0) {
            // Validate the arguments and transform varargs into arrays
            actualArguments = checkArguments(actualArguments);
        }

        if (remainingExecutors.isEmpty()) {
            // Stop because all executors were already invoked
            return null;
        } else {
            return remainingExecutors.poll().invoke(this, actualArguments);
        }
    }

    private Object[] checkArguments(Object[] arguments) {

        List<Class<?>> parameters = source.getParameters();

        // Generate error message
        StringBuilder errorStringBuilder = new StringBuilder();
        for (Class<?> parameter : parameters) {
            errorStringBuilder.append(", ").append(parameter.getSimpleName());
        }
        String errorString = "Wrong arguments: '" + (errorStringBuilder.length() == 0 ? "" : errorStringBuilder.substring(2)) + "' required";

        // Abort check if there are no parameters
        if (parameters.isEmpty()) {
            // Throw an error if there are arguments while there are no parameters
            if (arguments.length != 0) {
                throw new IllegalArgumentException(errorString);
            } else {
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
        Validate.isTrue(hasVararg || parameters.size() == arguments.length, errorString);
        // Check all parameters; if there is a vararg, don't check the last one
        for (int index = 0; index < parameters.size() - (hasVararg ? 1 : 0); index++) {
            Class<?> parameter = parameters.get(index);
            // Check whether the argument matches the parameter
            Validate.isTrue(arguments[index] == null || TypeUtils.isInstance(arguments[index], parameter), errorString);
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
                    throw new IllegalArgumentException(errorString);
                }
            }

            // Check whether all vararg arguments are of the actual vararg type
            for (Object varargArgument : varargArguments) {
                Validate.isInstanceOf(varargType, varargArgument, errorString);
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

    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this);
    }

}
