package cn.stars.reversal.ui.atmoic.mainmenu.impl.misc;

import cn.stars.elixir.account.MicrosoftAccount;
import cn.stars.elixir.compat.OAuthServer;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MiscGui;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.util.Session;

import java.awt.*;
import java.net.URI;

public class MsLoginGui extends MiscGui {
    private OAuthServer server;
    private String stage = "Initializing Login API...";
    private boolean finished;

    public MsLoginGui() {
        super("Microsoft Login");
        Reversal.threadPoolExecutor.execute(new Thread(() -> {
            server = MicrosoftAccount.Companion.buildFromOpenBrowser(new MicrosoftAccount.OAuthHandler() {
                @Override
                public void openUrl(String url) {
                    stage = "Check your browser to continue...";
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException ignored) {
                    }
                    ReversalLogger.info("Opening URL: {}");
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception e) {
                        finished = true;
                        ReversalLogger.error("Failed to open URL: " + url, e);
                    }
                }

                @Override
                public void authResult(MicrosoftAccount account) {
                    stage = login(account);
                }

                @Override
                public void authError(String error) {
                    stage = error.contains("OpenGL") || error.contains("context") ? "Succeed." : "Error: " + error;
                    finished = true;
                }
            });
        }));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 200, height / 2f - 60, 400, 120, 3, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 200, height / 2f - 60, 400, 120, 3, new Color(20, 20, 20, 160));

        if (!finished) RenderUtils.drawLoadingCircle2(this.width / 2f, this.height / 2f - 25, 10, Color.WHITE);
        regular24Bold.drawCenteredString(stage, this.width / 2f, this.height / 2f, Color.WHITE.getRGB());
    //    psm18.drawCenteredString(ip, this.width / 2f, this.height / 2f + 5, new Color(220, 220, 220, 220).getRGB());
    }

    @Override
    public void onGuiClosed() {
        if (server != null) server.stop(true);
    }

    private String login(MicrosoftAccount account) {
        try {
            mc.session = new Session(account.getSession().getUsername(), account.getSession().getUuid(), account.getSession().getUuid(), account.getSession().getType());
            return "Succeed.";
        } catch (Exception e) {
            ReversalLogger.error("Failed to login", e);
            return "Error: " + e.getMessage();
        } finally {
            finished = true;
        }
    }
}
