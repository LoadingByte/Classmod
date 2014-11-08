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
import static org.junit.Assert.assertEquals;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Prioritized;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.classmod.util.test.DefaultModMockery;

@SuppressWarnings ("unchecked")
public class DefaultModMockeryPropertyTest {

    @Rule
    public JUnitRuleMockery         context    = new JUnitRuleMockery();

    private final DefaultModMockery modMockery = new DefaultModMockery();

    // ----- Getter -----

    @Test
    public void testAddPropGetter() {

        final FunctionExecutor<String> getterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[0]));
                will(returnValue("testReturn"));

        }});
        // @formatter:on

        modMockery.addPropGetter(TestFHChild1.PROP, "mockGetter", TestFHChild1.class, new Priority7Executor<>(getterExecutor));

        String result = new TestFHChild1().get(TestFHChild1.PROP).get();
        assertEquals("Return value of mock invocation", "testReturn", result);
    }

    @Test
    public void testAddPropGetterOtherVariants() {

        TestFHChild2 holder1 = new TestFHChild2();
        holder1.get(TestFHChild2.PROP).set("origValue1");

        TestFH holder2 = new TestFH();
        holder2.get(TestFH.PROP).set("origValue2");

        final FunctionExecutor<String> getterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addPropGetter(TestFHChild1.PROP, "mockGetter", TestFHChild1.class, new Priority7Executor<>(getterExecutor));

        // Use TestFHChild2 instead of TestFHChild1
        String result1 = holder1.get(TestFHChild2.PROP).get();
        assertEquals("Return value of unmocked invocation", "origValue1", result1);

        // Use TestFH instead of TestFHChild1
        String result2 = holder2.get(TestFH.PROP).get();
        assertEquals("Return value of unmocked invocation", "origValue2", result2);
    }

    // ----- Setter -----

    @Test
    public void testAddPropSetter() {

        final FunctionExecutor<Void> setterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(setterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "testParam" }));

        }});
        // @formatter:on

        modMockery.addPropSetter(TestFHChild1.PROP, "mockSetter", TestFHChild1.class, new Priority7Executor<>(setterExecutor));

        new TestFHChild1().get(TestFHChild1.PROP).set("testParam");
    }

    @Test
    public void testAddPropSetterOtherVariants() {

        final FunctionExecutor<Void> setterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(setterExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addPropSetter(TestFHChild1.PROP, "mockGetter", TestFHChild1.class, new Priority7Executor<>(setterExecutor));

        // Use TestFHChild2 instead of TestFHChild1
        new TestFHChild2().get(TestFHChild2.PROP).set("origValue1");

        // Use TestFH instead of TestFHChild1
        new TestFH().get(TestFH.PROP).set("origValue2");
    }

    public static class TestFH extends DefaultFeatureHolder {

        public static final PropertyDefinition<String> PROP;

        static {

            PROP = create(new TypeLiteral<PropertyDefinition<String>>() {}, "name", "prop", "storage", new StandardStorage<>());

        }

    }

    public static class TestFHChild1 extends TestFH {

    }

    public static class TestFHChild2 extends TestFH {

    }

    private static class Priority7Executor<R> implements FunctionExecutor<R> {

        private final FunctionExecutor<R> wrapped;

        private Priority7Executor(FunctionExecutor<R> wrapped) {

            this.wrapped = wrapped;
        }

        @Override
        @Prioritized (Prioritized.LEVEL_7)
        public R invoke(FunctionInvocation<R> invocation, Object... arguments) {

            return wrapped.invoke(invocation, arguments);
        }

    }

}
