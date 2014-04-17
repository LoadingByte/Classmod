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

package com.quartercode.classmod.base;

/**
 * Initializable {@link Feature}s provide an initialiaztion method that is called the first time they are retrieved through a {@link FeatureHolder}.
 * The {@link #initialize(FeatureDefinition)} method expects the {@link FeatureDefinition} the initializable feature has been created by.
 * Moreover, there is an {@link #isInitialized()} method that returns whether the initialize method has already been called.<br>
 * <br>
 * The difference between the initialize method and the constructor has to do with custom definitions and persistence.
 * Say you have a persistent {@link Feature} F. F has some some not-persistent attributes that are stored in its {@link FeatureDefinition}.
 * These attributes need to be transfered into every new instance of F.
 * That problem could be solved with a custom constructor F's {@link FeatureDefinition} uses.
 * However, when F is saved and loaded again (persistence), the loading algorithm uses a default no-arg constructor.
 * So how do you transfer the non-persistent attributes from the custom {@link FeatureDefinition} again?<br>
 * <br>
 * The initialize method is called with the custom {@link FeatureDefinition} every time F is requested and {@link #isInitialized()} is false.
 * If you set the <code>initialized</code> variable to non-persistent, the method is invoked the first time F is requested in every new "session".
 * That way, you can work around the custom constructor problem, as well as have a nicer design.
 * 
 * @param <D> The type of the {@link FeatureDefinition} the implementing {@link Feature} is using; used in {@link #initialize(FeatureDefinition)}.
 */
public interface Initializable<D extends FeatureDefinition<?>> extends Feature {

    /**
     * Initializes the {@link Feature} with the contents of the given {@link FeatureDefinition}.<br>
     * See the {@link Initializable} docs for more details.
     * 
     * @param definition The feature definition the implementing feature is retrieving the intialization data from.
     */
    public void initialize(D definition);

    /**
     * Returns whether the {@link #initialize(FeatureDefinition)} method has already been called.
     * Whether "already been called" means the current "session" or the persistent lifetime of the feature is up to the implementation.<br>
     * See the {@link Initializable} docs for more details.
     * 
     * @return True if the {@link #initialize(FeatureDefinition)} method has already been called, false if not.
     */
    public boolean isInitialized();

}
