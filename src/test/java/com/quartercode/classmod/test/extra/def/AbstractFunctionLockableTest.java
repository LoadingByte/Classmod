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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.def.AbstractFunction;
import com.quartercode.classmod.extra.def.AbstractFunctionDefinition;

public class AbstractFunctionLockableTest {

    private FunctionDefinition<Void> definition;

    @Before
    public void setUp() {

        definition = new AbstractFunctionDefinition<Void>("testFunction") {

            @Override
            public Function<Void> create(FeatureHolder holder) {

                return new AbstractFunction<Void>(getName(), holder);
            }

        };
    }

    @Test
    public void testInvoke() throws ExecutorInvocationException {

        final boolean[] invocations = new boolean[2];
        definition.addExecutor("1", FeatureHolder.class, new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                invocations[0] = true;
                return invocation.next(arguments);
            }

        });
        definition.addExecutor("2", FeatureHolder.class, new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                invocations[1] = true;
                return invocation.next(arguments);
            }

        });

        Function<Void> function = new DefaultFeatureHolder().get(definition);
        function.getExecutor("1").setLocked(true);
        function.invoke();

        Assert.assertTrue("Locked executor was invoked", !invocations[0]);
        Assert.assertTrue("Not locked executor wasn't invoked", invocations[1]);
    }

}
