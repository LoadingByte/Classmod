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

import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;

/**
 * A {@link FunctionDefinition} that defines a {@link DefaultFunction}.
 * It is internally used by this package.
 * 
 * @param <R> The return type of the defined {@link Function}.
 */
class InternalDefaultFunctionDefinition<R> extends AbstractFunctionDefinition<R> {

    /**
     * Creates a new internal default function definition for defining a {@link Function} with the given name and parameters.
     * 
     * @param name The name of the defined function.
     * @param parameters The parameters for the defined function.
     */
    public InternalDefaultFunctionDefinition(String name, Class<?>... parameters) {

        super(name, parameters);
    }

    @Override
    public Function<R> create(FeatureHolder holder) {

        return new DefaultFunction<>(getName(), holder);
    }

}
