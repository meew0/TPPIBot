package tterrag.tppibot.commands;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.tppibot.Main;

public class AddCommand extends Command
{
    private static String toAdd;
    
    public AddCommand()
    {
        super("addcmd", PermLevel.OP);
    }

    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(user, "This requires at least two args, [command name] and [message]!");
            return false;
        }
        
        String cmdName = args[0];
        
        args = ArrayUtils.remove(args, 0);
        
        toAdd = "";
        for (String s : args)
        {
            toAdd += s + " ";    
        }
        
        toAdd = toAdd.substring(0, toAdd.length() - 1);
        
        Main.bot.registerCommand(new Command(cmdName, PermLevel.ANY)
        {
            @Override
            public boolean onCommand(String channel, String user, String... args)
            {
                sendMessage(channel, toAdd);
                return true;
            }
        });
        
        return true;
    }
}