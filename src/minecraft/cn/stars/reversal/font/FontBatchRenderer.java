package cn.stars.reversal.font;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FontBatchRenderer {
    private static final int MAX_BATCH = 1024;
    private static final int VERTEX_SIZE = 6; // x,y,u,v,r,g,b,a
    private final FloatBuffer buffer = ByteBuffer
        .allocateDirect(MAX_BATCH * 4 * VERTEX_SIZE * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    
    private int count;
    private int currentTexture = -1;
    public void addCharacter(FontCharacter ch, float x, float y) {
        if (count >= MAX_BATCH || ch.getTexture() != currentTexture) {
            flush();
        }
        
        if (currentTexture == -1) {
            currentTexture = ch.getTexture();
            GlStateManager.bindTexture(currentTexture);
        }

        // 顶点1
        buffer.put(x * 0.5f).put(y * 0.5f)
              .put(0).put(0); // UV
        // 顶点2
        buffer.put(x * 0.5f).put((y + ch.getHeight()) * 0.5f)
              .put(0).put(1);
        // 顶点3
        buffer.put((x + ch.getWidth()) * 0.5f).put((y + ch.getHeight()) * 0.5f)
              .put(1).put(1);
        // 顶点4
        buffer.put((x + ch.getWidth()) * 0.5f).put(y * 0.5f)
              .put(1).put(0);
        
        count++;
    }

    public void flush() {
        if (count == 0) return;

        buffer.flip();

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        buffer.position(0);
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 16, buffer);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 16, (FloatBuffer) buffer.position(2));

        GL11.glDrawArrays(GL11.GL_QUADS, 0, count * 4);

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        buffer.clear();
        count = 0;
        currentTexture = -1;
    }
}