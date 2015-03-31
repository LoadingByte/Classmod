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

import com.quartercode.classmod.base.FeatureHolder;

/**
 * A {@link FeatureHolderVisitor} that implements the {@link #postVisit(FeatureHolder)} method by returning {@code CONTINUE}.
 * Therefore, the {@link #preVisit(FeatureHolder)} method is left to be used.
 * 
 * @see FeatureHolderVisitor
 */
public abstract class FeatureHolderVisitorAdapter implements FeatureHolderVisitor {

    @Override
    public VisitResult postVisit(FeatureHolder holder) {

        return VisitResult.CONTINUE;
    }

}
