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

package com.quartercode.classmod.base.def;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * An abstract feature definition is used to retrieve a {@link Feature} from a {@link FeatureHolder}.
 * It's an implementation of the {@link FeatureDefinition} interface.
 * It contains the name of the feature and the type it has as a generic parameter.
 * You can use an abstract feature definition to construct a new instance of the defined feature through {@link #create(FeatureHolder)}.
 * 
 * @param <F> The type the defined feature has.
 * @see FeatureDefinition
 * @see Feature
 */
public abstract class AbstractFeatureDefinition<F extends Feature> implements FeatureDefinition<F> {

    private final String name;

    /**
     * Creates a new abstract feature definition for defining a {@link Feature} with the given name.
     * 
     * @param name The name of the defined feature.
     */
    public AbstractFeatureDefinition(String name) {

        Validate.notNull(name, "The name of a feature definition cannot be null");

        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this);
    }

}
