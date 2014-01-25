/*
 * This file is part of Classmod.
 * Copyright (c) 2014 QuarterCode <http://www.quartercode.com/>
 *
 * Classmod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Classmod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Classmod. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.classmod.extra;

import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureHolder;

/**
 * Lockable classes are normal Classmod related classes (like {@link Feature}s, {@link Function}s or even {@link FeatureHolder}s) which can be locked.
 * Locking a lockable object disables all {@link Lockable} {@link FunctionExecutor}s.
 */
public interface LockableClass {

    /**
     * Returns true if the lockable object is actually locked.
     * Locked object can only invoke {@link FunctionExecutor}s which aren't {@link Lockable}.
     * 
     * @return True if the lockable object is actually locked.
     */
    public boolean isLocked();

    /**
     * Locks or unlocks the lockable object.
     * Locked object can only invoke {@link FunctionExecutor}s which aren't {@link Lockable}.
     * Unlocking a locked object can be disabled and may throw an {@link UnlockNotPossibleException}.
     * 
     * @param locked True if the lockable object should be locked after the call, false if not.
     * @throws UnlockNotPossibleException Unlocking is disabled and you tried to unlock the object.
     */
    public void setLocked(boolean locked);

    /**
     * The unlock not possible exception is a simple {@link IllegalStateException}.
     * It is thrown if unlocking a {@link LockableClass} through {@link LockableClass#setLocked(boolean)} isn't possible.
     */
    public static class UnlockNotPossibleException extends IllegalStateException {

        private static final long serialVersionUID = -7670853858306483001L;

        /**
         * Creates a new unlock not possible exception with the given detail message.
         * 
         * @param message A detail message with contains information on the given circumstances.
         */
        public UnlockNotPossibleException(String message) {

            super(message);
        }

    }

}
