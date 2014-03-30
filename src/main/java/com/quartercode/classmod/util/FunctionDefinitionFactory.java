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

import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.def.AbstractFunction;
import com.quartercode.classmod.extra.def.AbstractFunctionDefinition;

/**
 * A utility class for creating very basic {@link FunctionDefinition}s.
 * 
 * @see FunctionDefinition
 */
public class FunctionDefinitionFactory {

    /**
     * Creates a new {@link FunctionDefinition} with the given parameters.
     * Of course, the parameters can be changed later on using {@link FunctionDefinition#setParameter(int, Class)}.
     * 
     * @param name The name of the new {@link FunctionDefinition}.
     * @param parameters The parameters for the defined function. See {@link FunctionDefinition#setParameter(int, Class)} for further explanation.
     * @return The new {@link FunctionDefinition}.
     */
    public static <R> FunctionDefinition<R> create(String name, Class<?>... parameters) {

        return new AbstractFunctionDefinition<R>(name, parameters) {

            @Override
            public Function<R> create(FeatureHolder holder) {

                return new AbstractFunction<R>(getName(), holder);
            }

        };
    }

    /**
     * Creates a new {@link FunctionDefinition} with the given parameters and adds the given default {@link FunctionExecutor} for the given default variant.
     * Of course, the parameters can be changed later on using {@link FunctionDefinition#setParameter(int, Class)}.
     * 
     * @param name The name of the new {@link FunctionDefinition}.
     * @param defaultVariation The class which defines the {@link FunctionDefinition} constant. The default executor is added here.
     * @param defaultExecutor The default {@link FunctionExecutor} to add to the definition.
     * @param parameters The parameters for the defined function. See {@link FunctionDefinition#setParameter(int, Class)} for further explanation.
     * @return The new {@link FunctionDefinition}.
     */
    public static <R> FunctionDefinition<R> create(String name, Class<? extends FeatureHolder> defaultVariation, FunctionExecutor<R> defaultExecutor, Class<?>... parameters) {

        FunctionDefinition<R> definition = new AbstractFunctionDefinition<R>(name, parameters) {

            @Override
            public Function<R> create(FeatureHolder holder) {

                return new AbstractFunction<R>(getName(), holder);
            }

        };

        definition.addExecutor(defaultVariation, "default", defaultExecutor);

        return definition;
    }

    private FunctionDefinitionFactory() {

    }

}
