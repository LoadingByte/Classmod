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

package com.quartercode.classmod.extra;

import java.lang.annotation.Annotation;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.Named;

/**
 * The function executor context wraps around a {@link FunctionExecutor} for storing data values along with the executor.
 * The data isn't stored in the actual {@link FunctionExecutor} object because it should only do the execution and nothing else.
 * 
 * @param <R> The type of the value the stored {@link FunctionExecutor} returns.
 * @see FunctionExecutor
 */
public interface FunctionExecutorContext<R> extends Named, LockableClass {

    /**
     * Returns the name of the {@link FunctionExecutor} which is stored by the context.
     * 
     * @return The name of the stored {@link FunctionExecutor}.
     */
    @Override
    public String getName();

    /**
     * Returns the actual {@link FunctionExecutor} which is stored in the context
     * 
     * @return The stored {@link FunctionExecutor}.
     */
    public FunctionExecutor<R> getExecutor();

    /**
     * Returns a value of the given {@link Annotation} type at the stored {@link FunctionExecutor}.
     * Using this, you can read the values of every annotation at the {@link FunctionExecutor} (for example {@link Limit#value()})
     * 
     * @param type The {@link Annotation} type whose value should be retrieved (could be {@link Limit}).
     * @param name The name of the value stored in the {@link Annotation} which should be retrieved (could be "value" for {@link Limit#value()}).
     * @return The value stored in the defined variable (could be 7 for {@link Limit#value()}).
     */
    public <A extends Annotation> Object getValue(Class<A> type, String name);

    /**
     * Sets a value of the given {@link Annotation} type at the stored {@link FunctionExecutor} to the given one.
     * That allows to modify {@link Annotation} values at runtime.
     * You can compare that with reflection.
     * 
     * @param type The {@link Annotation} type whose value should be changed (could be {@link Limit}).
     * @param name The name of the value stored in the {@link Annotation} which should be changed (could be "value" for {@link Limit#value()}).
     * @param value The new value for the defined variable (could be 7 for {@link Limit#value()}).
     */
    public <A extends Annotation> void setValue(Class<A> type, String name, Object value);

    /**
     * Sets the internal invocation counter for the function executor context to 0.
     * That allows to use {@link FunctionExecutor}s which are already over their {@link Limit}.
     */
    public void resetInvocationCounter();

    /**
     * Returns if the stored {@link FunctionExecutor} is locked.
     * The {@link FunctionExecutor} doesn't need to be {@link Lockable} for this to work.
     * The check algorithm ignores the {@link Lockable} annotation and locks every {@link FunctionExecutor}.
     * 
     * @return True if the stored {@link FunctionExecutor} is locked, false if not.
     */
    @Override
    public boolean isLocked();

    /**
     * Changes if the stored {@link FunctionExecutor} is locked.
     * The {@link FunctionExecutor} doesn't need to be {@link Lockable} for this to work.
     * The check algorithm ignores the {@link Lockable} annotation and locks every {@link FunctionExecutor}.
     * 
     * @param locked True if the stored {@link FunctionExecutor} should be locked, false if not.
     */
    @Override
    public void setLocked(boolean locked);

    /**
     * Invokes the stored {@link FunctionExecutor} inside the given {@link FeatureHolder} with the given arguments.
     * 
     * @param holder The {@link FeatureHolder} the stored {@link FunctionExecutor} is invoked in.
     * @param arguments Some arguments for the stored {@link FunctionExecutor}.
     * @return The value the invoked {@link FunctionExecutor} returns. Can be null.
     * @throws ExecutorInvocationException The stored {@link FunctionExecutor} sends a signal.
     */
    public R invoke(FeatureHolder holder, Object... arguments) throws ExecutorInvocationException;

}
