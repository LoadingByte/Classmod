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

import java.util.Objects;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * An abstract feature is a part of a {@link FeatureHolder} and is used for creating modifiable classes.
 * It's an implementation of the {@link Feature} interface.
 * The content of a feature is not limited, but it has to provide a name and store its holder.
 * 
 * @see Feature
 */
public class AbstractFeature implements Feature {

    @XmlAttribute
    private String        name;
    private FeatureHolder holder;

    /**
     * Creates a new empty abstract feature.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected AbstractFeature() {

    }

    /**
     * Creates a new abstract feature with the given name and {@link FeatureHolder}.
     * 
     * @param name The name of the feature.
     * @param holder The feature holder which has and uses the new feature.
     */
    public AbstractFeature(String name, FeatureHolder holder) {

        Validate.notNull(name, "The name of a feature cannot be null");
        Validate.notNull(holder, "The parent feature holder of a feature cannot be null");

        this.name = name;
        this.holder = holder;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public FeatureHolder getHolder() {

        return holder;
    }

    /**
     * Resolves the {@link FeatureHolder} which houses the abstract persistent feature.
     * 
     * @param unmarshaller The unmarshaller which unmarshals this task.
     * @param parent The object which was unmarshalled as the parent one from the xml structure.
     */
    protected void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {

        if (parent instanceof FeatureHolder) {
            holder = (FeatureHolder) parent;
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof AbstractFeature)) {
            return false;
        } else {
            AbstractFeature other = (AbstractFeature) obj;
            if (!Objects.equals(name, other.name)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public String toString() {

        return ReflectionToStringBuilder.toStringExclude(this, "holder");
    }

}
