package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.util.skills.CombatUtils;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.skills.SkillUtils;


import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.skills.AbilityType;

import org.bukkit.entity.LivingEntity;
import org.bukkit.permissions.Permissible;

import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.clubobsidian.mcmmoinjector.SkillManagerAPI;
import com.clubobsidian.mcmmoinjector.Skill;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;

public class AxesManager extends SkillManager
{
	private Method hasArmor;
    public AxesManager(final McMMOPlayer mcMMOPlayer) 
    {
        super(mcMMOPlayer, SkillType.AXES);
    }
    
    public boolean canUseAxeMastery() 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.AXES) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.AXE_MASTERY);
    }
    
    public boolean canCriticalHit(final LivingEntity target) 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.AXES) && target.isValid() && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.CRITICAL_HIT);
    }
    
    public boolean canImpact(final LivingEntity target) 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.AXES) && target.isValid() && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.ARMOR_IMPACT) && this.hasArmor(target);
    }
    
    public boolean canGreaterImpact(final LivingEntity target) 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.AXES) && target.isValid() && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.GREATER_IMPACT) && !this.hasArmor(target);
    }
    
    public boolean canUseSkullSplitter(final LivingEntity target) 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.AXES) && target.isValid() && this.mcMMOPlayer.getAbilityMode(AbilityType.SKULL_SPLITTER) && Permissions.skullSplitter((Permissible)this.getPlayer());
    }
    
    public boolean canActivateAbility() 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.AXES) && this.mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && Permissions.skullSplitter((Permissible)this.getPlayer());
    }
    
    public double axeMastery() 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.AXE_MASTERY, this.getPlayer())) 
        {
            return 0.0;
        }
        return Math.min(this.getSkillLevel() / (Axes.axeMasteryMaxBonusLevel / Axes.axeMasteryMaxBonus), Axes.axeMasteryMaxBonus);
    }
    
    public double criticalHit(final LivingEntity target, double damage) 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.CRITICAL_HIT, this.getPlayer(), this.getSkillLevel(), this.activationChance)) 
        {
            return 0.0;
        }
        
        final Player player = this.getPlayer();
        if (this.mcMMOPlayer.useChatNotifications()) 
        {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.CriticalHit"));
        }
        if (target instanceof Player) 
        {
            final Player defender = (Player)target;
            if (UserManager.getPlayer(defender).useChatNotifications()) {
                defender.sendMessage(LocaleLoader.getString("Axes.Combat.CritStruck"));
            }
            damage = damage * Axes.criticalHitPVPModifier - damage;
        }
        else 
        {
            damage = damage * Axes.criticalHitPVEModifier - damage;
        }
        return damage;
    }
    
    public void impactCheck(final LivingEntity target) 
    {
        final int durabilityDamage = 1 + this.getSkillLevel() / Axes.impactIncreaseLevel;
        for (final ItemStack armor : target.getEquipment().getArmorContents()) 
        {
            if (ItemUtils.isArmor(armor) && SkillUtils.activationSuccessful(SecondaryAbility.ARMOR_IMPACT, this.getPlayer(), Axes.impactChance, this.activationChance)) 
            {
                SkillUtils.handleDurabilityChange(armor, durabilityDamage, Axes.impactMaxDurabilityModifier);
            }
        }
    }
    
    public double greaterImpact(final LivingEntity target) 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.GREATER_IMPACT, this.getPlayer(), Axes.greaterImpactChance, this.activationChance)) 
        {
            return 0.0;
        }
        final Player player = this.getPlayer();
        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));
        if (this.mcMMOPlayer.useChatNotifications()) 
        {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));
        }
        if (target instanceof Player) 
        {
            final Player defender = (Player)target;
            if (UserManager.getPlayer(defender).useChatNotifications()) 
            {
                defender.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Struck"));
            }
        }
        return Axes.greaterImpactBonusDamage;
    }
    
    public void skullSplitterCheck(final LivingEntity target, final double damage, final Map<EntityDamageEvent.DamageModifier, Double> modifiers) 
    {
        CombatUtils.applyAbilityAoE(this.getPlayer(), target, damage / Axes.skullSplitterModifier, modifiers, this.skill);
    }
    
    private boolean hasArmor(LivingEntity entity)
    {
    	if(this.hasArmor == null)
    	{
    		try 
    		{
				this.hasArmor = Axes.class.getDeclaredMethod("hasArmor", LivingEntity.class);
				this.hasArmor.setAccessible(true);
			} 
    		catch (NoSuchMethodException | SecurityException e) 
    		{
				e.printStackTrace();
				return true;
			}
    	}
    	try 
    	{
			return (boolean) this.hasArmor.invoke(null, entity);
		} 
    	catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
    	{
			e.printStackTrace();
		}
    	return true;
    }
    
    public static Class<?> inject()
    {
    	return AxesManager.class;
    }
}