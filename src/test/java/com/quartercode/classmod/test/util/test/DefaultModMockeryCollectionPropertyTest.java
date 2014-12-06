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
import static com.quartercode.classmod.extra.func.Priorities.LEVEL_7;
import static com.quartercode.classmod.test.ExtraAssert.assertListEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.def.base.DefaultFeatureHolder;
import com.quartercode.classmod.extra.func.FunctionExecutor;
import com.quartercode.classmod.extra.func.FunctionInvocation;
import com.quartercode.classmod.extra.prop.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.classmod.extra.valuefactory.CloneValueFactory;
import com.quartercode.classmod.util.test.DefaultModMockery;

@SuppressWarnings ("unchecked")
public class DefaultModMockeryCollectionPropertyTest {

    @Rule
    public JUnitRuleMockery         context    = new JUnitRuleMockery();

    private final DefaultModMockery modMockery = new DefaultModMockery();

    @After
    public void tearDown() {

        // Revert mockery changes
        modMockery.close();
    }

    // ----- Getter -----

    @Test
    public void testAddCollPropGetter() {

        final FunctionExecutor<List<String>> getterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[0]));
                will(returnValue(Arrays.asList("testReturn")));

        }});
        // @formatter:on

        modMockery.addCollPropGetter(TestFHChild1.COLL_PROP, "mockGetter", TestFHChild1.class, getterExecutor, LEVEL_7);

        assertListEquals("Return value of mocked getter is wrong", new TestFHChild1().get(TestFHChild1.COLL_PROP).get(), "testReturn");
    }

    @Test
    public void testAddCollPropGetterAndClose() {

        TestFHChild1 holder = new TestFHChild1();
        holder.get(TestFHChild1.COLL_PROP).add("origValue");

        final FunctionExecutor<List<String>> getterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addCollPropGetter(TestFHChild1.COLL_PROP, "mockGetter", TestFHChild1.class, getterExecutor, LEVEL_7);
        modMockery.close();

        assertListEquals("Return value of mocked getter is wrong", holder.get(TestFHChild1.COLL_PROP).get(), "origValue");
    }

    @Test
    public void testAddCollPropGetterOtherVariants() {

        TestFHChild2 holder1 = new TestFHChild2();
        holder1.get(TestFHChild2.COLL_PROP).add("origValue1");

        TestFH holder2 = new TestFH();
        holder2.get(TestFH.COLL_PROP).add("origValue2");

        final FunctionExecutor<List<String>> getterExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(getterExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addCollPropGetter(TestFHChild1.COLL_PROP, "mockGetter", TestFHChild1.class, getterExecutor, LEVEL_7);

        // Use TestFHChild2 instead of TestFHChild1
        assertListEquals("Return value of unmocked getter is wrong", holder1.get(TestFHChild2.COLL_PROP).get(), "origValue1");
        // Use TestFH instead of TestFHChild1
        assertListEquals("Return value of unmocked getter is wrong", holder2.get(TestFH.COLL_PROP).get(), "origValue2");
    }

    // ----- Adder -----

    @Test
    public void testAddCollPropAdder() {

        final FunctionExecutor<Void> adderExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(adderExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "testParam" }));

        }});
        // @formatter:on

        modMockery.addCollPropAdder(TestFHChild1.COLL_PROP, "mockAdder", TestFHChild1.class, adderExecutor, LEVEL_7);

        new TestFHChild1().get(TestFHChild1.COLL_PROP).add("testParam");
    }

    @Test
    public void testAddCollPropAdderAndClose() {

        final FunctionExecutor<Void> adderExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(adderExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addCollPropAdder(TestFHChild1.COLL_PROP, "mockAdder", TestFHChild1.class, adderExecutor, LEVEL_7);
        modMockery.close();

        new TestFHChild1().get(TestFHChild1.COLL_PROP).add("testParam");
    }

    @Test
    public void testAddCollPropAdderOtherVariants() {

        final FunctionExecutor<Void> adderExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(adderExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addCollPropAdder(TestFHChild1.COLL_PROP, "mockAdder", TestFHChild1.class, adderExecutor, LEVEL_7);

        // Use TestFHChild2 instead of TestFHChild1
        new TestFHChild2().get(TestFHChild2.COLL_PROP).add("testParam");
        // Use TestFH instead of TestFHChild1
        new TestFH().get(TestFH.COLL_PROP).add("testParam");
    }

    // ----- Remover -----

    @Test
    public void testAddCollPropRemover() {

        final FunctionExecutor<Void> removerExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            oneOf(removerExecutor).invoke(with(any(FunctionInvocation.class)), with(new Object[] { "testParam" }));

        }});
        // @formatter:on

        modMockery.addCollPropRemover(TestFHChild1.COLL_PROP, "mockRemover", TestFHChild1.class, removerExecutor, LEVEL_7);

        new TestFHChild1().get(TestFHChild1.COLL_PROP).remove("testParam");
    }

    @Test
    public void testAddCollPropRemoverAndClose() {

        final FunctionExecutor<Void> removerExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(removerExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addCollPropRemover(TestFHChild1.COLL_PROP, "mockRemover", TestFHChild1.class, removerExecutor, LEVEL_7);
        modMockery.close();

        new TestFHChild1().get(TestFHChild1.COLL_PROP).remove("testParam");
    }

    @Test
    public void testAddCollPropRemoverOtherVariants() {

        final FunctionExecutor<Void> removerExecutor = context.mock(FunctionExecutor.class);

        // @formatter:off
        context.checking(new Expectations() {{

            never(removerExecutor).invoke(with(any(FunctionInvocation.class)), with(any(Object[].class)));

        }});
        // @formatter:on

        modMockery.addCollPropRemover(TestFHChild1.COLL_PROP, "mockRemover", TestFHChild1.class, removerExecutor, LEVEL_7);

        // Use TestFHChild2 instead of TestFHChild1
        new TestFHChild2().get(TestFHChild2.COLL_PROP).remove("testParam");
        // Use TestFH instead of TestFHChild1
        new TestFH().get(TestFH.COLL_PROP).remove("testParam");
    }

    public static class TestFH extends DefaultFeatureHolder {

        public static final CollectionPropertyDefinition<String, List<String>> COLL_PROP;

        static {

            COLL_PROP = create(new TypeLiteral<CollectionPropertyDefinition<String, List<String>>>() {}, "name", "collProp", "storage", new StandardStorage<>(), "collection", new CloneValueFactory<>(new ArrayList<>()));

        }

    }

    public static class TestFHChild1 extends TestFH {

    }

    public static class TestFHChild2 extends TestFH {

    }

}
