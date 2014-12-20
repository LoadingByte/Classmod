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

package com.quartercode.classmod.factory.def;

import org.apache.commons.lang3.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.extra.prop.AbstractPropertyDefinition;
import com.quartercode.classmod.def.extra.prop.DefaultProperty;
import com.quartercode.classmod.extra.prop.Property;
import com.quartercode.classmod.extra.prop.PropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;
import com.quartercode.classmod.extra.valuefactory.ValueFactory;
import com.quartercode.classmod.factory.PropertyDefinitionFactory;

/**
 * The default factory implementation provider for the {@link PropertyDefinitionFactory}.
 * 
 * @see PropertyDefinitionFactory
 */
public class DefaultPropertyDefinitionFactory implements PropertyDefinitionFactory {

    @Override
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate) {

        return create(name, storageTemplate, null, PropertyDefinition.HIDDEN_DEFAULT, PropertyDefinition.PERSISTENT_DEFAULT);
    }

    @Override
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate, ValueFactory<?> initialValueFactory) {

        return create(name, storageTemplate, initialValueFactory, PropertyDefinition.HIDDEN_DEFAULT, PropertyDefinition.PERSISTENT_DEFAULT);
    }

    @Override
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate, boolean hidden, boolean persistent) {

        return create(name, storageTemplate, null, hidden, persistent);
    }

    @Override
    @SuppressWarnings ("unchecked")
    public <T> PropertyDefinition<T> create(String name, Storage<?> storageTemplate, ValueFactory<?> initialValueFactory, boolean hidden, boolean persistent) {

        Validate.notNull(name, "Name of new property definition cannot be null");
        Validate.notNull(storageTemplate, "Storage template of new property definition cannot be null");

        return new AbstractPropertyDefinition<T>(name, (Storage<T>) storageTemplate, (ValueFactory<T>) initialValueFactory, hidden, persistent) {

            @Override
            public Property<T> create(FeatureHolder holder) {

                return new DefaultProperty<>(getName(), holder, newStorage(), newInitialValue());
            }

        };
    }

}