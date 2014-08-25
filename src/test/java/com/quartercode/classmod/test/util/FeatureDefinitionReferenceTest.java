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

package com.quartercode.classmod.test.util;

import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.base.def.AbstractFeatureDefinition;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.util.FeatureDefinitionReference;

@RunWith (Parameterized.class)
public class FeatureDefinitionReferenceTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[] { TestFeatureHolder.FD, "FD" });
        data.add(new Object[] { TestFeatureHolder.OTHER_FD, "OTHER_FD" });
        data.add(new Object[] { TestFeatureHolder.ANOTHER_FD, "ANOTHER_FD" });
        data.add(new Object[] { TestFeatureHolder.FEATURE_DEFINITION_3, "FEATURE_DEFINITION_3" });
        data.add(new Object[] { TestFeatureHolder.FEATURE_DEFINITION_456, "FEATURE_DEFINITION_456" });

        return data;
    }

    private final FeatureDefinition<Feature> definition;
    private final String                     definitionFieldName;

    public FeatureDefinitionReferenceTest(FeatureDefinition<Feature> definition, String definitionFieldName) {

        this.definition = definition;
        this.definitionFieldName = definitionFieldName;
    }

    @Test
    public void testClassAndNameConstructor() {

        FeatureDefinitionReference<FeatureDefinition<Feature>> reference = new FeatureDefinitionReference<>(TestFeatureHolder.class, definitionFieldName);

        assertEquals("Feature definition object retrieved from the reference", definition, reference.getDefinition());
    }

    @Test
    public void testClassAndObjectConstructor() {

        FeatureDefinitionReference<FeatureDefinition<Feature>> reference = new FeatureDefinitionReference<>(TestFeatureHolder.class, definition);

        assertEquals("Feature definition object retrieved from the reference", definition, reference.getDefinition());
    }

    @Test
    public void testObjectSerialization() {

        FeatureDefinitionReference<FeatureDefinition<Feature>> reference = new FeatureDefinitionReference<>(TestFeatureHolder.class, definitionFieldName);
        FeatureDefinitionReference<FeatureDefinition<Feature>> referenceCopy = SerializationUtils.roundtrip(reference);

        assertEquals("Feature definition object retrieved from the serialized and deserialized reference", definition, referenceCopy.getDefinition());
    }

    @Test
    public void testXmlSerialization() {

        FeatureDefinitionReference<FeatureDefinition<Feature>> reference = new FeatureDefinitionReference<>(TestFeatureHolder.class, definitionFieldName);

        StringWriter serialized = new StringWriter();
        JAXB.marshal(reference, serialized);
        FeatureDefinitionReference<?> referenceCopy = JAXB.unmarshal(new StringReader(serialized.toString()), FeatureDefinitionReference.class);

        assertEquals("Feature definition object retrieved from the serialized and deserialized reference", definition, referenceCopy.getDefinition());
    }

    @XmlRootElement
    private static class TestFeatureHolder extends DefaultFeatureHolder {

        public static final FeatureDefinition<Feature> FD                     = new DummyFeatureDefinition("fd");
        public static final FeatureDefinition<Feature> OTHER_FD               = new DummyFeatureDefinition("otherFd");
        public static final FeatureDefinition<Feature> ANOTHER_FD             = new DummyFeatureDefinition("anotherFD");
        public static final FeatureDefinition<Feature> FEATURE_DEFINITION_3   = new DummyFeatureDefinition("featureDefinition3");
        public static final FeatureDefinition<Feature> FEATURE_DEFINITION_456 = new DummyFeatureDefinition("featureDefinition456");

    }

    private static class DummyFeatureDefinition extends AbstractFeatureDefinition<Feature> {

        public DummyFeatureDefinition(String name) {

            super(name);
        }

        @Override
        public Feature create(FeatureHolder holder) {

            return null;
        }

    }

}
