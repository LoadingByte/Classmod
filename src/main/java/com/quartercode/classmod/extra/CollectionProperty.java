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

import java.util.Collection;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.Initializable;

/**
 * A collection property is a simple {@link Feature} which stores a {@link Collection} object.
 * 
 * @param <E> The type of object which can be stored inside the {@link Collection} the collection property holds.
 * @param <C> The type of {@link Collection} the collection property stores.
 */
public interface CollectionProperty<E, C extends Collection<E>> extends Feature, Initializable<CollectionPropertyDefinition<E, C>> {

    /**
     * Returns the {@link Collection} which is stored inside the property.
     * Note that all getter function executors are invoked when this is called.
     * 
     * @return The stored {@link Collection}.
     * @throws ExecutorInvocationException Something goes wrong while invoking a getter function executor.
     */
    public C get() throws ExecutorInvocationException;

    /**
     * Adds an element to the {@link Collection} which is stored inside the property.
     * Note that all adder function executors are invoked when this is called.
     * 
     * @param element The object to add to the stored {@link Collection}.
     * @throws ExecutorInvocationException Something goes wrong while invoking an adder function executor.
     */
    public void add(E element) throws ExecutorInvocationException;

    /**
     * Removes an element from the {@link Collection} which is stored inside the property.
     * Note that all remover function executors are invoked when this is called.
     * 
     * @param element The object to remove from the stored {@link Collection}.
     * @throws ExecutorInvocationException Something goes wrong while invoking a remover function executor.
     */
    public void remove(E element) throws ExecutorInvocationException;

}
