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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Persistent;
import com.quartercode.classmod.base.def.AbstractPersistentFeature;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.Property;

/**
 * A reference property is a simple {@link Property} which stores an object.
 * During serialization, there is only an id reference serialized. That means that the referenced object has to have an {@link XmlID} annotation.
 * 
 * @param <T> The type of object which can be stored inside the reference property.
 * @see Property
 */
@Persistent
public class ReferenceProperty<T> extends AbstractPersistentFeature implements Property<T> {

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
     * @param initialValue The value the new object property has directly after creation.
     */
    public ReferenceProperty(String name, FeatureHolder holder, T initialValue) {

        super(name, holder);

        set(initialValue);
    }

    @Override
    @XmlIDREF
    public T get() {

        return reference;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public void set(T value) {

        if (reference instanceof ChildFeatureHolder && ((ChildFeatureHolder<?>) reference).getParent().equals(getHolder())) {
            ((ChildFeatureHolder<FeatureHolder>) reference).setParent(null);
        }

        reference = value;

        if (reference instanceof ChildFeatureHolder) {
            ((ChildFeatureHolder<FeatureHolder>) reference).setParent(getHolder());
        }
    }

    @Override
    public Iterator<T> iterator() {

        Set<T> set = new HashSet<T>();
        set.add(reference);
        return set.iterator();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (reference == null ? 0 : reference.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReferenceProperty<?> other = (ReferenceProperty<?>) obj;
        if (reference == null) {
            if (other.reference != null) {
                return false;
            }
        } else if (!reference.equals(other.reference)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        return getClass().getName() + " [name=" + getName() + ", reference=" + reference + "]";
    }

}
