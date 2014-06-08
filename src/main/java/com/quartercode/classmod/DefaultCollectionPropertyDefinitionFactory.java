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

import java.util.Collection;
import org.apache.commons.lang3.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.Storage;
import com.quartercode.classmod.extra.ValueFactory;
import com.quartercode.classmod.extra.def.AbstractCollectionPropertyDefinition;
import com.quartercode.classmod.extra.def.DefaultCollectionProperty;
import com.quartercode.classmod.factory.Factory;

/**
 * A factory for {@link AbstractCollectionPropertyDefinition}s that can create {@link DefaultCollectionProperty} objects.
 */
class DefaultCollectionPropertyDefinitionFactory {

    @Factory (parameters = { "name", "storage", "collection", "ignoreEquals" })
    public <E, C extends Collection<E>> CollectionPropertyDefinition<E, C> create(String name, Storage<C> storageTemplate, ValueFactory<C> collectionFactory, boolean ignoreEquals) {

        Validate.notNull(name, "Name of new collection property definition cannot be null");
        Validate.notNull(storageTemplate, "Storage template of new collection property definition cannot be null");
        Validate.notNull(collectionFactory, "Collection factory of new collection property definition cannot be null");

        return new AbstractCollectionPropertyDefinition<E, C>(name, storageTemplate, collectionFactory, ignoreEquals) {

            @Override
            public CollectionProperty<E, C> create(FeatureHolder holder) {

                return new DefaultCollectionProperty<>(getName(), holder, newStorage());
            }

        };
    }

}
