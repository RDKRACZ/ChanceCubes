package chanceCubes.items;

import chanceCubes.blocks.CCubesBlocks;
import chanceCubes.client.gui.RewardSelectorPendantGui;
import chanceCubes.registry.ChanceCubeRegistry;
import chanceCubes.registry.GiantCubeRegistry;
import chanceCubes.rewards.IChanceCubeReward;
import chanceCubes.tileentities.TileGiantCube;
import chanceCubes.util.GiantCubeUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class ItemRewardSelectorPendant extends BaseChanceCubesItem
{

	public ItemRewardSelectorPendant()
	{
		super((new Item.Properties()).maxStackSize(1), "reward_selector_pendant");
		super.addLore("Shift right click to change the reward.");
		super.addLore("Right click a Chance Cube to summon the reward.");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		player.setActiveHand(hand);
		if(player.isSneaking() && world.isRemote)
			Minecraft.getInstance().displayGuiScreen(new RewardSelectorPendantGui(player, stack));
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		if(context.getWorld().isRemote)
			return ActionResultType.PASS;
		if(context.isPlacerSneaking())
			return ActionResultType.FAIL;

		if(context.getItem().getTag() != null && context.getItem().getTag().contains("Reward"))
		{
			if(context.getWorld().getBlockState(context.getPos()).getBlock().equals(CCubesBlocks.CHANCE_CUBE))
			{
				context.getWorld().setBlockState(context.getPos(), Blocks.AIR.getDefaultState());
				IChanceCubeReward reward = ChanceCubeRegistry.INSTANCE.getRewardByName(context.getItem().getTag().getString("Reward"));
				if(reward != null)
					ChanceCubeRegistry.INSTANCE.triggerReward(reward, context.getWorld(), context.getPos(), context.getPlayer());
				else
					context.getPlayer().sendMessage(new StringTextComponent("That reward does not exist for this cube!"));
			}
			else if(context.getWorld().getBlockState(context.getPos()).getBlock().equals(CCubesBlocks.GIANT_CUBE))
			{
				TileEntity ent = context.getWorld().getTileEntity(context.getPos());
				if(!(ent instanceof TileGiantCube))
					return ActionResultType.FAIL;
				TileGiantCube giant = (TileGiantCube) ent;
				IChanceCubeReward reward = GiantCubeRegistry.INSTANCE.getRewardByName(context.getItem().getTag().getString("Reward"));
				if(reward != null)
					GiantCubeRegistry.INSTANCE.triggerReward(reward, context.getWorld(), giant.getMasterPostion(), context.getPlayer());
				else
					context.getPlayer().sendMessage(new StringTextComponent("That reward does not exist for this cube!"));
				GiantCubeUtil.removeStructure(giant.getMasterPostion(), context.getWorld());
			}
		}
		return ActionResultType.SUCCESS;
	}
}