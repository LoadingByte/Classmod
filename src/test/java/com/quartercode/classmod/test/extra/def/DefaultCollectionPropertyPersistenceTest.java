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

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.def.DefaultCollectionProperty;
import com.quartercode.classmod.util.Classmod;

public class DefaultCollectionPropertyPersistenceTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private FeatureHolder   featureHolder;

    private Marshaller      marshaller;
    private Unmarshaller    unmarshaller;

    @SuppressWarnings ("unchecked")
    private <E, C extends Collection<E>> void initializeCollectionProperty(CollectionProperty<E, C> property) {

        final CollectionPropertyDefinition<E, C> definition = context.mock(CollectionPropertyDefinition.class, property.getName() + "Definition");

        // @formatter:off
        context.checking(new Expectations() {{

            allowing(definition).newCollection();
                will(returnValue(new ArrayList<>()));

            allowing(definition).isIgnoreEquals();
                will(returnValue(false));

            allowing(definition).getGetterExecutorsForVariant(with(any(Class.class)));
                will(returnValue(new HashMap<>()));
            allowing(definition).getAdderExecutorsForVariant(with(any(Class.class)));
                will(returnValue(new HashMap<>()));
            allowing(definition).getRemoverExecutorsForVariant(with(any(Class.class)));
                will(returnValue(new HashMap<>()));

        }});
        // @formatter:on

        property.initialize(definition);
    }

    @Before
    public void setUp() throws IOException, ClassNotFoundException {

        try {
            JAXBContext context = JAXBContext.newInstance(Classmod.CONTEXT_PATH + ":" + DefaultCollectionPropertyPersistenceTest.class.getPackage().getName());
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

        DummyStorage<List<String>> storage = new DummyStorage<>();
        DefaultCollectionProperty<String, List<String>> collectionProperty = new DefaultCollectionProperty<>("collectionProperty", featureHolder, storage);
        initializeCollectionProperty(collectionProperty);

        collectionProperty.add("teststring1");
        collectionProperty.add("teststring2");

        StringWriter serialized = new StringWriter();
        marshaller.marshal(collectionProperty, serialized);

        DefaultCollectionProperty<String, List<String>> copy = (DefaultCollectionProperty<String, List<String>>) unmarshaller.unmarshal(new StringReader(serialized.toString()));
        assertEquals("Serialized-deserialized copy of the collection property", collectionProperty, copy);
    }

}
