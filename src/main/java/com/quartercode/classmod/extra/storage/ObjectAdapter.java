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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import com.quartercode.classmod.extra.prop.NonPersistent;

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

    // ----- Utility -----

    private static boolean containsNonPersistentElements(Iterable<?> iterable) {

        for (Object element : iterable) {
            if (element != null && element.getClass().isAnnotationPresent(NonPersistent.class)) {
                return true;
            }
        }

        return false;
    }

    private static int getPersistentOrNullElementCount(Iterable<?> iterable) {

        int count = 0;

        for (Object element : iterable) {
            if (element == null || !element.getClass().isAnnotationPresent(NonPersistent.class)) {
                count++;
            }
        }

        return count;
    }

    // ----- Adapter -----

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

    // ----- Wrappers -----

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

        @XmlElement
        private Class<?> componentType;
        @XmlElement (name = "item", nillable = true)
        private Object[] array;

        private ArrayWrapper() {

        }

        private ArrayWrapper(Object[] array) {

            componentType = array.getClass().getComponentType();

            // Use all items because all items are persistent or null
            if (!containsNonPersistentElements(Arrays.asList(array))) {
                this.array = array;
            }
            // Only use persistent and null items
            else {
                // Retrieve the total number of persistent and null items and create a new array of that size
                this.array = new Object[getPersistentOrNullElementCount(Arrays.asList(array))];

                // Fill the array with the persistent and null items
                int index = 0;
                for (Object item : array) {
                    if (item == null || !item.getClass().isAnnotationPresent(NonPersistent.class)) {
                        this.array[index] = item;
                        index++;
                    }
                }
            }
        }

        @Override
        public Object[] getObject() {

            try {
                Object[] returnArray = (Object[]) Array.newInstance(componentType, array.length);
                System.arraycopy(array, 0, returnArray, 0, returnArray.length);
                return returnArray;
            } catch (Exception e) {
                LOGGER.warn("Cannot create new array instance with component type {} for deserializing array '{}'", componentType, array, e);
                return null;
            }
        }

    }

    @XmlType (name = "collection")
    private static class CollectionWrapper extends Wrapper<Collection<?>> {

        @XmlElement
        private Class<?>      collectionType;
        @XmlElement (name = "item", nillable = true)
        private Collection<?> collection;

        private CollectionWrapper() {

        }

        private CollectionWrapper(Collection<?> collection) {

            // ArrayList is the default collection type; therefore, there is no need for storing it
            if (collection.getClass() != ArrayList.class) {
                collectionType = collection.getClass();
            }

            // Use all elements because all elements are persistent or null
            if (!containsNonPersistentElements(collection)) {
                this.collection = collection;
            }
            // Only use persistent and null elements
            else {
                // Retrieve the total number of persistent and null elements and create a new collection of that size
                Collection<Object> newCollection = new ArrayList<>(getPersistentOrNullElementCount(collection));

                // Fill the collection with the persistent and null elements
                for (Object element : collection) {
                    if (element == null || !element.getClass().isAnnotationPresent(NonPersistent.class)) {
                        newCollection.add(element);
                    }
                }

                this.collection = newCollection;
            }
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
                if (isPersistent(entry.getKey()) && isPersistent(entry.getValue())) {
                    MapEntry entryObject = new MapEntry();
                    entryObject.key = entry.getKey();
                    entryObject.value = entry.getValue();
                    entries.add(entryObject);
                }
            }
        }

        private boolean isPersistent(Object object) {

            return !object.getClass().isAnnotationPresent(NonPersistent.class);
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
