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

package com.quartercode.classmod.test.extra.def;

import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;

class DummyFunctionExecutor<R> implements FunctionExecutor<R> {

    private final FunctionExecutor<R> dummy;
    private final boolean             useReturnValue;

    public DummyFunctionExecutor(FunctionExecutor<R> dummy, boolean useReturnValue) {

        this.dummy = dummy;
        this.useReturnValue = useReturnValue;
    }

    @Override
    public R invoke(FunctionInvocation<R> invocation, Object... arguments) {

        R dummyReturnValue = dummy.invoke(invocation, arguments);
        R nextReturnValue = invocation.next(arguments);

        return useReturnValue ? dummyReturnValue : nextReturnValue;
    }

}
