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

package com.quartercode.classmod.extra;

import java.util.Collection;
import java.util.Map;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * A collection property definition is used to retrieve a {@link CollectionProperty} from a {@link FeatureHolder}.
 * The property definition also stores the {@link FunctionExecutor}s which are used for the {@link CollectionProperty}'s getter, adder and remover.<br>
 * <br>
 * Getters are called every time the {@link CollectionProperty#get()} method is invoked,
 * while adders and removers are called through the {@link CollectionProperty#add(Object)} or {@link CollectionProperty#remove(Object)} methods.
 * Essentially, they are just {@link Function}s that are managed by the defined {@link CollectionProperty} object.
 * Note that the actual value changing get/set operations are performed at the default priority {@link Prioritized#DEFAULT}.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} of the defined {@link CollectionProperty}.
 * @param <C> The type of the {@link Collection} the defined {@link CollectionProperty} stores.
 * @see CollectionProperty
 * @see FunctionExecutor
 */
public interface CollectionPropertyDefinition<E, C extends Collection<E>> extends FeatureDefinition<CollectionProperty<E, C>> {

    /**
     * Returns all registered getter {@link FunctionExecutor}s mapped by their names for the given variant and all supervariants.
     * The variant class was set on the {@link #addGetterExecutor(String, Class, FunctionExecutor)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @param variant The variant whose getter {@link FunctionExecutor}s should be returned.
     * @return The registered getter {@link FunctionExecutor}s along with their names.
     */
    public Map<String, FunctionExecutor<C>> getGetterExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Registers a new getter {@link FunctionExecutor} under the given name to the definition.
     * The registered getter {@link FunctionExecutor} transfers to the getters of all newly created {@link CollectionProperty}s.
     * 
     * @param name The name of the getter {@link FunctionExecutor} to register.
     *        You can use that name to unregister the getter {@link FunctionExecutor} through {@link #removeGetterExecutor(String, Class)}.
     * @param variant The class the getter {@link FunctionExecutor} is used for. It will also be used for every subclass of this class.
     * @param executor The actual getter {@link FunctionExecutor} object to register.
     */
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<C> executor);

    /**
     * Unregisters an getter {@link FunctionExecutor} which is registered under the given name from the definition.
     * The unregistered getter {@link FunctionExecutor} won't transfer into new {@link CollectionProperty}s, but it will stay in the ones which are already created.
     * 
     * @param name The name the getter {@link FunctionExecutor} to unregister has.
     *        You have used that name for {@link #addGetterExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the getter {@link FunctionExecutor} was used for.
     */
    public void removeGetterExecutor(String name, Class<? extends FeatureHolder> variant);

    /**
     * Returns all registered adder {@link FunctionExecutor}s mapped by their names for the given variant and all supervariants.
     * The variant class was set on the {@link #addAdderExecutor(String, Class, FunctionExecutor)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @return The registered adder {@link FunctionExecutor}s along with their names.
     */
    public Map<String, FunctionExecutor<Void>> getAdderExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Registers a new adder {@link FunctionExecutor} under the given name to the definition.
     * The registered adder {@link FunctionExecutor} transfers to the adders of all newly created {@link CollectionProperty}s.
     * 
     * @param name The name of the adder {@link FunctionExecutor} to register.
     *        You can use that name to unregister the adder {@link FunctionExecutor} through {@link #removeAdderExecutor(String, Class)}.
     * @param variant The class the adder {@link FunctionExecutor} is used for. It will also be used for every subclass of this class.
     * @param executor The actual adder {@link FunctionExecutor} object to register.
     */
    public void addAdderExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Unregisters an adder {@link FunctionExecutor} which is registered under the given name from the definition.
     * The unregistered adder {@link FunctionExecutor} won't transfer into new {@link CollectionProperty}s, but it will stay in the ones which are already created.
     * 
     * @param name The name the adder {@link FunctionExecutor} to unregister has.
     *        You have used that name for {@link #addAdderExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the adder {@link FunctionExecutor} was used for.
     */
    public void removeAdderExecutor(String name, Class<? extends FeatureHolder> variant);

    /**
     * Returns all registered remover {@link FunctionExecutor}s mapped by their names for the given variant and all supervariants.
     * The variant class was set on the {@link #addRemoverExecutor(String, Class, FunctionExecutor)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @return The registered remover {@link FunctionExecutor}s along with their names.
     */
    public Map<String, FunctionExecutor<Void>> getRemoverExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Registers a new remover {@link FunctionExecutor} under the given name to the definition.
     * The registered remover {@link FunctionExecutor} transfers to the removers of all newly created {@link CollectionProperty}s.
     * 
     * @param name The name of the remover {@link FunctionExecutor} to register.
     *        You can use that name to unregister the remover {@link FunctionExecutor} through {@link #removeRemoverExecutor(String, Class)}.
     * @param variant The class the remover {@link FunctionExecutor} is used for. It will also be used for every subclass of this class.
     * @param executor The actual remover {@link FunctionExecutor} object to register.
     */
    public void addRemoverExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Unregisters an remover {@link FunctionExecutor} which is registered under the given name from the definition.
     * The unregistered remover {@link FunctionExecutor} won't transfer into new {@link CollectionProperty}s, but it will stay in the ones which are already created.
     * 
     * @param name The name the remover {@link FunctionExecutor} to unregister has.
     *        You have used that name for {@link #addRemoverExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the remover {@link FunctionExecutor} was used for.
     */
    public void removeRemoverExecutor(String name, Class<? extends FeatureHolder> variant);

}
