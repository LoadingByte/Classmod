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

package com.quartercode.classmod.extra.def;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The object adapter is used for mapping an {@link Object} field.
 * There are some bugs in JAXB the adapter works around.
 * 
 * Note: Of course, this way of doing things is really hacky, but there's currently no other solution.
 * Also note that you must add the {@link ClassWrapper} class to the "classpath" of your {@link JAXBContext}.
 */
public class ObjectAdapter extends XmlAdapter<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectAdapter.class);

    /**
     * Creates a new object adapter.
     */
    public ObjectAdapter() {

    }

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

    public static interface Wrapper<T> {

        public T getObject();

    }

    @XmlType (name = "class")
    public static class ClassWrapper implements Wrapper<Class<?>> {

        @XmlValue
        private Class<?> object;

        protected ClassWrapper() {

        }

        public ClassWrapper(Class<?> object) {

            this.object = object;
        }

        @Override
        public Class<?> getObject() {

            return object;
        }

    }

    @XmlType (name = "array")
    public static class ArrayWrapper implements Wrapper<Object[]> {

        @XmlElement (name = "item")
        private Object[] object;
        @XmlElement
        private Class<?> componentType;

        protected ArrayWrapper() {

        }

        public ArrayWrapper(Object[] object) {

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
    public static class CollectionWrapper implements Wrapper<Collection<?>> {

        @XmlElement (name = "item")
        private Collection<?> object;

        protected CollectionWrapper() {

        }

        public CollectionWrapper(Collection<?> object) {

            this.object = object;
        }

        @Override
        public Collection<?> getObject() {

            return object;
        }

    }

    @XmlType (name = "map")
    public static class MapWrapper implements Wrapper<Map<?, ?>> {

        @XmlElement
        private Class<?>       mapType;
        @XmlElement (name = "entry")
        private List<MapEntry> entries;

        protected MapWrapper() {

        }

        public MapWrapper(Map<?, ?> object) {

            mapType = object.getClass();

            entries = new ArrayList<>();
            for (Entry<?, ?> entry : object.entrySet()) {
                MapEntry entryObject = new MapEntry();
                entryObject.key = entry.getKey();
                entryObject.value = entry.getValue();
                entries.add(entryObject);
            }
        }

        @Override
        public Map<?, ?> getObject() {

            try {
                @SuppressWarnings ("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) mapType.newInstance();
                for (MapEntry entry : entries) {
                    map.put(entry.key, entry.value);
                }
                return map;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.warn("Cannot instantiate map type '{}'", mapType.getName(), e);
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
