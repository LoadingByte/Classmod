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

import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.Initializable;

/**
 * A property is a simple {@link Feature} which stores an object.
 * 
 * @param <T> The type of object which can be stored inside the property.
 */
public interface Property<T> extends Feature, ValueSupplier<T>, Initializable<PropertyDefinition<T>> {

    /**
     * Returns the object which is stored inside the property.
     * Note that all getter function executors are invoked when this is called.
     * 
     * @return The stored object.
     * @throws RuntimeException A getter function executor throws a custom getter-related exception.
     */
    @Override
    public T get();

    /**
     * Changes the object which is stored inside the property.
     * Note that all setter function executors are invoked when this is called.
     * 
     * @param value The new stored object.
     * @throws RuntimeException A setter function executor throws a custom setter-related exception.
     */
    public void set(T value);

}
