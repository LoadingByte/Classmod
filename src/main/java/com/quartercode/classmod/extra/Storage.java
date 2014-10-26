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

package com.quartercode.classmod.extra;

import javax.xml.bind.Unmarshaller;

/**
 * A storage object just stores another object inside of it.
 * The stored object can be changed with the {@link #set(Object)} method and retrieved with the {@link #get()} method.
 * Every storage implementation must guarantee that after setting an object it can be retrieved with the get method immediately.<br>
 * <br>
 * Note that this needs to be an abstract class in order for JAXB to accept serializing its subclasses.
 * 
 * @param <T> The type of object that can be stored inside the storage.
 */
public abstract class Storage<T> implements XmlPassthroughElement {

    private transient Object xmlParent;

    @Override
    public Object getXmlParent() {

        return xmlParent;
    }

    protected void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {

        xmlParent = parent;
    }

    /**
     * Returns the object that is stored by the storage.
     * That stored object can be changed with the {@link #set(Object)} method.
     * 
     * @return The stored object.
     */
    public abstract T get();

    /**
     * Changes the object that is stored by the storage to the given one.
     * That new object can be retrieved with the {@link #get()} method.
     * 
     * @param object The new stored object.
     */
    public abstract void set(T object);

    /**
     * Creates a new <b>empty</b> instance of the <b>same storage type</b> which implements the method.
     * The following code snippet describes the reproduction contract:
     * 
     * <pre>
     * Storage&lt;String&gt; storage1 = ...
     * storage1.set(&quot;test&quot;);
     * 
     * Storage&lt;String&gt; storage2 = storage1.reproduce();
     * 
     * System.out.println(storage1.get()); // test
     * System.out.println(storage2.get()); // null
     * </pre>
     * 
     * @return A new empty instance of the implementing storage type.
     */
    public abstract Storage<T> reproduce();

}
