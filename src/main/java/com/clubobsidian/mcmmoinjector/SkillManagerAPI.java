package com.clubobsidian.mcmmoinjector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class SkillManagerAPI {

	private static Map<Skill, List<SkillCallback>> callbacks = new HashMap<>();

	public static void addSkillCallback(SkillCallback callback, Skill... skills)
	{
		for(Skill skill : skills)
		{
			List<SkillCallback> callbacks = SkillManagerAPI.callbacks.get(skill);
			if(callbacks == null)
			{
				callbacks = new ArrayList<>();
				SkillManagerAPI.callbacks.put(skill, callbacks);
			}
			callbacks.add(callback);
		}
	}
	
	public static boolean testFor(Player player, Skill skill)
	{
		List<SkillCallback> callbacks = SkillManagerAPI.callbacks.get(skill);
		if(callbacks == null)
		{
			return true;
		}
		
		for(SkillCallback call : callbacks)
		{
			if(!call.call(player))
			{
				return false;
			}
		}
		
		return true;
	}
}