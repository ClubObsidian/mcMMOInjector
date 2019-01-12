package com.gmail.nossr50.skills.mining;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.gmail.nossr50.skills.mining.BlastMining.Tier;

public class TierReflection {

	private static Method toNumerical;
	private static Method getOreBonus;
	private static Method getLevel;
	private static Method getDebrisReduction;
	private static Method getDropMultiplier;
	private static Method getBlastRadiusModifier;
	private static Method getBlastDamageDecrease;

	public static int toNumerical(Tier tier)
	{
		if(toNumerical == null)
		{
			try 
			{
				toNumerical = tier.getClass().getDeclaredMethod("toNumerical");
				toNumerical.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (int) toNumerical.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static double getOreBonus(Tier tier)
	{
		if(getOreBonus == null)
		{
			try 
			{
				getOreBonus = tier.getClass().getDeclaredMethod("getOreBonus");
				getOreBonus.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (int) getOreBonus.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int getLevel(Tier tier)
	{
		if(getLevel == null)
		{
			try 
			{
				getLevel = tier.getClass().getDeclaredMethod("getLevel");
				getLevel.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (int) getLevel.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}

	public static double getDebrisReduction(Tier tier)
	{
		if(getDebrisReduction == null)
		{
			try 
			{
				getDebrisReduction = tier.getClass().getDeclaredMethod("getDebrisReduction");
				getDebrisReduction.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (double) getDebrisReduction.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int getDropMultiplier(Tier tier)
	{
		if(getDebrisReduction == null)
		{
			try 
			{
				getDropMultiplier = tier.getClass().getDeclaredMethod("getDropMultiplier");
				getDropMultiplier.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (int) getDropMultiplier.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int getBlastRadiusModifier(Tier tier)
	{
		if(getBlastRadiusModifier == null)
		{
			try 
			{
				getBlastRadiusModifier = tier.getClass().getDeclaredMethod("getBlastRadiusModifier");
				getBlastRadiusModifier.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (int) getBlastRadiusModifier.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int getBlastDamageDecrease(Tier tier)
	{
		if(getBlastDamageDecrease == null)
		{
			try 
			{
				getBlastDamageDecrease= tier.getClass().getDeclaredMethod("getBlastDamageDecrease");
				getBlastDamageDecrease.setAccessible(true);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			return (int) getBlastDamageDecrease.invoke(tier);
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
}
