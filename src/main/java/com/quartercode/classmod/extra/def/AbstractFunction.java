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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeature;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;

/**
 * An abstract function makes a method (also called a function) available.
 * Functions are executed by different {@link FunctionExecutor}s. That makes the function concept flexible.
 * The function object itself stores a list of those {@link FunctionExecutor}s.
 * 
 * @param <R> The type of the return value of the used {@link FunctionExecutor}s. The function returns a {@link List} with these values.
 * @see FunctionExecutor
 * @see Function
 */
public class AbstractFunction<R> extends AbstractFeature implements Function<R> {

    private static final String[]            EXCLUDED_FIELDS = { "holder", "executors" };

    private boolean                          initialized;
    private List<Class<?>>                   parameters;
    private List<FunctionExecutorContext<R>> executors;

    /**
     * Creates a new abstract function with the given name and {@link FeatureHolder} type.
     * 
     * @param name The name of the abstract function.
     * @param holder The feature holder type the abstract function is made for.
     */
    public AbstractFunction(String name, FeatureHolder holder) {

        super(name, holder);
    }

    @Override
    public void initialize(FunctionDefinition<R> definition) {

        initialized = true;

        parameters = definition.getParameters();
        for (Class<?> parameter : parameters) {
            Validate.notNull(parameter, "Null parameters are not allowed");
        }

        executors = new ArrayList<>();
        for (Entry<String, FunctionExecutor<R>> executor : definition.getExecutorsForVariant(getHolder().getClass()).entrySet()) {
            executors.add(new DefaultFunctionExecutorContext<>(executor.getKey(), executor.getValue()));
        }
        executors = Collections.unmodifiableList(executors);
    }

    @Override
    public boolean isInitialized() {

        return initialized;
    }

    @Override
    public List<Class<?>> getParameters() {

        return Collections.unmodifiableList(parameters);
    }

    @Override
    public List<FunctionExecutorContext<R>> getExecutors() {

        return executors;
    }

    @Override
    public R invoke(Object... arguments) {

        FunctionInvocation<R> invocation = new DefaultFunctionInvocation<>(this);
        return invocation.next(arguments);
    }

    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, EXCLUDED_FIELDS);
    }

    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj, EXCLUDED_FIELDS);
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, EXCLUDED_FIELDS);
    }

}
