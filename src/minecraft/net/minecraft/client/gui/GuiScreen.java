package net.minecraft.client.gui;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.video.BackgroundManager;
import cn.stars.reversal.util.render.video.VideoUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import cn.stars.reversal.util.shader.base.ShaderToy;
import cn.stars.reversal.util.shader.impl.BackgroundShader;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static cn.stars.reversal.GameInstance.*;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet(new String[] {"http", "https"});
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    protected Minecraft mc;
    protected RenderItem itemRender;
    public int width;
    public int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;
    protected FontRenderer fontRendererObj;
    public GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private URI clickedLinkURI;
    public float screenPartialTicks;
    public static ShaderToy mario;
    public static ShaderToy redround;
    public static ShaderToy water;
    public static ShaderToy blackhole;
    public static ShaderToy octagrams;
    public static ShaderToy tokyo;
    public static  ShaderToy curiosity;
    public static boolean isShaderToyInitialized = false;

    public GuiScreen() {
    }

    public static void initializeShaderToy() {
        if (!isShaderToyInitialized) {
            try {
                mario = new ShaderToy("mario.fsh");
                redround = new ShaderToy("redround.fsh");
                water = new ShaderToy("water.fsh");
                blackhole = new ShaderToy("blackhole.fsh");
                octagrams = new ShaderToy("octagrams.fsh");
                tokyo = new ShaderToy("tokyo.fsh");
                curiosity = new ShaderToy("curiosity.fsh");
                isShaderToyInitialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected static void uiClick() {
        GameInstance.mc.getSoundHandler().playUISound("click");
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiButton guiButton : this.buttonList) {
            guiButton.drawButton(this.mc, mouseX, mouseY);
        }

        for (GuiLabel guiLabel : this.labelList) {
            guiLabel.drawLabel(this.mc, mouseX, mouseY);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    public static String getClipboardString()
    {
        if (GLFW.glfwGetClipboardString(RainyAPI.window) != null) return GLFW.glfwGetClipboardString(RainyAPI.window);
        return "";
    }

    public static void setClipboardString(String copyText)
    {
        GLFW.glfwSetClipboardString(RainyAPI.window ,copyText);
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            }
            else
            {
                list.set(i, EnumChatFormatting.GRAY + (String)list.get(i));
            }
        }

        this.drawHoveringText(list, x, y);
    }

    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY)
    {
        this.drawHoveringText(Collections.singletonList(tabName), mouseX, mouseY);
    }

    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = this.fontRendererObj.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = (String)textLines.get(k1);
                this.fontRendererObj.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(IChatComponent component, int x, int y)
    {
        if (component != null && component.getChatStyle().getChatHoverEvent() != null)
        {
            HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack itemstack = null;

                try
                {
                    NBTTagCompound nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                    if (nbtbase != null)
                    {
                        itemstack = ItemStack.loadItemStackFromNBT(nbtbase);
                    }
                }
                catch (NBTException var11)
                {
                    ;
                }

                if (itemstack != null)
                {
                    this.renderToolTip(itemstack, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY)
            {
                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    try
                    {
                        NBTTagCompound nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                        if (nbtbase1 != null)
                        {
                            List<String> list1 = Lists.<String>newArrayList();
                            NBTTagCompound nbttagcompound = nbtbase1;
                            list1.add(nbttagcompound.getString("name"));

                            if (nbttagcompound.hasKey("type", 8))
                            {
                                String s = nbttagcompound.getString("type");
                                list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
                            }

                            list1.add(nbttagcompound.getString("id"));
                            this.drawHoveringText(list1, x, y);
                        }
                        else
                        {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                        }
                    }
                    catch (NBTException var10)
                    {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                    }
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT)
            {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x, y);
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT)
            {
                StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

                if (statbase != null)
                {
                    IChatComponent ichatcomponent = statbase.getStatName();
                    IChatComponent ichatcomponent1 = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"), new Object[0]);
                    ichatcomponent1.getChatStyle().setItalic(true);
                    String s1 = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                    List<String> list = Lists.newArrayList(ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText());

                    if (s1 != null)
                    {
                        list.addAll(this.fontRendererObj.listFormattedStringToWidth(s1, 150));
                    }

                    this.drawHoveringText(list, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite) {
    }

    /**
     * Executes the click event specified by the given chat component
     *
     * @param component The ChatComponent to check for click
     */
    protected boolean handleComponentClick(IChatComponent component) {
        if (component != null) {
            ClickEvent clickevent = component.getChatStyle().getChatClickEvent();

            if (isShiftKeyDown()) {
                if (component.getChatStyle().getInsertion() != null) {
                    this.setText(component.getChatStyle().getInsertion(), false);
                }
            } else if (clickevent != null) {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }

                    try {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();

                        if (s == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }

                        if (!PROTOCOLS.contains(s.toLowerCase())) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase());
                        }

                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
                        } else {
                            this.openWebLink(uri);
                        }
                    } catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for " + clickevent, urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = (new File(clickevent.getValue())).toURI();
                    this.openWebLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickevent.getValue(), false);
                } else {
                    LOGGER.error("Failed to handle {}", clickevent);
                }

                return true;
            }

        }
        return false;
    }

    public void sendChatMessage(String msg)
    {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat)
    {
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }

        this.mc.thePlayer.sendChatMessage(msg);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            for (GuiButton guiButton : this.buttonList) {

                if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = guiButton;
                    guiButton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guiButton);
                }
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    public void setGuiSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    public void initGui()
    {
    }

    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException
    {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    public void handleKeyboardInput() throws IOException
    {
        char eventCharacter = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState() || eventCharacter >= ' ' && eventKey == 0)
        {
            this.keyTyped(eventCharacter, eventKey);
        }

        this.mc.dispatchKeypresses();
    }

    public void updateScreen()
    {
    }

    public void onGuiClosed()
    {
    }

    public void drawDefaultBackground()
    {
        this.drawWorldBackground();
    }

    public void drawWorldBackground()
    {
        if (this.mc.theWorld != null)
        {
            if (ModuleInstance.getInterface().guiBackground.enabled) this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else {
            try {
                drawMenuBackground(screenPartialTicks, this.width, this.height);
                if (RainyAPI.backgroundBlur) {
                    ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.rect(0,0, this.width, this.height, Color.BLACK), 2, 2);
                }
            } catch (Exception e) {
                ReversalLogger.error("(GuiScreen) Error while loading background");
            }
        }
    }

    public void drawBackground(int tint)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 31102009)
        {
            if (result)
            {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {url});
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    public static boolean isCtrlKeyDown()
    {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isAltKeyDown()
    {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyCombo(int key1, int key2) {
        return Keyboard.isKeyDown(key1) && Keyboard.isKeyDown(key2);
    }

    public static boolean isKeyComboCtrlX(int keyID)
    {
        return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID)
    {
        return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID)
    {
        return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID)
    {
        return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int w, int h)
    {
        this.setWorldAndResolution(mcIn, w, h);
    }

    public void useShaderToyBackground(ShaderToy shader, int scale) {
        GlStateManager.disableCull();
        shader.useShader(this.width * scale, this.height * scale, (System.currentTimeMillis() - RenderUtil.initTime) / 1000.0f);
        GL11.glBegin(7);
        GL11.glVertex2d(-1.0, -1.0);
        GL11.glVertex2d(-1.0, 1.0);
        GL11.glVertex2d(1.0, 1.0);
        GL11.glVertex2d(1.0, -1.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.enableAlpha();
    }

    public void useShaderToyBackground(ShaderToy shader, int scale, int mouseX, int mouseY) {
        GlStateManager.disableCull();
        shader.useShader(this.width * scale, this.height * scale, (System.currentTimeMillis() - RenderUtil.initTime) / 1000.0f, mouseX, mouseY);
        GL11.glBegin(7);
        GL11.glVertex2d(-1.0, -1.0);
        GL11.glVertex2d(-1.0, 1.0);
        GL11.glVertex2d(1.0, 1.0);
        GL11.glVertex2d(1.0, -1.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.enableAlpha();
    }

    public void useShaderToyBackground(ShaderToy shader, int width, int height) {
        GlStateManager.disableCull();
        shader.useShader(width, height, (System.currentTimeMillis() - RenderUtil.initTime) / 1000.0f);
        GL11.glBegin(7);
        GL11.glVertex2d(-1.0, -1.0);
        GL11.glVertex2d(-1.0, 1.0);
        GL11.glVertex2d(1.0, 1.0);
        GL11.glVertex2d(1.0, -1.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.enableAlpha();
    }

    final Tessellator tessellator = Tessellator.getInstance();
    final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

    public void drawMenuBackground(float partialTicks, int mouseX, int mouseY) {
        if (RainyAPI.backgroundId > 10 || RainyAPI.backgroundId < 0) RainyAPI.backgroundId = 9;
        if (RainyAPI.backgroundId == 9) {
            VideoUtil.render(0, 0, width, height);
            return;
        }
        if (RainyAPI.backgroundId == 10) {
            RenderUtil.image(BackgroundManager.backgroundImage, 0, 0, width, height);
            return;
        }
        if (RainyAPI.isShaderCompatibility) {
            ReversalLogger.warn("Detected <DisableShader> option enabled! The option has forced reversal to disable shader backgrounds.");
            RainyAPI.backgroundId = 9;
            return;
        } else {
            initializeShaderToy();
        }
        if (RainyAPI.backgroundId == 0) {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            BackgroundShader.BACKGROUND_SHADER.startShader();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(0, height, 0.0D).endVertex();
            worldRenderer.pos(width, height, 0.0D).endVertex();
            worldRenderer.pos(width, 0, 0.0D).endVertex();
            worldRenderer.pos(0, 0, 0.0D).endVertex();
            tessellator.draw();
            BackgroundShader.BACKGROUND_SHADER.stopShader();
        } else if (RainyAPI.backgroundId == 1) {
            RiseShaders.MAIN_MENU_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, null);
        } else if (RainyAPI.backgroundId == 2) {
            useShaderToyBackground(mario, 2);
        } else if (RainyAPI.backgroundId == 3) {
            useShaderToyBackground(redround, 2);
        } else if (RainyAPI.backgroundId == 4) {
            useShaderToyBackground(water, 1);
        } else if (RainyAPI.backgroundId == 5) {
            useShaderToyBackground(blackhole, 2);
        } else if (RainyAPI.backgroundId == 6) {
            useShaderToyBackground(octagrams, 2);
        } else if (RainyAPI.backgroundId == 7) {
            useShaderToyBackground(tokyo, 2);
        } else if (RainyAPI.backgroundId == 8) {
            useShaderToyBackground(curiosity, 2, 0, 0);
        }
    }

    public static void changeMenuBackground(boolean previous) {
        RenderUtil.initTime = System.currentTimeMillis();
        BackgroundShader.BACKGROUND_SHADER.stopShader();
        RiseShaders.MAIN_MENU_SHADER.setActive(false);
        GL20.glUseProgram(0);
        if (RainyAPI.isShaderCompatibility) {
            ReversalLogger.warn("Detected <DisableShader> option enabled! The option has forced reversal to disable shader backgrounds.");
            RainyAPI.backgroundId = 9;
            return;
        }
        if (!previous) {
            if (RainyAPI.backgroundId < 10) RainyAPI.backgroundId++;
            else RainyAPI.backgroundId = 0;
        } else {
            if (RainyAPI.backgroundId > 0) RainyAPI.backgroundId--;
            else RainyAPI.backgroundId = 10;
        }
        Reversal.notificationManager.registerNotification("Background id changed to: " + RainyAPI.backgroundId, "Main Menu", 2000, NotificationType.SUCCESS);
        ReversalLogger.info("(GuiMainMenuNew) Current background id: " + RainyAPI.backgroundId);
    }

    public void updatePostProcessing(boolean pre, float partialTicks) {
        if (mc.theWorld == null) {
            if (pre) {
                // blur
                RiseShaders.GAUSSIAN_BLUR_SHADER.update();
                RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);

                // bloom
                RiseShaders.POST_BLOOM_SHADER.update();
                RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);

                GameInstance.clearRunnables();
            } else {
                UI_BLOOM_RUNNABLES.forEach(Runnable::run);
                UI_BLOOM_RUNNABLES.clear();
            }
        }
    }
}
