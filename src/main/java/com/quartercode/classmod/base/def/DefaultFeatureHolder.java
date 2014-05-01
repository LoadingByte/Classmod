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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Initializable;
import com.quartercode.classmod.base.Persistent;

/**
 * A default feature holder is a class which is modifiable through {@link Feature}s.
 * It is just an implementation of {@link FeatureHolder}.
 * A user can get {@link Feature}s through the central access method {@link #get(FeatureDefinition)}.
 * Such {@link Feature}s are defined by {@link FeatureDefinition} which describe how a feature looks like.
 * 
 * @see FeatureHolder
 * @see Feature
 * @see FeatureDefinition
 */
public class DefaultFeatureHolder implements FeatureHolder {

    private final List<Feature> features = new ArrayList<>();

    /**
     * Creates a new default feature holder.
     */
    public DefaultFeatureHolder() {

    }

    @Override
    /*
     * If one of the unchecked casts doesn't succeed, we throw an IllegalArgumentException
     * Sadly, this method needs to perform unchecked casts in order to keep the whole type system consistent.
     */
    @SuppressWarnings ("unchecked")
    public <F extends Feature> F get(FeatureDefinition<F> definition) {

        F feature = null;

        // Retrieve existing feature instance from the local storage
        for (Feature availableFeature : features) {
            if (availableFeature.getName().equals(definition.getName())) {
                try {
                    feature = (F) availableFeature;
                    break;
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Generic type argument of feature definition '" + definition.getName() + "' doesn't match existing feature", e);
                }
            }
        }

        // Check whether there actually is a feature instance in the local storage
        if (feature == null) {
            // If not, create one and put it in the local storage
            feature = definition.create(this);
            features.add(feature);
        }

        // Initialize the feature if it hasn't been done yet
        if (feature instanceof Initializable && ! ((Initializable<?>) feature).isInitialized()) {
            try {
                ((Initializable<FeatureDefinition<F>>) feature).initialize(definition);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Unknown generics error with feature definition '" + definition.getName() + "' and its created feature (Initializable cast)", e);
            }
        }

        return feature;
    }

    /**
     * Returns a {@link List} of all {@link Persistent} {@link Feature}s of the default feature holder.
     * Additions to the returned {@link List} are applied back to the feature holder.
     * 
     * @return All {@link Persistent} {@link Feature}s of the default feature holder.
     */
    @XmlAnyElement (lax = true)
    public List<Feature> getPersistentFeatures() {

        return new PersistentFeatureList(this);
    }

    @Override
    public Iterator<Feature> iterator() {

        return Collections.unmodifiableList(features).iterator();
    }

    /**
     * Returns the unique serialization id for the default feature holder.
     * The id is just the identy hash code ({@link System#identityHashCode(Object)}) of the object as a hexadecimal string.
     * 
     * @return The unique serialization id for the default feature holder.
     */
    @XmlAttribute
    @XmlID
    public String getId() {

        return Integer.toHexString(System.identityHashCode(this));
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (features == null ? 0 : features.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DefaultFeatureHolder other = (DefaultFeatureHolder) obj;
        if (features == null) {
            if (other.features != null) {
                return false;
            }
        } else if (!features.equals(other.features)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        StringBuilder featureString = new StringBuilder();
        for (Feature feature : features) {
            featureString.append(", ").append(feature.getName());
        }

        return getClass().getName() + " [features={" + (featureString.length() == 0 ? "" : featureString.substring(2)) + "}]";
    }

    // We need to use a custom list because JAXB adds the values using the add() method
    // We won't ever need to serialize this class (it's private)
    @SuppressWarnings ("serial")
    private static class PersistentFeatureList extends ArrayList<Feature> {

        private final DefaultFeatureHolder featureHolder;

        private PersistentFeatureList(DefaultFeatureHolder featureHolder) {

            this.featureHolder = featureHolder;

            // Add all persistent features
            for (Feature feature : featureHolder.features) {
                if (feature.getClass().isAnnotationPresent(Persistent.class)) {
                    // Don't use the overriden add() because that could have side-effects
                    super.add(feature);
                }
            }
        }

        @Override
        public boolean add(Feature feature) {

            featureHolder.features.add(feature);
            return super.add(feature);
        }

    }

}
