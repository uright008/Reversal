package net.minecraft.crash;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import net.optifine.CrashReporter;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport
{
    private static final Logger logger = LogManager.getLogger();
    private final String description;
    private final Throwable cause;
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "系统信息");
    private final List<CrashReportCategory> crashReportSections = Lists.<CrashReportCategory>newArrayList();
    private File crashReportFile;
    private boolean firstCategoryInCrashReport = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private boolean reported = false;

    public CrashReport(String descriptionIn, Throwable causeThrowable)
    {
        this.description = descriptionIn;
        this.cause = causeThrowable;
        this.populateEnvironment();
    }

    private void populateEnvironment()
    {
        this.theReportCategory.addCrashSectionCallable("Minecraft版本", new Callable<String>()
        {
            public String call()
            {
                return Reversal.MINECRAFT_VERSION;
            }
        });
        this.theReportCategory.addCrashSectionCallable("操作系统", new Callable<String>()
        {
            public String call()
            {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java版本", new Callable<String>()
        {
            public String call()
            {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("JVM版本", new Callable<String>()
        {
            public String call()
            {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
        });
        this.theReportCategory.addCrashSectionCallable("内存", new Callable<String>()
        {
            public String call()
            {
                Runtime runtime = Runtime.getRuntime();
                long i = runtime.maxMemory();
                long j = runtime.totalMemory();
                long k = runtime.freeMemory();
                long l = i / 1024L / 1024L;
                long i1 = j / 1024L / 1024L;
                long j1 = k / 1024L / 1024L;
                return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
            }
        });
        this.theReportCategory.addCrashSectionCallable("JVM参数", new Callable<String>()
        {
            public String call()
            {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List<String> list = runtimemxbean.getInputArguments();
                int i = 0;
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : list)
                {
                    if (s.startsWith("-X"))
                    {
                        if (i++ > 0)
                        {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append(s);
                    }
                }

                return String.format("共计 %d; %s", i, stringbuilder);
            }
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>()
        {
            public String call() {
                return IntCache.getCacheSizes();
            }
        });
    }

    public String getDescription()
    {
        return this.description;
    }

    public Throwable getCrashCause()
    {
        return this.cause;
    }

    public void getSectionsInStringBuilder(StringBuilder builder)
    {
        if ((this.stacktrace == null || this.stacktrace.length == 0) && !this.crashReportSections.isEmpty())
        {
            this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
        }

        if (this.stacktrace != null && this.stacktrace.length > 0)
        {
            builder.append("-- Head --\n");
            builder.append("Stacktrace:\n");

            for (StackTraceElement stacktraceelement : this.stacktrace)
            {
                builder.append("\t").append("at ").append(stacktraceelement.toString());
                builder.append("\n");
            }

            builder.append("\n");
        }

        for (CrashReportCategory crashreportcategory : this.crashReportSections)
        {
            crashreportcategory.appendToStringBuilder(builder);
            builder.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(builder);
    }

    public String getCauseStackTraceOrString()
    {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Throwable throwable = this.cause;

        if (throwable.getMessage() == null)
        {
            if (throwable instanceof NullPointerException)
            {
                throwable = new NullPointerException(this.description);
            }
            else if (throwable instanceof StackOverflowError)
            {
                throwable = new StackOverflowError(this.description);
            }
            else if (throwable instanceof OutOfMemoryError)
            {
                throwable = new OutOfMemoryError(this.description);
            }

            throwable.setStackTrace(this.cause.getStackTrace());
        }

        String s;

        try
        {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            throwable.printStackTrace(printwriter);
            s = stringwriter.toString();
        }
        finally
        {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    public String getCompleteReport()
    {
        if (!this.reported)
        {
            this.reported = true;
            CrashReporter.onCrashReport(this, this.theReportCategory);
        }

        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft 崩溃报告 ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("时间: ");
        stringbuilder.append((new SimpleDateFormat()).format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("简介: ");
        stringbuilder.append(this.description);
        stringbuilder.append("\n\n");
        stringbuilder.append(this.getCauseStackTraceOrString());
        stringbuilder.append("\n\n错误的详细回顾,相关的代码字段和所有已知的细节如下:\n");

        for (int i = 0; i < 87; ++i)
        {
            stringbuilder.append("-");
        }

        stringbuilder.append("\n\n");
        this.getSectionsInStringBuilder(stringbuilder);
        return stringbuilder.toString();
    }

    public File getFile()
    {
        return this.crashReportFile;
    }

    public boolean saveToFile(File toFile)
    {
        if (this.crashReportFile != null)
        {
            return false;
        }
        else
        {
            if (toFile.getParentFile() != null)
            {
                toFile.getParentFile().mkdirs();
            }

            try
            {
                FileWriter filewriter = new FileWriter(toFile);
                filewriter.write(this.getCompleteReport());
                filewriter.close();
                this.crashReportFile = toFile;
                return true;
            }
            catch (Throwable throwable)
            {
                logger.error("Could not save crash report to {}", toFile, throwable);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory()
    {
        return this.theReportCategory;
    }

    public CrashReportCategory makeCategory(String name)
    {
        return this.makeCategoryDepth(name, 1);
    }

    public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength)
    {
        CrashReportCategory crashreportcategory = new CrashReportCategory(this, categoryName);

        if (this.firstCategoryInCrashReport)
        {
            int i = crashreportcategory.getPrunedStackTrace(stacktraceLength);
            StackTraceElement[] astacktraceelement = this.cause.getStackTrace();
            StackTraceElement stacktraceelement = null;
            StackTraceElement stacktraceelement1 = null;
            int j = astacktraceelement.length - i;

            if (j < 0)
            {
                System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
            }

            if (0 <= j && j < astacktraceelement.length)
            {
                stacktraceelement = astacktraceelement[j];

                if (astacktraceelement.length + 1 - i < astacktraceelement.length)
                {
                    stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
                }
            }

            this.firstCategoryInCrashReport = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement, stacktraceelement1);

            if (i > 0 && !this.crashReportSections.isEmpty())
            {
                CrashReportCategory crashreportcategory1 = (CrashReportCategory)this.crashReportSections.get(this.crashReportSections.size() - 1);
                crashreportcategory1.trimStackTraceEntriesFromBottom(i);
            }
            else if (astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length)
            {
                this.stacktrace = new StackTraceElement[j];
                System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
            }
            else
            {
                this.firstCategoryInCrashReport = false;
            }
        }

        this.crashReportSections.add(crashreportcategory);
        return crashreportcategory;
    }

    private static String getWittyComment()
    {
        String[] astring = RainyAPI.wittyCrashReport;

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn)
    {
        CrashReport crashreport;

        if (causeIn instanceof ReportedException)
        {
            crashreport = ((ReportedException)causeIn).getCrashReport();
        }
        else
        {
            crashreport = new CrashReport(descriptionIn, causeIn);
        }

        return crashreport;
    }
}
