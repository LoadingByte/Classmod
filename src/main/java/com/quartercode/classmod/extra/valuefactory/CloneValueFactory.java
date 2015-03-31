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

import org.apache.commons.lang3.ObjectUtils;

/**
 * A clone value factory clones a provided template every time the {@link #get()} method is called.
 * Note that the supplied objects must be {@link Cloneable}.
 * 
 * @param <T> The type of object that can be created by the clone value factory.
 *        Note that the type which is provided here must be {@link Cloneable}.
 * @see ValueFactory
 */
public class CloneValueFactory<T extends Cloneable> implements ValueFactory<T> {

    private final T template;

    /**
     * Creates a new clone value factory with the given template object.
     * 
     * @param template The template object that is cloned every time {@link #get()} is called.
     */
    public CloneValueFactory(T template) {

        this.template = template;
    }

    @Override
    public T get() {

        return ObjectUtils.clone(template);
    }

}
