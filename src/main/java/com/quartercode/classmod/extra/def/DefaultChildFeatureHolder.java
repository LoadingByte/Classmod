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

package com.quartercode.classmod.extra.def;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;
import com.quartercode.classmod.extra.XmlPassthroughElement;

/**
 * A child feature holder is a {@link FeatureHolder} which stores its parent {@link FeatureHolder}.
 * The parent holder is an object that holds a (collection) property that holds the actual child feature holder.
 * It uses the {@link DefaultFeatureHolder} implementation.<br>
 * <br>
 * A user can get {@link Feature}s through the central access method {@link #get(FeatureDefinition)}.
 * Such {@link Feature}s are defined by {@link FeatureDefinition} which describe how a feature looks like.<br>
 * <br>
 * The "constant" {@code parentType} must be set during the construction of every child feature holder for more type-safety.
 * For example, a child feature holder class {@code TestChild&lt;TestParent&gt;} could look like this:
 * 
 * <pre>
 * public class TestChild extends DefaultChildFeatureHolder&lt;<b>TestParent</b>&gt; {
 * 
 *     public TestChild() {
 * 
 *         <i>setParentType(<b>TestParent.class</b>);</i>
 *     }
 * 
 *     ...
 * 
 * }
 * </pre>
 * 
 * Note that the parent type class is set in the no-arg constructor every time an object is created.
 * 
 * @param <P> The type the parent {@link FeatureHolder} has to have.
 * @see FeatureHolder
 * @see Feature
 * @see FeatureDefinition
 */
public class DefaultChildFeatureHolder<P extends FeatureHolder> extends DefaultFeatureHolder implements ChildFeatureHolder<P> {

    private Class<? super P> parentType;
    private P                parent;

    /**
     * Creates a new default child feature holder.
     * Please note that the method {@link #setParentType(Class)} should be called somewhere during the construction.
     */
    public DefaultChildFeatureHolder() {

    }

    @Override
    public Class<? super P> getParentType() {

        return parentType;
    }

    /**
     * Sets the {@link Class} representation of the generic {@code P} parameter (parent type).
     * This method should be called somewhere during the construction.
     * 
     * @param parentType A class object that represents the generic {@code P} parameter for providing more type-safety.
     */
    protected void setParentType(Class<? super P> parentType) {

        this.parentType = parentType;
    }

    @Override
    @XmlTransient
    public P getParent() {

        return parent;
    }

    @Override
    public void setParent(P parent) {

        this.parent = parent;
    }

    /**
     * Resolves the parent {@link FeatureHolder} which is storing this feature holder during umarshalling.
     * 
     * @param unmarshaller The unmarshaller which unmarshals this objects.
     * @param parent The object which was unmarshalled as the parent {@link FeatureHolder} from the xml structure.
     */
    @SuppressWarnings ("unchecked")
    protected void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {

        // Resolve the next parent feature that is only separated with XmlPassthroughChild objects from this holder
        Object effectiveParent = parent;
        while (! (effectiveParent instanceof Feature) && effectiveParent instanceof XmlPassthroughElement) {
            effectiveParent = ((XmlPassthroughElement) effectiveParent).getXmlParent();
        }

        if (effectiveParent instanceof Feature) {
            try {
                this.parent = (P) ((Feature) effectiveParent).getHolder();
            } catch (ClassCastException e) {
                throw new IllegalStateException("Unexpected parent type '" + effectiveParent.getClass().getName() + "': " + e.getMessage(), e);
            }
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (parentType == null ? 0 : parentType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultChildFeatureHolder) || !super.equals(obj)) {
            return false;
        } else {
            DefaultChildFeatureHolder<?> other = (DefaultChildFeatureHolder<?>) obj;
            return parentType == other.parentType;
        }
    }

}
