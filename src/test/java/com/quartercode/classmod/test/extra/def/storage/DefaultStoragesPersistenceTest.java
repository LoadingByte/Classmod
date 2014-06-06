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

package com.quartercode.classmod.test.extra.def.storage;

import static org.junit.Assert.assertArrayEquals;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.Storage;
import com.quartercode.classmod.extra.def.storage.ReferenceStorage;
import com.quartercode.classmod.extra.def.storage.StandardStorage;
import com.quartercode.classmod.util.Classmod;

@RunWith (Parameterized.class)
public class DefaultStoragesPersistenceTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[] { new Storage[] { new StandardStorage<>() } });
        data.add(new Object[] { new Storage[] { fillStorage(new StandardStorage<>(), "Test") } });
        data.add(new Object[] { new Storage[] { fillStorage(new StandardStorage<>(), String.class) } });
        data.add(new Object[] { new Storage[] { fillStorage(new StandardStorage<>(), new String[] { "Test1", "Test2", "Test3" }) } });
        data.add(new Object[] { new Storage[] { fillStorage(new StandardStorage<>(), new ArrayList<>(Arrays.asList("Test1", "Test2", "Test3"))) } });

        Map<String, Integer> map = new HashMap<>();
        map.put("somekey1", 12);
        map.put("somekey2", 300);
        data.add(new Object[] { new Storage[] { fillStorage(new StandardStorage<>(), map) } });

        DefaultFeatureHolder referencedObject = new DefaultFeatureHolder();
        data.add(new Object[] { new Storage[] { fillStorage(new ReferenceStorage<>(), referencedObject), fillStorage(new StandardStorage<>(), referencedObject) } });

        return data;
    }

    private static <T> Storage<T> fillStorage(Storage<T> storage, T object) {

        storage.set(object);
        return storage;
    }

    private final Storage<DataObject>[] storages;

    private Marshaller                  marshaller;
    private Unmarshaller                unmarshaller;

    public DefaultStoragesPersistenceTest(Storage<DataObject>[] storages) {

        this.storages = storages;
    }

    @Before
    public void setUp() throws IOException, ClassNotFoundException {

        try {
            JAXBContext context = JAXBContext.newInstance(Classmod.CONTEXT_PATH + ":" + DefaultStoragesPersistenceTest.class.getPackage().getName());
            marshaller = context.createMarshaller();
            // Only enable for debugging
            // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings ("unchecked")
    @Test
    public void test() throws JAXBException {

        StorageContainer<DataObject> storageContainer = new StorageContainer<>(storages);

        StringWriter serialized = new StringWriter();
        marshaller.marshal(storageContainer, serialized);

        StorageContainer<DataObject> copy = (StorageContainer<DataObject>) unmarshaller.unmarshal(new StringReader(serialized.toString()));
        assertArrayEquals("Serialized-deserialized copy of the storages", storageContainer.getStorages(), copy.getStorages());
    }

    @XmlRootElement
    private static class StorageContainer<T> {

        @XmlElement (name = "storage")
        private Storage<T>[] storages;

        // Default constructor for JAXB
        private StorageContainer() {

        }

        private StorageContainer(Storage<T>[] storages) {

            this.storages = storages;
        }

        private Storage<T>[] getStorages() {

            return storages;
        }

    }

    private static class DataObject {

        @XmlElement
        private final int    value1;
        @XmlElement
        private final String value2;

        private DataObject(int value1, String value2) {

            this.value1 = value1;
            this.value2 = value2;
        }

        @Override
        public int hashCode() {

            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {

            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public String toString() {

            return ToStringBuilder.reflectionToString(this);
        }

    }

}
