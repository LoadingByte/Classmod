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

package com.quartercode.classmod.def.extra.prop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.base.AbstractFeature;
import com.quartercode.classmod.def.extra.func.DefaultFunctionInvocation;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionDefinition;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;

/**
 * Dummy functions are really simple {@link Function} implementations that use values which are set through the constructor.
 * They are primarily used by the {@link DefaultProperty} and {@link DefaultCollectionProperty} classes.
 * 
 * @param <R> The return type of the dummy function.
 */
class DummyFunction<R> extends AbstractFeature implements Function<R> {

    private final List<Class<?>>                   dummyParameters;
    private final List<FunctionExecutorWrapper<R>> dummyExecutors;

    /**
     * Creates a new dummy function and sets the values the function should serve.
     * 
     * @param name The name of the function.
     * @param holder The {@link FeatureHolder} which supposedly holds the function.
     * @param dummyParameters The parameters the function has at all times.
     * @param dummyExecutors The {@link FunctionExecutor}s the function has at all times (as {@link FunctionExecutorWrapper}s).
     */
    public DummyFunction(String name, FeatureHolder holder, List<Class<?>> dummyParameters, List<FunctionExecutorWrapper<R>> dummyExecutors) {

        super(name, holder);

        this.dummyParameters = dummyParameters;

        // Sort the executor list by priority
        // By sorting at this point, sorting must not be done every time the function is invoked
        List<FunctionExecutorWrapper<R>> sortedDummyExecutors = new ArrayList<>(dummyExecutors);
        sortExecutorList(sortedDummyExecutors);

        // Make the executor list unmodifiable
        this.dummyExecutors = Collections.unmodifiableList(sortedDummyExecutors);
    }

    private void sortExecutorList(List<FunctionExecutorWrapper<R>> list) {

        Collections.sort(list, new Comparator<FunctionExecutorWrapper<R>>() {

            @Override
            public int compare(FunctionExecutorWrapper<R> object1, FunctionExecutorWrapper<R> object2) {

                return Integer.compare(object2.getPriority(), object1.getPriority());
            }

        });
    }

    @Override
    public boolean isHidden() {

        return true;
    }

    @Override
    public void initialize(FunctionDefinition<R> definition) {

        throw new UnsupportedOperationException("Initializing a dummy function is not supported");
    }

    @Override
    public boolean isInitialized() {

        return true;
    }

    @Override
    public List<Class<?>> getParameters() {

        return dummyParameters;
    }

    @Override
    public List<FunctionExecutorWrapper<R>> getExecutors() {

        return dummyExecutors;
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
        result = prime * result + (dummyExecutors == null ? 0 : dummyExecutors.hashCode());
        result = prime * result + (dummyParameters == null ? 0 : dummyParameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DummyFunction) || !super.equals(obj)) {
            return false;
        } else {
            DummyFunction<?> other = (DummyFunction<?>) obj;
            return Objects.equals(dummyParameters, other.dummyParameters)
                    && Objects.equals(dummyExecutors, other.dummyExecutors);
        }
    }

}
