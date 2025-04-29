package net.optifine.entity.model;

import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderRabbit;
import net.minecraft.entity.passive.EntityRabbit;

import java.util.HashMap;
import java.util.Map;

public class ModelAdapterRabbit extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterRabbit()
    {
        super(EntityRabbit.class, "rabbit", 0.3F);
    }

    public ModelBase makeModel()
    {
        return new ModelRabbit();
    }

    @SneakyThrows
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelRabbit))
        {
            return null;
        }
        else
        {
            ModelRabbit modelrabbit = (ModelRabbit)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                return (ModelRenderer) modelrabbit.getClass().getField(modelPart).get(modelrabbit);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"rabbitLeftFoot", "rabbitRightFoot", "rabbitLeftThigh", "rabbitRightThigh", "rabbitBody", "rabbitLeftArm", "rabbitRightArm", "rabbitHead", "rabbitRightEar", "rabbitLeftEar", "rabbitTail", "rabbitNose"};
    }

    private static Map<String, Integer> getMapPartFields()
    {
        if (mapPartFields == null) {
            mapPartFields = new HashMap();
            mapPartFields.put("rabbitLeftFoot", 0);
            mapPartFields.put("rabbitRightFoot", 1);
            mapPartFields.put("rabbitLeftThigh", 2);
            mapPartFields.put("rabbitRightThigh", 3);
            mapPartFields.put("rabbitBody", 4);
            mapPartFields.put("rabbitLeftArm", 5);
            mapPartFields.put("rabbitRightArm", 6);
            mapPartFields.put("rabbitHead", 7);
            mapPartFields.put("rabbitRightEar", 8);
            mapPartFields.put("rabbitLeftEar", 9);
            mapPartFields.put("rabbitTail", 10);
            mapPartFields.put("rabbitNose", 11);
        }
        return mapPartFields;
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderRabbit renderrabbit = new RenderRabbit(rendermanager, modelBase, shadowSize);
        return renderrabbit;
    }
}
