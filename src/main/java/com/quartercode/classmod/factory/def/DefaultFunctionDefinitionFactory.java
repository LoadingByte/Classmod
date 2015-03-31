/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
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

import org.apache.commons.lang3.Validate;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.extra.func.AbstractFunctionDefinition;
import com.quartercode.classmod.def.extra.func.DefaultFunction;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionDefinition;
import com.quartercode.classmod.factory.FunctionDefinitionFactory;

/**
 * The default factory implementation provider for the {@link FunctionDefinitionFactory}.
 * 
 * @see FunctionDefinitionFactory
 */
public class DefaultFunctionDefinitionFactory implements FunctionDefinitionFactory {

    @Override
    public <R> FunctionDefinition<R> create(String name, Class<?>[] parameters) {

        Validate.notNull(name, "Name of new function definition cannot be null");
        Validate.notNull(parameters, "Parameter array of new function definition can be empty but not null");

        return new AbstractFunctionDefinition<R>(name, parameters) {

            @Override
            public Function<R> create(FeatureHolder holder) {

                return new DefaultFunction<>(getName(), holder);
            }

        };
    }

}
