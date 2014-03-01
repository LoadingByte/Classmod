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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;

/**
 * The default implementation of the {@link FunctionExecutorContext} for storing data values along with a {@link FunctionExecutor}.
 * The data isn't stored in the actual {@link FunctionExecutor} object because it should only do the execution and nothing else.
 * 
 * @param <R> The type of the value the stored {@link FunctionExecutor} returns.
 */
public class DefaultFunctionExecutorContext<R> implements FunctionExecutorContext<R> {

    private static final Logger       LOGGER            = Logger.getLogger(DefaultFunctionExecutorContext.class.getName());

    private final String              name;
    private final FunctionExecutor<R> executor;
    private final Map<Method, Object> annotationValues  = new HashMap<Method, Object>();
    private int                       invocationCounter = 0;
    private boolean                   locked            = false;

    /**
     * Creates a new default function executor container and fills in the {@link FunctionExecutor} to store and its name.
     * 
     * @param name The name of the {@link FunctionExecutor} to store.
     * @param executor The {@link FunctionExecutor} which is stored by the container
     */
    public DefaultFunctionExecutorContext(String name, FunctionExecutor<R> executor) {

        this.name = name;
        this.executor = executor;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public FunctionExecutor<R> getExecutor() {

        return executor;
    }

    @Override
    public <A extends Annotation> Object getValue(Class<A> type, String name) {

        try {
            Method valueMethod = type.getMethod(name);

            if (!annotationValues.containsKey(valueMethod)) {
                // Fill in annotation value
                try {
                    A annotation = executor.getClass().getMethod("invoke", FeatureHolder.class, Object[].class).getAnnotation(type);
                    if (annotation != null) {
                        Object value = valueMethod.invoke(annotation);
                        annotationValues.put(valueMethod, value);
                        return value;
                    }
                } catch (NoSuchMethodException e) {
                    LOGGER.log(Level.SEVERE, "Programmer's fault: Can't find invoke() method (should be defined by interface)", e);
                } catch (IllegalAccessException e) {
                    LOGGER.log(Level.SEVERE, "No access to annotation method because it's not public; What the ... ?", e);
                } catch (InvocationTargetException e) {
                    LOGGER.log(Level.SEVERE, "Can't invoke annotation method", e);
                }

                // Fill in default value
                Object value = valueMethod.getDefaultValue();
                annotationValues.put(valueMethod, value);
                return value;
            }

            // Return stored value
            return annotationValues.get(valueMethod);
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.WARNING, "Tried to access not existing annotation method for getting annotation value", e);
            return null;
        }
    }

    @Override
    public <A extends Annotation> void setValue(Class<A> type, String name, Object value) {

        try {
            annotationValues.put(type.getMethod(name), value);
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.WARNING, "Tried to access not existing annotation method for setting annotation value", e);
        }
    }

    /**
     * Returns the amount of times the stored {@link FunctionExecutor} was invoked through {@link #invoke(FeatureHolder, Object...)}.
     * 
     * @return The amount of times the {@link FunctionExecutor} was invoked.
     */
    public int getInvocationCounter() {

        return invocationCounter;
    }

    @Override
    public void resetInvocationCounter() {

        invocationCounter = 0;
    }

    @Override
    public boolean isLocked() {

        return locked;
    }

    @Override
    public void setLocked(boolean locked) {

        this.locked = locked;
    }

    /**
     * Invokes the stored {@link FunctionExecutor} in the given {@link FeatureHolder} with the given arguments.
     * Also increases the amount of times the {@link FunctionExecutor} was invoked. You can retrieve the value with {@link #getInvocationCounter()}.
     * 
     * @param holder The {@link FeatureHolder} the stored {@link FunctionExecutor} is invoked in.
     * @param arguments Some arguments for the stored {@link FunctionExecutor}.
     * @return The value the invoked {@link FunctionExecutor} returns. Can be null.
     * @throws ExecutorInvocationException The execution of the invocation queue should stop.
     */
    @Override
    public R invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

        if (!locked) {
            invocationCounter++;
            return executor.invoke(holder, arguments);
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DefaultFunctionExecutorContext<?> other = (DefaultFunctionExecutorContext<?>) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + name + ", executor=" + executor + ", locked=" + locked + ", invocationCounter=" + invocationCounter + "]";
    }

}
