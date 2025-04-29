package net.optifine.entity.model;

import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityHorse;

import java.util.HashMap;
import java.util.Map;

public class ModelAdapterHorse extends ModelAdapter
{
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterHorse()
    {
        super(EntityHorse.class, "horse", 0.75F);
    }

    protected ModelAdapterHorse(Class entityClass, String name, float shadowSize)
    {
        super(entityClass, name, shadowSize);
    }

    public ModelBase makeModel()
    {
        return new ModelHorse();
    }

    @SneakyThrows
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelHorse))
        {
            return null;
        }
        else
        {
            ModelHorse modelhorse = (ModelHorse)model;
            Map<String, Integer> map = getMapPartFields();

            if (map.containsKey(modelPart))
            {
                return (ModelRenderer) modelhorse.getClass().getField(modelPart).get(modelhorse);
            }
            else
            {
                return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "upperMouth", "lowerMouth", "horseLeftEar", "horseRightEar", "muleLeftEar", "muleRightEar", "neck", "horseFaceRopes", "mane", "body", "tailBase", "tailMiddle", "tailTip", "backLeftLeg", "backLeftShin", "backLeftHoof", "backRightLeg", "backRightShin", "backRightHoof", "frontLeftLeg", "frontLeftShin", "frontLeftHoof", "frontRightLeg", "frontRightShin", "frontRightHoof", "muleLeftChest", "muleRightChest", "horseSaddleBottom", "horseSaddleFront", "horseSaddleBack", "horseLeftSaddleRope", "horseLeftSaddleMetal", "horseRightSaddleRope", "horseRightSaddleMetal", "horseLeftFaceMetal", "horseRightFaceMetal", "horseLeftRein", "horseRightRein"};
    }

    private static Map<String, Integer> getMapPartFields()
    {
        if (mapPartFields == null) {
            mapPartFields = new HashMap();
            mapPartFields.put("head", 0);
            mapPartFields.put("upperMouth", 1);
            mapPartFields.put("lowerMouth", 2);
            mapPartFields.put("horseLeftEar", 3);
            mapPartFields.put("horseRightEar", 4);
            mapPartFields.put("muleLeftEar", 5);
            mapPartFields.put("muleRightEar", 6);
            mapPartFields.put("neck", 7);
            mapPartFields.put("horseFaceRopes", 8);
            mapPartFields.put("mane", 9);
            mapPartFields.put("body", 10);
            mapPartFields.put("tailBase", 11);
            mapPartFields.put("tailMiddle", 12);
            mapPartFields.put("tailTip", 13);
            mapPartFields.put("backLeftLeg", 14);
            mapPartFields.put("backLeftShin", 15);
            mapPartFields.put("backLeftHoof", 16);
            mapPartFields.put("backRightLeg", 17);
            mapPartFields.put("backRightShin", 18);
            mapPartFields.put("backRightHoof", 19);
            mapPartFields.put("frontLeftLeg", 20);
            mapPartFields.put("frontLeftShin", 21);
            mapPartFields.put("frontLeftHoof", 22);
            mapPartFields.put("frontRightLeg", 23);
            mapPartFields.put("frontRightShin", 24);
            mapPartFields.put("frontRightHoof", 25);
            mapPartFields.put("muleLeftChest", 26);
            mapPartFields.put("muleRightChest", 27);
            mapPartFields.put("horseSaddleBottom", 28);
            mapPartFields.put("horseSaddleFront", 29);
            mapPartFields.put("horseSaddleBack", 30);
            mapPartFields.put("horseLeftSaddleRope", 31);
            mapPartFields.put("horseLeftSaddleMetal", 32);
            mapPartFields.put("horseRightSaddleRope", 33);
            mapPartFields.put("horseRightSaddleMetal", 34);
            mapPartFields.put("horseLeftFaceMetal", 35);
            mapPartFields.put("horseRightFaceMetal", 36);
            mapPartFields.put("horseLeftRein", 37);
            mapPartFields.put("horseRightRein", 38);
        }
        return mapPartFields;
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderHorse renderhorse = new RenderHorse(rendermanager, (ModelHorse)modelBase, shadowSize);
        return renderhorse;
    }
}
