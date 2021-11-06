/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package net.mamoe.mirai.silkconverter

import io.github.kasukusakura.silkcodec.AudioToSilkCoder
import io.github.kasukusakura.silkcodec.NativeLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import net.mamoe.mirai.spi.AudioToSilkService
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.withAutoClose
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

internal object Data {
    var ffmpeg = "ffmpeg"
    var threads = Executors.newScheduledThreadPool(5, object : ThreadFactory {
        private val counter = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return Thread(r, "Mirai Silk Converter Thread#" + counter.getAndIncrement()).also {
                it.isDaemon = true
            }
        }
    })
    var newAudioToSilkCoder: ((Executor) -> AudioToSilkCoder) = ::AudioToSilkCoder

    fun convert0(source: ExternalResource): ExternalResource {
        val convert = newAudioToSilkCoder(threads)
        val rsp = AccessibleByteArrayOutputStream(max(20480, (source.size / 10).toInt()))
        when (val origin = source.origin) {
            is File -> {
                convert.connect(ffmpeg, origin.path, rsp)
            }
            else -> {
                source.inputStream().use { res ->
                    convert.connect(ffmpeg, res, rsp)
                }
            }
        }
        return rsp.toByteArray().toExternalResource().toAutoCloseable()
    }
}

@MiraiExperimentalApi
internal class SilkConverterImpl : AudioToSilkService {
    override suspend fun convert(source: ExternalResource): ExternalResource {
        if (source.formatName == "silk") return source
        return runInterruptible(Dispatchers.IO) { source.withAutoClose { Data.convert0(source) } }
    }
}

@MiraiExperimentalApi
public class SilkConverter : AudioToSilkService {
    private val delegate: AudioToSilkService

    init {
        NativeLoader.initialize(null)
        delegate = SilkConverterImpl()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun convert(source: ExternalResource): ExternalResource = delegate.convert(source)
}
