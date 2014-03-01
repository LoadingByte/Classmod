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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.def.AbstractFunction;
import com.quartercode.classmod.extra.def.AbstractFunctionDefinition;

@RunWith (Parameterized.class)
public class AbstractFunctionDefinitionInheritanceTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[] { new Object[][] { { Parent.class, true }, { Child.class, false } }, Parent.class });
        data.add(new Object[] { new Object[][] { { Parent.class, true }, { Child.class, true } }, Child.class });

        return data;
    }

    private final Object[][]                     executors;
    private final Class<? extends FeatureHolder> variant;

    private AbstractFunctionDefinition<Void>     functionDefinition;

    public AbstractFunctionDefinitionInheritanceTest(Object[][] executors, Class<? extends FeatureHolder> variant) {

        this.executors = executors;
        this.variant = variant;
    }

    @Before
    public void setUp() {

        functionDefinition = new AbstractFunctionDefinition<Void>("testFunctionDefinition") {

            @Override
            protected Function<Void> create(FeatureHolder holder, List<Class<?>> parameters, Map<String, FunctionExecutor<Void>> executors) {

                return new AbstractFunction<Void>(getName(), holder, parameters, executors);
            }

        };
    }

    private FunctionExecutor<Void> createTestExecutor(final boolean[] invocationArray, final int index) {

        return new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                invocationArray[index] = true;
                return invocation.next(arguments);
            }

        };
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testCreateFeatureHolder() throws InstantiationException, IllegalAccessException, ExecutorInvocationException {

        boolean[] expectedInvocations = new boolean[executors.length];
        boolean[] actualInvocations = new boolean[executors.length];

        int index = 0;
        for (Object[] entry : executors) {
            expectedInvocations[index] = (Boolean) entry[1];

            Class<? extends FeatureHolder> variant = (Class<? extends FeatureHolder>) entry[0];
            functionDefinition.addExecutor(variant, "executor" + index, createTestExecutor(actualInvocations, index));

            index++;
        }

        Function<Void> function = functionDefinition.create(variant.newInstance());
        function.invoke();

        Assert.assertTrue("Invocation pattern doesn't equal", Arrays.equals(expectedInvocations, actualInvocations));
    }

    private static class Parent extends DefaultFeatureHolder {

        public Parent() {

        }

    }

    private static class Child extends Parent {

        @SuppressWarnings ("unused")
        public Child() {

        }

    }

}
