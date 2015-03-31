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

import static com.quartercode.classmod.test.ExtraAssert.assertListEquals;
import static org.junit.Assert.assertArrayEquals;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.Classmod;
import com.quartercode.classmod.extra.prop.NonPersistent;
import com.quartercode.classmod.extra.storage.ReferenceCollectionStorage;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.classmod.extra.storage.Storage;

public class StandardStoragePartialCollectionPersistenceTest {

    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;

    @Before
    public void setUp() throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Classmod.CONTEXT_PATH + ":" + getClass().getPackage().getName());
        marshaller = context.createMarshaller();
        // Only enable for debugging
        // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshaller = context.createUnmarshaller();
    }

    @SuppressWarnings ("unchecked")
    @SafeVarargs
    private final <T> Storage<T>[] roundtrip(Storage<T>... storages) throws JAXBException {

        StorageContainer container = new StorageContainer(storages);

        StringWriter serialized = new StringWriter();
        marshaller.marshal(container, serialized);

        StorageContainer containerCopy = (StorageContainer) unmarshaller.unmarshal(new StringReader(serialized.toString()));

        // Copy "roundtriped" storages into new array
        Storage<T>[] storagesCopy = storages.clone();
        for (int index = 0; index < storagesCopy.length; index++) {
            storagesCopy[index] = (Storage<T>) containerCopy.getStorages()[index];
        }

        return storagesCopy;
    }

    @Test
    public void testArray() throws JAXBException {

        StandardStorage<Object[]> storage = new StandardStorage<>();
        storage.set(new Object[5]);

        storage.get()[0] = new PersistentDataObject("teststring1");
        storage.get()[1] = new NonPersistentDataObject("teststring2");
        storage.get()[2] = null;
        storage.get()[3] = new PersistentDataObject("teststring3");
        storage.get()[4] = new NonPersistentDataObject("teststring4");

        Storage<Object[]>[] copy = roundtrip(storage);

        assertArrayEquals("Serialized-deserialized copy of StandardStorage contains non-persistent elements", new Object[] { new PersistentDataObject("teststring1"), null, new PersistentDataObject("teststring3") }, copy[0].get());
    }

    @Test
    public void testCollection() throws JAXBException {

        StandardStorage<List<Object>> storage = new StandardStorage<>();
        storage.set(new ArrayList<>());

        storage.get().add(new PersistentDataObject("teststring1"));
        storage.get().add(new NonPersistentDataObject("teststring2"));
        storage.get().add(null);
        storage.get().add(new PersistentDataObject("teststring3"));
        storage.get().add(new NonPersistentDataObject("teststring4"));

        Storage<List<Object>>[] copy = roundtrip(storage);

        assertListEquals("Serialized-deserialized copy of StandardStorage contains non-persistent elements", copy[0].get(), new PersistentDataObject("teststring1"), null, new PersistentDataObject("teststring3"));
    }

    @Test
    public void testCollectionWithNonPersistentReferences() throws JAXBException {

        StandardStorage<List<Object>> mainStorage = new StandardStorage<>();
        mainStorage.set(new ArrayList<>());

        mainStorage.get().add(new PersistentDataObject("teststring1"));
        mainStorage.get().add(new NonPersistentDataObject("teststring2"));
        mainStorage.get().add(new PersistentDataObject("teststring3"));
        mainStorage.get().add(new NonPersistentDataObject("teststring4"));

        ReferenceCollectionStorage<List<Object>> refStorage = new ReferenceCollectionStorage<>();
        refStorage.set(new ArrayList<>());

        refStorage.get().add(new PersistentDataObject("teststring1"));
        refStorage.get().add(new NonPersistentDataObject("teststring2"));
        refStorage.get().add(new PersistentDataObject("teststring3"));
        refStorage.get().add(new NonPersistentDataObject("teststring4"));

        Storage<List<Object>>[] copy = roundtrip(mainStorage, refStorage);

        assertListEquals("Serialized-deserialized copy of main StandardStorage contains non-persistent elements", copy[0].get(), new PersistentDataObject("teststring1"), new PersistentDataObject("teststring3"));
        assertListEquals("Serialized-deserialized copy of ReferenceCollectionStorage contains non-persistent elements", copy[1].get(), new PersistentDataObject("teststring1"), new PersistentDataObject("teststring3"));
    }

    private static class PersistentDataObject {

        @XmlElement
        @XmlID
        private String value;

        // Default constructor for JAXB
        private PersistentDataObject() {

        }

        private PersistentDataObject(String value) {

            this.value = value;
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

    @NonPersistent
    private static class NonPersistentDataObject extends PersistentDataObject {

        // Default constructor for JAXB
        private NonPersistentDataObject() {

        }

        private NonPersistentDataObject(String value) {

            super(value);
        }

    }

}
