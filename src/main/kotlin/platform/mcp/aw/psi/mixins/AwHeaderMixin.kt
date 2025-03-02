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

import com.demonwav.mcdev.platform.mcp.aw.psi.AwElement

interface AwHeaderMixin : AwElement {

    val versionString: String?
    val namespaceString: String?
}
