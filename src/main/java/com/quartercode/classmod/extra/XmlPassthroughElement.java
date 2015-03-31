/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
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

/**
 * Classes that implement this interface represent a "passthrough" between two essential elements in the xml tree.
 * For example, the {@code Storage} class implements it because it just represents a "passthrough" between a feature and a stored object.<br>
 * <br>
 * Internally, "passthrough" elements are used to retrieve the parent feature of some object.
 * Each "passthrough" element must implement the {@code beforeUnmarshal()} listener and store its parent object.
 * Afterwards, a loop can just climb the tree upwards through all "passthrough" elements until it reaches a non-"passthrough" element.
 */
public interface XmlPassthroughElement {

    /**
     * Returns the parent xml element of this element in the xml tree.
     * It can be retrieved by implementing the {@code beforeUnmarshal()} listener and storing the provided parent object.
     * 
     * @return The parent xml element of this element.
     */
    public Object getXmlParent();

}
