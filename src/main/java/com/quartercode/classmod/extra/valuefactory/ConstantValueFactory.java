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

package com.quartercode.classmod.extra.valuefactory;

import com.quartercode.classmod.extra.ValueFactory;

/**
 * The constant value factory returns the same provided value every time {@link #get()} is called.
 * 
 * @param <T> The type of object that can be created by the constant value factory.
 * @see ValueFactory
 */
public class ConstantValueFactory<T> implements ValueFactory<T> {

    private final T value;

    /**
     * Creates a new constant value factory with the given constant value object.
     * 
     * @param value The object that is returned unchanged every time {@link #get()} is called.
     */
    public ConstantValueFactory(T value) {

        this.value = value;
    }

    @Override
    public T get() {

        return value;
    }

}
