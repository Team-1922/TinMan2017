package org.usfirst.frc.team1922.robot.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CommandRetrieval {
	
	public static final String Prefix = "org.usfirst.frc.team1922.robot.commands.";
	
	//not all parameters must be present or in the correct order default type values will otherwise be used
	public static Command GetCommandFromName(String name, HashMap<String, String> param)
	{
		if(!areTypesLoaded)
		{
			LoadTypes();
		}
		
		String compName = Prefix + name;
		Class<?> clazz;
		Object instance = null;
		try {
			SmartDashboard.putString(name, "");
			clazz = Class.forName(compName);
			SmartDashboard.putString(name, "Class Gotten");
			Constructor<?> constructor = clazz.getConstructors()[0];
			SmartDashboard.putString(name, "Constructor Gotten");
			Parameter[] paramNames = constructor.getParameters();
			SmartDashboard.putString(name, "Parameters Gotten");

			//TODO: something is failing after here with commands with parameters
			if(paramNames.length > 0)
			{
				Object[] paramValues = new Object[paramNames.length];
				
				//iterate through the parameters and parse them
				for(int i = 0; i < paramNames.length; ++i)
				{
					if(param.containsKey(paramNames[i].getName()))
					{
						//Parse the string input into an object and store it
						paramValues[i] = 
								ParseType(
										paramNames[i], 
										param.get(paramNames[i].getName()));
					}
					else
					{
						paramValues[i] = GetDefaultValue(paramNames[i]);
					}
				}
				instance = constructor.newInstance(paramValues);
			}
			else
			{
				instance = constructor.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
			instance = null;
		}
		
		return (Command)instance;
		/*switch(name)
		{
		case "OverwriteXMLCfg":
			return new OverwriteXMLCfg();
		case "ReloadXMLCfg":
			return new ReloadXMLCfg();
		case "DriveForward":
			return new DriveForward();
		default:
			return null;
		}*/
	}
	
	private static boolean areTypesLoaded = false;
	private static String intName;
	private static String doubleName;
	private static String booleanName;
	private static String floatName;
	private static void LoadTypes()
	{
		intName = Integer.TYPE.getName();
		doubleName = Double.TYPE.getName();
		booleanName = Boolean.TYPE.getName();
		floatName = Float.TYPE.getName();		
		
		areTypesLoaded = true;
	}

	private static Object ParseType(Parameter param, String value)
	{
		String name = param.getType().getTypeName();

		if(name == intName)
		{
			return Integer.parseInt(value);
		}
		else if(name == doubleName)
		{
			return Double.parseDouble(value);
		}
		else if(name == booleanName)
		{
			return Boolean.parseBoolean(value);
		}
		else if(name == floatName)
		{
			return Float.parseFloat(value);
		}
		else
		{
			return value;
		}
	}

	private static Object GetDefaultValue(Parameter param)
	{
		String name = param.getType().getTypeName();

		if(name == intName)
		{
			return 0;
		}
		else if(name == doubleName)
		{
			return 0.0;
		}
		else if(name == booleanName)
		{
			return false;
		}
		else if(name == floatName)
		{
			return 0.0f;
		}
		else
		{
			return null;
		}
	}
}
