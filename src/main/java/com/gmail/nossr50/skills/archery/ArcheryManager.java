package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permissible;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.clubobsidian.mcmmoinjector.SkillManagerAPI;
import com.clubobsidian.mcmmoinjector.Skill;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;

public class ArcheryManager extends SkillManager
{
	private Method incrementTrackerValue;
    public ArcheryManager(final McMMOPlayer mcMMOPlayer) 
    {
        super(mcMMOPlayer, SkillType.ARCHERY);
    }
    
    public boolean canDaze(final LivingEntity target) 
    {
        return target instanceof Player && SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.ARCHERY) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.DAZE);
    }
    
    public boolean canSkillShot() 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.ARCHERY) && this.getSkillLevel() >= Archery.skillShotIncreaseLevel && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.SKILL_SHOT);
    }
    
    public boolean canRetrieveArrows() 
    {
        return SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.ARCHERY) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.RETRIEVE);
    }
    
    public void distanceXpBonus(final LivingEntity target, final Entity damager) 
    {
        final Location firedLocation = (Location)damager.getMetadata("mcMMO: Arrow Distance").get(0).value();
        final Location targetLocation = target.getLocation();
        if (firedLocation.getWorld() != targetLocation.getWorld()) 
        {
            return;
        }
        this.applyXpGain((int)(Math.min(firedLocation.distanceSquared(targetLocation), 2500.0) * Archery.DISTANCE_XP_MULTIPLIER), this.getXPGainReason(target, damager));
    }
    
    public void retrieveArrows(final LivingEntity target) 
    {
        if (SkillUtils.activationSuccessful(SecondaryAbility.RETRIEVE, this.getPlayer(), this.getSkillLevel(), this.activationChance)) 
        {
            this.incrementTrackerValue(target);
        }
    }
    
    public double daze(final Player defender) 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.DAZE, this.getPlayer(), this.getSkillLevel(), this.activationChance)) 
        {
            return 0.0;
        }
        final Location dazedLocation = defender.getLocation();
        dazedLocation.setPitch((float)(90 - Misc.getRandom().nextInt(181)));
        defender.teleport(dazedLocation);
        defender.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 10));
        if (UserManager.getPlayer(defender).useChatNotifications()) {
            defender.sendMessage(LocaleLoader.getString("Combat.TouchedFuzzy"));
        }
        if (this.mcMMOPlayer.useChatNotifications()) {
            this.getPlayer().sendMessage(LocaleLoader.getString("Combat.TargetDazed"));
        }
        return Archery.dazeBonusDamage;
    }
    
    public double skillShot(final double damage) 
    {
        if (!SkillUtils.activationSuccessful(SecondaryAbility.SKILL_SHOT, this.getPlayer())) {
            return damage;
        }
        final double damageBonusPercent = Math.min(this.getSkillLevel() / Archery.skillShotIncreaseLevel * Archery.skillShotIncreasePercentage, Archery.skillShotMaxBonusPercentage);
        return Math.min(damage * damageBonusPercent, Archery.skillShotMaxBonusDamage);
    }
    
    public static Class<?> inject()
    {
    	return ArcheryManager.class;
    }
    
    private void incrementTrackerValue(LivingEntity target)
    {
    	if(this.incrementTrackerValue == null)
    	{
    		try {
				this.incrementTrackerValue = Archery.class.getDeclaredMethod("incrementTrackerValue", LivingEntity.class);
	    		this.incrementTrackerValue.setAccessible(true);
			} 
    		catch (NoSuchMethodException | SecurityException e) 
    		{
				e.printStackTrace();
			}
    	}
    }
}