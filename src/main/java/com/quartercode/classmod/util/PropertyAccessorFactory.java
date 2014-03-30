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
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
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
            public T invoke(FunctionInvocation<T> invocation, Object... arguments) throws ExecutorInvocationException {

                invocation.next(arguments);

                return invocation.getHolder().get(propertyDefinition).get();
            }

        };
    }

    /**
     * Creates a new setter {@link FunctionExecutor} for the given {@link Property} definition.
     * A setter function changes the value of a {@link Property}.
     * The new value must be supplied as first argument.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <T> FunctionExecutor<Void> createSet(final FeatureDefinition<? extends Property<T>> propertyDefinition) {

        return new FunctionExecutor<Void>() {

            @SuppressWarnings ("unchecked")
            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                // Hope that the using FunctionDefinition has the correct parameters
                invocation.getHolder().get(propertyDefinition).set((T) arguments[0]);

                return invocation.next(arguments);
            }

        };
    }

    private PropertyAccessorFactory() {

    }

}
