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

import java.util.Map;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * A property definition is used to retrieve a {@link Property} from a {@link FeatureHolder}.
 * The property definition also stores the {@link FunctionExecutor}s which are used for the {@link Property}'s getter and setter.<br>
 * <br>
 * Getters and setters are called every time the {@link Property#get()} or {@link Property#set(Object)} method is invoked.
 * Essentially, they are just {@link Function}s that are managed by the defined {@link Property} object.
 * Note that the actual value changing get/set operations are performed at the default priority {@link Prioritized#DEFAULT}.
 * 
 * @param <T> The type of object which can be stored inside the defined {@link Property}.
 * @see Property
 * @see FunctionExecutor
 */
public interface PropertyDefinition<T> extends FeatureDefinition<Property<T>> {

    /**
     * Returns all registered getter {@link FunctionExecutor}s mapped by their names.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @return The registered getter {@link FunctionExecutor}s along with their names.
     */
    public Map<String, FunctionExecutor<T>> getGetterExecutors();

    /**
     * Registers a new getter {@link FunctionExecutor} under the given name to the definition.
     * The registered getter {@link FunctionExecutor} transfers to the getters of all newly created {@link Property}s.
     * 
     * @param name The name of the getter {@link FunctionExecutor} to register.
     *        You can use that name to unregister the getter {@link FunctionExecutor} through {@link #removeGetterExecutor(String)}.
     * @param executor The actual getter {@link FunctionExecutor} object to register.
     */
    public void addGetterExecutor(String name, FunctionExecutor<T> executor);

    /**
     * Unregisters an getter {@link FunctionExecutor} which is registered under the given name from the definition.
     * The unregistered getter {@link FunctionExecutor} won't transfer into new {@link Property}s, but it will stay in the ones which are already created.
     * 
     * @param name The name the getter {@link FunctionExecutor} to unregister has. You have used that name for {@link #addGetterExecutor(String, FunctionExecutor)}.
     */
    public void removeGetterExecutor(String name);

    /**
     * Returns all registered setter {@link FunctionExecutor}s mapped by their names.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @return The registered setter {@link FunctionExecutor}s along with their names.
     */
    public Map<String, FunctionExecutor<Void>> getSetterExecutors();

    /**
     * Registers a new setter {@link FunctionExecutor} under the given name to the definition.
     * The registered setter {@link FunctionExecutor} transfers to the setters of all newly created {@link Property}s.
     * 
     * @param name The name of the setter {@link FunctionExecutor} to register.
     *        You can use that name to unregister the setter {@link FunctionExecutor} through {@link #removeSetterExecutor(String)}.
     * @param executor The actual setter {@link FunctionExecutor} object to register.
     */
    public void addSetterExecutor(String name, FunctionExecutor<Void> executor);

    /**
     * Unregisters an setter {@link FunctionExecutor} which is registered under the given name from the definition.
     * The unregistered setter {@link FunctionExecutor} won't transfer into new {@link Property}s, but it will stay in the ones which are already created.
     * 
     * @param name The name the setter {@link FunctionExecutor} to unregister has. You have used that name for {@link #addSetterExecutor(String, FunctionExecutor)}.
     */
    public void removeSetterExecutor(String name);

}
