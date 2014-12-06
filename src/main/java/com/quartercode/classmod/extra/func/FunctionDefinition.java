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

package com.quartercode.classmod.extra.func;

import java.util.List;
import java.util.Map;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * A function definition is used to retrieve a {@link Function} from a {@link FeatureHolder}.
 * The function definition also stores the parameters and {@link FunctionExecutor}s which are used for the function object.
 * 
 * @param <R> The type of the return value of the defined function.
 * @see Function
 * @see FunctionExecutor
 */
public interface FunctionDefinition<R> extends FeatureDefinition<Function<R>> {

    /**
     * Returns a list of all parameters which are used by the function definition.
     * See {@link #setParameter(int, Class)} for further explanation.
     * 
     * @return All parameters which are used by the function definition.
     */
    public List<Class<?>> getParameters();

    /**
     * Sets a function parameter which is required in the {@link Function#invoke(Object...)} method.
     * Such a parameter is comparable with a normal method parameter.
     * This method could be created using the calls:
     * 
     * <pre>
     * setParameter(0, Integer.class);
     * setParameter(1, Class.class);
     * </pre>
     * 
     * @param index The index of the parameter (0 is the first one, 1 the second one etc.).
     * @param type The type the argument for the parameter must have. null removes the parameter.
     */
    public void setParameter(int index, Class<?> type);

    /**
     * Returns {@link FunctionExecutorWrapper}s for the {@link FunctionExecutor}s that are registered for the given variant and all supervariants.
     * The variant class was set during the {@link #addExecutor(String, Class, FunctionExecutor)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @param variant The variant whose function executors should be returned.
     * @return The function executors that belong to the given variant or any of its supervariants (as wrappers).
     *         Note that these executors are returned along with their names.
     */
    public Map<String, FunctionExecutorWrapper<R>> getExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Adds a new {@link FunctionExecutor} with the default priority ({@link Priorities#DEFAULT}) to the definition.
     * The registered function executor will be used by all {@link Function}s created after the call.
     * 
     * @param name The name of the function executor to register.
     *        This name can be used to remove the function executor through {@link #removeExecutor(String, Class)}.
     * @param variant The class the function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual function executor object to register.
     */
    public void addExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor);

    /**
     * Adds a new {@link FunctionExecutor} with the given priority to the definition.
     * The registered function executor will be used by all {@link Function}s created after the call.
     * 
     * @param name The name of the function executor to register.
     *        This name can be used to remove the function executor through {@link #removeExecutor(String, Class)}.
     * @param variant The class the function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual function executor object to register.
     * @param priority The priority of the new function executor.
     *        It is used to determine the order in which the available function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor, int priority);

    /**
     * Removes an existing {@link FunctionExecutor} from the definition.
     * The unregistered function executor won't be used by any new {@link Function}s, but it will stay in the ones which have already been created.
     * 
     * @param name The name the function executor to unregister has.
     *        This name was used for {@link #addExecutor(String, Class, FunctionExecutor, int)}.
     * @param variant The class the function executor for removal is used for.
     */
    public void removeExecutor(String name, Class<? extends FeatureHolder> variant);

}
