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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.def.AbstractFunction;

@RunWith (Parameterized.class)
public class AbstractFunctionParameterTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<Object[]>();

        // Simple arguments test
        data.add(new Object[] { new Class<?>[] { String.class }, new Object[] { "" }, true });
        data.add(new Object[] { new Class<?>[] { String.class }, new Object[] { 0 }, false });
        data.add(new Object[] { new Class<?>[] { String.class, Integer.class }, new Object[] { "", 0 }, true });
        data.add(new Object[] { new Class<?>[] { String.class, Integer.class }, new Object[] { "", "" }, false });

        // Inheritance test
        data.add(new Object[] { new Class<?>[] { Number.class }, new Object[] { (int) 0 }, true });
        data.add(new Object[] { new Class<?>[] { Integer.class }, new Object[] { (short) 0 }, false });

        // Vararg test
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { 0 }, true });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { 0, 1, 2 }, true });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { "" }, false });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { "", "" }, false });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { 0, 1, "" }, false });
        data.add(new Object[] { new Class<?>[] { String.class, Integer[].class }, new Object[] { "", 0, 1, 2 }, true });
        data.add(new Object[] { new Class<?>[] { String.class, Integer[].class }, new Object[] { "", "", 0, 1, 2 }, false });

        // Vararg inheritance test
        data.add(new Object[] { new Class<?>[] { Number[].class }, new Object[] { (int) 0, (short) 0 }, true });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { (int) 0, (short) 0 }, false });

        // Array test
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { new Integer[] { 0, 1, 2 } }, true });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { new Object[] { 0, 1, 2 } }, false });
        data.add(new Object[] { new Class<?>[] { Integer[].class }, new Object[] { new Object[] { 0, 1, "" } }, false });

        // Null arguments test
        data.add(new Object[] { new Class<?>[] { String.class }, new Object[] { null }, true });
        data.add(new Object[] { new Class<?>[] { String.class, Integer.class }, new Object[] { "", null }, true });
        data.add(new Object[] { new Class<?>[] { String.class, Integer.class }, new Object[] { null, "" }, false });

        // Less arguments than parameters test
        data.add(new Object[] { new Class<?>[] { String.class }, new Object[] {}, false });
        data.add(new Object[] { new Class<?>[] { String.class, Integer.class }, new Object[] {}, false });
        data.add(new Object[] { new Class<?>[] { String.class, Integer.class }, new Object[] { "" }, false });

        // Less parameters than arguments test
        data.add(new Object[] { new Class<?>[] {}, new Object[] { "" }, false });
        data.add(new Object[] { new Class<?>[] {}, new Object[] { "", 0 }, false });
        data.add(new Object[] { new Class<?>[] { String.class }, new Object[] { "", 10 }, false });

        return data;
    }

    private final Class<?>[] parameters;
    private final Object[]   arguments;
    private final boolean    works;

    public AbstractFunctionParameterTest(Class<?>[] parameters, Object[] arguments, boolean works) {

        this.parameters = parameters;
        this.arguments = arguments;
        this.works = works;
    }

    @Test
    public void testInvoke() throws InstantiationException, IllegalAccessException {

        Map<String, FunctionExecutor<Void>> executors = new HashMap<String, FunctionExecutor<Void>>();
        executors.put("default", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                return invocation.next(arguments);
            }

        });

        boolean actuallyWorks;
        try {
            AbstractFunction<Void> function = new AbstractFunction<Void>("testFunction", new DefaultFeatureHolder(), Arrays.asList(parameters), executors);
            function.invoke(arguments);
            actuallyWorks = true;
        } catch (ExecutorInvocationException e) {
            actuallyWorks = false;
        }

        Assert.assertTrue("Function call " + (works ? "doesn't work" : "works") + "; parameters rejected", actuallyWorks == works);
    }

}
