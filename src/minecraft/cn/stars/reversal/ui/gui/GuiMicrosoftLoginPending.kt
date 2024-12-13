package cn.stars.reversal.ui.gui

import cn.stars.elixir.account.MicrosoftAccount
import cn.stars.elixir.account.MinecraftAccount
import cn.stars.elixir.compat.OAuthServer
import cn.stars.reversal.GameInstance
import cn.stars.reversal.font.FontManager
import cn.stars.reversal.ui.modern.TextButton
import cn.stars.reversal.util.ReversalLogger
import cn.stars.reversal.util.math.TimeUtil
import cn.stars.reversal.util.shader.RiseShaders
import cn.stars.reversal.util.shader.base.ShaderRenderType
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.Session
import java.awt.Color
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.function.Consumer

class GuiMicrosoftLoginPending(private val prevGui: GuiScreen) : GuiScreen() {
    val timer = TimeUtil()
    private var stage = "Initializing..."
    private lateinit var server: OAuthServer
    private var cancelButton : TextButton? = null
    private var isError = false;

    override fun initGui() {
        GameInstance.clearRunnables()
        cancelButton =
            TextButton(width / 2 - 40.0, height / 2 + 100.0, 80.0, 30.0,
                {
                    server.stop(true)
                    mc.displayGuiScreen(prevGui);

                }, "取消", "g", true, 6, 35, 9
            )
        server = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
            override fun openUrl(url: String) {
                stage = "Check your browser to continue..."
                ReversalLogger.info("Opening URL: $url")
                showURL(url)
            }

            override fun authError(error: String) {
                stage = if (error.contains("OpenGL") || error.equals("context")) "Succeed."
                else {
                    "Error: $error"
                }
                isError = true
            }

            override fun authResult(account: MicrosoftAccount) {
                stage = login(account)
                isError = true
                if (timer.hasReached(5000)) {
                    mc.displayGuiScreen(prevGui)
                    timer.reset()
                }
            }
        })
        
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawMenuBackground(partialTicks, mouseX, mouseY)

        // blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update()
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, GameInstance.NORMAL_BLUR_RUNNABLES)

        // bloom
        RiseShaders.POST_BLOOM_SHADER.update()
        RiseShaders.POST_BLOOM_SHADER.run(
            ShaderRenderType.OVERLAY,
            partialTicks,
            GameInstance.NORMAL_POST_BLOOM_RUNNABLES
        )

        GameInstance.clearRunnables()

        cancelButton!!.draw(mouseX, mouseY, partialTicks)

        FontManager.getPSB(24).drawCenteredString(stage, width / 2.0, height / 2.0 - 50, Color.WHITE.rgb)
        FontManager.getPSB(36).drawCenteredString("### Microsoft Login Process ###", width / 2.0, 70.0, Color.WHITE.rgb)


        GameInstance.UI_BLOOM_RUNNABLES.forEach(Consumer { obj: Runnable -> obj.run() })
        GameInstance.UI_BLOOM_RUNNABLES.clear()
    }

    override fun onGuiClosed() {
        server.stop(true)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {

        // 执行runnable
        if (mouseButton == 0) {
            if (isHovered(cancelButton!!.x, cancelButton!!.y, cancelButton!!.width, cancelButton!!.height, mouseX, mouseY)) {
                mc.soundHandler.playButtonPress()
                cancelButton!!.runAction()
            }
        }
    }

    private fun isHovered(x: Double, y: Double, width: Double, height: Double, mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
    }

    fun showURL(url: String) {
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }


    fun login(account: MinecraftAccount): String {
        return try {
            val mc = Minecraft.getMinecraft()
            mc.session = account.session.let { Session(it.username, it.uuid, it.token, it.type) }
            "Succeed."
        } catch (e: Exception) {
            e.printStackTrace()
            isError = true
            "Failed: " + e.message
        }
    }
}