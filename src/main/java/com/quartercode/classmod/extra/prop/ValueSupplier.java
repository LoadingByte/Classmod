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

package com.quartercode.classmod.extra.prop;

import com.quartercode.classmod.base.Feature;

/**
 * Value suppliers provide a {@link #get()} method for retrieving some value they provide.
 * Typical value suppliers are properties, like the {@link Property} and {@link CollectionProperty} classes.
 * 
 * @param <T> The type of object that can be retrieved through the {@link #get()} method (return value).
 */
public interface ValueSupplier<T> extends Feature {

    /**
     * Returns some kind of value that is an instance of the generic type {@code T}.
     * The type of value should be specified by the implementing class.
     * 
     * @return The value that is provided by the implementing class.
     */
    public T get();

}
