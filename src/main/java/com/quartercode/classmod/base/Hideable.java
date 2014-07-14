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

package com.quartercode.classmod.base;

/**
 * Hideable features can be hidden from their parent {@link FeatureHolder}s' {@code hashCode()} and {@code equals()} methods.
 * That means that the features do not affect the result of these methods.
 * The output is the same regardless of whether the feature is added to the holder or not.<br>
 * <br>
 * The hiding state of a feature is supplied with the {@link #isHidden()} method.
 * It is updated inside the feature holder every time the feature is retrieved through {@link FeatureHolder#get(FeatureDefinition)}.
 * That means that a new hiding state is only applied when the feature is retrieved.
 */
public interface Hideable extends Feature {

    /**
     * Returns whether the feature is currently hidden.
     * If this returns {@code true}, it should be excluded from the {@code hashCode()} and {@code equals()} methods of its parent {@link FeatureHolder}.
     * 
     * @return The current hiding state of the feature.
     */
    public boolean isHidden();

}
