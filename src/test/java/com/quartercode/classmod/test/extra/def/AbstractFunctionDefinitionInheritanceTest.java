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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
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

        // Multiple executors
        data.add(new Object[] { new Object[][] { { A.class, true }, { AB.class, false }, { AC.class, false }, { ACD.class, false } }, A.class, true });
        data.add(new Object[] { new Object[][] { { A.class, true }, { AB.class, true }, { AC.class, false }, { ACD.class, false } }, AB.class, true });
        data.add(new Object[] { new Object[][] { { A.class, true }, { AB.class, false }, { AC.class, true }, { ACD.class, false } }, AC.class, true });
        data.add(new Object[] { new Object[][] { { A.class, true }, { AB.class, false }, { AC.class, true }, { ACD.class, true } }, ACD.class, true });

        // Overriding executors
        data.add(new Object[] { new Object[][] { { A.class, true }, { AB.class, false }, { AC.class, false }, { ACD.class, false } }, A.class, false });
        data.add(new Object[] { new Object[][] { { A.class, false }, { AB.class, true }, { AC.class, false }, { ACD.class, false } }, AB.class, false });
        data.add(new Object[] { new Object[][] { { A.class, false }, { AB.class, false }, { AC.class, true }, { ACD.class, false } }, AC.class, false });
        data.add(new Object[] { new Object[][] { { A.class, false }, { AB.class, false }, { AC.class, false }, { ACD.class, true } }, ACD.class, false });

        return data;
    }

    private final Object[][]                     executors;
    private final Class<? extends FeatureHolder> variant;
    private final boolean                        differentExecutorNames;

    private AbstractFunctionDefinition<Void>     functionDefinition;

    public AbstractFunctionDefinitionInheritanceTest(Object[][] executors, Class<? extends FeatureHolder> variant, boolean differentExecutorNames) {

        this.executors = executors;
        this.variant = variant;
        this.differentExecutorNames = differentExecutorNames;
    }

    @Before
    public void setUp() {

        functionDefinition = new AbstractFunctionDefinition<Void>("testFunctionDefinition") {

            @Override
            public Function<Void> create(FeatureHolder holder) {

                return new AbstractFunction<Void>(getName(), holder);
            }

        };
    }

    private FunctionExecutor<Void> createTestExecutor(final boolean[] invocationArray, final int index) {

        return new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                invocationArray[index] = true;
                return invocation.next(arguments);
            }

        };
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void testCreateFeatureHolder() throws InstantiationException, IllegalAccessException {

        boolean[] expectedInvocations = new boolean[executors.length];
        boolean[] actualInvocations = new boolean[executors.length];

        int index = 0;
        for (Object[] entry : executors) {
            expectedInvocations[index] = (Boolean) entry[1];

            Class<? extends FeatureHolder> variant = (Class<? extends FeatureHolder>) entry[0];
            String executorName = "testExecutor" + (differentExecutorNames ? index : "");
            functionDefinition.addExecutor(executorName, variant, createTestExecutor(actualInvocations, index));

            index++;
        }

        Function<Void> function = variant.newInstance().get(functionDefinition);
        function.invoke();

        Assert.assertTrue("Invocation pattern doesn't equal", Arrays.equals(expectedInvocations, actualInvocations));
    }

    private static class A extends DefaultFeatureHolder {

        public A() {

        }

    }

    private static class AB extends A {

        @SuppressWarnings ("unused")
        public AB() {

        }

    }

    private static class AC extends A {

        public AC() {

        }

    }

    private static class ACD extends AC {

        @SuppressWarnings ("unused")
        public ACD() {

        }

    }

}
