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
 * Persistable features can be serialized.
 * Actually, they should be serialized when their parent {@link FeatureHolder} is serialized.
 * The persistence state of a feature is supplied with the {@link #isPersistent()} method.
 */
public interface Persistable extends Feature {

    /**
     * Returns whether the feature is currently serializable.
     * If this returns {@code true}, it should be serialized when its parent {@link FeatureHolder} is serialized.
     * 
     * @return The current persistence state of the feature.
     */
    public boolean isPersistent();

}
