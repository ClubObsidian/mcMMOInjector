package com.gmail.nossr50.skills.acrobatics;

import com.gmail.nossr50.util.Misc;
import com.clubobsidian.mcmmoinjector.SkillManagerAPI;
import com.clubobsidian.mcmmoinjector.Skill;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;

import com.gmail.nossr50.skills.SkillManager;

public class AcrobaticsManager extends SkillManager
{
    private int fallTries;
    Location lastFallLocation;
    
    private Method calculateModifiedDodgeDamage;
    private Method calculateModifiedRollDamage;
    public AcrobaticsManager(final McMMOPlayer mcMMOPlayer) 
    {
        super(mcMMOPlayer, SkillType.ACROBATICS);
        this.fallTries = 0;
    }
    
    public boolean canRoll() 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.ACROBATICS) && !this.exploitPrevention() && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.ROLL);
    }
    
    public boolean canDodge(final Entity damager) 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.ACROBATICS) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.DODGE) && (!(damager instanceof LightningStrike) || !Acrobatics.dodgeLightningDisabled) && this.skill.shouldProcess(damager);
    }
    
    public double dodgeCheck(final double damage) 
    {
    	if(calculateModifiedDodgeDamage == null)
    	{
    		try 
    		{
				calculateModifiedDodgeDamage = Acrobatics.class.getDeclaredMethod("calculateModifiedDodgeDamage", double.class, double.class);
				calculateModifiedDodgeDamage.setAccessible(true);
			} 
    		catch (NoSuchMethodException | SecurityException e) 
    		{
				e.printStackTrace();
			}
    	}
        double modifiedDamage = -1;
		try 
		{
			modifiedDamage = (double) this.calculateModifiedDodgeDamage.invoke(null, damage, Acrobatics.dodgeDamageModifier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
        final Player player = this.getPlayer();
        if (!this.isFatal(modifiedDamage) && SkillUtils.activationSuccessful(SecondaryAbility.DODGE, player, this.getSkillLevel(), this.activationChance)) 
        {
            ParticleEffectUtils.playDodgeEffect(player);
            if (this.mcMMOPlayer.useChatNotifications()) 
            {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
            }
            if (SkillUtils.cooldownExpired(this.mcMMOPlayer.getRespawnATS(), 5)) 
            {
                this.applyXpGain((float)(damage * Acrobatics.dodgeXpModifier), XPGainReason.PVP);
            }
            return modifiedDamage;
        }
        return damage;
    }
    
    public double rollCheck(final double damage) 
    {
        final Player player = this.getPlayer();
        if (player.isSneaking() && Permissions.secondaryAbilityEnabled((Permissible)player, SecondaryAbility.GRACEFUL_ROLL)) 
        {
            return this.gracefulRollCheck(damage);
        }
        
    	if(this.calculateModifiedRollDamage == null)
    	{
    		try 
    		{
    			this.calculateModifiedRollDamage = Acrobatics.class.getDeclaredMethod("calculateModifiedRollDamage", double.class, double.class);
    			this.calculateModifiedRollDamage.setAccessible(true);
			} 
    		catch (NoSuchMethodException | SecurityException e) 
    		{
				e.printStackTrace();
			}
    	}
        double modifiedDamage = -1;
		try 
		{
			modifiedDamage = (double) this.calculateModifiedRollDamage.invoke(null, damage, Acrobatics.rollThreshold);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
        //final double modifiedDamage = Acrobatics.calculateModifiedRollDamage(damage, Acrobatics.rollThreshold);
        if (!this.isFatal(modifiedDamage) && SkillUtils.activationSuccessful(SecondaryAbility.ROLL, player, this.getSkillLevel(), this.activationChance)) 
        {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
            this.applyXpGain(this.calculateRollXP(damage, true), XPGainReason.PVE);
            return modifiedDamage;
        }
        if (!this.isFatal(damage)) 
        {
            this.applyXpGain(this.calculateRollXP(damage, false), XPGainReason.PVE);
        }
        this.lastFallLocation = player.getLocation();
        return damage;
    }
    
    private double gracefulRollCheck(final double damage) 
    {
    	if(this.calculateModifiedRollDamage == null)
    	{
    		try 
    		{
    			this.calculateModifiedRollDamage = Acrobatics.class.getDeclaredMethod("calculateModifiedRollDamage", double.class, double.class);
    			this.calculateModifiedRollDamage.setAccessible(true);
			} 
    		catch (NoSuchMethodException | SecurityException e) 
    		{
				e.printStackTrace();
			}
    	}
        double modifiedDamage = -1;
		try 
		{
			modifiedDamage = (double) this.calculateModifiedRollDamage.invoke(damage, Acrobatics.gracefulRollThreshold);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
        if (!this.isFatal(modifiedDamage) && SkillUtils.activationSuccessful(SecondaryAbility.GRACEFUL_ROLL, this.getPlayer(), this.getSkillLevel(), this.activationChance)) 
        {
            this.getPlayer().sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
            this.applyXpGain(this.calculateRollXP(damage, true), XPGainReason.PVE);
            return modifiedDamage;
        }
        if (!this.isFatal(damage)) 
        {
            this.applyXpGain(this.calculateRollXP(damage, false), XPGainReason.PVE);
        }
        return damage;
    }
    
    public boolean exploitPrevention() 
    {
        if (!Config.getInstance().getAcrobaticsPreventAFK()) 
        {
            return false;
        }
        final Player player = this.getPlayer();
        if (player.getItemInHand().getType() == Material.ENDER_PEARL || player.isInsideVehicle()) 
        {
            return true;
        }
        final Location fallLocation = player.getLocation();
        final int maxTries = Config.getInstance().getAcrobaticsAFKMaxTries();
        final boolean sameLocation = this.lastFallLocation != null && Misc.isNear(this.lastFallLocation, fallLocation, 2.0);
        this.fallTries = (sameLocation ? Math.min(this.fallTries + 1, maxTries) : Math.max(this.fallTries - 1, 0));
        this.lastFallLocation = fallLocation;
        return this.fallTries + 1 > maxTries;
    }
    
    private boolean isFatal(final double damage) 
    {
        return this.getPlayer().getHealth() - damage <= 0.0;
    }
    
    private float calculateRollXP(final double damage, final boolean isRoll) 
    {
        final ItemStack boots = this.getPlayer().getInventory().getBoots();
        float xp = (float)(damage * (isRoll ? Acrobatics.rollXpModifier : Acrobatics.fallXpModifier));
        if (boots != null && boots.containsEnchantment(Enchantment.PROTECTION_FALL)) 
        {
            xp *= (float)Acrobatics.featherFallXPModifier;
        }
        return xp;
    }
    
    public static Class<?> inject()
    {
    	return AcrobaticsManager.class;
    }
}