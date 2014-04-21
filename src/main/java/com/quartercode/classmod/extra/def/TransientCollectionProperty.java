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
import com.quartercode.classmod.extra.CollectionPropertyDefinition;

/**
 * A transient collection property is a simple <b>non-persistent</b> {@link CollectionProperty} which stores a {@link Collection}.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} the transient collection property holds.
 * @param <C> The type of {@link Collection} the transient collection property stores.
 * @see CollectionProperty
 */
public class TransientCollectionProperty<E, C extends Collection<E>> extends AbstractCollectionProperty<E, C> {

    /**
     * Creates a new {@link CollectionPropertyDefinition} that describes a transient collection property with the given name and initial value.
     * 
     * @param name The name of the transient collection property which the returned {@link CollectionPropertyDefinition} describes.
     * @param collection The {@link Collection} the {@link CollectionProperty} that {@link CollectionPropertyDefinition} describes uses.
     * @param cloneCollection Whether the collection object should be cloned for every new instance of the property (mostly {@code true}).
     *        By cloning the collection, the collection that is stored in the definition is not affected by changes made to the collection that is stored in the property.
     * @return A {@link CollectionPropertyDefinition} which can be used to describe a transient collection property.
     */
    // Use generic I(mplementation) parameter for preventing unchecked casts by the method user
    public static <E, C extends Collection<E>, I extends C> CollectionPropertyDefinition<E, C> createDefinition(String name, final I collection, final boolean cloneCollection) {

        return new AbstractCollectionPropertyDefinition<E, C>(name) {

            @Override
            public CollectionProperty<E, C> create(FeatureHolder holder) {

                C actualCollection = collection;
                if (cloneCollection) {
                    actualCollection = PropertyCloneUtil.cloneInitialValue(collection);
                }

                return new TransientCollectionProperty<E, C>(getName(), holder, actualCollection);
            }

        };
    }

    private C collection;

    /**
     * Creates a new transient collection property with the given name, {@link FeatureHolder} and stored {@link Collection}.
     * 
     * @param name The name of the transient collection property.
     * @param holder The feature holder which has and uses the new transient collection property.
     * @param collection The {@link Collection} the new transient collection property stores.
     */
    public TransientCollectionProperty(String name, FeatureHolder holder, C collection) {

        super(name, holder, collection);
    }

    @Override
    protected C getInternal() {

        return collection;
    }

    @Override
    protected void setInternal(C collection) {

        this.collection = collection;
    }

}
