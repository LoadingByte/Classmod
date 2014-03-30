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

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Persistent;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.PropertyDefinition;

/**
 * A reference property is a simple {@link Property} which stores an object.
 * During serialization, there is only an id reference serialized. That means that the referenced object has to have an {@link XmlID} annotation.
 * 
 * @param <T> The type of object which can be stored inside the reference property.
 * @see Property
 */
@Persistent
@XmlRootElement
public class ReferenceProperty<T> extends AbstractProperty<T> {

    /**
     * Creates a new {@link PropertyDefinition} that describes a reference property with the given name.
     * 
     * @param name The name of the reference property which the returned {@link PropertyDefinition} describes.
     * @return A {@link PropertyDefinition} which can be used to describe a reference property.
     */
    public static <T> PropertyDefinition<T> createDefinition(String name) {

        return new AbstractPropertyDefinition<T>(name) {

            @Override
            public Property<T> create(FeatureHolder holder) {

                return new ReferenceProperty<T>(getName(), holder);
            }

        };
    }

    /**
     * Creates a new {@link PropertyDefinition} that describes a reference property with the given name and initial value.
     * 
     * @param name The name of the reference property which the returned {@link PropertyDefinition} describes.
     * @param initialValue The initial value of the reference property which the returned {@link PropertyDefinition} describes.
     * @return A {@link PropertyDefinition} which can be used to describe a reference property.
     */
    public static <T> PropertyDefinition<T> createDefinition(String name, final T initialValue) {

        return new AbstractPropertyDefinition<T>(name) {

            @Override
            public Property<T> create(FeatureHolder holder) {

                return new ReferenceProperty<T>(getName(), holder, initialValue);
            }

        };
    }

    @XmlIDREF
    private T reference;

    /**
     * Creates a new empty reference property.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected ReferenceProperty() {

    }

    /**
     * Creates a new reference property with the given name and {@link FeatureHolder}.
     * 
     * @param name The name of the reference property.
     * @param holder The feature holder which has and uses the new reference property.
     */
    public ReferenceProperty(String name, FeatureHolder holder) {

        super(name, holder);
    }

    /**
     * Creates a new reference property with the given name and {@link FeatureHolder}, and sets the initial value.
     * 
     * @param name The name of the reference property.
     * @param holder The feature holder which has and uses the new reference property.
     * @param initialValue The value the new reference property has directly after creation.
     */
    public ReferenceProperty(String name, FeatureHolder holder, T initialValue) {

        super(name, holder, initialValue);
    }

    @Override
    @XmlTransient
    protected T getInternal() {

        return reference;
    }

    @Override
    protected void setInternal(T value) {

        reference = value;
    }

}
