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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Initializable;
import com.quartercode.classmod.extra.ValueSupplier;

/**
 * The tree initializer walks over a tree of {@link FeatureHolder}s and invokes the {@link FeatureHolder#get(FeatureDefinition)} method with predefined {@link FeatureDefinition}s.
 * For each feature holder of a certain type, the mapped feature definitions for that type are put into the method for all existing features.
 * By calling the {@code get()} method, the feature, which is defined by the definition, is initialized.
 * For example, features which implement the {@link Initializable} interface are initialized.
 */
public class TreeInitializer {

    private final Map<Class<? extends FeatureHolder>, Set<FeatureDefinition<?>>> initializationDefinitions = new HashMap<>();

    /**
     * Adds the given {@link FeatureDefinition} mapping for the given {@link FeatureHolder} type.
     * The algorithm will invoke the {@link FeatureHolder#get(FeatureDefinition)} method with the definition every time it encounters a holder of the given type.
     * 
     * @param holderType The type of the feature holder the feature definition belongs to.
     * @param definition The actual feature definition which should be put inside the {@code get()} method.
     */
    public void addInitializationDefinition(Class<? extends FeatureHolder> holderType, FeatureDefinition<?> definition) {

        if (!initializationDefinitions.containsKey(holderType)) {
            initializationDefinitions.put(holderType, new HashSet<FeatureDefinition<?>>());
        }

        initializationDefinitions.get(holderType).add(definition);
    }

    /**
     * Applies the added mappings to the given {@link FeatureHolder} and to all holders which are stored inside {@link ValueSupplier}s of the holder.
     * Actually, the algorithm works recursively and finds all holders on all levels in the tree.
     * See {@link TreeInitializer} for more information on the functionality of this method.
     * 
     * @param root The root feature holder where the algorithm should start.
     */
    public void apply(FeatureHolder root) {

        apply(root, new ArrayList<FeatureHolder>());
    }

    private void apply(FeatureHolder currentHolder, List<FeatureHolder> visitedHolders) {

        if (visitedHolders.contains(currentHolder)) {
            return;
        }

        visitedHolders.add(currentHolder);

        // Initialize all features of the current holder if there are initialization definitions available for the holder type
        if (initializationDefinitions.containsKey(currentHolder.getClass())) {
            initialize(currentHolder);
        }

        // Iterate over the current feature holder's features and look for any ValueSuppliers with child features
        for (Feature feature : currentHolder) {
            if (feature instanceof ValueSupplier) {
                Object value = ((ValueSupplier<?>) feature).get();

                if (value instanceof FeatureHolder) {
                    apply((FeatureHolder) value, visitedHolders);
                } else if (value instanceof List) {
                    for (Object entry : (List<?>) value) {
                        if (entry instanceof FeatureHolder) {
                            apply((FeatureHolder) entry, visitedHolders);
                        }
                    }
                }
            }
        }
    }

    private void initialize(FeatureHolder holder) {

        // Collect the names of all the features which are stored by the holder
        Set<String> featureNames = new HashSet<>();
        for (Feature feature : holder) {
            featureNames.add(feature.getName());
        }

        // Iterate over all initialization definitions for the holder
        for (FeatureDefinition<?> initializationDefinition : initializationDefinitions.get(holder.getClass())) {
            // Ignore definitions which haven't been added to the holder yet
            if (featureNames.contains(initializationDefinition.getName())) {
                // Get the feature of the definition
                // This call also initializes the feature
                holder.get(initializationDefinition);
            }
        }
    }

}
