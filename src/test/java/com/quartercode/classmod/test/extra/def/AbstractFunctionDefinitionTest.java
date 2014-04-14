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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionExecutorContext;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.def.AbstractFunction;
import com.quartercode.classmod.extra.def.AbstractFunctionDefinition;

public class AbstractFunctionDefinitionTest {

    private AbstractFunctionDefinition<Void> functionDefinition;

    @Before
    public void setUp() {

        functionDefinition = new AbstractFunctionDefinition<Void>("testFunctionDefinition") {

            @Override
            public Function<Void> create(FeatureHolder holder) {

                return new AbstractFunction<Void>(getName(), holder);
            }

        };
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testSetParameter() {

        functionDefinition.setParameter(0, String.class);
        functionDefinition.setParameter(5, Object.class);
        functionDefinition.setParameter(3, Integer.class);
        functionDefinition.setParameter(5, null);

        Assert.assertEquals("Parameter pattern", Arrays.asList(String.class, null, null, Integer.class), functionDefinition.getParameters());
    }

    @Test
    public void testCreateFeatureHolder() {

        FunctionExecutor<Void> executor = new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return invocation.next(arguments);
            }

        };
        functionDefinition.addExecutor("default", FeatureHolder.class, executor);
        functionDefinition.setParameter(0, String.class);
        Function<Void> function = new DefaultFeatureHolder().get(functionDefinition);

        List<Class<?>> expectedParameters = new ArrayList<Class<?>>();
        expectedParameters.add(String.class);
        List<FunctionExecutor<Void>> expectedExecutors = new ArrayList<FunctionExecutor<Void>>();
        expectedExecutors.add(executor);
        List<FunctionExecutor<Void>> actualExecutors = new ArrayList<FunctionExecutor<Void>>();
        for (FunctionExecutorContext<Void> context : function.getExecutors()) {
            actualExecutors.add(context.getExecutor());
        }
        Assert.assertEquals("Function object's parameters", expectedParameters, function.getParameters());
        Assert.assertEquals("Function object's executors", expectedExecutors, actualExecutors);
    }

}
