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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.Delay;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.FunctionExecutionException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.Limit;
import com.quartercode.classmod.extra.def.AbstractFunction;

@RunWith (Parameterized.class)
public class AbstractFunctionLimitsTest {

    private static AtomicInteger actualTimesInvoked = new AtomicInteger();

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<Object[]>();

        // Check if the test works
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 5, 5 });

        // No delay does nothing
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Delay
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 5, 5 });

        // First delay test
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Delay (firstDelay = 2)
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 5, 3 });

        // Periodic delay test
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Delay (delay = 2)
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 5, 2 });
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Delay (delay = 2)
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 10, 4 });

        // First and periodic delay test
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Delay (firstDelay = 2, delay = 3)
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 20, 5 });

        // Limit test
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Limit (3)
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 5, 3 });

        // Delay and limit test
        data.add(new Object[] { new FunctionExecutor<Void>() {

            @Override
            @Delay (firstDelay = 2, delay = 3)
            @Limit (3)
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException {

                actualTimesInvoked.incrementAndGet();
                return null;
            }
        }, 20, 3 });

        return data;
    }

    private final FunctionExecutor<Void> executor;
    private final int                    invocations;
    private final int                    expectedTimesInvoked;

    public AbstractFunctionLimitsTest(FunctionExecutor<Void> executor, int invocations, int expectedTimesInvoked) {

        this.executor = executor;
        this.invocations = invocations;
        this.expectedTimesInvoked = expectedTimesInvoked;
    }

    @Test
    public void testInvoke() throws InstantiationException, IllegalAccessException, FunctionExecutionException {

        Map<String, FunctionExecutor<Void>> executors = new HashMap<String, FunctionExecutor<Void>>();
        executors.put("default", executor);
        AbstractFunction<Void> function = new AbstractFunction<Void>("testFunction", new DefaultFeatureHolder(), new ArrayList<Class<?>>(), executors);

        for (int counter = 0; counter < invocations; counter++) {
            function.invoke();
        }

        Assert.assertEquals("Number of invocations", expectedTimesInvoked, actualTimesInvoked.get());
        actualTimesInvoked.set(0);
    }

}
