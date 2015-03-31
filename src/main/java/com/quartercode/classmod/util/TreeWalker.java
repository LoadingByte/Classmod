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

package com.quartercode.classmod.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.quartercode.classmod.base.Feature;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.prop.ValueSupplier;
import com.quartercode.classmod.util.FeatureHolderVisitor.VisitResult;

/**
 * The tree walker walks over a tree of {@link FeatureHolder}s and calls a {@link FeatureHolderVisitor} with all feature holders it encounters.
 * A feature holder can have child holders by holding {@link ValueSupplier} {@link Feature}s that return other feature holders.
 * However, the visitor interface also allows to provide {@link VisitResult return codes} for controlling the behavior of the walker.
 * 
 * @see FeatureHolderVisitor
 */
public class TreeWalker {

    /**
     * Walks over the {@link FeatureHolder} tree that starts at the given root holder and calls the given {@link FeatureHolderVisitor} with all holders it encounters.
     * Note that endless loops caused by cycles in the tree graph are avoided.
     * 
     * @param start The root feature holder where the tree for walking starts.
     * @param visitor The visitor which is called with all encountered holders.
     *        It is also able to control the behavior of the tree walker using {@link VisitResult return codes}.
     * @param callPost Whether the {@link FeatureHolderVisitor#postVisit(FeatureHolder)} method should be called.
     *        Setting this to {@code false} if the method is not needed significantly improves performance.
     */
    public static void walk(FeatureHolder start, FeatureHolderVisitor visitor, boolean callPost) {

        try {
            walk(start, visitor, new HashSet<UUID>(), callPost);
        } catch (ExitStackException e) {
            // Ignore (the exception is only used to exit the recursion)
        }
    }

    private static void walk(FeatureHolder currentHolder, FeatureHolderVisitor visitor, Set<UUID> visitedHolders, boolean callPost) throws ExitStackException {

        // Avoid stack overflows resulting from cycles
        if (visitedHolders.contains(currentHolder.getUUID())) {
            return;
        }
        visitedHolders.add(currentHolder.getUUID());

        // Call the pre-visiting callback
        VisitResult preResult = visitor.preVisit(currentHolder);

        if (preResult == VisitResult.TERMINATE) {
            // Terminate the recursion by exiting the stack
            throw new ExitStackException();
        } else if (preResult != VisitResult.SKIP_SUBTREE) {
            // Iterate over the current feature holder's features and look for any ValueSuppliers with child features
            for (Feature feature : currentHolder) {
                if (feature instanceof ValueSupplier) {
                    Object value = ((ValueSupplier<?>) feature).get();

                    if (value instanceof FeatureHolder) {
                        walk((FeatureHolder) value, visitor, visitedHolders, callPost);
                    } else if (value instanceof Iterable) {
                        for (Object entry : (Iterable<?>) value) {
                            if (entry instanceof FeatureHolder) {
                                walk((FeatureHolder) entry, visitor, visitedHolders, callPost);
                            }
                        }
                    }
                }
            }
        }

        if (callPost) {
            // Call the post-visiting callback
            if (visitor.postVisit(currentHolder) == VisitResult.TERMINATE) {
                // Terminate the recursion by exiting the stack
                throw new ExitStackException();
            }
        }
    }

    private TreeWalker() {

    }

    @SuppressWarnings ("serial")
    private static class ExitStackException extends Exception {

    }

}
