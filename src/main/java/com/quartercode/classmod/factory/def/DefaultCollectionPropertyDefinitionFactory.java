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

import java.util.Collection;
import org.apache.commons.lang3.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.extra.prop.AbstractCollectionPropertyDefinition;
import com.quartercode.classmod.def.extra.prop.DefaultCollectionProperty;
import com.quartercode.classmod.extra.prop.CollectionProperty;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;
import com.quartercode.classmod.extra.valuefactory.ValueFactory;
import com.quartercode.classmod.factory.CollectionPropertyDefinitionFactory;

/**
 * The default factory implementation provider for the {@link CollectionPropertyDefinitionFactory}.
 * 
 * @see CollectionPropertyDefinitionFactory
 */
public class DefaultCollectionPropertyDefinitionFactory implements CollectionPropertyDefinitionFactory {

    @Override
    public <E, C extends Collection<E>> CollectionPropertyDefinition<E, C> create(String name, Storage<?> storageTemplate, ValueFactory<?> collectionFactory) {

        return create(name, storageTemplate, collectionFactory, CollectionPropertyDefinition.HIDDEN_DEFAULT, CollectionPropertyDefinition.PERSISTENT_DEFAULT);
    }

    @Override
    @SuppressWarnings ("unchecked")
    public <E, C extends Collection<E>> CollectionPropertyDefinition<E, C> create(String name, Storage<?> storageTemplate, ValueFactory<?> collectionFactory, boolean hidden, boolean persistent) {

        Validate.notNull(name, "Name of new collection property definition cannot be null");
        Validate.notNull(storageTemplate, "Storage template of new collection property definition cannot be null");
        Validate.notNull(collectionFactory, "Collection factory of new collection property definition cannot be null");

        return new AbstractCollectionPropertyDefinition<E, C>(name, (Storage<C>) storageTemplate, (ValueFactory<C>) collectionFactory, hidden, persistent) {

            @Override
            public CollectionProperty<E, C> create(FeatureHolder holder) {

                return new DefaultCollectionProperty<>(getName(), holder, newStorage());
            }

        };
    }

}
