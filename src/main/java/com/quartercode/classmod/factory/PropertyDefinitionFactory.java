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

package com.quartercode.classmod.factory;

import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.base.Persistable;
import com.quartercode.classmod.extra.prop.Property;
import com.quartercode.classmod.extra.prop.PropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;
import com.quartercode.classmod.extra.valuefactory.ValueFactory;

/**
 * A factory specification interface for creating {@link PropertyDefinition}s.
 * 
 * @see PropertyDefinition
 */
public interface PropertyDefinitionFactory {

    /**
     * Creates a new {@link PropertyDefinition} for defining a <b>persistent and unhidden</b> {@link Property} with the given name (<b>without an initial value</b>).
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     */
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate);

    /**
     * Creates a new {@link PropertyDefinition} for defining a <b>persistent and unhidden</b> {@link Property} with the given name and <b>initial value</b>.
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     * @param initialValueFactory A {@link ValueFactory} that returns initial value objects for all created properties.
     */
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate, ValueFactory<?> initialValueFactory);

    /**
     * Creates a new {@link PropertyDefinition} for defining a {@link Property} with the given name, <b>hiding flag, and persistence flag</b> (<b>without an initial value</b>).
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     * @param hidden Whether the defined property should be excluded from equality checks of its feature holder.
     *        See {@link Hideable} for more information.
     * @param persistent Whether the defined property should be serializable.
     *        See {@link Persistable} for more information.
     */
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate, boolean hidden, boolean persistent);

    /**
     * Creates a new {@link PropertyDefinition} for defining a {@link Property} with the given name, <b>initial value, hiding flag, and persistence flag</b>.
     * 
     * @param name The name of the defined property.
     * @param storageTemplate A {@link Storage} implementation that should be reproduced and used by every created property for storing values.
     * @param initialValueFactory A {@link ValueFactory} that returns initial value objects for all created properties.
     * @param hidden Whether the defined property should be excluded from equality checks of its feature holder.
     *        See {@link Hideable} for more information.
     * @param persistent Whether the defined property should be serializable.
     *        See {@link Persistable} for more information.
     */
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate, ValueFactory<?> initialValueFactory, boolean hidden, boolean persistent);

}
