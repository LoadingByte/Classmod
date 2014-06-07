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

package com.quartercode.classmod;

import org.apache.commons.lang3.reflect.Typed;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.factory.FactoryManager;

/**
 * The classmod factory class stores a {@link FactoryManager} and makes it publicly accessible.
 * It also provides several default factories for the default abstract classes, like {@link PropertyDefinition}.
 * 
 * @see FactoryManager
 */
public class ClassmodFactory {

    private static FactoryManager factoryManager = new FactoryManager();

    static {

        factoryManager.setFactory(PropertyDefinition.class, new DefaultPropertyDefinitionFactory());
        factoryManager.setFactory(CollectionPropertyDefinition.class, new DefaultCollectionPropertyDefinitionFactory());

    }

    /**
     * Returns the {@link FactoryManager} manager which is internally used by the static class.
     * New factory mappings should be added here.
     * 
     * @return The internal factory manager.
     */
    public static FactoryManager getFactoryManager() {

        return factoryManager;
    }

    /**
     * Creates a new object of the given abstract type with the given parameters.
     * This method uses the internal {@link FactoryManager} ({@link #getFactoryManager()}).
     * The supplied parameters are mapped to the parameters of the factory method of the responsible factory.
     * They must be provided in a key-value-scheme:
     * 
     * <pre>
     * create(..., "param1", value1, "param2", value2, ...)
     * </pre>
     * 
     * @param type The type of the object that should be created.
     * @param parameters The parameters which should be supplied to the responsible factory method.
     * @return The newly created object.
     * @throws IllegalArgumentException There is no factory mapped for the given type or the provided parameters are invalid.
     * @throws RuntimeException An unknown error occurs while invoking the selected factory.
     *         That might be caused by some unknown reflection problems or a programming error.
     * @see FactoryManager#create(Typed, Object...)
     */
    public static <T> T create(Typed<T> type, Object... parameters) {

        return factoryManager.create(type, parameters);
    }

    private ClassmodFactory() {

    }

}
