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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.classmod.Classmod;
import com.quartercode.classmod.extra.prop.NonPersistent;
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
    private <T> Storage<T> roundtrip(Storage<T> storage) throws JAXBException {

        StorageContainer container = new StorageContainer(new Storage[] { storage });

        StringWriter serialized = new StringWriter();
        marshaller.marshal(container, serialized);

        StorageContainer containerCopy = (StorageContainer) unmarshaller.unmarshal(new StringReader(serialized.toString()));
        return (Storage<T>) containerCopy.getStorages()[0];
    }

    @Test
    public void testArray() throws JAXBException {

        StandardStorage<Object[]> storage = new StandardStorage<>();
        storage.set(new Object[5]);

        storage.get()[0] = "teststring1";
        storage.get()[1] = new NonPersistentDataObject("teststring2");
        storage.get()[2] = null;
        storage.get()[3] = "teststring3";
        storage.get()[4] = new NonPersistentDataObject("teststring4");

        Storage<Object[]> copy = roundtrip(storage);

        assertArrayEquals("Serialized-deserialized copy of StandardStorage contains non-persistent elements", new Object[] { "teststring1", null, "teststring3" }, copy.get());
    }

    @Test
    public void testCollection() throws JAXBException {

        StandardStorage<List<Object>> storage = new StandardStorage<>();
        storage.set(new ArrayList<>());

        storage.get().add("teststring1");
        storage.get().add(new NonPersistentDataObject("teststring2"));
        storage.get().add(null);
        storage.get().add("teststring3");
        storage.get().add(new NonPersistentDataObject("teststring4"));

        Storage<List<Object>> copy = roundtrip(storage);

        assertListEquals("Serialized-deserialized copy of StandardStorage contains non-persistent elements", copy.get(), "teststring1", null, "teststring3");
    }

    @NonPersistent
    private static class NonPersistentDataObject {

        @XmlElement
        private String value;

        // Default constructor for JAXB
        private NonPersistentDataObject() {

        }

        private NonPersistentDataObject(String value) {

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

}
