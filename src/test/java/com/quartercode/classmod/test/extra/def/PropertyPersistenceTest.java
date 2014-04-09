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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.Property;
import com.quartercode.classmod.extra.def.ObjectProperty;
import com.quartercode.classmod.extra.def.ReferenceProperty;
import com.quartercode.classmod.util.Classmod;

@RunWith (Parameterized.class)
public class PropertyPersistenceTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[] { new FeatureDefinition[] { ObjectProperty.createDefinition("property") } });
        data.add(new Object[] { new FeatureDefinition[] { ObjectProperty.createDefinition("property", "Test") } });
        data.add(new Object[] { new FeatureDefinition[] { ObjectProperty.createDefinition("property", String.class) } });
        data.add(new Object[] { new FeatureDefinition[] { ObjectProperty.createDefinition("property", Arrays.asList("Test1", "Test2", "Test3")) } });

        DefaultFeatureHolder referencedObject = new DefaultFeatureHolder();
        data.add(new Object[] { new FeatureDefinition[] { ObjectProperty.createDefinition("property", referencedObject), ReferenceProperty.createDefinition("reference", referencedObject) } });

        return data;
    }

    private final DefaultFeatureHolder featureHolder;

    private Marshaller                 marshaller;
    private Unmarshaller               unmarshaller;

    public PropertyPersistenceTest(FeatureDefinition<Property<?>>[] propertyDefinitions) {

        featureHolder = new DefaultFeatureHolder();

        // Add properties
        for (FeatureDefinition<Property<?>> propertyDefinition : propertyDefinitions) {
            featureHolder.get(propertyDefinition);
        }
    }

    @Before
    public void setUp() throws IOException, ClassNotFoundException {

        try {
            JAXBContext context = JAXBContext.newInstance(Classmod.CONTEXT_PATH + ":com.quartercode.classmod.test.extra.def");
            marshaller = context.createMarshaller();
            // Enable only for debugging
            // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testObjectProperty() throws JAXBException {

        RootElement root = new RootElement(featureHolder);

        StringWriter serialized = new StringWriter();
        marshaller.marshal(root, serialized);

        RootElement copy = (RootElement) unmarshaller.unmarshal(new StringReader(serialized.toString()));
        Assert.assertEquals("Serialized-deserialized copy of the feature holder", root.featureHolder, copy.featureHolder);
    }

    @XmlRootElement (namespace = "http://quartercode.com")
    private static class RootElement {

        @XmlElement
        private DefaultFeatureHolder featureHolder;

        // Default constructor for JAXB
        private RootElement() {

        }

        private RootElement(DefaultFeatureHolder featureHolder) {

            this.featureHolder = featureHolder;
        }

    }

}
