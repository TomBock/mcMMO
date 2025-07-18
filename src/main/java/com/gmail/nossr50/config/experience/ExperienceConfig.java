package com.gmail.nossr50.config.experience;

import static com.gmail.nossr50.util.text.ConfigStringUtils.getConfigEntityTypeString;
import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.MaterialType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.util.text.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;

public class ExperienceConfig extends BukkitConfig {
    private static ExperienceConfig instance;
    final private Map<PrimarySkillType, Map<Material, Integer>> blockExperienceMap = new HashMap<>();

    private ExperienceConfig() {
        super("experience.yml");
        validate();
    }

    public static ExperienceConfig getInstance() {
        if (instance == null) {
            instance = new ExperienceConfig();
            for (PrimarySkillType skill : PrimarySkillType.values()) {
                final Map<Material, Integer> experienceMap = new HashMap<>();
                instance.blockExperienceMap.put(skill, experienceMap);
                for (Material material : Material.values()) {
                    int xp = instance.getConfigXp(skill, material);

                    if (xp > 0) {
                        experienceMap.put(material, xp);
                    }
                }

            }
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
    }

    @Override
    protected boolean validateKeys() {
        List<String> reason = new ArrayList<>();

        /*
         * FORMULA SETTINGS
         */

        /* Curve values */
        if (getMultiplier(FormulaType.EXPONENTIAL) <= 0) {
            reason.add(
                    "Experience_Formula.Exponential_Values.multiplier should be greater than 0!");
        }

        if (getMultiplier(FormulaType.LINEAR) <= 0) {
            reason.add("Experience_Formula.Linear_Values.multiplier should be greater than 0!");
        }

        if (getExponent(FormulaType.EXPONENTIAL) <= 0) {
            reason.add("Experience_Formula.Exponential_Values.exponent should be greater than 0!");
        }

        /* Global modifier */
        if (getExperienceGainsGlobalMultiplier() <= 0) {
            reason.add("Experience_Formula.Multiplier.Global should be greater than 0!");
        }

        /* PVP modifier */
        if (getPlayerVersusPlayerXP() < 0) {
            reason.add("Experience_Formula.Multiplier.PVP should be at least 0!");
        }

        /* Spawned Mob modifier */
        if (getSpawnedMobXpMultiplier() < 0) {
            reason.add("Experience_Formula.Mobspawners.Multiplier should be at least 0!");
        }

        /* Bred Mob modifier */
        if (getBredMobXpMultiplier() < 0) {
            reason.add("Experience_Formula.Breeding.Multiplier should be at least 0!");
        }

        /* Conversion */
        if (getExpModifier() <= 0) {
            reason.add("Conversion.Exp_Modifier should be greater than 0!");
        }

        /*
         * XP SETTINGS
         */

        /* Alchemy */
        for (PotionStage potionStage : PotionStage.values()) {
            if (getPotionXP(potionStage) < 0) {
                reason.add(
                        "Experience_Values.Alchemy.Potion_Stage_" + potionStage.toNumerical()
                                + " should be at least 0!");
            }
        }

        /* Archery */
        if (getArcheryDistanceMultiplier() < 0) {
            reason.add("Experience_Values.Archery.Distance_Multiplier should be at least 0!");
        }

        /* Combat XP Multipliers */
        if (getAnimalsXP() < 0) {
            reason.add("Experience_Values.Combat.Multiplier.Animals should be at least 0!");
        }

        if (getDodgeXPModifier() < 0) {
            reason.add("Skills.Acrobatics.Dodge_XP_Modifier should be at least 0!");
        }

        if (getRollXPModifier() < 0) {
            reason.add("Skills.Acrobatics.Roll_XP_Modifier should be at least 0!");
        }

        if (getFallXPModifier() < 0) {
            reason.add("Skills.Acrobatics.Fall_XP_Modifier should be at least 0!");
        }

        /* Fishing */
        // TODO: Add validation for each fish type once enum is available.

        if (getFishingShakeXP() <= 0) {
            reason.add("Experience_Values.Fishing.Shake should be greater than 0!");
        }

        /* Repair */
        if (getRepairXPBase() <= 0) {
            reason.add("Experience_Values.Repair.Base should be greater than 0!");
        }

        /* Taming */
        if (getTamingXP(EntityType.WOLF) <= 0) {
            reason.add("Experience_Values.Taming.Animal_Taming.Wolf should be greater than 0!");
        }

        if (getTamingXP(EntityType.OCELOT) <= 0) {
            reason.add("Experience_Values.Taming.Animal_Taming.Ocelot should be greater than 0!");
        }

        return noErrorsInConfig(reason);
    }

    public boolean isEarlyGameBoostEnabled() {
        return config.getBoolean("EarlyGameBoost.Enabled", true);
    }

    /*
     * FORMULA SETTINGS
     */

    /* EXPLOIT TOGGLES */
    public boolean isSnowExploitPrevented() {
        return config.getBoolean("ExploitFix.SnowGolemExcavation", true);
    }

    public boolean isEndermanEndermiteFarmingPrevented() {
        return config.getBoolean("ExploitFix.EndermanEndermiteFarms", true);
    }

    public boolean isPistonCheatingPrevented() {
        return config.getBoolean("ExploitFix.PistonCheating", true);
    }

    public boolean isPistonExploitPrevented() {
        return config.getBoolean("ExploitFix.Pistons", false);
    }

    public boolean allowUnsafeEnchantments() {
        return config.getBoolean("ExploitFix.UnsafeEnchantments", false);
    }

    public boolean isCOTWBreedingPrevented() {
        return config.getBoolean("ExploitFix.COTWBreeding", true);
    }

    public boolean isNPCInteractionPrevented() {
        return config.getBoolean("ExploitFix.PreventPluginNPCInteraction", true);
    }

    public boolean isArmorStandInteractionPrevented() {
        return config.getBoolean("ExploitFix.PreventArmorStandInteraction", true);
    }

    public boolean isFishingExploitingPrevented() {
        return config.getBoolean("ExploitFix.Fishing", true);
    }

    public int getFishingExploitingOptionMoveRange() {
        return config.getInt("Fishing_ExploitFix_Options.MoveRange", 3);
    }

    public int getFishingExploitingOptionOverFishLimit() {
        return config.getInt("Fishing_ExploitFix_Options.OverFishLimit", 10);
    }

    public boolean isAcrobaticsExploitingPrevented() {
        return config.getBoolean("ExploitFix.Acrobatics", true);
    }

    public boolean isTreeFellerXPReduced() {
        return config.getBoolean("ExploitFix.TreeFellerReducedXP", true);
    }

    /* Curve settings */
    public FormulaType getFormulaType() {
        return FormulaType.getFormulaType(config.getString("Experience_Formula.Curve"));
    }

    public boolean getCumulativeCurveEnabled() {
        return config.getBoolean("Experience_Formula.Cumulative_Curve", false);
    }

    /* Curve values */
    public double getMultiplier(FormulaType type) {
        return config.getDouble(
                "Experience_Formula." + StringUtils.getCapitalized(type.toString())
                        + "_Values.multiplier");
    }

    public int getBase(FormulaType type) {
        return config.getInt("Experience_Formula." + StringUtils.getCapitalized(type.toString())
                + "_Values.base");
    }

    public double getExponent(FormulaType type) {
        return config.getDouble(
                "Experience_Formula." + StringUtils.getCapitalized(type.toString())
                        + "_Values.exponent");
    }

    /* Global modifier */
    public double getExperienceGainsGlobalMultiplier() {
        return config.getDouble("Experience_Formula.Multiplier.Global", 1.0);
    }

    public void setExperienceGainsGlobalMultiplier(double value) {
        config.set("Experience_Formula.Multiplier.Global", value);
    }

    /* PVP modifier */
    public double getPlayerVersusPlayerXP() {
        return config.getDouble("Experience_Formula.Multiplier.PVP", 1.0);
    }

    /* Spawned Mob modifier */
    public double getSpawnedMobXpMultiplier() {
        return config.getDouble("Experience_Formula.Mobspawners.Multiplier", 0.0);
    }

    public double getEggXpMultiplier() {
        return config.getDouble("Experience_Formula.Eggs.Multiplier", 0.0);
    }

    public double getTamedMobXpMultiplier() {
        return config.getDouble("Experience_Formula.Player_Tamed.Multiplier", 0.0);
    }

    public double getNetherPortalXpMultiplier() {
        return config.getDouble("Experience_Formula.Nether_Portal.Multiplier", 0.0);
    }

    public double getBredMobXpMultiplier() {
        return config.getDouble("Experience_Formula.Breeding.Multiplier", 1.0);
    }

    /* Skill modifiers */
    public double getFormulaSkillModifier(PrimarySkillType skill) {
        return config.getDouble(
                "Experience_Formula.Skill_Multiplier." + StringUtils.getCapitalized(
                        skill.toString()),
                1);
    }

    /* Custom XP perk */
    public double getCustomXpPerkBoost() {
        return config.getDouble("Experience_Formula.Custom_XP_Perk.Boost", 1.25);
    }

    /* Diminished Returns */
    public float getDiminishedReturnsCap() {
        return (float) config.getDouble("Diminished_Returns.Guaranteed_Minimum_Percentage", 0.05D);
    }

    public boolean getDiminishedReturnsEnabled() {
        return config.getBoolean("Diminished_Returns.Enabled", false);
    }

    public int getDiminishedReturnsThreshold(PrimarySkillType skill) {
        return config.getInt(
                "Diminished_Returns.Threshold." + StringUtils.getCapitalized(skill.toString()),
                20000);
    }

    public int getDiminishedReturnsTimeInterval() {
        return config.getInt("Diminished_Returns.Time_Interval", 10);
    }

    /* Conversion */
    public double getExpModifier() {
        return config.getDouble("Conversion.Exp_Modifier", 1);
    }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() {
        return config.getBoolean("Experience_Values.PVP.Rewards", true);
    }

    /* Combat XP Multipliers */
    public double getCombatXP(String entity) {
        return config.getDouble("Experience_Values.Combat.Multiplier." + entity);
    }

    public double getCombatXP(EntityType entity) {
        return config.getDouble(
                "Experience_Values.Combat.Multiplier." + getConfigEntityTypeString(entity).replace(
                        " ", "_"));
    }

    public double getAnimalsXP(EntityType entity) {
        return config.getDouble(
                "Experience_Values.Combat.Multiplier." + getConfigEntityTypeString(entity).replace(
                        " ", "_"),
                getAnimalsXP());
    }

    public double getAnimalsXP() {
        return config.getDouble("Experience_Values.Combat.Multiplier.Animals", 1.0);
    }

    public boolean hasCombatXP(EntityType entity) {
        return config.contains(
                "Experience_Values.Combat.Multiplier." + getConfigEntityTypeString(entity).replace(
                        " ", "_"));
    }

    /* Materials  */
    private int getConfigXp(PrimarySkillType skill, Material material) {
        // prevents exploit
        if (material == Material.LILY_PAD) {
            return 0;
        }

        final String baseString =
                "Experience_Values." + StringUtils.getCapitalized(skill.toString()) + ".";
        final String configPath = baseString + getMaterialConfigString(material);
        return config.getInt(configPath, 0);
    }

    public int getXp(PrimarySkillType skill, Material material) {
        return blockExperienceMap.get(skill).getOrDefault(material, 0);
    }

    public int getXp(PrimarySkillType skill, BlockState blockState) {
        return getXp(skill, blockState.getType());
    }

    public int getXp(PrimarySkillType skill, Block block) {
        Material material = block.getType();
        return getXp(skill, material);
    }

    public int getXp(PrimarySkillType skill, BlockData data) {
        return getXp(skill, data.getMaterial());
    }

    public boolean doesBlockGiveSkillXP(PrimarySkillType skill, Material material) {
        return getXp(skill, material) > 0;
    }

    @Deprecated(forRemoval = true, since = "2.2.024")
    public boolean doesBlockGiveSkillXP(PrimarySkillType skill, BlockData data) {
        return getXp(skill, data) > 0;
    }

    /*
     * Experience Bar Stuff
     */

    public boolean isPartyExperienceBarsEnabled() {
        return config.getBoolean("Experience_Bars.Update.Party", true);
    }

    public boolean isPassiveGainsExperienceBarsEnabled() {
        return config.getBoolean("Experience_Bars.Update.Passive", true);
    }

    public boolean getDoExperienceBarsAlwaysUpdateTitle() {
        return config.getBoolean(
                "Experience_Bars.ThisMayCauseLag.AlwaysUpdateTitlesWhenXPIsGained.Enable",
                false) || getAddExtraDetails();
    }

    public boolean getAddExtraDetails() {
        return config.getBoolean(
                "Experience_Bars.ThisMayCauseLag.AlwaysUpdateTitlesWhenXPIsGained.ExtraDetails",
                false);
    }

    public boolean useCombatHPCeiling() {
        return config.getBoolean("ExploitFix.Combat.XPCeiling.Enabled", true);
    }

    public int getCombatHPCeiling() {
        return config.getInt("ExploitFix.Combat.XPCeiling.HP_Modifier_Limit", 100);
    }

    public boolean isExperienceBarsEnabled() {
        return config.getBoolean("Experience_Bars.Enable", true);
    }

    public boolean isExperienceBarEnabled(PrimarySkillType primarySkillType) {
        return config.getBoolean(
                "Experience_Bars." + StringUtils.getCapitalized(primarySkillType.toString())
                        + ".Enable", true);
    }

    public BarColor getExperienceBarColor(PrimarySkillType primarySkillType) {
        String colorValueFromConfig = config.getString(
                "Experience_Bars." + StringUtils.getCapitalized(primarySkillType.toString())
                        + ".Color");

        for (BarColor barColor : BarColor.values()) {
            if (barColor.toString().equalsIgnoreCase(colorValueFromConfig)) {
                return barColor;
            }
        }

        //In case the value is invalid
        return BarColor.WHITE;
    }

    public BarStyle getExperienceBarStyle(PrimarySkillType primarySkillType) {
        String colorValueFromConfig = config.getString(
                "Experience_Bars." + StringUtils.getCapitalized(primarySkillType.toString())
                        + ".BarStyle");

        for (BarStyle barStyle : BarStyle.values()) {
            if (barStyle.toString().equalsIgnoreCase(colorValueFromConfig)) {
                return barStyle;
            }
        }

        //In case the value is invalid
        return BarStyle.SOLID;
    }

    /* Acrobatics */
    public int getDodgeXPModifier() {
        return config.getInt("Experience_Values.Acrobatics.Dodge", 120);
    }

    public int getRollXPModifier() {
        return config.getInt("Experience_Values.Acrobatics.Roll", 80);
    }

    public int getFallXPModifier() {
        return config.getInt("Experience_Values.Acrobatics.Fall", 120);
    }

    public double getFeatherFallXPModifier() {
        return config.getDouble("Experience_Values.Acrobatics.FeatherFall_Multiplier", 2.0);
    }

    /* Alchemy */
    public double getPotionXP(PotionStage stage) {
        return config.getDouble(
                "Experience_Values.Alchemy.Potion_Brewing.Stage_" + stage.toNumerical(), 10D);
    }

    /* Archery */
    public double getArcheryDistanceMultiplier() {
        return config.getDouble("Experience_Values.Archery.Distance_Multiplier", 0.025);
    }

    public int getFishingShakeXP() {
        return config.getInt("Experience_Values.Fishing.Shake", 50);
    }

    /* Repair */
    public double getRepairXPBase() {
        return config.getDouble("Experience_Values.Repair.Base", 1000.0);
    }

    public double getRepairXP(MaterialType repairMaterialType) {
        return config.getDouble(
                "Experience_Values.Repair." + StringUtils.getCapitalized(
                        repairMaterialType.toString()));
    }

    /* Taming */
    public int getTamingXP(EntityType type) {
        return config.getInt(
                "Experience_Values.Taming.Animal_Taming." + getConfigEntityTypeString(type));
    }

    public boolean preventStoneLavaFarming() {
        return config.getBoolean("ExploitFix.LavaStoneAndCobbleFarming", true);
    }

    public boolean limitXPOnTallPlants() {
        return config.getBoolean("ExploitFix.LimitTallPlantFarming", true);
    }
}
