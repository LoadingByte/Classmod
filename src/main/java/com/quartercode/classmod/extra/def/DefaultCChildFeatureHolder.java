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

import java.util.Collection;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CChildFeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.ValueSupplier;

/**
 * The default implementation of the {@link CChildFeatureHolder} interface.
 * It uses the basic logic implemented by {@link DefaultChildFeatureHolder}.
 * See that second reference for more information on the usage of this class.
 * 
 * @param <P> The type the parent {@link FeatureHolder} has to have.
 *        See {@link DefaultChildFeatureHolder} and {@link ChildFeatureHolder} for more information on this parameter.
 * @see CChildFeatureHolder
 * @see DefaultChildFeatureHolder
 */
public class DefaultCChildFeatureHolder<P extends FeatureHolder> extends DefaultChildFeatureHolder<P> implements CChildFeatureHolder<P> {

    @Override
    public <R> R invoke(FeatureDefinition<? extends Function<R>> functionDefinition, Object... arguments) {

        return get(functionDefinition).invoke(arguments);
    }

    @Override
    public <T> T getObj(FeatureDefinition<? extends ValueSupplier<T>> valueSupplierDefinition) {

        return get(valueSupplierDefinition).get();
    }

    @Override
    public <T> void setObj(FeatureDefinition<? extends Property<T>> propertyDefinition, T value) {

        get(propertyDefinition).set(value);
    }

    @Override
    public <E, C extends Collection<E>> C getColl(FeatureDefinition<? extends CollectionProperty<E, C>> collectionPropertyDefinition) {

        return get(collectionPropertyDefinition).get();
    }

    @Override
    public <E, C extends Collection<E>> void addToColl(FeatureDefinition<? extends CollectionProperty<E, C>> collectionPropertyDefinition, E element) {

        get(collectionPropertyDefinition).add(element);
    }

    @Override
    public <E, C extends Collection<E>> void removeFromColl(FeatureDefinition<? extends CollectionProperty<E, C>> collectionPropertyDefinition, E element) {

        get(collectionPropertyDefinition).remove(element);
    }

}
