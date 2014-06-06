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

import com.quartercode.classmod.extra.Storage;

public class StorageWrapper<T> extends Storage<T> {

    private final StorageInterface<T> storageInterface;

    public StorageWrapper(StorageInterface<T> storageInterface) {

        this.storageInterface = storageInterface;
    }

    @Override
    public T get() {

        return storageInterface.get();
    }

    @Override
    public void set(T object) {

        storageInterface.set(object);
    }

    @Override
    public Storage<T> reproduce() {

        return storageInterface.reproduce();
    }

    public static interface StorageInterface<T> {

        public T get();

        public void set(T object);

        public Storage<T> reproduce();

    }

}
