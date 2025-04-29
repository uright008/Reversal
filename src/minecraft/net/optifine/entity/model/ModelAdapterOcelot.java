package net.optifine.entity.model;

import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderOcelot;
import net.minecraft.entity.passive.EntityOcelot;

import java.util.HashMap;
import java.util.Map;

public class ModelAdapterOcelot extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterOcelot()
    {
        super(EntityOcelot.class, "ocelot", 0.4F);
    }

    public ModelBase makeModel()
    {
        return new ModelOcelot();
    }

    @SneakyThrows
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelOcelot))
        {
            return null;
        }
        else
        {
            ModelOcelot modelocelot = (ModelOcelot)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                return (ModelRenderer) modelocelot.getClass().getField(modelPart).get(modelocelot);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"ocelotBackLeftLeg", "ocelotBackRightLeg", "ocelotFrontLeftLeg", "ocelotFrontRightLeg", "ocelotTail", "ocelotTail2", "ocelotHead", "ocelotBody"};
    }

    private static Map<String, Integer> getMapPartFields()
    {
        if (mapPartFields == null) {
            mapPartFields = new HashMap();
            mapPartFields.put("ocelotBackLeftLeg", 0);
            mapPartFields.put("ocelotBackRightLeg", 1);
            mapPartFields.put("ocelotFrontLeftLeg", 2);
            mapPartFields.put("ocelotFrontRightLeg", 3);
            mapPartFields.put("ocelotTail", 4);
            mapPartFields.put("ocelotTail2", 5);
            mapPartFields.put("ocelotHead", 6);
            mapPartFields.put("ocelotBody", 7);
        }
        return mapPartFields;
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderOcelot renderocelot = new RenderOcelot(rendermanager, modelBase, shadowSize);
        return renderocelot;
    }
}
