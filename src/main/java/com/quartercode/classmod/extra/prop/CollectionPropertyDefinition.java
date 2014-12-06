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

package com.quartercode.classmod.extra.prop;

import java.util.Collection;
import java.util.Map;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.base.Persistable;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.Priorities;

/**
 * A collection property definition is used to retrieve a {@link CollectionProperty} from a {@link FeatureHolder}.
 * The property definition also stores the {@link FunctionExecutor}s which are used for the collection property's getter, adder and remover.<br>
 * <br>
 * Getters are called every time the {@link CollectionProperty#get()} method is invoked,
 * while adders and removers are called through the {@link CollectionProperty#add(Object)} or {@link CollectionProperty#remove(Object)} methods.
 * Essentially, they are just {@link Function}s that are managed by the defined collection property object.
 * Note that the actual value changing get/set operations are performed at the default priority {@link Priorities#DEFAULT}.
 * 
 * @param <E> The type of object that can be stored inside the defined collection property's {@link Collection}.
 * @param <C> The type of collection that can be stored inside the defined collection property.
 * @see CollectionProperty
 * @see FunctionExecutor
 */
public interface CollectionPropertyDefinition<E, C extends Collection<E>> extends FeatureDefinition<CollectionProperty<E, C>>, ValueSupplierDefinition<C, CollectionProperty<E, C>> {

    /**
     * Returns a {@link Collection} instance that can be used by a {@link CollectionProperty}.
     * This method should always create a new object, so the same entries are not shared through different properties (that would be a monostate).
     * 
     * @return A collection instance that can be used by a collection property.
     */
    public C newCollection();

    /**
     * Returns the hiding flag for all created {@link CollectionProperty} instances.
     * See {@link Hideable#isHidden()} for more information on that attribute.
     * 
     * @return The hiding flag of the definition.
     */
    public boolean isHidden();

    /**
     * Returns the persistence flag for all created {@link CollectionProperty} instances.
     * See {@link Persistable#isPersistent()} for more information on that attribute.
     * 
     * @return The persistence flag of the definition.
     */
    public boolean isPersistent();

    /**
     * Returns {@link FunctionExecutorWrapper}s for the getter {@link FunctionExecutor}s that are registered for the given variant and all supervariants.
     * The variant class was set during the {@link #addGetterExecutor(String, Class, FunctionExecutor, int)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @param variant The variant whose getter function executors should be returned.
     * @return The getter function executors that belong to the given variant or any of its supervariants (as wrappers).
     *         Note that these executors are returned along with their names.
     */
    public Map<String, FunctionExecutorWrapper<C>> getGetterExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Adds a new getter {@link FunctionExecutor} with the default priority ({@link Priorities#DEFAULT}) to the definition.
     * The registered getter function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the getter function executor to register.
     *        This name can be used to remove the getter function executor through {@link #removeGetterExecutor(String, Class)}.
     * @param variant The class the getter function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual getter function executor object to register.
     */
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor);

    /**
     * Adds a new getter {@link FunctionExecutor} with the given priority to the definition.
     * The registered getter function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the getter function executor to register.
     *        This name can be used to remove the getter function executor through {@link #removeGetterExecutor(String, Class)}.
     * @param variant The class the getter function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual getter function executor object to register.
     * @param priority The priority of the new getter function executor.
     *        It is used to determine the order in which the available getter function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor, int priority);

    /**
     * Removes an existing getter function executor from the definition.
     * The unregistered getter function executor won't be used by any new {@link Property}s, but it will stay in the ones which have already been created.
     * 
     * @param name The name the getter function executor to unregister has.
     *        This name was used for {@link #addGetterExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the getter function executor was used for.
     */
    public void removeGetterExecutor(String name, Class<? extends FeatureHolder> variant);

    /**
     * Returns {@link FunctionExecutorWrapper}s for the adder {@link FunctionExecutor}s that are registered for the given variant and all supervariants.
     * The variant class was set during the {@link #addAdderExecutor(String, Class, FunctionExecutor, int)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @param variant The variant whose adder function executors should be returned.
     * @return The adder function executors that belong to the given variant or any of its supervariants (as wrappers).
     *         Note that these executors are returned along with their names.
     */
    public Map<String, FunctionExecutorWrapper<Void>> getAdderExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Adds a new adder {@link FunctionExecutor} with the default priority ({@link Priorities#DEFAULT}) to the definition.
     * The registered adder function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the adder function executor to register.
     *        This name can be used to remove the adder function executor through {@link #removeAdderExecutor(String, Class)}.
     * @param variant The class the adder function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual adder function executor object to register.
     */
    public void addAdderExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Adds a new adder {@link FunctionExecutor} with the given priority to the definition.
     * The registered adder function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the adder function executor to register.
     *        This name can be used to remove the adder function executor through {@link #removeAdderExecutor(String, Class)}.
     * @param variant The class the adder function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual adder function executor object to register.
     * @param priority The priority of the new adder function executor.
     *        It is used to determine the order in which the available adder function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addAdderExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority);

    /**
     * Removes an existing adder function executor from the definition.
     * The unregistered adder function executor won't be used by any new {@link Property}s, but it will stay in the ones which have already been created.
     * 
     * @param name The name the adder function executor to unregister has.
     *        This name was used for {@link #addAdderExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the adder function executor was used for.
     */
    public void removeAdderExecutor(String name, Class<? extends FeatureHolder> variant);

    /**
     * Returns {@link FunctionExecutorWrapper}s for the remover {@link FunctionExecutor}s that are registered for the given variant and all supervariants.
     * The variant class was set during the {@link #addRemoverExecutor(String, Class, FunctionExecutor, int)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @param variant The variant whose remover function executors should be returned.
     * @return The remover function executors that belong to the given variant or any of its supervariants (as wrappers).
     *         Note that these executors are returned along with their names.
     */
    public Map<String, FunctionExecutorWrapper<Void>> getRemoverExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Adds a new remover {@link FunctionExecutor} with the default priority ({@link Priorities#DEFAULT}) to the definition.
     * The registered remover function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the remover function executor to register.
     *        This name can be used to remove the remover function executor through {@link #removeRemoverExecutor(String, Class)}.
     * @param variant The class the remover function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual remover function executor object to register.
     */
    public void addRemoverExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Adds a new remover {@link FunctionExecutor} with the given priority to the definition.
     * The registered remover function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the remover function executor to register.
     *        This name can be used to remove the remover function executor through {@link #removeRemoverExecutor(String, Class)}.
     * @param variant The class the remover function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual remover function executor object to register.
     * @param priority The priority of the new remover function executor.
     *        It is used to determine the order in which the available remover function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addRemoverExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority);

    /**
     * Removes an existing remover function executor from the definition.
     * The unregistered remover function executor won't be used by any new {@link Property}s, but it will stay in the ones which have already been created.
     * 
     * @param name The name the remover function executor to unregister has.
     *        This name was used for {@link #addRemoverExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the remover function executor was used for.
     */
    public void removeRemoverExecutor(String name, Class<? extends FeatureHolder> variant);

}
