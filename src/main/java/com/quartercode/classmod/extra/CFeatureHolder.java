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
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * A convenient feature holder is a {@link FeatureHolder} that implements some common methods for directly accessing features
 * without using {@link #get(FeatureDefinition)}.
 * For example, {@link #invoke(FeatureDefinition, Object...)} directly invokes a {@link Function}.
 * 
 * @see #invoke(FeatureDefinition, Object...)
 * @see #getObj(FeatureDefinition)
 * @see #setObj(FeatureDefinition, Object)
 * @see #getCol(FeatureDefinition)
 * @see #addCol(FeatureDefinition, Object)
 * @see #removeCol(FeatureDefinition, Object)
 */
public interface CFeatureHolder extends FeatureHolder {

    /**
     * Invokes the {@link Function}, which is defined by the given function definition, with the given arguments.
     * This method just retrieves the function feature using {@link #get(FeatureDefinition)} and then calls {@link Function#invoke(Object...)} on the resulting object.
     * It should create a new function feature from the definition if the requested one doesn't exist.
     * See {@link Function#invoke(Object...)} for more information on the invocation process.
     * 
     * @param functionDefinition The function definition that defines the function for invocation.
     * @param arguments Some arguments for the function.
     * @return The value the function returns. Can be {@code null}.
     * @throws IllegalArgumentException The supplied arguments are not valid and do not match the required parameters of the function.
     * @throws RuntimeException A function executor throws a custom function-related exception.
     * @see Function#invoke(Object...)
     */
    public <R> R invoke(FeatureDefinition<? extends Function<R>> functionDefinition, Object... arguments);

    /**
     * Returns the object which is stored inside the {@link ValueSupplier} defined by the given value supplier definition.
     * This method just retrieves the value supplier feature using {@link #get(FeatureDefinition)} and then calls {@link ValueSupplier#get()} on the resulting object.
     * It should create a new value supplier feature from the definition if the requested one doesn't exist.
     * See {@link ValueSupplier#get()} for more information on the getting process.
     * 
     * @param valueSupplierDefinition The value supplier definition that defines the value supplier whose value should be returned.
     * @return The object stored inside the defined value supplier.
     * @throws RuntimeException If a {@link Property} is used:
     *         A getter function executor throws a custom getter-related exception.
     * @see ValueSupplier#get()
     */
    public <T> T getObj(FeatureDefinition<? extends ValueSupplier<T>> valueSupplierDefinition);

    /**
     * Changes the object which is stored inside the {@link Property} defined by the given property definition.
     * This method just retrieves the property feature using {@link #get(FeatureDefinition)} and then calls {@link Property#set(Object)} on the resulting object.
     * It should create a new property feature from the definition if the requested one doesn't exist.
     * See {@link Property#set(Object)} for more information on the setting process.
     * 
     * @param propertyDefinition The property definition that defines the property whose value should be changed.
     * @param value The new object that should be stored inside the defined property.
     * @throws RuntimeException A setter function executor throws a custom setter-related exception.
     * @see Property#set(Object)
     */
    public <T> void setObj(FeatureDefinition<? extends Property<T>> propertyDefinition, T value);

    /**
     * Returns the collection which is stored inside the {@link CollectionProperty} defined by the given collection property definition.
     * This method just retrieves the collection property feature using {@link #get(FeatureDefinition)} and then calls {@link CollectionProperty#get()} on the resulting object.
     * It should create a new collection property feature from the definition if the requested one doesn't exist.
     * See {@link CollectionProperty#get()} for more information on the getting process.
     * 
     * @param collectionPropertyDefinition The collection property definition that defines the collection property whose collection should be returned.
     * @return The collection stored inside the defined collection property.
     * @throws RuntimeException A getter function executor throws a custom getter-related exception.
     * @see CollectionProperty#get()
     */
    public <E, C extends Collection<E>> C getCol(FeatureDefinition<? extends CollectionProperty<E, C>> collectionPropertyDefinition);

    /**
     * Adds the given object to the collection which is stored inside the {@link CollectionProperty} defined by the given collection property definition.
     * This method just retrieves the collection property feature using {@link #get(FeatureDefinition)} and then calls {@link CollectionProperty#add(Object)} on the resulting object.
     * It should create a new collection property feature from the definition if the requested one doesn't exist.
     * See {@link CollectionProperty#add(Object)} for more information on the adding process.
     * 
     * @param collectionPropertyDefinition The collection property definition that defines the collection property the element should be added to.
     * @param element The object that should be added to the defined collection property.
     * @throws RuntimeException An adder function executor throws a custom adder-related exception.
     * @see CollectionProperty#add(Object)
     */
    public <E, C extends Collection<E>> void addCol(FeatureDefinition<? extends CollectionProperty<E, C>> collectionPropertyDefinition, E element);

    /**
     * Removes the given object from the collection which is stored inside the {@link CollectionProperty} defined by the given collection property definition.
     * This method just retrieves the collection property feature using {@link #get(FeatureDefinition)} and then calls {@link CollectionProperty#remove(Object)} on the resulting object.
     * It should create a new collection property feature from the definition if the requested one doesn't exist.
     * See {@link CollectionProperty#remove(Object)} for more information on the removal process.
     * 
     * @param collectionPropertyDefinition The collection property definition that defines the collection property the element should be removed from.
     * @param element The object that should be removed from the defined collection property.
     * @throws RuntimeException A remover function executor throws a custom remover-related exception.
     * @see CollectionProperty#remove(Object)
     */
    public <E, C extends Collection<E>> void removeCol(FeatureDefinition<? extends CollectionProperty<E, C>> collectionPropertyDefinition, E element);

}
