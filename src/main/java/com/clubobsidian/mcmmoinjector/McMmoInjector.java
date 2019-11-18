package com.clubobsidian.mcmmoinjector;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;

public class McMmoInjector extends JavaPlugin {

	@Override
	public void onLoad()
	{
		boolean skillManagersInjected = this.injectSkillManagers();
		if(skillManagersInjected)
		{
			this.getLogger().log(Level.INFO, "mcMMO skill managers were successfully injected!");
		}
		else
		{
			this.getLogger().log(Level.INFO, "Skill managers were not able to be injected, is mcMMO installed?");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		this.saveResource("config.yml", false);
		
		FileConfiguration config = this.getConfig();
		for(String skill : config.getStringList("disabled-skills"))
		{
			SkillManagerAPI.addSkillCallback(new SkillCallback() {

				@Override
				public boolean call(Player player) 
				{
					return false;
				}
				
			}, Skill.valueOf(skill.toUpperCase()));
		}
	}
	
	private boolean injectSkillManagers()
	{
		this.getLogger().log(Level.INFO, "Loaded MCMMOmanager...attempting to inject skills into classpath");
		this.getLogger().log(Level.INFO, "Attempting to inject classes into classpath..");
		try
		{
			AcrobaticsManager.inject();
			ArcheryManager.inject();
			AxesManager.inject();
			SwordsManager.inject();
			UnarmedManager.inject();
			MiningManager.inject();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}