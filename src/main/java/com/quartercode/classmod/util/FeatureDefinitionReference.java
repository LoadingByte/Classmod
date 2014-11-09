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

package com.quartercode.classmod.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.quartercode.classmod.base.FeatureDefinition;

/**
 * A feature definition reference stores some serializable data which is required to reference a {@link FeatureDefinition} object.
 * The referenced definition can be accessed through the {@link #getDefinition()} method which looks the object up and returns it.
 * Actually, a cache is involved for minimizing the reflection calls.
 * That means that a feature definition reference is able to make a specific feature definition available between different
 * virtual machines using object or xml serialization.<br>
 * <br>
 * Internally, a feature definition reference stores the class which contains the definition constant and the name of the definition constant.
 * 
 * @param <D> The type of the referenced feature definition.
 * @see FeatureDefinition
 */
public class FeatureDefinitionReference<D extends FeatureDefinition<?>> implements Serializable {

    private static final long serialVersionUID = -7405840006826808957L;

    // Cache for the actual feature definition object
    private transient D       definition;

    // Two fields which reference to the feature definition object constant
    @XmlAttribute (name = "class")
    private Class<?>          definitionClass;
    @XmlAttribute (name = "field")
    private String            definitionFieldName;

    /**
     * Creates a new empty feature definition reference.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected FeatureDefinitionReference() {

    }

    /**
     * Creates a new feature definition reference using the {@link Class} which contains the the definition constant and the name of the constant.<br>
     * <br>
     * For example, if your feature holder {@code SomeFeatureHolder} contained the {@link FeatureDefinition} constant {@code public static final
     * FeatureDefinition<...> SOME_FEATURE}, you would have to provide {@code SomeFeatureHolder.class} as the first and {@code "SOME_FEATURE"} as
     * the second argument to this constructor.
     * 
     * @param definitionClass The {@link Class} that contains the referenced {@link FeatureDefinition} constant.
     * @param definitionFieldName The actual name of the referenced {@link FeatureDefinition} constant.
     */
    public FeatureDefinitionReference(Class<?> definitionClass, String definitionFieldName) {

        this.definitionClass = definitionClass;
        this.definitionFieldName = definitionFieldName;

        // Read the definition object and test whether the field exists; if not, the method throws an exception
        definition = resolveDefinition();
    }

    /**
     * Creates a new feature definition reference using the {@link Class} which contains the the definition constant and the actual {@link FeatureDefinition} object.
     * The name of the constant is resolved from the name of the feature definition.
     * Please note that the name of the definition constant needs to match the name of the feature definition.
     * Examples:
     * 
     * <pre>
     * Feature:  xyz
     * Constant: XYZ
     * 
     * Feature:  someFeature
     * Constant: SOME_FEATURE
     * 
     * Feature:  someFeature2
     * Constant: SOME_FEATURE_2
     * </pre>
     * 
     * <b>Note that this is the preferred constructor since it allows the name of the definition to be refactored without any problems.</b>
     * 
     * @param definitionClass The {@link Class} that contains the referenced {@link FeatureDefinition} constant.
     * @param definition The {@link FeatureDefinition} object which should be referenced.
     */
    public FeatureDefinitionReference(Class<?> definitionClass, D definition) {

        this.definitionClass = definitionClass;

        // Build the name of the feature definition constant
        definitionFieldName = toConstantName(definition.getName());

        // Read the definition object and test whether the field exists; if not, the method throws an exception
        this.definition = resolveDefinition();

        // Since the correct definition object is supplied, we can check whether the correct definition has been resolved
        Validate.isTrue(this.definition == definition, "Can't resolve correct constant name of feature definition '%s' in class '%s'", definition.getName(), definitionClass.getName());
    }

    private String toConstantName(String camelCaseName) {

        StringBuilder constantName = new StringBuilder();
        char[] camelCaseNameChars = camelCaseName.toCharArray();

        // Iterate over all characters
        for (int index = 0; index < camelCaseNameChars.length; index++) {
            char current = camelCaseNameChars[index];

            // If the current character is not the first one
            if (index > 0) {
                char previous = camelCaseNameChars[index - 1];

                // Append separator if the current character is upper case and the previous one wasn't
                if (Character.isUpperCase(current) && !Character.isUpperCase(previous)) {
                    constantName.append("_");
                }
                // Append separator if the current character is a digit and the previous one wasn't
                else if (Character.isDigit(current) && !Character.isDigit(previous)) {
                    constantName.append("_");
                }
            }

            // Append the current character to the result
            constantName.append(Character.toUpperCase(current));
        }

        return constantName.toString();
    }

    /**
     * Returns the referenced {@link FeatureDefinition} object.
     * Note that this method might need to make some reflection calls if the definition object is not in the cache.
     * However, multiple calls of this method are very cheap.
     * 
     * @return The referenced feature definition.
     */
    public D getDefinition() {

        if (definition == null) {
            definition = resolveDefinition();
        }

        return definition;
    }

    @SuppressWarnings ("unchecked")
    private D resolveDefinition() {

        try {
            Field field = definitionClass.getField(definitionFieldName);
            field.setAccessible(true);
            return (D) field.get(null);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Feature definition field '" + definitionFieldName + "' doesn't exist in class '" + definitionClass.getName() + "'", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Feature definition field '" + definitionFieldName + "' in class '" + definitionClass.getName() + "' isn't public", e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Public static field '" + definitionFieldName + "' in class '" + definitionClass.getName() + "' doesn't contain a feature definition", e);
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (definitionClass == null ? 0 : definitionClass.hashCode());
        result = prime * result + (definitionFieldName == null ? 0 : definitionFieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof FeatureDefinitionReference)) {
            return false;
        } else {
            FeatureDefinitionReference<?> other = (FeatureDefinitionReference<?>) obj;
            return definitionClass == other.definitionClass && Objects.equals(definitionFieldName, other.definitionFieldName);
        }
    }

    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this, ToStringBuilder.getDefaultStyle(), false);
    }

}
