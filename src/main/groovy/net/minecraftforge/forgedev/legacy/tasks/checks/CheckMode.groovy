/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.legacy.tasks.checks

import groovy.transform.CompileStatic

@CompileStatic
enum CheckMode {
    CHECK,
    FIX

    CheckMode() {}
}
