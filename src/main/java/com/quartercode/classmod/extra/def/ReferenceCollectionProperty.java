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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Persistent;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;

/**
 * A reference collection property is a simple persistent {@link CollectionProperty} which stores a {@link Collection}.
 * During serialization, only the id references of the elements of the stored {@link Collection}s are serialized.
 * That means that the referenced elements of the stored {@link Collection} must have an {@link XmlID} annotation.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} the reference collection property holds.
 * @param <C> The type of {@link Collection} the reference collection property stores.
 * @see CollectionProperty
 */
@Persistent
@XmlRootElement
public class ReferenceCollectionProperty<E, C extends Collection<E>> extends AbstractCollectionProperty<E, C> {

    /**
     * Creates a new {@link CollectionPropertyDefinition} that describes a reference collection property with the given name and initial value.
     * 
     * @param name The name of the reference collection property which the returned {@link CollectionPropertyDefinition} describes.
     * @param collection The {@link Collection} the {@link CollectionProperty} that {@link CollectionPropertyDefinition} describes uses.
     * @return A {@link CollectionPropertyDefinition} which can be used to describe a reference collection property.
     */
    // Use generic I(mplementation) parameter for preventing unchecked casts by the method user
    public static <E, C extends Collection<E>, I extends C> CollectionPropertyDefinition<E, C> createDefinition(String name, final I collection) {

        return new AbstractCollectionPropertyDefinition<E, C>(name) {

            @Override
            public CollectionProperty<E, C> create(FeatureHolder holder) {

                return new ReferenceCollectionProperty<E, C>(getName(), holder, collection);
            }

        };
    }

    @XmlIDREF
    private C referenceCollection;

    /**
     * Creates a new empty reference collection property.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected ReferenceCollectionProperty() {

    }

    /**
     * Creates a new reference collection property with the given name, {@link FeatureHolder} and stored {@link Collection}.
     * 
     * @param name The name of the reference collection property.
     * @param holder The feature holder which has and uses the new reference collection property.
     * @param collection The {@link Collection} the new reference collection property stores.
     */
    public ReferenceCollectionProperty(String name, FeatureHolder holder, C collection) {

        super(name, holder, collection);
    }

    @Override
    @XmlTransient
    protected C getInternal() {

        return referenceCollection;
    }

    @Override
    protected void setInternal(C collection) {

        referenceCollection = collection;
    }

}
