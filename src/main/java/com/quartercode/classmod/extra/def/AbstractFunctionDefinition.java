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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeatureDefinition;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;

/**
 * An abstract function definition is used to retrieve a {@link Function} from a {@link FeatureHolder}.
 * It's an implementation of the {@link FunctionDefinition} interface.<br>
 * <br>
 * Every definition contains the name of the {@link Function}, the parameters and the {@link FunctionExecutor}s that are used.
 * You can use an abstract function definition to construct a new instance of the defined {@link Function} through {@link #create(FeatureHolder)}.
 * 
 * @param <R> The type of the return value of the defined {@link Function}.
 * @see FunctionDefinition
 * @see Function
 */
public abstract class AbstractFunctionDefinition<R> extends AbstractFeatureDefinition<Function<R>> implements FunctionDefinition<R> {

    private static final String[]                                                       EXCLUDED_FIELDS = { "executors", "variantCache" };

    private final List<Class<?>>                                                        parameters      = new ArrayList<>();
    private final Map<String, Map<Class<? extends FeatureHolder>, FunctionExecutor<R>>> executors       = new HashMap<>();

    // Performance: Cache for different variants
    private final Map<Class<? extends FeatureHolder>, Map<String, FunctionExecutor<R>>> variantCache    = new HashMap<>();

    /**
     * Creates a new abstract function definition for defining a {@link Function} with the given name and parameters.
     * Of course, the parameters can be changed later on using {@link #setParameter(int, Class)}.
     * 
     * @param name The name of the defined {@link Function}.
     * @param parameters The parameters for the defined function. See {@link #setParameter(int, Class)} for further explanation.
     */
    public AbstractFunctionDefinition(String name, Class<?>... parameters) {

        super(name);

        for (int index = 0; index < parameters.length; index++) {
            setParameter(index, parameters[index]);
        }
    }

    @Override
    public List<Class<?>> getParameters() {

        return Collections.unmodifiableList(parameters);
    }

    @Override
    public void setParameter(int index, Class<?> type) {

        while (parameters.size() <= index) {
            parameters.add(null);
        }

        parameters.set(index, type);

        while (parameters.get(parameters.size() - 1) == null) {
            parameters.remove(parameters.size() - 1);
        }
    }

    @Override
    public Map<String, FunctionExecutor<R>> getExecutorsForVariant(Class<? extends FeatureHolder> variant) {

        Map<String, FunctionExecutor<R>> variantExecutors = variantCache.get(variant);

        if (variantExecutors == null) {
            variantExecutors = new HashMap<>();

            for (Entry<String, Map<Class<? extends FeatureHolder>, FunctionExecutor<R>>> executors : this.executors.entrySet()) {
                // Select the executor whose variant is as near as possible to the given variant
                Class<? extends FeatureHolder> currentExecutorVariant = null;
                for (Class<? extends FeatureHolder> executorVariant : executors.getValue().keySet()) {
                    if (executorVariant.isAssignableFrom(variant) && (currentExecutorVariant == null || currentExecutorVariant.isAssignableFrom(executorVariant))) {
                        currentExecutorVariant = executorVariant;
                    }
                }

                // If there is such an executor, put it into the result
                if (currentExecutorVariant != null) {
                    variantExecutors.put(executors.getKey(), executors.getValue().get(currentExecutorVariant));
                }
            }

            variantCache.put(variant, variantExecutors);
        }

        return variantExecutors;
    }

    @Override
    public void addExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor) {

        if (!executors.containsKey(name)) {
            executors.put(name, new HashMap<Class<? extends FeatureHolder>, FunctionExecutor<R>>());
        }

        executors.get(name).put(variant, executor);

        // Invalidate variant cache
        variantCache.clear();
    }

    @Override
    public void removeExecutor(String name, Class<? extends FeatureHolder> variant) {

        if (executors.containsKey(name)) {
            executors.get(name).remove(variant);

            if (executors.get(name).isEmpty()) {
                executors.remove(name);
            }
        }

        // Invalidate variant cache
        variantCache.clear();
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
