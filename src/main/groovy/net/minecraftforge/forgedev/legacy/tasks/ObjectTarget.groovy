/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.legacy.tasks

import groovy.transform.EqualsAndHashCode
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

@EqualsAndHashCode
public class ObjectTarget implements Comparable<ObjectTarget> {
    @Input
    String owner
    
    @Input
    String name
    
    @Input
    @Optional
    String desc
    
    @Override
    String toString() {
        if (desc == null)
            return owner + '.' + name
        return owner + '.' + name + desc
    }
    
    @Override
    int compareTo(ObjectTarget o) {
        return toString() <=> o.toString()
    }
}
