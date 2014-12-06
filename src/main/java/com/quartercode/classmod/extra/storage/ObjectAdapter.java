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

package com.quartercode.classmod.extra.storage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.classmod.extra.XmlPassthroughElement;

/**
 * The object adapter is used for mapping an {@link Object} field.
 * There are some bugs in JAXB the adapter works around.<br>
 * <br>
 * Note that this way of getting around the limitations of JAXB is really hacky, but currently there is no other solution.
 * Also note that you must add the following classes to the context path of your jaxb context:
 * 
 * <ul>
 * <li>{@link ObjectAdapter.ClassWrapper}</li>
 * <li>{@link ObjectAdapter.ArrayWrapper}</li>
 * <li>{@link ObjectAdapter.CollectionWrapper}</li>
 * <li>{@link ObjectAdapter.MapWrapper}</li>
 * </ul>
 */
class ObjectAdapter extends XmlAdapter<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectAdapter.class);

    @Override
    public Object unmarshal(Object v) {

        if (v instanceof Wrapper) {
            return ((Wrapper<?>) v).getObject();
        } else {
            return v;
        }
    }

    @Override
    public Object marshal(Object v) {

        if (v == null) {
            return null;
        } else if (v instanceof Class) {
            return new ClassWrapper((Class<?>) v);
        } else if (v.getClass().isArray()) {
            return new ArrayWrapper((Object[]) v);
        } else if (v instanceof Collection) {
            return new CollectionWrapper((Collection<?>) v);
        } else if (v instanceof Map) {
            return new MapWrapper((Map<?, ?>) v);
        } else {
            return v;
        }
    }

    @XmlTransient
    private static abstract class Wrapper<T> implements XmlPassthroughElement {

        private Object xmlParent;

        @Override
        public Object getXmlParent() {

            return xmlParent;
        }

        @SuppressWarnings ("unused")
        protected void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {

            xmlParent = parent;
        }

        abstract T getObject();

    }

    @XmlType (name = "class")
    private static class ClassWrapper extends Wrapper<Class<?>> {

        @XmlValue
        private Class<?> object;

        private ClassWrapper() {

        }

        private ClassWrapper(Class<?> object) {

            this.object = object;
        }

        @Override
        public Class<?> getObject() {

            return object;
        }

    }

    @XmlType (name = "array")
    private static class ArrayWrapper extends Wrapper<Object[]> {

        @XmlElement (name = "item")
        private Object[] object;
        @XmlElement
        private Class<?> componentType;

        private ArrayWrapper() {

        }

        private ArrayWrapper(Object[] object) {

            this.object = object;
            componentType = object.getClass().getComponentType();
        }

        @Override
        public Object[] getObject() {

            try {
                Object[] array = (Object[]) Array.newInstance(componentType, object.length);
                System.arraycopy(object, 0, array, 0, object.length);
                return array;
            } catch (Exception e) {
                LOGGER.warn("Cannot copy array {} to new instance with component type {}", object, componentType, e);
                return null;
            }
        }

    }

    @XmlType (name = "collection")
    private static class CollectionWrapper extends Wrapper<Collection<?>> {

        @XmlElement
        private Class<?>      collectionType;
        @XmlElement (name = "item")
        private Collection<?> collection;

        private CollectionWrapper() {

        }

        private CollectionWrapper(Collection<?> collection) {

            // ArrayList is the default collection type; therefore, there is no need for storing it
            if (collection.getClass() != ArrayList.class) {
                collectionType = collection.getClass();
            }

            this.collection = collection;
        }

        @Override
        public Collection<?> getObject() {

            // Consider the default collection type (ArrayList) if no specific collection type is set and and ArrayList has been unmarshalled by JAXB
            if (collectionType == null && collection.getClass() == ArrayList.class) {
                return collection;
            }
            // Otherwise, create a new collection of the specified type and copy all elements into it
            else {
                Class<?> effectiveCollectionType = collectionType == null ? ArrayList.class : collectionType;

                try {
                    @SuppressWarnings ("unchecked")
                    Collection<Object> returnCollection = (Collection<Object>) effectiveCollectionType.newInstance();
                    returnCollection.addAll(collection);
                    return returnCollection;
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.warn("Cannot instantiate collection type '{}'", effectiveCollectionType.getName(), e);
                    return null;
                }
            }
        }

    }

    @XmlType (name = "map")
    private static class MapWrapper extends Wrapper<Map<?, ?>> {

        @XmlElement
        private Class<?>       mapType;
        @XmlElement (name = "entry")
        private List<MapEntry> entries;

        private MapWrapper() {

        }

        private MapWrapper(Map<?, ?> map) {

            // HashMap is the default map type; therefore, there is no need for storing it
            if (map.getClass() != HashMap.class) {
                mapType = map.getClass();
            }

            entries = new ArrayList<>();
            for (Entry<?, ?> entry : map.entrySet()) {
                MapEntry entryObject = new MapEntry();
                entryObject.key = entry.getKey();
                entryObject.value = entry.getValue();
                entries.add(entryObject);
            }
        }

        @Override
        public Map<?, ?> getObject() {

            Class<?> effectiveMapType = mapType == null ? HashMap.class : mapType;

            // Create a new map with the stored entries
            try {
                @SuppressWarnings ("unchecked")
                Map<Object, Object> returnMap = (Map<Object, Object>) effectiveMapType.newInstance();
                for (MapEntry entry : entries) {
                    returnMap.put(entry.key, entry.value);
                }
                return returnMap;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.warn("Cannot instantiate map type '{}'", effectiveMapType.getName(), e);
                return null;
            }
        }

        private static class MapEntry {

            @XmlElement
            private Object key;
            @XmlElement
            private Object value;

        }

    }

}
