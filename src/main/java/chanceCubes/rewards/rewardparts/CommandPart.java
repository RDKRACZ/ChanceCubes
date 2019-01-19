package chanceCubes.rewards.rewardparts;

import chanceCubes.rewards.variableTypes.IntVar;
import chanceCubes.rewards.variableTypes.StringVar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommandPart
{
	private StringVar command;

	private IntVar delay = new IntVar(0);

	public CommandPart(String command)
	{
		this(command, 0);
	}

	public CommandPart(String command, int delay)
	{
		this(new StringVar(command), new IntVar(delay));
	}

	public CommandPart(StringVar command)
	{
		this(command, new IntVar(0));
	}

	public CommandPart(StringVar command, IntVar delay)
	{
		this.command = command;
		this.delay = delay;
	}

	public String getRawCommand()
	{
		return command.getValue();
	}

	public int getDelay()
	{
		return delay.getIntValue();
	}

	public void setDelay(int delay)
	{
		this.setDelay(new IntVar(delay));
	}

	public void setDelay(IntVar delay)
	{
		this.delay = delay;
	}

	public String getParsedCommand(World world, int x, int y, int z, EntityPlayer player)
	{
		String parsedCommand = command.getValue();
		parsedCommand = parsedCommand.replace("%player", player.getCommandSenderEntity().getName());
		parsedCommand = parsedCommand.replace("%x", "" + x);
		parsedCommand = parsedCommand.replace("%y", "" + y);
		parsedCommand = parsedCommand.replace("%z", "" + z);
		parsedCommand = parsedCommand.replace("%px", "" + player.posX);
		parsedCommand = parsedCommand.replace("%py", "" + player.posY);
		parsedCommand = parsedCommand.replace("%pz", "" + player.posZ);

		return parsedCommand;
	}
}
