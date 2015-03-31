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

package com.quartercode.classmod.base;

import java.util.UUID;

/**
 * A feature holder is a class which is modifiable through {@link Feature}s.
 * A user can retrieve features through the central access method {@link #get(FeatureDefinition)}.
 * Such features are defined by {@link FeatureDefinition}s which describe how a feature looks like.
 * 
 * @see Feature
 * @see FeatureDefinition
 */
public interface FeatureHolder extends Iterable<Feature> {

    /**
     * Returns the {@link UUID} of the feature holder.
     * It has been generated using {@link UUID#randomUUID()} on object creation and is persistent.
     * 
     * @return The unique feature holder id.
     */
    public UUID getUUID();

    /**
     * Returns the {@link Feature} which is defined by the given {@link FeatureDefinition}.
     * The method should create a new feature from the definition if the requested one doesn't exist.
     * 
     * @param definition The feature definition which describes the requested feature.
     * @return The feature which is requested.
     */
    public <F extends Feature> F get(FeatureDefinition<F> definition);

}
