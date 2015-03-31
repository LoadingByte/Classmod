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

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.extra.storage.ReferenceCollectionStorage;
import com.quartercode.classmod.extra.storage.ReferenceStorage;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.classmod.extra.storage.Storage;

@RunWith (Parameterized.class)
public class DefaultStoragesTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[] { new StandardStorage<>(), "test1", "test2" });
        data.add(new Object[] { new ReferenceStorage<>(), "test1", "test2" });
        data.add(new Object[] { new ReferenceCollectionStorage<>(), new ArrayList<>(Arrays.asList("test1", "test2")), new ArrayList<>(Arrays.asList("test3", "test4")) });

        return data;
    }

    private final Storage<Object> storage;
    private final Object          content1;
    private final Object          content2;

    public DefaultStoragesTest(Storage<Object> storage, Object content1, Object content2) {

        this.storage = storage;
        this.content1 = ObjectUtils.cloneIfPossible(content1);
        this.content2 = ObjectUtils.cloneIfPossible(content2);
    }

    @Test
    public void testGetSet() {

        storage.set(content1);
        assertEquals("Value that is stored by the storage", content1, storage.get());
    }

    @Test
    public void testReproduce() {

        storage.set(content1);
        Storage<Object> newStorage = storage.reproduce();

        assertEquals("Value that is stored by the original storage", content1, storage.get());
        assertEquals("Value that is stored by the new storage", null, newStorage.get());
    }

    @Test
    public void testHashCodeAndEquals() {

        Storage<Object> storage2 = storage.reproduce();
        Storage<Object> storage3 = storage.reproduce();

        storage.set(content1);
        storage2.set(content1);
        storage3.set(content2);

        assertTrue("Storage objects with same content don't equal each other", storage.equals(storage2));
        assertEquals("Storage object with same content don't have the same hash code", storage.hashCode(), storage2.hashCode());

        assertFalse("Storage objects with different content equal each other", storage.equals(storage3));
        assertNotEquals("Storage objects with different content have the same hash code", storage.hashCode(), storage3.hashCode());
    }

}
