package com.gmail.nossr50.skills.swords;

import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.clubobsidian.mcmmoinjector.SkillManagerAPI;
import com.clubobsidian.mcmmoinjector.Skill;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.skills.SkillUtils;

import java.util.Map;


import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permissible;

public class SwordsManager extends SkillManager
{
    public SwordsManager(final McMMOPlayer mcMMOPlayer) 
    {
        super(mcMMOPlayer, SkillType.SWORDS);
    }
    
    public boolean canActivateAbility() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.SWORDS) && this.mcMMOPlayer.getToolPreparationMode(ToolType.SWORD) && Permissions.serratedStrikes((Permissible)this.getPlayer());
    }
    
    public boolean canUseBleed() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.SWORDS) && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.BLEED);
    }
    
    public boolean canUseCounterAttack(final Entity target) 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.SWORDS) && target instanceof LivingEntity && Permissions.secondaryAbilityEnabled((Permissible)this.getPlayer(), SecondaryAbility.COUNTER);
    }
    
    public boolean canUseSerratedStrike() 
    {
        return !SkillManagerAPI.testFor(this.mcMMOPlayer.getPlayer(), Skill.SWORDS) && this.mcMMOPlayer.getAbilityMode(AbilityType.SERRATED_STRIKES) && Permissions.serratedStrikes((Permissible)this.getPlayer());
    }
    
    public void bleedCheck(final LivingEntity target) 
    {
        if (SkillUtils.activationSuccessful(SecondaryAbility.BLEED, this.getPlayer(), this.getSkillLevel(), this.activationChance)) 
        {
            if (this.getSkillLevel() >= AdvancedConfig.getInstance().getMaxBonusLevel(SecondaryAbility.BLEED)) 
            {
                BleedTimerTask.add(target, Swords.bleedMaxTicks);
            }
            else 
            {
                BleedTimerTask.add(target, Swords.bleedBaseTicks);
            }
            if (this.mcMMOPlayer.useChatNotifications()) 
            {
                this.getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding"));
            }
            if (target instanceof Player) 
            {
                final Player defender = (Player)target;
                if (UserManager.getPlayer(defender).useChatNotifications()) 
                {
                    defender.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Started"));
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	public void counterAttackChecks(final LivingEntity attacker, final double damage) 
    {
        if (Swords.counterAttackRequiresBlock && !this.getPlayer().isBlocking()) 
        {
            return;
        }
        if (SkillUtils.activationSuccessful(SecondaryAbility.COUNTER, this.getPlayer(), this.getSkillLevel(), this.activationChance)) 
        {
            CombatUtils.dealDamage(attacker, damage / Swords.counterAttackModifier, (LivingEntity)this.getPlayer());
            this.getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));
           
            if (attacker instanceof Player) 
            {
                ((Player)attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
            }
        }
    }
    
    public void serratedStrikes(final LivingEntity target, final double damage, final Map<EntityDamageEvent.DamageModifier, Double> modifiers) 
    {
        CombatUtils.applyAbilityAoE(this.getPlayer(), target, damage / Swords.serratedStrikesModifier, modifiers, this.skill);
        BleedTimerTask.add(target, Swords.serratedStrikesBleedTicks);
    }
    
    public static Class<?> inject()
    {
    	return SwordsManager.class;
    }
}