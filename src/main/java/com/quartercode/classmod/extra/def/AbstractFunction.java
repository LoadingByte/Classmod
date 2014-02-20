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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.Delay;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutionException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Limit;
import com.quartercode.classmod.extra.Lockable;
import com.quartercode.classmod.extra.LockableClass;
import com.quartercode.classmod.extra.Prioritized;
import com.quartercode.classmod.extra.ReturnNextException;
import com.quartercode.classmod.extra.StopExecutionException;

/**
 * An abstract function makes a method (also called a function) avaiable.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a set of those {@link FunctionExecutor}s.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s. The function returns a {@link List} with these values.
 * @see FunctionExecutor
 * @see Function
 * @see LockableClass
 */
public class AbstractFunction<R> extends AbstractFeature implements Function<R> {

    private static final Logger                            LOGGER = Logger.getLogger(AbstractFunction.class.getName());

    private final List<Class<?>>                           parameters;
    private final Set<DefaultFunctionExecutorContainer<R>> executors;
    private boolean                                        locked;
    private int                                            invocationCounter;

    /**
     * Creates a new abstract function with the given name, parent {@link FeatureHolder}, parameters and {@link FunctionExecutor}s.
     * 
     * @param name The name of the abstract function.
     * @param holder The {@link FeatureHolder} which has and uses the new abstract function.
     * @param parameters The argument types an {@link #invoke(Object...)} call must have (see {@link FunctionDefinition#setParameter(int, Class)} for further explanation).
     * @param executors The {@link FunctionExecutor}s which will be executing the function calls for this particular function.
     */
    public AbstractFunction(String name, FeatureHolder holder, List<Class<?>> parameters, Map<String, FunctionExecutor<R>> executors) {

        super(name, holder);

        for (Class<?> parameter : parameters) {
            Validate.isTrue(parameter != null, "Null parameters are not allowed");
        }
        this.parameters = parameters;

        this.executors = new HashSet<DefaultFunctionExecutorContainer<R>>();
        for (Entry<String, FunctionExecutor<R>> executor : executors.entrySet()) {
            this.executors.add(new DefaultFunctionExecutorContainer<R>(executor.getKey(), executor.getValue()));
        }

        locked = true;
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
     * Returns the amount of times the {@link #invoke(Object...)} method was called on the function.
     * 
     * @return The amount of times the function was invoked.
     */
    public int getInvocationCounter() {

        return invocationCounter;
    }

    @Override
    public List<Class<?>> getParameters() {

        return Collections.unmodifiableList(parameters);
    }

    @Override
    public Set<FunctionExecutorContainer<R>> getExecutors() {

        return Collections.unmodifiableSet(new HashSet<FunctionExecutorContainer<R>>(this.executors));
    }

    @Override
    public FunctionExecutorContainer<R> getExecutor(String name) {

        for (FunctionExecutorContainer<R> executor : executors) {
            if (executor.getName().equals(name)) {
                return executor;
            }
        }

        return null;
    }

    /**
     * Collects the {@link FunctionExecutor}s which can be invoked through {@link #invoke(Object...)} or {@link #invokeRA(Object...)}.
     * This can be overriden to modify which {@link FunctionExecutor}s should be invoked.
     * 
     * @return The {@link FunctionExecutor}s which can be invoked.
     */
    protected Set<FunctionExecutorContainer<R>> getExecutableExecutors() {

        Set<DefaultFunctionExecutorContainer<R>> executableExecutors = new HashSet<DefaultFunctionExecutorContainer<R>>(this.executors);

        for (DefaultFunctionExecutorContainer<R> executor : new HashSet<DefaultFunctionExecutorContainer<R>>(executableExecutors)) {
            // Lockable
            try {
                Method invokeMethod = executor.getExecutor().getClass().getMethod("invoke", FeatureHolder.class, Object[].class);
                if (executor.isLocked() || locked && invokeMethod.isAnnotationPresent(Lockable.class)) {
                    executableExecutors.remove(executor);
                }
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.SEVERE, "Programmer's fault: Can't find invoke() method (should be defined by interface)", e);
            }

            // Limit
            if (executor.getValue(Limit.class, "value") != null && executor.getInvocationCounter() + 1 > (Integer) executor.getValue(Limit.class, "value")) {
                executableExecutors.remove(executor);
                continue;
            }

            // Delay
            int invocation = invocationCounter - 1;
            int firstDelay = (Integer) executor.getValue(Delay.class, "firstDelay");
            int delay = (Integer) executor.getValue(Delay.class, "delay");
            if (invocation < firstDelay) {
                executableExecutors.remove(executor);
                continue;
            } else if (delay > 0 && (invocation - firstDelay) % (delay + 1) != 0) {
                executableExecutors.remove(executor);
                continue;
            }
        }

        return new HashSet<FunctionExecutorContainer<R>>(executableExecutors);
    }

    @Override
    public R invoke(Object... arguments) throws FunctionExecutionException {

        List<R> returnValues = invokeRA(arguments);
        if (!returnValues.isEmpty()) {
            return returnValues.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<R> invokeRA(Object... arguments) throws FunctionExecutionException {

        invocationCounter++;

        // Argument validation
        try {
            StringBuffer errorStringBuffer = new StringBuffer();
            for (Class<?> parameter : parameters) {
                errorStringBuffer.append(", ").append(parameter.getSimpleName());
            }
            errorStringBuffer.append("Wrong arguments: '").append(errorStringBuffer.length() == 0 ? "" : errorStringBuffer.substring(2)).append("' required");
            String errorString = errorStringBuffer.toString();

            for (int index = 0; index < parameters.size(); index++) {
                if (!parameters.get(index).isAssignableFrom(arguments[index].getClass())) {
                    Validate.isTrue(parameters.get(index).isArray(), errorString);
                    for (int varargIndex = index; varargIndex < arguments.length; varargIndex++) {
                        Validate.isTrue(parameters.get(index).getComponentType().isAssignableFrom(arguments[varargIndex].getClass()), errorString);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw new FunctionExecutionException(e);
        }

        // Check if there are any executable executors
        Set<FunctionExecutorContainer<R>> executableExecutors = getExecutableExecutors();
        if (executableExecutors.isEmpty()) {
            // Would not do anything -> Don't run unnecessary stuff
            return new ArrayList<R>();
        }

        // Sort the executors by priority
        SortedMap<Integer, Set<FunctionExecutorContainer<R>>> sortedExecutors = new TreeMap<Integer, Set<FunctionExecutorContainer<R>>>(new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {

                return o2 - o1;
            }

        });
        for (FunctionExecutorContainer<R> executor : executableExecutors) {
            int priority = Prioritized.DEFAULT;

            // Read custom priorities
            try {
                Method invokeMethod = executor.getExecutor().getClass().getMethod("invoke", FeatureHolder.class, Object[].class);
                if (invokeMethod.isAnnotationPresent(Prioritized.class)) {
                    priority = invokeMethod.getAnnotation(Prioritized.class).value();
                }
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.SEVERE, "Programmer's fault: Can't find invoke() method (should be defined by interface)", e);
            }

            if (!sortedExecutors.containsKey(priority)) {
                sortedExecutors.put(priority, new HashSet<FunctionExecutorContainer<R>>());
            }
            sortedExecutors.get(priority).add(executor);
        }

        // Invoke the executors
        List<R> returnValues = new ArrayList<R>();
        invokeExecutors:
        for (Set<FunctionExecutorContainer<R>> priorityGroup : sortedExecutors.values()) {
            for (FunctionExecutorContainer<R> executor : priorityGroup) {
                try {
                    try {
                        returnValues.add(executor.invoke(getHolder(), arguments));
                    } catch (IllegalArgumentException e) {
                        throw new StopExecutionException(e);
                    }
                } catch (ReturnNextException e) {
                    // Return next executor return value -> Just skip this one (already did that) and invoke the next executor
                    continue;
                } catch (ExecutorInvocationException e) {
                    // Simply stop the execution
                    if (priorityGroup.size() > 1) {
                        StringBuffer otherExecutors = new StringBuffer();
                        for (FunctionExecutorContainer<R> otherExecutor : priorityGroup) {
                            if (!otherExecutor.equals(executor)) {
                                otherExecutors.append(", '").append(otherExecutor.getExecutor().getClass().getName()).append("'");
                            }
                        }
                        LOGGER.warning("Function executor '" + executor.getExecutor().getClass().getName() + "' stopped while having the same priority as the executors " + otherExecutors.substring(2));
                    }

                    if (e.getCause() == null) {
                        break invokeExecutors;
                    } else {
                        throw new FunctionExecutionException(e.getCause());
                    }
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "Function executor '" + executor.getExecutor().getClass().getName() + "' threw an unexpected exception", e);
                }
            }
        }

        return returnValues;
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + getName() + ", " + getExecutableExecutors().size() + "/" + getExecutors().size() + " executors, locked=" + locked + "]";
    }

    /**
     * The default implementation of the {@link FunctionExecutorContainer} for storing data values along with a {@link FunctionExecutor}.
     * The data isn't stored in the actual {@link FunctionExecutor} object because it should only do the execution and nothing else.
     * 
     * @param <R> The type of the value the stored {@link FunctionExecutor} returns.
     */
    public static class DefaultFunctionExecutorContainer<R> implements FunctionExecutorContainer<R> {

        private static final Logger       LOGGER            = Logger.getLogger(DefaultFunctionExecutorContainer.class.getName());

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
        public DefaultFunctionExecutorContainer(String name, FunctionExecutor<R> executor) {

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
            DefaultFunctionExecutorContainer<?> other = (DefaultFunctionExecutorContainer<?>) obj;
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

}
