package com.gmail.nossr50.skills.unarmed;

import com.clubobsidian.mcmmoinjector.SkillManagerAPI;
import com.clubobsidian.mcmmoinjector.Skill;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SmoothBrick;
import org.bukkit.block.BlockState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.permissions.Permissible;

public class UnarmedManager extends SkillManager
{
    public UnarmedManager(final McMMOPlayer mcMMOPlayer) 
    {
        super(mcMMOPlayer, SkillType.UNARMED);
    }
    
    public boolean canActivateAbility() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.UNARMED) && this.mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && Permissions.berserk((Permissible)this.getPlayer());
    }
    
    public boolean canUseIronArm() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.UNARMED) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.IRON_ARM);
    }
    
    public boolean canUseBerserk() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.UNARMED) && this.mcMMOPlayer.getAbilityMode(AbilityType.BERSERK) && Permissions.berserk((Permissible)this.getPlayer());
    }
    
    public boolean canDisarm(final LivingEntity target) 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.UNARMED) && target instanceof Player && ((Player)target).getItemInHand().getType() != Material.AIR && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.DISARM);
    }
    
    public boolean canDeflect() 
    {
        final Player player = this.getPlayer();
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.UNARMED) && ItemUtils.isUnarmed(player.getItemInHand()) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.DEFLECT);
    }
    
    public boolean canUseBlockCracker() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.UNARMED) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.BLOCK_CRACKER);
    }
    
    public boolean blockCrackerCheck(final BlockState blockState) 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.BLOCK_CRACKER, this.getPlayer())) 
        {
            return false;
        }
        final MaterialData data = blockState.getData();
        switch (blockState.getType()) 
        {
            case SMOOTH_BRICK: 
            {
                if (!Unarmed.blockCrackerSmoothBrick) 
                {
                    return false;
                }
                final SmoothBrick smoothBrick = (SmoothBrick)data;
                if (smoothBrick.getMaterial() != Material.STONE) 
                {
                    return false;
                }
                smoothBrick.setMaterial(Material.COBBLESTONE);
                return true;
            }
            default:
                return false;
        }
    }
    
    public void disarmCheck(final Player defender) 
    {
        if (SkillUtils.activationSuccessful(SecondaryAbility.DISARM, this.getPlayer(), this.getSkillLevel(), this.activationChance) && !this.hasIronGrip(defender)) 
        {
            if (EventUtils.callDisarmEvent(defender).isCancelled()) {
                return;
            }
            final Item item = Misc.dropItem(defender.getLocation(), defender.getItemInHand());
            if (item != null && AdvancedConfig.getInstance().getDisarmProtected()) 
            {
                item.setMetadata("mcMMO: Disarmed Item", (MetadataValue)UserManager.getPlayer(defender).getPlayerMetadata());
            }
            defender.setItemInHand(new ItemStack(Material.AIR));
            defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));
        }
    }
    
    public boolean deflectCheck() 
    {
        if (SkillUtils.activationSuccessful(SecondaryAbility.DEFLECT, this.getPlayer(), this.getSkillLevel(), this.activationChance)) {
            this.getPlayer().sendMessage(LocaleLoader.getString("Combat.ArrowDeflect"));
            return true;
        }
        return false;
    }
    
    public double berserkDamage(double damage) 
    {
        damage = damage * Unarmed.berserkDamageModifier - damage;
        return damage;
    }
    
    public double ironArm() 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.IRON_ARM, this.getPlayer())) {
            return 0.0;
        }
        return Math.min(Unarmed.ironArmMinBonusDamage + this.getSkillLevel() / Unarmed.ironArmIncreaseLevel, Unarmed.ironArmMaxBonusDamage);
    }
    
    private boolean hasIronGrip(final Player defender) 
    {
        if (!Misc.isNPCEntity((Entity)defender) && Permissions.secondaryAbilityEnabled((Permissible)defender, SecondaryAbility.IRON_GRIP) && SkillUtils.activationSuccessful(SecondaryAbility.IRON_GRIP, defender, this.skill)) 
        {
            defender.sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Defender"));
            this.getPlayer().sendMessage(LocaleLoader.getString("Unarmed.Ability.IronGrip.Attacker"));
            return true;
        }
        return false;
    }
    
    public static Class<?> inject()
    {
    	return UnarmedManager.class;
    }
}
