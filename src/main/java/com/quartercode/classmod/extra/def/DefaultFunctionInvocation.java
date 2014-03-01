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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Delay;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Limit;
import com.quartercode.classmod.extra.Lockable;
import com.quartercode.classmod.extra.Prioritized;

/**
 * A default implementation of the {@link FunctionInvocation} interface for executing a {@link Function}.
 * 
 * @param <R> The return type of the function invocation.
 * @see FunctionInvocation
 * @see Function
 */
public class DefaultFunctionInvocation<R> implements FunctionInvocation<R> {

    private static final Logger                     LOGGER = Logger.getLogger(DefaultFunctionInvocation.class.getName());

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
        LinkedList<FunctionExecutorContext<R>> executors = new LinkedList<FunctionExecutorContext<R>>();
        for (FunctionExecutorContext<R> executor : source.getExecutors()) {
            if (isExecutorInvocable(executor)) {
                executors.add(executor);
            }
        }

        Collections.sort(executors, new Comparator<FunctionExecutorContext<R>>() {

            @Override
            public int compare(FunctionExecutorContext<R> o1, FunctionExecutorContext<R> o2) {

                return ((Integer) o2.getValue(Prioritized.class, "value")).compareTo((Integer) o1.getValue(Prioritized.class, "value"));
            }

        });

        remainingExecutors = executors;
    }

    /**
     * Returns wether the given {@link FunctionExecutorContext} is invocable.
     * For example, a {@link FunctionExecutor} which already exceeded its invocation limit is not invocable.
     * This can be overriden to modify which {@link FunctionExecutor}s should be invoked.
     * 
     * @param executorContext The {@link FunctionExecutorContext} to check.
     * @return Wether the given {@link FunctionExecutorContext} is invocable.
     */
    protected boolean isExecutorInvocable(FunctionExecutorContext<R> executor) {

        // Lockable
        try {
            Method invokeMethod = executor.getExecutor().getClass().getMethod("invoke", FunctionInvocation.class, Object[].class);
            if (executor.isLocked() || source.isLocked() && invokeMethod.isAnnotationPresent(Lockable.class)) {
                return false;
            }
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, "Programmer's fault: Can't find invoke() method (should be defined by interface)", e);
        }

        // Limit
        if (executor.getInvocations() + 1 > (Integer) executor.getValue(Limit.class, "value")) {
            return false;
        }

        // Delay
        int invocation = source.getInvocations() - 1;
        int firstDelay = (Integer) executor.getValue(Delay.class, "firstDelay");
        int delay = (Integer) executor.getValue(Delay.class, "delay");
        if (invocation < firstDelay) {
            return false;
        } else if (delay > 0 && (invocation - firstDelay) % (delay + 1) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public FeatureHolder getHolder() {

        return source.getHolder();
    }

    @Override
    public R next(Object... arguments) throws ExecutorInvocationException {

        // Argument validation
        try {
            List<Class<?>> parameters = source.getParameters();

            // Generate error string
            StringBuffer errorStringBuffer = new StringBuffer();
            for (Class<?> parameter : parameters) {
                errorStringBuffer.append(", ").append(parameter.getSimpleName());
            }
            String errorString = "Wrong arguments: '" + (errorStringBuffer.length() == 0 ? "" : errorStringBuffer.substring(2)) + "' required";

            // Check all arguments
            for (int index = 0; index < parameters.size(); index++) {
                if (!parameters.get(index).isAssignableFrom(arguments[index].getClass())) {
                    Validate.isTrue(parameters.get(index).isArray(), errorString);
                    for (int varargIndex = index; varargIndex < arguments.length; varargIndex++) {
                        Validate.isTrue(parameters.get(index).getComponentType().isAssignableFrom(arguments[varargIndex].getClass()), errorString);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw new ExecutorInvocationException(e);
        }

        if (remainingExecutors.size() == 0) {
            // Abort because all executors were already invoked
            return null;
        } else {
            try {
                return remainingExecutors.poll().invoke(this, arguments);
            } catch (RuntimeException e) {
                throw new ExecutorInvocationException();
            }
        }
    }

}
