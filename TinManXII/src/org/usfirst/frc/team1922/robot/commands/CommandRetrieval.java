package org.usfirst.frc.team1922.robot.commands;

import java.lang.reflect.Constructor;

import edu.wpi.first.wpilibj.command.Command;

public class CommandRetrieval {
	
	public static final String Prefix = "org.usfirst.frc.team1922.robot.commands.";
	public static Command GetCommandFromName(String name)
	{
		String compName = Prefix + name;
		Class<?> clazz;
		Object instance = null;
		try {
			clazz = Class.forName(compName);
			Constructor<?> constructor = clazz.getConstructor();
			instance = constructor.newInstance();
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

}
