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

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Prioritized;
import com.quartercode.classmod.extra.def.AbstractFunction;
import com.quartercode.classmod.extra.def.AbstractFunctionDefinition;

public class AbstractFunctionPriorityTest {

    @Test
    public void testInvoke() {

        FunctionDefinition<Integer> definition = new AbstractFunctionDefinition<Integer>("testFunction") {

            @Override
            public Function<Integer> create(FeatureHolder holder) {

                return new AbstractFunction<Integer>(getName(), holder);
            }

        };

        final AtomicBoolean invokedFunctionExecutor1 = new AtomicBoolean();
        definition.addExecutor("1", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            @Prioritized (4)
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor1.set(true);
                return invocation.next(arguments);
            }

        });

        final AtomicBoolean invokedFunctionExecutor2 = new AtomicBoolean();
        definition.addExecutor("2", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            @Prioritized (3)
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor2.set(true);
                invocation.next(arguments); // Execute next, but don't return next value
                return 2;
            }

        });

        final AtomicBoolean invokedFunctionExecutor3 = new AtomicBoolean();
        definition.addExecutor("3", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            @Prioritized (2)
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor3.set(true);
                return 3; // Do not even execute next
            }

        });

        final AtomicBoolean invokedFunctionExecutor4 = new AtomicBoolean();
        definition.addExecutor("4", FeatureHolder.class, new FunctionExecutor<Integer>() {

            @Override
            @Prioritized (1)
            public Integer invoke(FunctionInvocation<Integer> invocation, Object... arguments) {

                invokedFunctionExecutor4.set(true);
                invocation.next(arguments);
                return 4;
            }

        });

        Function<Integer> function = new DefaultFeatureHolder().get(definition);

        int result = function.invoke();

        Assert.assertTrue("Executor 1 wasn't invoked", invokedFunctionExecutor1.get());
        Assert.assertTrue("Executor 2 wasn't invoked", invokedFunctionExecutor2.get());
        Assert.assertTrue("Executor 3 wasn't invoked", invokedFunctionExecutor3.get());
        Assert.assertFalse("Executor 4 was invoked", invokedFunctionExecutor4.get());
        Assert.assertEquals("Return value", 2, result);
    }

}
