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

package com.quartercode.classmod.test.extra.storage;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.extra.storage.EqualsUtil;

@RunWith (Parameterized.class)
public class EqualsUtilTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[] { null, null, true });
        data.add(new Object[] { 10, null, false });
        data.add(new Object[] { null, 10, false });

        data.add(new Object[] { 10, 10, true });
        data.add(new Object[] { 10, 11, false });
        data.add(new Object[] { 11, 10, false });

        data.add(new Object[] { "string1", "string1", true });
        data.add(new Object[] { "string1", "string2", false });
        data.add(new Object[] { "string2", "string1", false });

        data.add(new Object[] { Arrays.asList("string1", "string2"), Arrays.asList("string1", "string2"), true });
        data.add(new Object[] { Arrays.asList("string1", "string2"), Arrays.asList("string3", "string4"), false });
        data.add(new Object[] { Arrays.asList("string3", "string4"), Arrays.asList("string1", "string2"), false });

        data.add(new Object[] { new String[] { "string1", "string2" }, new String[] { "string1", "string2" }, true });
        data.add(new Object[] { new String[] { "string1", "string2" }, new String[] { "string3", "string4" }, false });
        data.add(new Object[] { new String[] { "string3", "string4" }, new String[] { "string1", "string2" }, false });

        data.add(new Object[] { new String[] { "string1", "string2" }, null, false });
        data.add(new Object[] { new String[] { "string1", "string2" }, 10, false });
        data.add(new Object[] { new String[] { "string1", "string2" }, "string1", false });
        data.add(new Object[] { new String[] { "string1", "string2" }, Arrays.asList("string1", "string2"), false });

        return data;
    }

    private final Object  object1;
    private final Object  object2;
    private final boolean equal;

    public EqualsUtilTest(Object object1, Object object2, boolean equal) {

        this.object1 = object1;
        this.object2 = object2;
        this.equal = equal;
    }

    @Test
    public void testEqualsConsiderArrays() {

        assertEquals("Whether both objects equal each other considering arrays", equal, EqualsUtil.equalsConsiderArrays(object1, object2));
    }

}
