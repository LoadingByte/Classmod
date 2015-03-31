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

package com.quartercode.classmod.extra.conv;

import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.ChildFeatureHolder;

/**
 * A convenient child feature holder is a combination of the {@link ChildFeatureHolder} and {@link CFeatureHolder} interfaces.
 * See those two interfaces for more information on the two feature holder variants.
 * 
 * @see ChildFeatureHolder
 * @see CFeatureHolder
 */
public interface CChildFeatureHolder<P extends FeatureHolder> extends ChildFeatureHolder<P>, CFeatureHolder {

}
