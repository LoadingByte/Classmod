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

import com.quartercode.classmod.base.FeatureDefinition;

/**
 * Value supplier definitions define features that must be {@link ValueSupplier}s.
 * Such value suppliers provide the {@link ValueSupplier#get()} method that returns the supplied value.<br>
 * <br>
 * See the value supplier doc for more information on value suppliers.
 * 
 * @param <T> The type of object that can be retrieved through the {@link ValueSupplier#get()} method (return value).
 * @param <S> The exact type of the returned {@link ValueSupplier}.
 * @see ValueSupplier
 */
public interface ValueSupplierDefinition<T, S extends ValueSupplier<T>> extends FeatureDefinition<S> {

}
