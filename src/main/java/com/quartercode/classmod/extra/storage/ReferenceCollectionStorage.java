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
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.quartercode.classmod.extra.Storage;

/**
 * The reference collection storage stores multiple objects, whose {@link XmlID} references are serialized, in a plain member variable {@link Collection}.
 * That means that the referenced collection entries objects must have a {@link XmlID} annotation.
 * 
 * @param <E> The type of object that can be referenced inside the reference collection.
 * @param <C> The type of collection that can be stored by the reference collection storage.
 * @see Storage
 */
@XmlRootElement
public class ReferenceCollectionStorage<E, C extends Collection<E>> extends Storage<C> {

    @XmlElement (name = "referenceEntry")
    @XmlIDREF
    private C referenceCollection;

    @Override
    public C get() {

        return referenceCollection;
    }

    @Override
    public void set(C object) {

        this.referenceCollection = object;
    }

    @Override
    public Storage<C> reproduce() {

        return new ReferenceCollectionStorage<E, C>();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (referenceCollection == null ? 0 : referenceCollection.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof ReferenceCollectionStorage)) {
            return false;
        } else {
            ReferenceCollectionStorage<?, ?> other = (ReferenceCollectionStorage<?, ?>) obj;
            if (!Objects.equals(referenceCollection, other.referenceCollection)) {
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
