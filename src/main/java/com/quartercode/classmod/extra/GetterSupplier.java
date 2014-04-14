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

/**
 * Getter suppliers provide a {@link #get()} method for retrieving some value of the set generics type.
 * Typical getter suppliers are properties, like the {@link Property} and {@link CollectionProperty} classes.
 * 
 * @param <T> The type of object that can be retrieved through the {@link #get()} method (return value).
 */
public interface GetterSupplier<T> {

    /**
     * Returns some kind of value that has the set generic type <code>T</code>.
     * The type of value should be specified by the implementing class.
     * 
     * @return The value that is provided by the implementing class.
     * @throws RuntimeException A getter function executor throws a custom getter-related exception.
     */
    public T get();

}
