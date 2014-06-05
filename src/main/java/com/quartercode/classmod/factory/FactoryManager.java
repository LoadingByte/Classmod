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

package com.quartercode.classmod.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 * A factory manager assigns factory objects to abstract types.
 * For example, a factory for objects of the type {@code B implements A} could be assigned to the interface type {@code A}.
 * Users can simply input {@code A} in the {@link #create(Class, Object...)} method and get a new instance of {@code B} without knowing that it even exists.<br>
 * <br>
 * Factory implementations must implement <b>exactly one</b> method that is annotated with the {@link Factory} annotation.
 * That annotation indicates that the method is called by the factory manager for creating new objects.
 * The factory manager automatically maps the supplied parameters to the parameters of the factory method.
 * 
 * @see Factory
 */
public class FactoryManager {

    private final Map<Class<?>, Triple<Object, Method, Factory>> factories = new HashMap<>();

    /**
     * Creates a new factory manager.
     */
    public FactoryManager() {

    }

    /**
     * Assigns the given factory object to the given abstract type.
     * The type must not be abstract, but it's recommended that only interfaces are added here.
     * The factory must implement <b>exactly one</b> method that is annotated with a valid {@link Factory} annotation.
     * It then creates objects when the {@link #create(Class, Object...)} method is called with the given type.
     * 
     * @param type The abstract type the given factory is assigned to.
     * @param factory The factory which is assigned to the given abstract type.
     * @throws IllegalStateException The {@link Factory} annotation is not present or invalid.
     */
    public void setFactory(Class<?> type, Object factory) {

        Pair<Method, Factory> factoryData = getFactoryData(factory.getClass());
        factories.put(type, Triple.of(factory, factoryData.getLeft(), factoryData.getRight()));
    }

    private Pair<Method, Factory> getFactoryData(Class<?> factoryType) {

        for (Method method : factoryType.getMethods()) {
            if (method.isAnnotationPresent(Factory.class)) {
                Factory annotation = method.getAnnotation(Factory.class);

                if (annotation.parameters().length != method.getParameterTypes().length) {
                    throw new IllegalStateException("Factory method of class '" + factoryType.getName() + "' provides invalid parameters");
                }

                return Pair.of(method, annotation);
            }
        }

        throw new IllegalStateException("Factory class '" + factoryType.getName() + "' doesn't have no factory method");
    }

    /**
     * Creates a new object of the given abstract type with the given parameters.
     * Internally, a factory, which was mapped with {@link #setFactory(Class, Object)}, creates the object.
     * This method maps the supplied parameters to the parameters of the {@link Factory} method of the given factory.
     * The parameters must be provided in a key-value-scheme:
     * 
     * <pre>
     * create(..., "param1", value1, "param2", value2, ...)
     * </pre>
     * 
     * The parameters are mapped to the method parameters of the responsible factory method.
     * 
     * @param type The type of the object that should be created.
     * @param parameters The parameters which should be supplied to the responsible factory method.
     * @return The newly created object.
     * @throws IllegalArgumentException There is no factory mapped for the given type or the provided parameters are invalid.
     * @throws RuntimeException An unknown error occurs while invoking the selected factory.
     *         That might be caused by some unknown reflection problems or a programming error.
     */
    public <T> T create(Class<T> type, Object... parameters) {

        Validate.isTrue(factories.containsKey(type), "Factory manager doesn't contain factory for type '%s'", type.getName());

        Triple<Object, Method, Factory> factoryData = factories.get(type);
        Object factory = factoryData.getLeft();
        Method factoryMethod = factoryData.getMiddle();
        Factory factoryAnnotation = factoryData.getRight();

        Object[] arguments = getMethodArguments(factoryMethod, factoryAnnotation, parameters);
        try {
            return type.cast(factoryMethod.invoke(factory, arguments));
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Factory '" + factory.getClass() + "' threw an unexpected exception", e.getTargetException());
        } catch (Exception e) {
            throw new RuntimeException("Unknown error while creating object of type '" + type.getName() + "' with parameters " + Arrays.toString(parameters), e);
        }
    }

    private Map<String, Object> mapParameters(Object... parameters) {

        Validate.isTrue(parameters.length % 2 == 0, "Parameter array does not have an equal amount of parameters and arguments");

        Map<String, Object> parameterMap = new HashMap<>();
        for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex += 2) {
            Validate.isInstanceOf(String.class, parameters[parameterIndex], "Parameter array has a no-string value at %d", parameterIndex);
            parameterMap.put((String) parameters[parameterIndex], parameters[parameterIndex + 1]);
        }

        return parameterMap;
    }

    private Object[] getMethodArguments(Method method, Factory annotation, Object[] parameters) {

        Map<String, Object> parameterMap = mapParameters(parameters);

        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int parameter = 0; parameter < annotation.parameters().length; parameter++) {
            String parameterName = annotation.parameters()[parameter];
            Object argument = parameterMap.get(parameterName);

            Class<?> parameterType = parameterTypes[parameter];
            if (argument == null) {
                argument = getDefaultValue(parameterType);
            } else if (!ClassUtils.isAssignable(argument.getClass(), parameterType)) {
                String paramTypeName = parameterType.getName();
                String argTypeName = argument.getClass().getName();
                String message = "Parameter " + parameterName + " (index " + parameter + ") must be an instance of '" + paramTypeName + "' ('" + argTypeName + "' provided)";
                throw new IllegalArgumentException(message);
            }

            arguments[parameter] = argument;
        }

        return arguments;
    }

    private Object getDefaultValue(Class<?> type) {

        if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
            return (byte) 0;
        } else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
            return (short) 0;
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return (int) 0;
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return (long) 0;
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return (float) 0;
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return (double) 0;
        } else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return false;
        } else if (type.equals(Character.TYPE) || type.equals(Character.class)) {
            return '\u0000';
        } else {
            return null;
        }
    }

}
