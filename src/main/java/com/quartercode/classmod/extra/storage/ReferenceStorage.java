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

package com.quartercode.classmod.extra.storage;

import java.util.Collection;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.quartercode.classmod.extra.Storage;

/**
 * The reference storage stores objects in a plain member variable and only serializes their {@link XmlID} references.
 * That means that the referenced object must have a {@link XmlID} annotation.<br>
 * <br>
 * Note that the {@link ReferenceCollectionStorage} class should be used for referencing entire collections.
 * 
 * @param <T> The type of object that can be stored inside the reference storage.
 * @see Storage
 */
@XmlRootElement
public class ReferenceStorage<T> extends Storage<T> {

    @XmlIDREF
    private T reference;

    @Override
    public T get() {

        return reference;
    }

    @Override
    public void set(T object) {

        // Catch programming mistakes
        if (object instanceof Collection) {
            throw new IllegalStateException("ReferenceCollectionStorage instances should be used for referencing multiple objects in a collection");
        }

        this.reference = object;
    }

    @Override
    public Storage<T> reproduce() {

        return new ReferenceStorage<>();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (reference == null ? 0 : reference.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof ReferenceStorage)) {
            return false;
        } else {
            ReferenceStorage<?> other = (ReferenceStorage<?>) obj;
            if (!EqualsUtil.equalsConsiderArrays(reference, other.reference)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this);
    }

}
