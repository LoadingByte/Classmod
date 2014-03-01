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
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;

/**
 * A child feature holder is a {@link FeatureHolder} which stores its parent {@link FeatureHolder}.
 * It uses the {@link DefaultFeatureHolder} implementation.
 * A user can get {@link Feature}s through the central access method {@link #get(FeatureDefinition)}.
 * Such {@link Feature}s are defined by {@link FeatureDefinition} which describe how a feature looks like.
 * 
 * @param <P> The type the parent {@link FeatureHolder} has to have.
 * @see FeatureHolder
 * @see Feature
 * @see FeatureDefinition
 */
public class DefaultChildFeatureHolder<P extends FeatureHolder> extends DefaultFeatureHolder implements ChildFeatureHolder<P> {

    private P parent;

    /**
     * Creates a new default child feature holder.
     */
    public DefaultChildFeatureHolder() {

    }

    @Override
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

        if (parent instanceof Feature) {
            try {
                this.parent = (P) ((Feature) parent).getHolder();
            } catch (ClassCastException e) {
                throw new IllegalStateException("Unexpected parent type '" + parent.getClass().getName() + "': " + e.getMessage(), e);
            }
        }
    }

    // Don't override hashCode() and equals() because we don't want to check for the parent (that would create a cycle)

}
