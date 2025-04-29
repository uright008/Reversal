package net.optifine;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.optifine.http.FileUploadThread;
import net.optifine.http.IFileUploadListener;
import net.optifine.shaders.Shaders;

public class CrashReporter
{
    public static void onCrashReport(CrashReport crashReport, CrashReportCategory category)
    {
        try
        {
            Throwable throwable = crashReport.getCrashCause();

            if (throwable == null) {
                return;
            }

            extendCrashReport(category);
        }
        catch (Exception exception)
        {
            Config.dbg(exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    public static void extendCrashReport(CrashReportCategory cat)
    {
        cat.addCrashSection("Optifine版本", Config.getVersion());
        cat.addCrashSection("Optifine构建", Config.getBuild());

        if (Config.getGameSettings() != null)
        {
            cat.addCrashSection("渲染距离", "" + Config.getChunkViewDistance());
            cat.addCrashSection("Mipmaps", "" + Config.getMipmapLevels());
            cat.addCrashSection("各向异性过滤", "" + Config.getAnisotropicFilterLevel());
            cat.addCrashSection("抗锯齿", "" + Config.getAntialiasingLevel());
            cat.addCrashSection("多材质", "" + Config.isMultiTexture());
        }

        cat.addCrashSection("光源", Shaders.getShaderPackName());
        cat.addCrashSection("OpenGL版本", Config.openGlVersion);
        cat.addCrashSection("OpenGL渲染器", Config.openGlRenderer);
        cat.addCrashSection("OpenGL供应者", Config.openGlVendor);
        cat.addCrashSection("CPU数量", "" + Config.getAvailableProcessors());
    }
}
