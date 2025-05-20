package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "ItemIndicator", localizedName = "module.ItemIndicator.name", description = "Indicate useful items in the world", localizedDescription = "module.ItemIndicator.desc", category = Category.RENDER)
public class ItemIndicator extends Module {
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final BoolValue throwable = new BoolValue("Throwable", this, false);

    private PlayerInventoryDatabase database = new PlayerInventoryDatabase();
    private PlayerInventoryDatabase containerDatabase = new PlayerInventoryDatabase();
    private final TimeUtil timeUtil = new TimeUtil();

    @Override
    public void onRender2D(Render2DEvent event) {
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) mc.currentScreen;

            // Too many iterations, check it durationally.
            if (timeUtil.hasReached(100)) {
                database = new PlayerInventoryDatabase();
                containerDatabase = new PlayerInventoryDatabase();
                searchItems(database, mc.thePlayer.inventory);
                searchItems(containerDatabase, chest.lowerChestInventory);
                timeUtil.reset();
            }

            for (int i : containerDatabase.getAll()) {
                if (i >= 0) {
                    if (chest.lowerChestInventory.getStackInSlot(i) != null && chest.lowerChestInventory.getStackInSlot(i) != null) {
                        if (shouldFlag(database, containerDatabase, chest.lowerChestInventory, containerDatabase.getAll().indexOf(i))) {
                            drawFlaggedItem(chest, i);
                        }
                    }
                }
            }
            for (int i = 0; i < chest.lowerChestInventory.getSizeInventory(); i++) {
                if (chest.lowerChestInventory.getStackInSlot(i) != null && chest.lowerChestInventory.getStackInSlot(i) != null) {
                    if (checkItemUseful(chest.lowerChestInventory.getStackInSlot(i))) {
                        drawFlaggedItem(chest, i);
                    }
                }
            }
        }
    }

    private void drawFlaggedItem(GuiChest chest, int i) {
        int x = (chest.width - chest.xSize) / 2 + 7;
        int y = (chest.height - chest.ySize) / 2 + 17;
        if (i < 9) {
            chest.POST_DRAW_RUNNABLES.add(() -> RenderUtil.roundedOutlineRectangle(x + 18 * i, y, 18, 18, 2, 1.5, colorValue.getColor()));
        } else if (i < 18) {
            chest.POST_DRAW_RUNNABLES.add(() -> RenderUtil.roundedOutlineRectangle(x + 18 * (i - 9), y + 18, 18, 18, 2, 1.5, colorValue.getColor()));
        } else if (i < 27) {
            chest.POST_DRAW_RUNNABLES.add(() -> RenderUtil.roundedOutlineRectangle(x + 18 * (i - 18), y + 36, 18, 18, 2, 1.5, colorValue.getColor()));
        } else if (i < 36) {
            chest.POST_DRAW_RUNNABLES.add(() -> RenderUtil.roundedOutlineRectangle(x + 18 * (i - 27), y + 54, 18, 18, 2, 1.5, colorValue.getColor()));
        } else if (i < 45) {
            chest.POST_DRAW_RUNNABLES.add(() -> RenderUtil.roundedOutlineRectangle(x + 18 * (i - 36), y + 72, 18, 18, 2, 1.5, colorValue.getColor()));
        } else if (i < 54) {
            chest.POST_DRAW_RUNNABLES.add(() -> RenderUtil.roundedOutlineRectangle(x + 18 * (i - 45), y + 90, 18, 18, 2, 1.5, colorValue.getColor()));
        }
    }

    private void searchItems(PlayerInventoryDatabase database, IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null) {
                Item item = itemStack.getItem();
                if (item instanceof ItemArmor) {
                    switch (((ItemArmor) item).armorType) {
                        case 0:
                            database.helmetPos = checkItemBetterFromWeight(inventory.getStackInSlot(database.helmetPos), itemStack) ? i : database.helmetPos;
                            break;
                        case 1:
                            database.chestplatePos = checkItemBetterFromWeight(inventory.getStackInSlot(database.chestplatePos), itemStack) ? i : database.chestplatePos;
                            break;
                        case 2:
                            database.leggingsPos = checkItemBetterFromWeight(inventory.getStackInSlot(database.leggingsPos), itemStack) ? i : database.leggingsPos;
                            break;
                        case 3:
                            database.bootsPos = checkItemBetterFromWeight(inventory.getStackInSlot(database.bootsPos), itemStack) ? i : database.bootsPos;
                            break;
                    }
                } else if (item instanceof ItemPickaxe) {
                    database.pickaxePos = checkItemBetterFromWeight(inventory.getStackInSlot(database.pickaxePos), itemStack) ? i : database.pickaxePos;
                } else if (item instanceof ItemSword) {
                    database.swordPos = checkItemBetterFromWeight(inventory.getStackInSlot(database.swordPos), itemStack) ? i : database.swordPos;
                } else if (item instanceof ItemAxe) {
                    database.axePos = checkItemBetterFromWeight(inventory.getStackInSlot(database.axePos), itemStack) ? i : database.axePos;
                } else if (item instanceof ItemHoe) {
                    database.hoePos = checkItemBetterFromWeight(inventory.getStackInSlot(database.hoePos), itemStack) ? i : database.hoePos;
                } else if (item instanceof ItemSpade) {
                    database.spadePos = checkItemBetterFromWeight(inventory.getStackInSlot(database.spadePos), itemStack) ? i : database.spadePos;
                } else if (item instanceof ItemBow) {
                    database.bowPos = checkItemBetterFromWeight(inventory.getStackInSlot(database.bowPos), itemStack) ? i : database.bowPos;
                }
            }
        }
    }

    public boolean shouldFlag(PlayerInventoryDatabase database1, PlayerInventoryDatabase database2, IInventory inventory, int index) {
        return checkItemBetterFromWeight(mc.thePlayer.inventory.getStackInSlot(database1.getAll().get(index)), inventory.getStackInSlot(database2.getAll().get(index)));
    }

    public boolean checkItemUseful(ItemStack itemStack) {
        if (itemStack != null && itemStack.getItem() != null) {
            Item item = itemStack.getItem();
            return item instanceof ItemAppleGold || item instanceof ItemPotion || item instanceof ItemEnderEye || (throwable.enabled && (item instanceof ItemEgg || item instanceof ItemSnowball || item instanceof ItemFireball));
        }
        return false;
    }

    public boolean checkItemBetterFromWeight(ItemStack itemStack1, ItemStack itemStack2) {
        float itemWeight = getWeightFromItem(itemStack1, itemStack2);
        float enchantmentWeight = getWeightFromEnchantment(itemStack1, itemStack2);
        return itemWeight + enchantmentWeight > 0;
    }

    public float getWeightFromEnchantment(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack2 == null) { return 0; }
        else if (itemStack1 == null) {
            Item item2 = itemStack2.getItem();
            if (item2 instanceof ItemArmor || item2 instanceof ItemTool || item2 instanceof ItemSword || item2 instanceof ItemHoe || item2 instanceof ItemBow) return 1;
            else return 0;
        }
        Item item1 = itemStack1.getItem();
        Item item2 = itemStack2.getItem();
        if (item1 instanceof ItemArmor && item2 instanceof ItemArmor) {
            int item1ProtLvl = EnchantmentHelper.getEnchantmentLevel(0, itemStack1);
            int item2ProtLvl = EnchantmentHelper.getEnchantmentLevel(0, itemStack2);
            return item2ProtLvl - item1ProtLvl;
        } else if ((item1 instanceof ItemPickaxe && item2 instanceof ItemPickaxe)
                || (item1 instanceof ItemAxe && item2 instanceof ItemAxe)
                || (item1 instanceof ItemSpade && item2 instanceof ItemSpade)) {
            int item1effLvl = EnchantmentHelper.getEnchantmentLevel(32, itemStack1);
            int item2effLvl = EnchantmentHelper.getEnchantmentLevel(32, itemStack2);
            return item2effLvl - item1effLvl;
        } else if ((item1 instanceof ItemSword && item2 instanceof ItemSword)) {
            int item1sharpLvl = EnchantmentHelper.getEnchantmentLevel(16, itemStack1);
            int item2sharpLvl = EnchantmentHelper.getEnchantmentLevel(16, itemStack2);
            // Sharpness add 1.25 damage each level in Minecraft 1.8.9, calculate the weight by total damage.
            return item2sharpLvl * 1.25f - item1sharpLvl * 1.25f;
        } else if ((item1 instanceof ItemHoe && item2 instanceof ItemHoe)) {
            return 0;
        } else if ((item1 instanceof ItemBow && item2 instanceof ItemBow)) {
            int item1powerLvl = EnchantmentHelper.getEnchantmentLevel(48, itemStack1);
            int item2powerLvl = EnchantmentHelper.getEnchantmentLevel(48, itemStack2);
            return item2powerLvl - item1powerLvl;
        }
        return 0;
    }

    public float getWeightFromItem(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack2 == null) { return 0; }
        else if (itemStack1 == null) {
            Item item2 = itemStack2.getItem();
            if (item2 instanceof ItemArmor || item2 instanceof ItemTool || item2 instanceof ItemSword || item2 instanceof ItemHoe || item2 instanceof ItemBow) return 1;
            else return 0;
        }
        Item item1 = itemStack1.getItem();
        Item item2 = itemStack2.getItem();
        if (item1 instanceof ItemArmor && item2 instanceof ItemArmor) {
            ItemArmor armor1 = (ItemArmor) item1;
            ItemArmor armor2 = (ItemArmor) item2;
            if (armor1.armorType == armor2.armorType) {
                return getArmorWeight(armor1.getArmorMaterial(), armor2.getArmorMaterial());
            }
        } else if ((item1 instanceof ItemPickaxe && item2 instanceof ItemPickaxe)
                || (item1 instanceof ItemAxe && item2 instanceof ItemAxe)
                || (item1 instanceof ItemSpade && item2 instanceof ItemSpade)) {
            ItemTool tool1 = (ItemTool) item1;
            ItemTool tool2 = (ItemTool) item2;
            return getToolWeight(tool1.getToolMaterial(), tool2.getToolMaterial());
        } else if ((item1 instanceof ItemSword && item2 instanceof ItemSword)) {
            ItemSword sword1 = (ItemSword) item1;
            ItemSword sword2 = (ItemSword) item2;
            return getToolWeight(sword1.getToolMaterial(), sword2.getToolMaterial());
        } else if ((item1 instanceof ItemHoe && item2 instanceof ItemHoe)) {
            ItemHoe hoe1 = (ItemHoe) item1;
            ItemHoe hoe2 = (ItemHoe) item2;
            return getToolWeight(hoe1.getToolMaterial(), hoe2.getToolMaterial());
        }
        return 0;
    }

    public int getArmorWeight(ItemArmor.ArmorMaterial material1, ItemArmor.ArmorMaterial material2) {
        return getArmorMaterialIndex(material2) - getArmorMaterialIndex(material1);
    }

    public int getToolWeight(Item.ToolMaterial material1, Item.ToolMaterial material2) {
        return getToolMaterialIndex(material2) - getToolMaterialIndex(material1);
    }

    private int getArmorMaterialIndex(ItemArmor.ArmorMaterial material) {
        switch (material) {
            case LEATHER:
            case GOLD:
                return 1;

            case CHAIN: return 2;
            case IRON: return 3;
            case DIAMOND: return 4;
            default: return 0;
        }
    }

    private int getToolMaterialIndex(Item.ToolMaterial material) {
        switch (material) {
            case WOOD:
            case GOLD:
                return 1;
            case STONE: return 2;
            case IRON: return 3;
            case EMERALD: return 4;
            default: return 0;
        }
    }

    public static class PlayerInventoryDatabase {
        public int helmetPos = -1;
        public int chestplatePos = -2;
        public int leggingsPos = -3;
        public int bootsPos = -4;
        public int swordPos = -5;
        public int pickaxePos = -6;
        public int axePos = -7;
        public int spadePos = -8;
        public int hoePos = -9;
        public int bowPos = -10;

        public List<Integer> getAll() {
            return Arrays.asList(helmetPos, chestplatePos, leggingsPos, bootsPos, swordPos, pickaxePos, axePos, spadePos, hoePos, bowPos);
        }
    }
}
