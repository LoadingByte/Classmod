/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
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

package com.quartercode.classmod.factory;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import com.quartercode.classmod.factory.def.DefaultCollectionPropertyDefinitionFactory;
import com.quartercode.classmod.factory.def.DefaultFunctionDefinitionFactory;
import com.quartercode.classmod.factory.def.DefaultPropertyDefinitionFactory;

/**
 * This class provides factory provider implementation which are registered under factory specification interfaces.
 * For example, {@link DefaultFunctionDefinitionFactory} is a factory provider for the factory interface {@link FunctionDefinitionFactory}.
 * The returned factory providers can then be used to create objects using the methods supplied by the factory.
 * The result is a really nice syntax for creating feature definition if the {@link #factory(Class)} method is statically imported:
 * 
 * <pre>
 * public static final PropertyDefinition&lt;String&gt; ID;
 * 
 * static {
 *     ID = factory(PropertyDefinitionFactory.class).create("id", new StandardStorage&lt;&gt;(), new ConstantValueFactory&lt;&gt;("defaultId"));
 * }
 * </pre>
 * 
 * By default, factory implementations for {@link FunctionDefinitionFactory}, {@link PropertyDefinitionFactory} and {@link CollectionPropertyDefinitionFactory} are available.
 */
public class ClassmodFactory {

    private static Map<Class<?>, Object> factories = new HashMap<>();

    static {

        registerFactoryProvider(FunctionDefinitionFactory.class, new DefaultFunctionDefinitionFactory());

        registerFactoryProvider(PropertyDefinitionFactory.class, new DefaultPropertyDefinitionFactory());
        registerFactoryProvider(CollectionPropertyDefinitionFactory.class, new DefaultCollectionPropertyDefinitionFactory());

    }

    /**
     * Returns the registered factory provider implementation for the given factory specification interface.
     * If no such provider is found, an {@link IllegalStateException} is thrown.
     * 
     * @param factorySpec The factory specification interface the returned provider implements.
     * @return A factory provider implementation which implements the given factory specification.
     * @throws IllegalStateException Thrown if no provider can be found for the given factory specification.
     */
    public static <F> F factory(Class<F> factorySpec) throws IllegalStateException {

        Validate.notNull(factorySpec, "Cannot retrieve factory provider for null factory specification");
        Validate.validState(factories.containsKey(factorySpec), "No registered factory provider found for factory specification '%s'", factorySpec.getName());

        return factorySpec.cast(factories.get(factorySpec));
    }

    /**
     * Registers the given factory provider implementation for the given factory specification interface.
     * If a provider already exists for the given factory specification, the old provider is overwritten with the new one.
     * 
     * @param factorySpec The factory specification interface the given factory provider should be registered to.
     * @param factoryProvider The factory provider implementation which should be registered to the given factory specification.
     */
    public static <P> void registerFactoryProvider(Class<? super P> factorySpec, P factoryProvider) {

        Validate.notNull(factorySpec, "Cannot register factory provider for null factory specification");
        Validate.notNull(factoryProvider, "Cannot register null factory provider");

        factories.put(factorySpec, factoryProvider);
    }

    private ClassmodFactory() {

    }

}
