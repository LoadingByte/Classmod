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

package com.quartercode.classmod.test.util.test;

import static com.quartercode.classmod.ClassmodFactory.create;
import static com.quartercode.classmod.extra.Priorities.LEVEL_7;
import static org.junit.Assert.assertEquals;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.util.test.DefaultModMockery;

@SuppressWarnings ("unchecked")
public class DefaultModMockeryFunctionTest {

    @Rule
    public JUnitRuleMockery         context    = new JUnitRuleMockery();

    private final DefaultModMockery modMockery = new DefaultModMockery();

    @After
    public void tearDown() {

        // Revert mockery changes
        modMockery.close();
    }

    @Test
    public void testAddFuncExec() {

        final FunctionExecutor<String> funcExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(funcExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "testParam" }));
                will(returnValue("testReturn"));

        }});
        // @formatter:on

        modMockery.addFuncExec(TestFHChild1.FUNC, "mock", TestFHChild1.class, funcExecutor);

        assertEquals("Return value of mock invocation", "prefix-testReturn", new TestFHChild1().get(TestFHChild1.FUNC).invoke("testParam"));
    }

    @Test
    public void testAddFuncExecAndClose() {

        final FunctionExecutor<String> funcExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(funcExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addFuncExec(TestFHChild1.FUNC, "mock", TestFHChild1.class, funcExecutor);
        modMockery.close();

        assertEquals("Return value of mock invocation", "prefix-null", new TestFHChild1().get(TestFHChild1.FUNC).invoke("testParam"));
    }

    @Test
    public void testAddFuncExecOtherVariants() {

        final FunctionExecutor<String> funcExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(funcExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addFuncExec(TestFHChild1.FUNC, "mock", TestFHChild1.class, funcExecutor);

        // Use TestFHChild2 instead of TestFHChild1
        assertEquals("Return value of unmocked invocation", "prefix-null", new TestFHChild2().get(TestFHChild2.FUNC).invoke("testParam"));
        // Use TestFH instead of TestFHChild1
        assertEquals("Return value of unmocked invocation", "prefix-null", new TestFH().get(TestFH.FUNC).invoke("testParam"));
    }

    public static class TestFH extends DefaultFeatureHolder {

        public static final FunctionDefinition<String> FUNC;

        static {

            FUNC = create(new TypeLiteral<FunctionDefinition<String>>() {}, "name", "func", "parameters", new Class[] { String.class });

            // Add a default executor (priority level 7) which is executed before the mock ones
            FUNC.addExecutor("default", TestFH.class, new FunctionExecutor<String>() {

                @Override
                public String invoke(FunctionInvocation<String> invocation, Object... arguments) {

                    return "prefix-" + invocation.next(arguments);
                }

            }, LEVEL_7);

        }

    }

    public static class TestFHChild1 extends TestFH {

    }

    public static class TestFHChild2 extends TestFH {

    }

}
