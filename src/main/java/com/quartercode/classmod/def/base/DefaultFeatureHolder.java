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

package com.quartercode.classmod.def.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Hideable;
import com.quartercode.classmod.base.Initializable;
import com.quartercode.classmod.base.Persistable;

/**
 * A default feature holder is a class which is modifiable through {@link Feature}s.
 * It is just an implementation of {@link FeatureHolder}.
 * A user can get features through the central access method {@link #get(FeatureDefinition)}.
 * Such features are defined by {@link FeatureDefinition} which describe how a feature looks like.
 * 
 * @see FeatureHolder
 * @see Feature
 * @see FeatureDefinition
 */
public class DefaultFeatureHolder implements FeatureHolder {

    private final Map<String, Feature> features         = new HashMap<>();

    // Performance: Cache for all unhidden features in order to make the hashCode() and equals() methods faster
    private final List<Feature>        unhiddenFeatures = new ArrayList<>();

    /*
     * If one of the unchecked casts doesn't succeed, we throw an IllegalArgumentException
     * Sadly, this method needs to perform unchecked casts in order to keep the whole type system consistent.
     */
    @SuppressWarnings ("unchecked")
    @Override
    public <F extends Feature> F get(FeatureDefinition<F> definition) {

        String name = definition.getName();
        F feature = null;

        // Try to retrieve existing feature instance from the local storage (will be null if it doesn't exist)
        feature = (F) features.get(name);

        // Check whether there actually is a feature instance in the local storage
        if (feature == null) {
            // If not, create one and put it in the local storage
            feature = definition.create(this);
            features.put(name, feature);
        }

        // Initialize the feature and update the unhidden features list
        initializeFeatureIfNecessary(definition, feature);
        updateFeatureHiding(feature);

        return feature;
    }

    /*
     * Initializes the feature if it hasn't been done yet.
     */
    @SuppressWarnings ("unchecked")
    private <F extends Feature> void initializeFeatureIfNecessary(FeatureDefinition<F> definition, F feature) {

        if (feature instanceof Initializable && ! ((Initializable<?>) feature).isInitialized()) {
            try {
                ((Initializable<FeatureDefinition<F>>) feature).initialize(definition);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Unknown generics error with feature definition '" + definition.getName() + "' and its created feature (Initializable cast)", e);
            }
        }
    }

    /*
     * Updates the unhiddenFeatures list with the feature.
     */
    private <F extends Feature> void updateFeatureHiding(F feature) {

        boolean presentInUnhiddenList = unhiddenFeatures.contains(feature);

        if (feature instanceof Hideable) {
            boolean hidden = ((Hideable) feature).isHidden();

            if (hidden && presentInUnhiddenList) {
                unhiddenFeatures.remove(feature);
            } else if (!hidden && !presentInUnhiddenList) {
                unhiddenFeatures.add(feature);
            }
        } else if (!presentInUnhiddenList) {
            unhiddenFeatures.add(feature);
        }
    }

    /**
     * Returns a {@link List} of all persistent {@link Feature}s of the default feature holder.
     * Such features implement {@link Persistable} and return {@code true} for {@link Persistable#isPersistent()}.
     * Additions to the returned list are applied back to the feature holder.
     * 
     * @return All persistent features of the default feature holder.
     */
    @XmlAnyElement (lax = true)
    public List<Feature> getPersistentFeatures() {

        return new PersistentFeatureList(this);
    }

    @Override
    public Iterator<Feature> iterator() {

        // The value collection of a HashMap is unmodifiable
        return features.values().iterator();
    }

    /**
     * Returns the unique serialization id for the default feature holder.
     * The id is just the identity hash code ({@link System#identityHashCode(Object)}) of the object as a hexadecimal string.
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
        result = prime * result + (unhiddenFeatures == null ? 0 : unhiddenFeatures.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null || ! (obj instanceof DefaultFeatureHolder)) {
            return false;
        } else {
            DefaultFeatureHolder other = (DefaultFeatureHolder) obj;
            return Objects.equals(unhiddenFeatures, other.unhiddenFeatures);
        }
    }

    @Override
    public String toString() {

        List<String> featureNames = new ArrayList<>();
        for (Feature feature : features.values()) {
            featureNames.add(new StringBuilder(feature.getName()).append(":").append(feature.getClass().getSimpleName()).toString());
        }

        return new ToStringBuilder(this).append("features", featureNames).build();
    }

    // We need to use a custom list because JAXB adds the values using the add() method
    // We won't ever need to serialize this class (it's private)
    @SuppressWarnings ("serial")
    private static class PersistentFeatureList extends ArrayList<Feature> {

        private final DefaultFeatureHolder featureHolder;

        private PersistentFeatureList(DefaultFeatureHolder featureHolder) {

            this.featureHolder = featureHolder;

            // Add all persistent features
            for (Feature feature : featureHolder.features.values()) {
                if (feature instanceof Persistable && ((Persistable) feature).isPersistent()) {
                    // Don't use the overridden add() method because that could have side effects
                    super.add(feature);
                }
            }
        }

        @Override
        public boolean add(Feature feature) {

            featureHolder.features.put(feature.getName(), feature);
            featureHolder.updateFeatureHiding(feature);

            return super.add(feature);
        }

    }

}
