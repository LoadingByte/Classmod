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

package com.quartercode.classmod.factory;

import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionDefinition;

/**
 * A factory specification interface for creating {@link FunctionDefinition}s.
 * 
 * @see FunctionDefinition
 */
public interface FunctionDefinitionFactory {

    /**
     * Creates a new {@link FunctionDefinition} for defining a {@link Function} with the given name and parameters.
     * Of course, the parameters can be changed later on using {@link FunctionDefinition#setParameter(int, Class)}.
     * Note that the parameter array is <b>not a vararg</b> in order to ensure that users do not forget to specifiy the parameters.
     * An empty parameter list needs to be represented with an empty array.
     * 
     * @param name The name of the defined function.
     * @param parameters The parameters for the defined function. See {@link FunctionDefinition#setParameter(int, Class)} for further explanation.
     *        Note that this array is <b>not a vararg</b> in order to ensure that users do not forget to specifiy the parameters.
     *        An empty parameter list needs to be represented with an empty array.
     */
    public <R> FunctionDefinition<R> create(String name, Class<?>[] parameters);

}
