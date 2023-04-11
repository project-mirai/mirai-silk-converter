/*
 * Copyright 2019-2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package net.mamoe.mirai.silkconverter

import io.github.kasukusakura.silkcodec.AudioToSilkCoder
import io.github.kasukusakura.silkcodec.NativeLoader
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.spi.AudioToSilkService
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.verbose
import java.io.File

@PublishedApi
internal object MiraiSilkConverterConsolePlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "net.mamoe.mirai-silk-converter",
        version = "0.0.6",
        name = "Silk Converter"
    )
) {
    @MiraiExperimentalApi
    override fun PluginComponentStorage.onLoad() {
        kotlin.runCatching {
            NativeLoader.initialize(dataFolder)
            Data.newAudioToSilkCoder = { exec ->
                object : AudioToSilkCoder(exec) {
                    override fun cmdLine(nextLine: String?) {
                        logger.verbose { nextLine ?: "null" }
                    }

                    override fun errorLog(throwable: Throwable?) {
                        logger.error("SilkCoder execute error", throwable)
                    }
                }
            }
            AudioToSilkService.setService(SilkConverterImpl())
        }.onFailure {
            logger.error(it)
            logger.error { "Configuration location: " + dataFolder.absolutePath }
        }
    }

    override fun onEnable() {
        if (dataFolder.resolve("DEBUG_MODE").exists()) {
            kotlin.runCatching {
                Class.forName("io.github.kasukusakura.silkcodec.NativeBridge")
                    .getDeclaredField("DEB")
                    .also { it.isAccessible = true }
                    .setBoolean(null, true)
            }.onFailure { logger.error(it) }
            val vozperm = PermissionService.INSTANCE.register(
                permissionId("debug"),
                "",
                this.parentPermission
            )
            globalEventChannel().subscribeAlways<MessageEvent> {
                val sender = try {
                    toCommandSender()
                } catch (ignored: Throwable) {
                    null
                } ?: return@subscribeAlways
                if (!sender.hasPermission(vozperm)) return@subscribeAlways

                val content = this.message.content
                if (content.startsWith(".voiced ")) {
                    val s = subject
                    if (s is AudioSupported) {
                        s.sendMessage(
                            s.uploadAudio(
                                File(content.removePrefix(".voiced").trim())
                                    .toExternalResource()
                                    .toAutoCloseable()
                            )
                        )
                    } else {
                        s.sendMessage("Current session not supported audio sending")
                    }
                }
            }
        }
    }
}
