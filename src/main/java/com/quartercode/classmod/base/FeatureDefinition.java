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
 * A feature definition is used to retrieve a {@link Feature} from a {@link FeatureHolder}.
 * It contains the name of the feature and the type it has as a generic parameter.
 * You can use a feature definition to construct a new instance of the defined feature through {@link #create(FeatureHolder)}.
 * 
 * @param <F> The type the defined feature has.
 * @see Feature
 * @see FeatureHolder
 */
public interface FeatureDefinition<F extends Feature> extends Named {

    /**
     * Returns the name of the defined {@link Feature}.
     * The name is used for storing and accessing a created feature in a {@link FeatureHolder}.
     * 
     * @return The name of the feature.
     */
    @Override
    public String getName();

    /**
     * Creates a new {@link Feature} which is defined by this feature definition using the given holder.
     * The holder is a {@link FeatureHolder} which can have different features.
     * 
     * @param holder The feature holder which holds the new feature.
     * @return The newly created feature.
     */
    public F create(FeatureHolder holder);

}
