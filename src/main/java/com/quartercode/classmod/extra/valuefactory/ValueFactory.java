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

package com.quartercode.classmod.extra.valuefactory;

/**
 * A value factory just supplies objects based on the implementation of the factory.
 * It may return different objects or the same object on each call.
 * 
 * @param <T> The type of object that can be created by the value factory.
 */
public interface ValueFactory<T> {

    /**
     * Supplies an object of the type of the generic parameter {@code <T>}.
     * This method may return different objects or the same object on each call.
     * 
     * @return The object that is supplied by the value factory.
     */
    public T get();

}
