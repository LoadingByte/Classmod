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

import java.util.Collection;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.Storage;

/**
 * A transient collection property is a simple {@link CollectionProperty} which isn't persistent.
 * It extends the {@link DefaultCollectionProperty} class which provides all the base functionality.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} the transient collection property holds.
 * @param <C> The type of {@link Collection} the transient collection property stores.
 * @see CollectionProperty
 */
public class TransientCollectionProperty<E, C extends Collection<E>> extends DefaultCollectionProperty<E, C> {

    /**
     * Creates a new transient collection property with the given name, {@link FeatureHolder}, and {@link Storage} implementation.
     * 
     * @param name The name of the transient collection property.
     * @param holder The feature holder which has and uses the new transient collection property.
     * @param storage The {@link Storage} implementation that should be used by the transient collection property for storing its {@link Collection}.
     */
    public TransientCollectionProperty(String name, FeatureHolder holder, Storage<C> storage) {

        super(name, holder, storage);
    }

}
