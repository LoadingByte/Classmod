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

package com.quartercode.classmod.extra.def;

import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.Storage;

/**
 * A transient property is a simple {@link Property} which isn't persistent.
 * It extends the {@link DefaultProperty} class which provides all the base functionality.
 * 
 * @param <T> The type of object that can be stored inside the transient property.
 * @see DefaultProperty
 */
public class TransientProperty<T> extends DefaultProperty<T> {

    /**
     * Creates a new transient property with the given name, {@link FeatureHolder} and {@link Storage} implementation.
     * Also sets the initially stored object.
     * 
     * @param name The name of the transient property.
     * @param holder The feature holder which has and uses the new transient property.
     * @param storage The {@link Storage} implementation that should be used by the transient property in order to store its values.
     * @param initialValue The value the new transient property has directly after creation.
     */
    public TransientProperty(String name, FeatureHolder holder, Storage<T> storage, T initialValue) {

        super(name, holder, storage, initialValue);
    }

}
