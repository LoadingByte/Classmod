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

package com.quartercode.classmod.test.def.extra.func;

import static org.junit.Assert.*;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.base.DefaultFeatureHolder;
import com.quartercode.classmod.def.extra.func.AbstractFunctionDefinition;
import com.quartercode.classmod.def.extra.func.DefaultFunction;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionDefinition;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionInvocation;

public class DefaultFunctionPriorityTest {

    @Test
    public void testInvoke() {

        FunctionDefinition<Integer> definition = new AbstractFunctionDefinition<Integer>("testFunction") {

            @Override
            public Function<Integer> create(FeatureHolder holder) {

                return new DefaultFunction<>(getName(), holder);
            }

        };

        final MutableBoolean invokedFunctionExecutor1 = new MutableBoolean();
        definition.addExecutor("1", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor1.setTrue();
                return invocation.next(arguments);
            }

        }, 4);

        final MutableBoolean invokedFunctionExecutor2 = new MutableBoolean();
        definition.addExecutor("2", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor2.setTrue();
                invocation.next(arguments); // Execute next, but don't return next value
                return 2;
            }

        }, 3);

        final MutableBoolean invokedFunctionExecutor3 = new MutableBoolean();
        definition.addExecutor("3", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor3.setTrue();
                return 3; // Do not even execute next
            }

        }, 2);

        final MutableBoolean invokedFunctionExecutor4 = new MutableBoolean();
        definition.addExecutor("4", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor4.setTrue();
                invocation.next(arguments);
                return 4;
            }

        }, 1);

        Function<Integer> function = new DefaultFeatureHolder().get(definition);

        int result = function.invoke();

        assertTrue("Executor 1 wasn't invoked", invokedFunctionExecutor1.getValue());
        assertTrue("Executor 2 wasn't invoked", invokedFunctionExecutor2.getValue());
        assertTrue("Executor 3 wasn't invoked", invokedFunctionExecutor3.getValue());
        assertFalse("Executor 4 was invoked", invokedFunctionExecutor4.getValue());
        assertEquals("Return value", 2, result);
    }

}