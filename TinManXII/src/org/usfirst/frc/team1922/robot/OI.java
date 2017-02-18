package org.usfirst.frc.team1922.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import org.ozram1922.Tuple;
import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;
import org.usfirst.frc.team1922.robot.commands.CommandRetrieval;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI implements CfgInterface{

	public enum TriggerAction
	{
		kWhileHeld,
		kWhenPressed,
		kWhenReleased
	}
	
	public static TriggerAction StringToTriggerAction(String action)
	{
		switch(action)
		{
		default:
		case "WhenPressed":
			return TriggerAction.kWhenPressed;
		case "WhenReleased":
			return TriggerAction.kWhenReleased;
		case "WhileHeld":
			return TriggerAction.kWhileHeld;
		}
	}
	
	public static String TriggerActionToString(TriggerAction action)
	{
		switch(action)
		{
		default:
		case kWhenPressed:
			return "WhenPressed";
		case kWhenReleased:
			return "WhenReleased";
		case kWhileHeld:
			return "WhileHeld";
		
		}
	}
	/*
	 * 
	 * Config Variables
	 * 
	 */
	
	//HashMap<Tuple<Joystick Name, Button Number>, Tuple<Command instance, trigger action type>>
	protected HashMap<Tuple<String,Integer>, Tuple<Command, TriggerAction>> mCommandMap = new HashMap<Tuple<String,Integer>, Tuple<Command, TriggerAction>>();
	
	/*
	 * 
	 * Member Variables
	 * 
	 */
	//HashMap<Joystick name, Tuple<Joystick ID, Joystick instance>>
	HashMap<String, Tuple<Integer,Joystick>> mJoysticks = new HashMap<String, Tuple<Integer,Joystick>>();
	
	ArrayList<JoystickButton> mButtonCommands = new ArrayList<JoystickButton>();
	/*JoystickButton mNewBtn;
	Joystick mTestJoy = new Joystick(0);
	DriveForward cmd = new DriveForward();*/
	
	public Joystick GetLeftJoystick()
	{
		return mJoysticks.get("LeftStick").y;
	}
	
	public Joystick GetRightJoystick()
	{
		return mJoysticks.get("RightStick").y;
	}
	
	public Joystick GetOpJoystick()
	{
		return  mJoysticks.get("OpStick").y;
	}
	
	public static double NormalizeThrottle(Joystick joystick)
	{
		return (joystick.getThrottle() + 1.0) / 2.0;
	}
	
	/*
	 * 
	 * Member Functions
	 * 
	 */
	
	public OI()
	{
		//Reconstruct();
	}
	public void Reconstruct()
	{
		mButtonCommands.clear();
		
		//setup all of the command bindings with the loaded XML data
	    for(Map.Entry<Tuple<String,Integer>, Tuple<Command, TriggerAction>> pair : mCommandMap.entrySet())
		{
			JoystickButton j = new JoystickButton(mJoysticks.get(pair.getKey().x).y, pair.getKey().y) ;
			switch(pair.getValue().y)
			{
			default:
			case kWhenPressed:
				j.whenPressed(pair.getValue().x);
				break;
			case kWhenReleased:
				j.whenReleased(pair.getValue().x);
				break;
			case kWhileHeld:
				j.whileHeld(pair.getValue().x);
				break;
			}
			System.out.println(pair.getValue().toString());
			mButtonCommands.add(j);
		}
		
		
		//mNewBtn = new JoystickButton(mTestJoy, 9);
		//mNewBtn.whenPressed(cmd);
		
	}
	
	/*public float GetLeftPower()
	{
		Joystick LeftStick = mJoysticks.get("LeftJoystick").y;
		return LeftStick.getAxisChannel(AxisType.kY);
	}
	
	public float GetRightPower()
	{
		Joystick RightStick = mJoysticks.get("RightJoystick").y;
		return RightStick.getAxisChannel(AxisType.kY);
	}*/
	
	public Joystick GetJoystick(String name)
	{
		return this.mJoysticks.get(name).y;
	}

	/*
	 * 
	 * Override Functions
	 * 
	 */

	@Override
	public boolean Deserialize(CfgElement element) {
		//System.out.println("TEST AGAIN");
		
		//get children
		mJoysticks.clear();
		NodeList joysticks = element.mInternalElement.getElementsByTagName("Joystick");
		for(int i = 0; i < joysticks.getLength(); ++i)
		{
			Element thisElement = (Element)joysticks.item(i);
			mJoysticks.put(
					thisElement.getAttribute("Name"), 
					new Tuple<Integer,Joystick>(
							Integer.parseInt(thisElement.getAttribute("Id")), 
							new Joystick(Integer.parseInt(thisElement.getAttribute("Id")))));
		}
		
		//make list of joysticks
		
		//make map of buttons and commands
		mCommandMap.clear();
		NodeList commands = element.mInternalElement.getElementsByTagName("Command");
		for(int i = 0; i < commands.getLength(); ++i)
		{
			Element thisElement = (Element)commands.item(i);
			System.out.println(thisElement.getAttribute("Name"));
			//Command thisCommand = CommandRetrieval.GetCommandFromName(thisElement.getAttribute("Name"));
			mCommandMap.put(
					new Tuple<String,Integer>(
							thisElement.getAttribute("Joystick"), 
							Integer.parseInt(thisElement.getAttribute("Button"))), 
					new Tuple<Command, TriggerAction>(CommandRetrieval.GetCommandFromName(thisElement.getAttribute("Name"), ConvertNodeMap(thisElement.getAttributes())), 
							StringToTriggerAction(thisElement.getAttribute("TriggerType"))));
		}
		
		Reconstruct();
		
		return true;
	}
	
	private static HashMap<String, String> ConvertNodeMap(NamedNodeMap map)
	{
		HashMap<String, String> ret = new HashMap<String, String>();
		for(int i = 0; i < map.getLength(); ++i)
		{
			Node node = map.item(i);
			ret.put(node.getNodeName(), node.getNodeValue());
		}
		return ret;
	}

	@Override
	public CfgElement Serialize(CfgElement element, CfgDocument doc) {

	    Iterator<Entry<String, Tuple<Integer,Joystick>>> it = mJoysticks.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, Tuple<Integer,Joystick>> pair = (Entry<String, Tuple<Integer,Joystick>>)it.next();
			CfgElement ej = doc.CreateElement("Joystick");
			ej.SetAttribute("Name", pair.getKey());
			ej.SetAttribute("Id", pair.getValue().x);
			element.AppendChild(ej);
		}

	    Iterator<Entry<Tuple<String,Integer>, Tuple<Command, TriggerAction>>> it0 = mCommandMap.entrySet().iterator();
		while(it0.hasNext())
		{
			Entry<Tuple<String,Integer>, Tuple<Command, TriggerAction>> pair = (Entry<Tuple<String,Integer>, Tuple<Command, TriggerAction>>)it0.next();
			CfgElement ec = doc.CreateElement("Command");
			ec.SetAttribute("Name", pair.getValue().x.getName());
			ec.SetAttribute("TriggerType", TriggerActionToString(pair.getValue().y));
			ec.SetAttribute("JoystickId", pair.getKey().x);
			ec.SetAttribute("ButtonId", pair.getKey().y);
			element.AppendChild(ec);
		}
		
		return element;		
	}

	@Override
	public void MakeCfgClassesNull() {
		mCommandMap.clear();
		mJoysticks.clear();
		mButtonCommands.clear();		
	}

	@Override
	public String GetElementTitle() {
		return "OI";
	}
	
	//this outputs all of the button mapped commands onto the smart dashboard
	public void WriteSmartDashboardItems() {

		/*for(Map.Entry<Tuple<String,Integer>, Tuple<Command, TriggerAction>> entry : mCommandMap.entrySet())
		{
			
		}*/
	}


    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);
    
    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.
    
    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:
    
    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());
    
    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());
    
    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());
}

