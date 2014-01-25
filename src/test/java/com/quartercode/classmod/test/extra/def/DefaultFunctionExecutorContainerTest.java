/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://www.quartercode.com/>
 *
 * Classmod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Classmod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Classmod. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.classmod.test.extra.def;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvokationException;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.def.AbstractFunction.DefaultFunctionExecutorContainer;

public class DefaultFunctionExecutorContainerTest {

    @Test
    public void testGetValueAnnotated() {

        DefaultFunctionExecutorContainer<Void> container = new DefaultFunctionExecutorContainer<Void>("test", new FunctionExecutor<Void>() {

            @Override
            @TestAnnotation (value1 = 7, value2 = "test")
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvokationException {

                return null;
            }
        });

        Assert.assertEquals("First read annotation value", 7, container.getValue(TestAnnotation.class, "value1"));
        Assert.assertEquals("Second read annotation value", "test", container.getValue(TestAnnotation.class, "value2"));
    }

    @Test
    public void testGetValueDefault() {

        DefaultFunctionExecutorContainer<Void> container = new DefaultFunctionExecutorContainer<Void>("test", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvokationException {

                return null;
            }
        });

        Assert.assertEquals("First read annotation value", 2, container.getValue(TestAnnotation.class, "value1"));
        Assert.assertEquals("Second read annotation value", "defaultvalue", container.getValue(TestAnnotation.class, "value2"));
    }

    @Test
    public void testSetValue() {

        DefaultFunctionExecutorContainer<Void> container = new DefaultFunctionExecutorContainer<Void>("test", new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvokationException {

                return null;
            }
        });

        container.setValue(TestAnnotation.class, "value1", 17);
        container.setValue(TestAnnotation.class, "value2", "testvalue");

        Assert.assertEquals("First read annotation value", 17, container.getValue(TestAnnotation.class, "value1"));
        Assert.assertEquals("Second read annotation value", "testvalue", container.getValue(TestAnnotation.class, "value2"));
    }

    @Retention (RetentionPolicy.RUNTIME)
    public static @interface TestAnnotation {

        int value1 () default 2;

        String value2 () default "defaultvalue";

    }

}
