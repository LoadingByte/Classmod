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

package com.quartercode.classmod.test.extra.storage;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.extra.Storage;
import com.quartercode.classmod.extra.storage.ReferenceStorage;
import com.quartercode.classmod.extra.storage.StandardStorage;

@RunWith (Parameterized.class)
public class DefaultStoragesTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[] { new StandardStorage<>() });
        data.add(new Object[] { new ReferenceStorage<>() });

        return data;
    }

    private final Storage<String> storage;

    public DefaultStoragesTest(Storage<String> storage) {

        this.storage = storage;
    }

    @Test
    public void testGetSet() {

        storage.set("test");
        assertEquals("Value that is stored by the storage", "test", storage.get());
    }

    @Test
    public void testReproduce() {

        storage.set("test");
        Storage<String> newStorage = storage.reproduce();

        assertEquals("Value that is stored by the original storage", "test", storage.get());
        assertEquals("Value that is stored by the new storage", null, newStorage.get());
    }

    @Test
    public void testHashCodeAndEquals() {

        Storage<String> storage2 = storage.reproduce();
        Storage<String> storage3 = storage.reproduce();

        storage.set("test");
        storage2.set("test");
        storage3.set("test2");

        assertTrue("Storage objects with same content don't equal each other", storage.equals(storage2));
        assertEquals("Storage object with same content don't have the same hash code", storage.hashCode(), storage2.hashCode());

        assertFalse("Storage objects with different content equal each other", storage.equals(storage3));
        assertNotEquals("Storage objects with different content have the same hash code", storage.hashCode(), storage3.hashCode());
    }

}
