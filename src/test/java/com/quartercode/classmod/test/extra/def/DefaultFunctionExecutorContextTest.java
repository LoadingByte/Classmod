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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Test;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.def.DefaultFunctionExecutorContext;

public class DefaultFunctionExecutorContextTest {

    @Test
    public void testGetValueAnnotated() {

        DefaultFunctionExecutorContext<Void> context = new DefaultFunctionExecutorContext<Void>("test", new FunctionExecutor<Void>() {

            @Override
            @TestAnnotation (value1 = 7, value2 = "test")
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return invocation.next(arguments);
            }
        });

        Assert.assertEquals("First read annotation value", 7, context.getValue(TestAnnotation.class, "value1"));
        Assert.assertEquals("Second read annotation value", "test", context.getValue(TestAnnotation.class, "value2"));
    }

    @Test
    public void testGetValueDefault() {

        DefaultFunctionExecutorContext<Void> context = new DefaultFunctionExecutorContext<Void>("test", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return invocation.next(arguments);
            }
        });

        Assert.assertEquals("First read annotation value", 2, context.getValue(TestAnnotation.class, "value1"));
        Assert.assertEquals("Second read annotation value", "defaultvalue", context.getValue(TestAnnotation.class, "value2"));
    }

    @Test
    public void testSetValue() {

        DefaultFunctionExecutorContext<Void> context = new DefaultFunctionExecutorContext<Void>("test", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return invocation.next(arguments);
            }
        });

        context.setValue(TestAnnotation.class, "value1", 17);
        context.setValue(TestAnnotation.class, "value2", "testvalue");

        Assert.assertEquals("First read annotation value", 17, context.getValue(TestAnnotation.class, "value1"));
        Assert.assertEquals("Second read annotation value", "testvalue", context.getValue(TestAnnotation.class, "value2"));
    }

    @Retention (RetentionPolicy.RUNTIME)
    public static @interface TestAnnotation {

        int value1 () default 2;

        String value2 () default "defaultvalue";

    }

}
