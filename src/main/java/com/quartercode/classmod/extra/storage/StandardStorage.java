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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.quartercode.classmod.extra.Storage;

/**
 * The standard storage stores objects in a plain member variable and makes them serializable.
 * 
 * @param <T> The type of object that can be stored inside the standard storage.
 * @see Storage
 */
@XmlRootElement
public class StandardStorage<T> extends Storage<T> {

    @XmlElement
    @XmlJavaTypeAdapter (ObjectAdapter.class)
    private T object;

    @Override
    public T get() {

        return object;
    }

    @Override
    public void set(T object) {

        this.object = object;
    }

    @Override
    public Storage<T> reproduce() {

        return new StandardStorage<>();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (object == null ? 0 : object.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof StandardStorage)) {
            return false;
        } else {
            StandardStorage<?> other = (StandardStorage<?>) obj;
            if (!EqualsUtil.equalsConsiderArrays(object, other.object)) {
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
