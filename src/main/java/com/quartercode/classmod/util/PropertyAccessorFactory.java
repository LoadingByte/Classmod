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

package com.quartercode.classmod.util;

import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvokationException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Property;

/**
 * A utility class for creating {@link FunctionExecutor}s which can access simple {@link Property}s (like getters or setters).
 * 
 * @see Property
 * @see FunctionExecutor
 */
public class PropertyAccessorFactory {

    /**
     * Creates a new getter {@link FunctionExecutor} for the given {@link Property} definition.
     * A getter {@link FunctionExecutor} returns the value of a {@link Property}.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <T> FunctionExecutor<T> createGet(final FeatureDefinition<? extends Property<T>> propertyDefinition) {

        return new FunctionExecutor<T>() {

            @Override
            public T invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvokationException {

                return holder.get(propertyDefinition).get();
            }

        };
    }

    /**
     * Creates a new setter {@link FunctionExecutor} for the given {@link Property} definition.
     * A setter function changes the value of a {@link Property}.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <T> FunctionExecutor<Void> createSet(final FeatureDefinition<? extends Property<T>> propertyDefinition) {

        return new FunctionExecutor<Void>() {

            @SuppressWarnings ("unchecked")
            @Override
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvokationException {

                // Set the parent of the old object to null
                if (holder.get(propertyDefinition).get() instanceof ChildFeatureHolder) {
                    // Is always true because of <P extends FeatureHolder> in ChildFeatureHolder
                    ((ChildFeatureHolder<FeatureHolder>) holder.get(propertyDefinition).get()).setParent(null);
                }

                // Hope that the using FunctionDefinition has the correct parameters
                holder.get(propertyDefinition).set((T) arguments[0]);

                // Set the parent of the new object the new holder
                if (arguments[0] instanceof ChildFeatureHolder) {
                    // Is always true because of <P extends FeatureHolder> in ChildFeatureHolder
                    ((ChildFeatureHolder<FeatureHolder>) arguments[0]).setParent(holder);
                }

                return null;
            }

        };
    }

    private PropertyAccessorFactory() {

    }

}
