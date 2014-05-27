package tterrag.tppibot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jibble.pircbot.PircBot;

import tterrag.tppibot.commands.Command;
import tterrag.tppibot.runnables.ReminderProcess;
import tterrag.tppibot.util.IRCUtils;

public class TPPIBot extends PircBot
{
    public String controlChar;
    
    public ArrayList<Command> commands;
    
    private Map<String, Boolean> reminderMap;

    public TPPIBot()
    {
        this.setName("TPPIBot");

        reminderMap = new HashMap<String, Boolean>();
        commands = new ArrayList<Command>();
        
        runThreads();
        
        controlChar = "`";
    }

    private void runThreads()
    {
        Thread reminderThread = new Thread(new ReminderProcess(this,
                
                "[Reminder] You can open the chat and press tab to talk with us!",
                "[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense."
        ));
        
        reminderThread.start();
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        if (message.startsWith(controlChar))
        {
            message = pruneMessage(message);
            String[] args = message.split(" ");
            
            if (args.length < 1) return;
            
            for (Command c : commands)
            {
                if (c.getName().equalsIgnoreCase(args[0]))
                {
                    if (IRCUtils.userMatchesPerms(channel, sender, c.getPermLevel()))
                    {
                        c.onCommand(channel, sender, ArrayUtils.remove(args, 0));
                    }
                    else
                    {
                        this.sendNotice(sender, "You have no permission, you must be at least: " + c.getPermLevel().toString());
                    }
                }
            }
        }
    }

    private String pruneMessage(String message)
    {
        return message.substring(controlChar.length());
    }

    public void join(String channel)
    {
        this.joinChannel(channel.startsWith("#") ? channel : "#" + channel);
        this.reminderMap.put(channel, true);
    }

    public boolean areRemindersEnabledFor(String channel)
    {
        synchronized (reminderMap)
        {
            if (reminderMap == null)
                return false;
            else
                return reminderMap.get(channel);
        }
    }

    public void registerCommand(Command command)
    {
        commands.add(command);
    }
}