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
 * A property definition is used to retrieve a {@link Property} from a {@link FeatureHolder}.
 * The property definition also stores the {@link FunctionExecutor}s which are used for the property's getter and setter.<br>
 * <br>
 * Getters and setters are called every time the {@link Property#get()} or {@link Property#set(Object)} method is invoked.
 * Essentially, they are just {@link Function}s that are managed by the defined property object.
 * Note that the actual value changing get/set operations are performed at the default priority {@link Priorities#DEFAULT}.
 * 
 * @param <T> The type of object that can be stored inside the defined property.
 * @see Property
 * @see FunctionExecutor
 */
public interface PropertyDefinition<T> extends FeatureDefinition<Property<T>>, ValueSupplierDefinition<T, Property<T>> {

    /**
     * The default value of the {@link #isHidden() hiding flag}.
     * It should be used when no hiding flag is explicitly specified or a {@link Property} hasn't been initialized yet.
     */
    public static final boolean HIDDEN_DEFAULT     = false;

    /**
     * The default value of the {@link #isPersistent() persistence flag}.
     * It should be used when no persistence flag is explicitly specified or a {@link Property} hasn't been initialized yet.
     */
    public static final boolean PERSISTENT_DEFAULT = true;

    /**
     * Returns the hiding flag for all created {@link Property} instances.
     * See {@link Hideable#isHidden()} for more information on that attribute.
     * 
     * @return The hiding flag of the definition.
     * @see #HIDDEN_DEFAULT
     */
    public boolean isHidden();

    /**
     * Returns the persistence flag for all created {@link Property} instances.
     * See {@link Persistable#isPersistent()} for more information on that attribute.
     * 
     * @return The persistence flag of the definition.
     * @see #PERSISTENT_DEFAULT
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
    public Map<String, FunctionExecutorWrapper<T>> getGetterExecutorsForVariant(Class<? extends FeatureHolder> variant);

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
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor);

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
    public void addGetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<T> executor, int priority);

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
     * Returns {@link FunctionExecutorWrapper}s for the setter {@link FunctionExecutor}s that are registered for the given variant and all supervariants.
     * The variant class was set during the {@link #addSetterExecutor(String, Class, FunctionExecutor, int)} call.
     * Modifications to the returned map do not affect the storage of the definition.
     * 
     * @param variant The variant whose setter function executors should be returned.
     * @return The setter function executors that belong to the given variant or any of its supervariants (as wrappers).
     *         Note that these executors are returned along with their names.
     */
    public Map<String, FunctionExecutorWrapper<Void>> getSetterExecutorsForVariant(Class<? extends FeatureHolder> variant);

    /**
     * Adds a new setter {@link FunctionExecutor} with the default priority ({@link Priorities#DEFAULT}) to the definition.
     * The registered setter function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the setter function executor to register.
     *        This name can be used to remove the setter function executor through {@link #removeSetterExecutor(String, Class)}.
     * @param variant The class the setter function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual setter function executor object to register.
     */
    public void addSetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor);

    /**
     * Adds a new setter {@link FunctionExecutor} with the given priority to the definition.
     * The registered setter function executor will be used by all {@link Property}s created after the call.
     * 
     * @param name The name of the setter function executor to register.
     *        This name can be used to remove the setter function executor through {@link #removeSetterExecutor(String, Class)}.
     * @param variant The class the setter function executor is used for.
     *        It will also be used for every subclass of this class.
     * @param executor The actual setter function executor object to register.
     * @param priority The priority of the new setter function executor.
     *        It is used to determine the order in which the available setter function executors are invoked.
     *        Executors with a high priority are invoked before executors with a low priority.
     */
    public void addSetterExecutor(String name, Class<? extends FeatureHolder> variant, FunctionExecutor<Void> executor, int priority);

    /**
     * Removes an existing setter function executor from the definition.
     * The unregistered setter function executor won't be used by any new {@link Property}s, but it will stay in the ones which have already been created.
     * 
     * @param name The name the setter function executor to unregister has.
     *        This name was used for {@link #addSetterExecutor(String, Class, FunctionExecutor)}.
     * @param variant The class the setter function executor was used for.
     */
    public void removeSetterExecutor(String name, Class<? extends FeatureHolder> variant);

}
