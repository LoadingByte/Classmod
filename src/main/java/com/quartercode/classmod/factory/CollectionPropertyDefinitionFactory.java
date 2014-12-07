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

package com.quartercode.classmod.factory;

import java.util.Collection;
import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.base.Persistable;
import com.quartercode.classmod.extra.prop.CollectionProperty;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.storage.Storage;
import com.quartercode.classmod.extra.valuefactory.ValueFactory;

/**
 * A factory specification interface for creating {@link CollectionPropertyDefinition}s.
 * 
 * @see CollectionPropertyDefinition
 */
public interface CollectionPropertyDefinitionFactory {

    /**
     * Creates a new {@link CollectionPropertyDefinition} for defining a {@link CollectionProperty} with the given name and {@link Storage} implementation.
     * Also sets a template {@link Collection} whose clones are used by collection property instances.
     * 
     * @param name The name of the defined collection property.
     * @param storageTemplate A storage implementation that should be reproduced and used by every created collection property for storing collections.
     * @param collectionFactory A {@link ValueFactory} that returns new collections for all created collection properties.
     */
    public <E, C extends Collection<E>> CollectionPropertyDefinition<E, C> create(String name, Storage<?> storageTemplate, ValueFactory<?> collectionFactory);

    /**
     * Creates a new {@link CollectionPropertyDefinition} for defining a {@link CollectionProperty} with the given name, {@link Storage} implementation, and hiding flag.
     * Also sets a template {@link Collection} whose clones are used by collection property instances.
     * 
     * @param name The name of the defined collection property.
     * @param storageTemplate A storage implementation that should be reproduced and used by every created collection property for storing collections.
     * @param collectionFactory A {@link ValueFactory} that returns new collections for all created collection properties.
     * @param hidden Whether the defined collection property should be excluded from equality checks of its feature holder.
     *        See {@link Hideable} for more information.
     * @param persistent Whether the defined collection property should be serializable.
     *        See {@link Persistable} for more information.
     */
    public <E, C extends Collection<E>> CollectionPropertyDefinition<E, C> create(String name, Storage<?> storageTemplate, ValueFactory<?> collectionFactory, boolean hidden, boolean persistent);

}
