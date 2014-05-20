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

package com.quartercode.classmod.test.extra.def;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.def.AbstractFunction;
import com.quartercode.classmod.extra.def.AbstractFunctionDefinition;

public class AbstractFunctionTest {

    @Test
    public void testInvoke() {

        FunctionDefinition<Object> definition = new AbstractFunctionDefinition<Object>("testFunction") {

            @Override
            public Function<Object> create(FeatureHolder holder) {

                return new AbstractFunction<>(getName(), holder);
            }

        };
        definition.setParameter(0, String.class);
        definition.setParameter(1, Class.class);
        definition.setParameter(2, Object[].class);

        final List<Object> actualArguments = new ArrayList<>();
        final Object returnValue = "ReturnValue";
        definition.addExecutor("default", FeatureHolder.class, new FunctionExecutor<Object>() {

            @Override
            public Object invoke(FunctionInvocation<Object> invocation, Object... arguments) {

                actualArguments.addAll(Arrays.asList(arguments));
                invocation.next(arguments);
                return returnValue;
            }

        });

        Function<Object> function = new DefaultFeatureHolder().get(definition);

        Object[] arguments = { "Test", String.class, new Object[] { "Test", 12345, true } };
        Object actualReturnValue = function.invoke(arguments);

        assertArrayEquals("Received arguments", arguments, actualArguments.toArray(new Object[actualArguments.size()]));
        assertEquals("Received return value", returnValue, actualReturnValue);
    }

}
