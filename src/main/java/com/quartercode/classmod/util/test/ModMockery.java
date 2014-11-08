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

package com.quartercode.classmod.util.test;

import java.util.Collection;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Priorities;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * A mod mockery is able to temporarily change {@link FeatureDefinition}s for testing purposes.
 * For example, a {@link FunctionExecutor} could be added to a {@link FunctionDefinition} for simulating an interface implementation.
 * After the test, the {@link #close()} method automatically reverts everything that was modified by the mockery.
 */
public interface ModMockery extends AutoCloseable {

    // ----- Function -----

    /**
     * Temporarily adds the given {@link FunctionExecutor} to the given {@link FunctionDefinition} using the given parameters and {@link Priorities#DEFAULT}.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The function definition the executor should be added to.
     * @param name The name the function executor should be registered under.
     * @param variant The variant that defines which classes use the function executor.
     *        A custom class, which extends the original function-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added function executor.
     * @param executor The function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor);

    /**
     * Temporarily adds the given {@link FunctionExecutor} to the given {@link FunctionDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The function definition the executor should be added to.
     * @param name The name the function executor should be registered under.
     * @param variant The variant that defines which classes use the function executor.
     *        A custom class, which extends the original function-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added function executor.
     * @param executor The function executor that should be added.
     *        It could be created with a regular mocking framework.
     * @param priority The priority of the function executor.
     *        It is used to determine the order in which the available function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public <R> void addFuncExec(FunctionDefinition<R> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<R> executor, int priority);

    // ----- Property -----

    /**
     * Temporarily adds the given getter {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters and {@link Priorities#DEFAULT}.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The property definition the getter executor should be added to.
     * @param name The name the getter function executor should be registered under.
     * @param variant The variant that defines which classes use the getter function executor.
     *        A custom class, which extends the original property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added getter function executor.
     * @param executor The getter function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor);

    /**
     * Temporarily adds the given getter {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The property definition the getter executor should be added to.
     * @param name The name the getter function executor should be registered under.
     * @param variant The variant that defines which classes use the getter function executor.
     *        A custom class, which extends the original property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added getter function executor.
     * @param executor The getter function executor that should be added.
     *        It could be created with a regular mocking framework.
     * @param priority The priority of the getter function executor.
     *        It is used to determine the order in which the available getter function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public <T> void addPropGetter(PropertyDefinition<T> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor, int priority);

    /**
     * Temporarily adds the given setter {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters and {@link Priorities#DEFAULT}.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The property definition the setter executor should be added to.
     * @param name The name the setter function executor should be registered under.
     * @param variant The variant that defines which classes use the setter function executor.
     *        A custom class, which extends the original property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added setter function executor.
     * @param executor The setter function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public void addPropSetter(PropertyDefinition<?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Temporarily adds the given setter {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The property definition the setter executor should be added to.
     * @param name The name the setter function executor should be registered under.
     * @param variant The variant that defines which classes use the setter function executor.
     *        A custom class, which extends the original property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added setter function executor.
     * @param executor The setter function executor that should be added.
     *        It could be created with a regular mocking framework.
     * @param priority The priority of the setter function executor.
     *        It is used to determine the order in which the available setter function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addPropSetter(PropertyDefinition<?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority);

    // ----- Collection Property -----

    /**
     * Temporarily adds the given getter {@link FunctionExecutor} to the given {@link CollectionPropertyDefinition} using the given parameters and {@link Priorities#DEFAULT}.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The collection property definition the getter executor should be added to.
     * @param name The name the getter function executor should be registered under.
     * @param variant The variant that defines which classes use the getter function executor.
     *        A custom class, which extends the original collection-property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added getter function executor.
     * @param executor The getter function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public <C extends Collection<?>> void addCollPropGetter(CollectionPropertyDefinition<?, C> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor);

    /**
     * Temporarily adds the given getter {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The collection property definition the getter executor should be added to.
     * @param name The name the getter function executor should be registered under.
     * @param variant The variant that defines which classes use the getter function executor.
     *        A custom class, which extends the original collection-property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added getter function executor.
     * @param executor The getter function executor that should be added.
     *        It could be created with a regular mocking framework.
     * @param priority The priority of the getter function executor.
     *        It is used to determine the order in which the available getter function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public <C extends Collection<?>> void addCollPropGetter(CollectionPropertyDefinition<?, C> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor, int priority);

    /**
     * Temporarily adds the given adder {@link FunctionExecutor} to the given {@link CollectionPropertyDefinition} using the given parameters and {@link Priorities#DEFAULT}.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The collection property definition the adder executor should be added to.
     * @param name The name the adder function executor should be registered under.
     * @param variant The variant that defines which classes use the adder function executor.
     *        A custom class, which extends the original collection-property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added adder function executor.
     * @param executor The adder function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public void addCollPropAdder(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Temporarily adds the given adder {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The collection property definition the adder executor should be added to.
     * @param name The name the adder function executor should be registered under.
     * @param variant The variant that defines which classes use the adder function executor.
     *        A custom class, which extends the original collection-property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added adder function executor.
     * @param executor The adder function executor that should be added.
     *        It could be created with a regular mocking framework.
     * @param priority The priority of the adder function executor.
     *        It is used to determine the order in which the available adder function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addCollPropAdder(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority);

    /**
     * Temporarily adds the given remover {@link FunctionExecutor} to the given {@link CollectionPropertyDefinition} using the given parameters and {@link Priorities#DEFAULT}.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The collection property definition the remover executor should be added to.
     * @param name The name the remover function executor should be registered under.
     * @param variant The variant that defines which classes use the remover function executor.
     *        A custom class, which extends the original collection-property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added remover function executor.
     * @param executor The remover function executor that should be added.
     *        It could be created with a regular mocking framework.
     */
    public void addCollPropRemover(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Temporarily adds the given remover {@link FunctionExecutor} to the given {@link PropertyDefinition} using the given parameters.
     * The change is reverted by the {@link #close()} method.
     * 
     * @param definition The collection property definition the remover executor should be added to.
     * @param name The name the remover function executor should be registered under.
     * @param variant The variant that defines which classes use the remover function executor.
     *        A custom class, which extends the original collection-property-defining class, should be used here.
     *        Later on, a new instance of that custom class can be created for using the added remover function executor.
     * @param executor The remover function executor that should be added.
     *        It could be created with a regular mocking framework.
     * @param priority The priority of the remover function executor.
     *        It is used to determine the order in which the available remover function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addCollPropRemover(CollectionPropertyDefinition<?, ?> definition, String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority);

    /**
     * Reverts everything that was modified by the mockery.
     * For example, any added {@link FunctionExecutor}s are removed.
     * This method should be called after the test.
     */
    @Override
    public void close();

}
