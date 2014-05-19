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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Property;

/**
 * The property clone util class is used to clone or pseudo-clone initial values for properties.
 * Especially the property definition factories (like {@link ObjectProperty#createDefinition(String, Object, boolean)}) need to clone initial values,
 * so accessing the value that is stored in the final property doesn't affect the value that is stored in the definition.
 * 
 * @see Property
 */
public class PropertyCloneUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyCloneUtil.class);

    /**
     * Clones or pseudo-clones the given initial property value.
     * There are some rules for the cloning process:
     * 
     * <ul>
     * <li>If the input object is a {@link FeatureHolder}, an empty copy of that holder with no features is returned.</li>
     * <li>If the input object implements {@link Cloneable}, an actual clone of the object is returned.</li>
     * <li>If no rule of the above matches, the input object is returned with no modification or cloning.</li>
     * </ul>
     * 
     * @param initialValue The initial property value that should be cloned.
     * @return The cloned or pseudo-cloned initial value object.
     */
    public static <T> T cloneInitialValue(T initialValue) {

        // Just return an empty pseudo-clone
        if (initialValue instanceof FeatureHolder) {
            try {
                @SuppressWarnings ("unchecked")
                T clone = (T) initialValue.getClass().newInstance();
                return clone;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Cannot create new instance of feature holder '{}' for pseudo clone", initialValue.getClass().getName(), e);
            }
        }
        // Try to really clone the object
        else if (initialValue instanceof Cloneable) {
            try {
                return ObjectUtils.cloneIfPossible(initialValue);
            } catch (CloneFailedException e) {
                LOGGER.error("Unknown exception while cloning object of type '{}'", initialValue.getClass().getName(), e);
            }
        }

        // Use same instance if cloning is not possible (might cause errors)
        return initialValue;
    }

    private PropertyCloneUtil() {

    }

}
