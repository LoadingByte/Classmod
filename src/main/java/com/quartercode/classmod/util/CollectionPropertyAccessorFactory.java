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

package com.quartercode.classmod.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.extra.CollectionProperty;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Property;

/**
 * A utility class for creating {@link FunctionExecutor}s which can access simple {@link Collection} {@link Property}s.
 * 
 * @see Property
 * @see Collection
 * @see FunctionExecutor
 */
public class CollectionPropertyAccessorFactory {

    /**
     * Creates a new getter {@link FunctionExecutor} for the given {@link Collection} {@link Property} definition.
     * A getter function returns an unmodifiable instance of the {@link Collection} stored by a {@link Property}.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Collection} {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E, C extends Collection<E>> FunctionExecutor<C> createGet(final CollectionPropertyDefinition<E, C> propertyDefinition) {

        return createGet(propertyDefinition, new CriteriumMatcher<E>() {

            @Override
            public boolean matches(E element, Object... arguments) {

                return true;
            }

        });
    }

    /**
     * Creates a new getter {@link FunctionExecutor} for the given {@link Collection} {@link Property} definition with the given {@link CriteriumMatcher}.
     * A getter function returns an unmodifiable instance of the {@link Collection} stored by a {@link Property}.
     * The {@link CriteriumMatcher} only lets certain elements through into the return collection.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Collection} {@link Property} to access.
     *        Content must be of type {@link List}, {@link Set}, {@link SortedSet} or {@link Collection}, no implementations like {@link ArrayList} are allowed!
     * @param matcher The {@link CriteriumMatcher} for checking if certain elements should be returned.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E, C extends Collection<E>> FunctionExecutor<C> createGet(final CollectionPropertyDefinition<E, C> propertyDefinition, final CriteriumMatcher<E> matcher) {

        return new FunctionExecutor<C>() {

            @SuppressWarnings ("unchecked")
            @Override
            public C invoke(FunctionInvocation<C> invocation, Object... arguments) {

                C originalCollection = invocation.getHolder().get(propertyDefinition).get();

                Collection<E> collection = new ArrayList<E>();
                for (E element : originalCollection) {
                    if (matcher.matches(element, arguments)) {
                        collection.add(element);
                    }
                }

                invocation.next(arguments);

                // These casts always return the right value IF C is no implementation (e.g. ArrayList instead of just List)
                if (originalCollection instanceof List) {
                    return (C) Collections.unmodifiableList(new ArrayList<E>(collection));
                } else if (originalCollection instanceof Set) {
                    return (C) Collections.unmodifiableSet(new HashSet<E>(collection));
                } else if (originalCollection instanceof SortedSet) {
                    return (C) Collections.unmodifiableSortedSet(new TreeSet<E>(collection));
                } else {
                    return (C) Collections.unmodifiableCollection(collection);
                }
            }

        };
    }

    /**
     * Creates a new single getter {@link FunctionExecutor} for the given {@link Collection} {@link Property} definition with the given {@link CriteriumMatcher}.
     * A single getter function returns the first element of the {@link Collection} stored by a {@link Property} recognized by a {@link CriteriumMatcher}.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Collection} {@link Property} to access.
     * @param matcher The {@link CriteriumMatcher} for checking the elements.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E> FunctionExecutor<E> createGetSingle(final CollectionPropertyDefinition<E, ? extends Collection<E>> propertyDefinition, final CriteriumMatcher<E> matcher) {

        return new FunctionExecutor<E>() {

            @Override
            public E invoke(FunctionInvocation<E> invocation, Object... arguments) {

                E result = null;
                for (E element : invocation.getHolder().get(propertyDefinition).get()) {
                    if (matcher.matches(element, arguments)) {
                        result = element;
                    }
                }

                invocation.next(arguments);
                return result;
            }

        };
    }

    /**
     * Creates a new adder {@link FunctionExecutor} for the given {@link Collection} {@link Property} definition.
     * An adder function adds an element to the {@link Collection} of a {@link Property}.
     * The element that should be added must be supplied as first argument.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Collection} {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E> FunctionExecutor<Void> createAdd(final CollectionPropertyDefinition<E, ? extends Collection<E>> propertyDefinition) {

        return new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                // Hope that the using FunctionDefinition has the correct parameters
                @SuppressWarnings ("unchecked")
                E element = (E) arguments[0];
                invocation.getHolder().get(propertyDefinition).add(element);

                return invocation.next(arguments);
            }

        };
    }

    /**
     * Creates a new remover {@link FunctionExecutor} for the given {@link Collection} {@link Property} definition.
     * A remover function removes an element from the {@link Collection} of a {@link Property}.
     * The element that should be removed must be supplied as first argument.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Collection} {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E> FunctionExecutor<Void> createRemove(final CollectionPropertyDefinition<E, ? extends Collection<E>> propertyDefinition) {

        return new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                // Hope that the using FunctionDefinition has the correct parameters
                @SuppressWarnings ("unchecked")
                E element = (E) arguments[0];
                invocation.getHolder().get(propertyDefinition).remove(element);

                return invocation.next(arguments);
            }

        };
    }

    /**
     * Creates a new peeker {@link FunctionExecutor} for the given {@link Queue} {@link Property} definition.
     * A peeker function looks up and returns the head element of a {@link Queue}.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Queue} {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E> FunctionExecutor<E> createPeek(final CollectionPropertyDefinition<E, ? extends Queue<E>> propertyDefinition) {

        return new FunctionExecutor<E>() {

            @Override
            public E invoke(FunctionInvocation<E> invocation, Object... arguments) {

                E element = invocation.getHolder().get(propertyDefinition).get().peek();

                invocation.next(arguments);
                return element;
            }

        };
    }

    /**
     * Creates a new poller {@link FunctionExecutor} for the given {@link Queue} {@link Property} definition.
     * A poller function looks up, removes, and returns the head element of a {@link Queue}.
     * 
     * @param propertyDefinition The {@link FeatureDefinition} of the {@link Queue} {@link Property} to access.
     * @return The created {@link FunctionExecutor}.
     */
    public static <E> FunctionExecutor<E> createPoll(final CollectionPropertyDefinition<E, ? extends Queue<E>> propertyDefinition) {

        return new FunctionExecutor<E>() {

            @Override
            public E invoke(FunctionInvocation<E> invocation, Object... arguments) {

                CollectionProperty<E, ? extends Queue<E>> property = invocation.getHolder().get(propertyDefinition);
                E element = property.get().peek();
                if (element != null) {
                    property.remove(element);
                }

                invocation.next(arguments);
                return element;
            }

        };
    }

    /**
     * Criterium matchers are used for limiting the output of {@link FunctionExecutor}s created by the getter utilities.
     * 
     * @param <E> The type of elements the matcher checks.
     * @see CollectionPropertyAccessorFactory#createGet(CollectionPropertyDefinition, CriteriumMatcher)
     * @see CollectionPropertyAccessorFactory#createGetSingle(CollectionPropertyDefinition, CriteriumMatcher)
     */
    public static interface CriteriumMatcher<E> {

        /**
         * Checks if the given element applies to the criterium defined by the matcher.
         * 
         * @param element The element to check.
         * @param arguments The arguments which were passed during invocation.
         * @return True if the given element matches, false if not.
         */
        public boolean matches(E element, Object... arguments);

    }

    private CollectionPropertyAccessorFactory() {

    }

}
