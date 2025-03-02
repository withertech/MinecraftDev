/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2021 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.aw.psi.mixins

interface AwFieldEntryMixin : AwEntryMixin {

    val fieldName: String?
    val fieldDescriptor: String?
}
