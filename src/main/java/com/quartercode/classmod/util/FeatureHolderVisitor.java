/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
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

import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.prop.ValueSupplier;

/**
 * A feature holder visitor is the callback used when visiting a {@link FeatureHolder} tree.
 * Implementations of this interface are primarily used by the {@link TreeWalker} utility class.
 * 
 * @see TreeWalker
 */
public interface FeatureHolderVisitor {

    /**
     * The different return codes {@link FeatureHolderVisitor} callbacks can use to control the behavior of the {@link TreeWalker} they are used by.
     * 
     * @see FeatureHolderVisitor#preVisit(FeatureHolder)
     * @see FeatureHolderVisitor#postVisit(FeatureHolder)
     */
    public static enum VisitResult {

        /**
         * Continue walking the {@link FeatureHolder} tree.
         */
        CONTINUE,
        /**
         * Terminate walking the {@link FeatureHolder} tree immediately.
         * No other method will be called.
         */
        TERMINATE,
        /**
         * Continue without visiting the children of the current {@link FeatureHolder}.
         * This result is only meaningful when it is returned from the {@link FeatureHolderVisitor#preVisit(FeatureHolder)}.
         */
        SKIP_SUBTREE;

    }

    /**
     * Called before the algorithm enters the child {@link FeatureHolder}s, which are stored by the given feature holder through {@link ValueSupplier} {@link Feature}s.
     * This should be the standard method when the exact visiting order is not important.
     * Note that {@link #postVisit(FeatureHolder)} is called directly after this method if the current feature holder is a leaf (has no more children).
     * 
     * @param holder The current feature holder that is visited.
     *        The children of this holder will be visited as soon as this method returns.
     * @return A {@link VisitResult} return code that controls the further behavior of the caller of this method.
     */
    public VisitResult preVisit(FeatureHolder holder);

    /**
     * Called after the algorithm entered and processed the child {@link FeatureHolder}s, which are stored by the given feature holder through {@link ValueSupplier} {@link Feature}s.
     * Note that {@link #preVisit(FeatureHolder)} has been called directly before this method if the current feature holder is a leaf (has no more children).
     * 
     * @param holder The current feature holder that is visited.
     *        The children of this holder have already been visited.
     * @return A {@link VisitResult} return code that controls the further behavior of the caller of this method.
     *         Note that {@link VisitResult#SKIP_SUBTREE} is equivalent to {@link VisitResult#CONTINUE} when returned here.
     */
    public VisitResult postVisit(FeatureHolder holder);

}
