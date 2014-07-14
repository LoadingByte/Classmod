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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Prioritized;

/**
 * An abstract function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a list of those {@link FunctionExecutor}s.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s. The function returns a {@link List} with these values.
 * @see FunctionExecutor
 * @see Function
 */
public class DefaultFunction<R> extends AbstractFeature implements Function<R> {

    private static final Logger                 LOGGER            = LoggerFactory.getLogger(DefaultFunction.class);

    /*
     * This field caches all priority values of all FunctionExecutor classes which were ever used inside a function.
     * That is useful since retrieving the value every time is rather expensive.
     */
    private static final Map<Class<?>, Integer> FE_PRIORITY_CACHE = new HashMap<>();

    private boolean                             initialized;
    private List<Class<?>>                      parameters;
    private List<FunctionExecutor<R>>           executors;

    /**
     * Creates a new abstract function with the given name and {@link FeatureHolder} type.
     * 
     * @param name The name of the abstract function.
     * @param holder The feature holder type the abstract function is made for.
     */
    public DefaultFunction(String name, FeatureHolder holder) {

        super(name, holder);
    }

    @Override
    public boolean isHidden() {

        return true;
    }

    @Override
    public void initialize(FunctionDefinition<R> definition) {

        initialized = true;

        parameters = Collections.unmodifiableList(definition.getParameters());
        for (Class<?> parameter : parameters) {
            Validate.notNull(parameter, "Null parameters are not allowed");
        }

        executors = new ArrayList<>(definition.getExecutorsForVariant(getHolder().getClass()).values());

        // Sort the executor list by priority
        // By sorting at this point, sorting must not be done every time the function is invoked
        sortExecutorList(executors);

        // Make the executor list unmodifiable
        executors = Collections.unmodifiableList(executors);
    }

    /**
     * Sorts the given {@link FunctionExecutor} list by the priorities of the function executors.
     * This method is used internally and by subclasses.
     * 
     * @param list The list which should be sorted.
     */
    protected void sortExecutorList(List<FunctionExecutor<R>> list) {

        Collections.sort(list, new Comparator<FunctionExecutor<R>>() {

            @Override
            public int compare(FunctionExecutor<R> object1, FunctionExecutor<R> object2) {

                return Integer.compare(getPriority(object2), getPriority(object1));
            }

        });
    }

    private int getPriority(FunctionExecutor<?> executor) {

        if (FE_PRIORITY_CACHE.containsKey(executor.getClass())) {
            return FE_PRIORITY_CACHE.get(executor.getClass());
        } else {
            int priority = Prioritized.DEFAULT;

            try {
                Method invokeMethod = executor.getClass().getMethod("invoke", FunctionInvocation.class, Object[].class);
                if (invokeMethod.isAnnotationPresent(Prioritized.class)) {
                    priority = invokeMethod.getAnnotation(Prioritized.class).value();
                }
            } catch (NoSuchMethodException e) {
                LOGGER.error("Can't find invoke() method in function executor for retrieving priority (bytecode error: should be defined by interface)", e);
            }

            FE_PRIORITY_CACHE.put(executor.getClass(), priority);
            return priority;
        }
    }

    @Override
    public boolean isInitialized() {

        return initialized;
    }

    @Override
    public List<Class<?>> getParameters() {

        return parameters;
    }

    @Override
    public List<FunctionExecutor<R>> getExecutors() {

        return executors;
    }

    @Override
    public R invoke(Object... arguments) {

        FunctionInvocation<R> invocation = new DefaultFunctionInvocation<>(this);
        return invocation.next(arguments);
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (executors == null ? 0 : executors.hashCode());
        result = prime * result + (parameters == null ? 0 : parameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj || !super.equals(obj)) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultFunction)) {
            return false;
        } else {
            DefaultFunction<?> other = (DefaultFunction<?>) obj;
            return Objects.equals(parameters, other.parameters) && Objects.equals(executors, other.executors);
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder", "intialized");
    }

}
