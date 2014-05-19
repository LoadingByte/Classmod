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

import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutorContext;

/**
 * Dummy functions are really simple {@link Function} implementations that use values which are set through the constructor.
 * They are primarily used by the {@link AbstractProperty} and {@link AbstractCollectionProperty} classes.
 * 
 * @param <R> The return type of the dummy function.
 */
class DummyFunction<R> extends AbstractFunction<R> {

    private final List<Class<?>>                   dummyParameters;
    private final List<FunctionExecutorContext<R>> dummyExecutors;

    /**
     * Creates a new dummy function and sets the values the function should serve.
     * 
     * @param name The name of the function.
     * @param holder The {@link FeatureHolder} which supposedly holds the function.
     * @param dummyParameters The parameters the function has at all times.
     * @param dummyExecutors The {@link FunctionExecutorContext}s the function has at all times.
     */
    public DummyFunction(String name, FeatureHolder holder, List<Class<?>> dummyParameters, List<FunctionExecutorContext<R>> dummyExecutors) {

        super(name, holder);

        this.dummyParameters = dummyParameters;
        this.dummyExecutors = dummyExecutors;
    }

    @Override
    public List<Class<?>> getParameters() {

        return dummyParameters;
    }

    @Override
    public List<FunctionExecutorContext<R>> getExecutors() {

        return dummyExecutors;
    }

    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, "holder");
    }

    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj, "holder");
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder", "parameters", "executors");
    }

}
