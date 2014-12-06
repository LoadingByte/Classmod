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

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.def.base.DefaultFeatureHolder;
import com.quartercode.classmod.def.extra.func.AbstractFunctionDefinition;
import com.quartercode.classmod.def.extra.func.DefaultFunction;
import com.quartercode.classmod.def.extra.func.DefaultFunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.Function;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionExecutorWrapper;
import com.quartercode.classmod.extra.func.FunctionInvocation;
import com.quartercode.classmod.extra.func.Priorities;

public class AbstractFunctionDefinitionTest {

    private AbstractFunctionDefinition<Void> functionDefinition;

    @Before
    public void setUp() {

        functionDefinition = new AbstractFunctionDefinition<Void>("testFunctionDefinition") {

            @Override
            public Function<Void> create(FeatureHolder holder) {

                return new DefaultFunction<>(getName(), holder);
            }

        };
    }

    @Test
    public void testSetParameter() {

        functionDefinition.setParameter(0, String.class);
        functionDefinition.setParameter(5, Object.class);
        functionDefinition.setParameter(3, Integer.class);
        functionDefinition.setParameter(5, null);

        assertEquals("Parameter pattern", Arrays.asList(String.class, null, null, Integer.class), functionDefinition.getParameters());
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

        List<Class<?>> expectedParameters = new ArrayList<>();
        expectedParameters.add(String.class);
        List<FunctionExecutorWrapper<Void>> expectedExecutors = new ArrayList<>();
        expectedExecutors.add(new DefaultFunctionExecutorWrapper<>(executor, Priorities.DEFAULT));
        List<FunctionExecutorWrapper<Void>> actualExecutors = new ArrayList<>(function.getExecutors());
        assertEquals("Function object's parameters", expectedParameters, function.getParameters());
        assertEquals("Function object's executors", expectedExecutors, actualExecutors);
    }

}