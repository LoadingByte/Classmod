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

package com.quartercode.classmod.test.factory;

import static org.junit.Assert.*;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.factory.Factory;
import com.quartercode.classmod.factory.FactoryManager;

public class FactoryManagerTest {

    private FactoryManager factoryManager;

    @Before
    public void setUp() {

        factoryManager = new FactoryManager();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testWithUnknownType() {

        // No factory is registered for type Abstract
        factoryManager.create(Abstract.class);
    }

    @Test
    public void testAddFactoryNoErrors() {

        factoryManager.setFactory(Abstract.class, new NoParameterFactory());
    }

    @Test (expected = IllegalStateException.class)
    public void testAddFactoryWithoutMethod() {

        factoryManager.setFactory(Abstract.class, new NoMethodFactory());
    }

    @Test (expected = IllegalStateException.class)
    public void testAddFactoryWithWrongParameterCount() {

        factoryManager.setFactory(Abstract.class, new WrongParameterCountFactory());
    }

    @Test
    public void testWithoutParameters() {

        factoryManager.setFactory(Abstract.class, new NoParameterFactory());

        Abstract actualResult = factoryManager.create(Abstract.class);
        assertNotNull("Factory manager returned null", actualResult);
        assertTrue("Factory manager didn't create correct object", Objects.equals("noParameters", actualResult.getData()));
    }

    @Test
    public void testWithParameters() {

        factoryManager.setFactory(Abstract.class, new ParameterFactory());

        Abstract actualResult = factoryManager.create(Abstract.class, "parameter1", "test", "parameter2", 17, "parameter3", new StringBuilder("test2"));
        assertNotNull("Factory manager returned null", actualResult);
        assertTrue("Factory manager didn't create correct object", Objects.equals("test17test2", actualResult.getData()));
    }

    @Test
    public void testWithNullParameter() {

        factoryManager.setFactory(Abstract.class, new ParameterFactory());

        Abstract actualResult = factoryManager.create(Abstract.class, "parameter1", "test", "parameter2", 17, "parameter3", null);
        assertNotNull("Factory manager returned null", actualResult);
        assertTrue("Factory manager didn't create correct object", Objects.equals("test17null", actualResult.getData()));
    }

    @Test
    public void testWithNullPrimitiveParameter() {

        factoryManager.setFactory(Abstract.class, new ParameterFactory());

        Abstract actualResult = factoryManager.create(Abstract.class, "parameter1", "test", "parameter2", null, "parameter3", new StringBuilder("test2"));
        assertNotNull("Factory manager returned null", actualResult);
        assertTrue("Factory manager didn't create correct object", Objects.equals("test0test2", actualResult.getData()));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testWithWrongParameterType() {

        factoryManager.setFactory(Abstract.class, new ParameterFactory());

        // parameter is a StringBuilder although it should be a String
        factoryManager.create(Abstract.class, "parameter1", new StringBuilder("test1"), "parameter2", 17, "parameter3", new StringBuilder("test2"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testWithWrongPrimitiveParameterType() {

        factoryManager.setFactory(Abstract.class, new ParameterFactory());

        // parameter2 is a boolean although it should be an int
        factoryManager.create(Abstract.class, "parameter1", "test", "parameter2", true, "parameter3", new StringBuilder("test"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testWithOddParamaterArrayLength() {

        factoryManager.setFactory(Abstract.class, new ParameterFactory());

        // The value of parameter3 is missing
        factoryManager.create(Abstract.class, "parameter1", "test", "parameter2", true, "parameter3");
    }

    @Test
    public void testRethrowFactoryException() {

        factoryManager.setFactory(Abstract.class, new ThrowExceptionFactory());

        try {
            factoryManager.create(Abstract.class, "parameter1", "test", "parameter2", null, "parameter3", new StringBuilder("test2"));
        } catch (RuntimeException e) {
            assertTrue("Not the correct exception was rethrown", e.getCause() instanceof TestException);
            return;
        }

        fail("No exception was thrown");
    }

    private static interface Abstract {

        public String getData();

    }

    private static class Implementation implements Abstract {

        private final String data;

        private Implementation(String data) {

            this.data = data;
        }

        @Override
        public String getData() {

            return data;
        }
    }

    /*
     * Factories must be protected for reflection to work without setAccessible().
     */

    protected static class NoMethodFactory {

    }

    protected static class WrongParameterCountFactory {

        @Factory (parameters = { "parameter1" })
        public Abstract create(String parameter1, int parameter2, Object parameter3) {

            return new Implementation(parameter1 + parameter2 + parameter3);
        }

    }

    protected static class NoParameterFactory {

        @Factory
        public Abstract create() {

            return new Implementation("noParameters");
        }

    }

    protected static class ParameterFactory {

        @Factory (parameters = { "parameter1", "parameter2", "parameter3" })
        public Abstract create(String parameter1, int parameter2, Object parameter3) {

            return new Implementation(parameter1 + parameter2 + parameter3);
        }

    }

    protected static class ThrowExceptionFactory {

        @Factory
        public Abstract create() {

            throw new TestException();
        }

    }

    @SuppressWarnings ("serial")
    private static class TestException extends RuntimeException {

    }

}
