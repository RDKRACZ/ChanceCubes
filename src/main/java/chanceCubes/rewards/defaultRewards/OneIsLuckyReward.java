package chanceCubes.rewards.defaultRewards;

import chanceCubes.CCubesCore;
import chanceCubes.blocks.CCubesBlocks;
import chanceCubes.tileentities.TileChanceCube;
import chanceCubes.util.RewardsUtil;
import chanceCubes.util.Scheduler;
import chanceCubes.util.Task;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class OneIsLuckyReward extends BaseCustomReward
{
	public OneIsLuckyReward()
	{
		super(CCubesCore.MODID + ":one_is_lucky", 0);
	}

	@Override
	public void trigger(final ServerWorld world, final BlockPos pos, PlayerEntity player, JsonObject settings)
	{
		RewardsUtil.sendMessageToNearPlayers(world, pos, 32, "A Lucky Block Salute");
		SignTileEntity sign = new SignTileEntity();
		sign.setText(0, new StringTextComponent("One is lucky"));
		sign.setText(1, new StringTextComponent("One is not"));
		sign.setText(3, new StringTextComponent("#OGLuckyBlocks"));
		boolean leftLucky = RewardsUtil.rand.nextBoolean();
		TileChanceCube leftCube = new TileChanceCube(leftLucky ? 100 : -100);
		TileChanceCube rightCube = new TileChanceCube(!leftLucky ? 100 : -100);

		if(RewardsUtil.placeBlock(CCubesBlocks.CHANCE_CUBE.getDefaultState(), world, pos.add(-1, 0, 0)))
			world.setTileEntity(pos.add(-1, 0, 0), leftCube);
		if(RewardsUtil.placeBlock(Blocks.OAK_SIGN.getDefaultState(), world, pos))
			world.setTileEntity(pos, sign);
		if(RewardsUtil.placeBlock(CCubesBlocks.CHANCE_CUBE.getDefaultState(), world, pos.add(1, 0, 0)))
			world.setTileEntity(pos.add(1, 0, 0), rightCube);

		Scheduler.scheduleTask(new Task("One_Is_Lucky_Reward", 6000, 10)
		{
			@Override
			public void callback()
			{
				world.setBlockState(pos.add(-1, 0, 0), Blocks.AIR.getDefaultState());
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				world.setBlockState(pos.add(1, 0, 0), Blocks.AIR.getDefaultState());
			}

			@Override
			public void update()
			{
				if(world.isAirBlock(pos.add(-1, 0, 0)) || world.isAirBlock(pos.add(1, 0, 0)))
				{
					this.callback();
					Scheduler.removeTask(this);
				}
				else if(world.isAirBlock(pos.add(0, 0, 0)))
				{
					SignTileEntity sign3 = new SignTileEntity();
					sign3.setText(0, new StringTextComponent("Middle? Really?"));
					sign3.setText(1, new StringTextComponent("Fine you don't"));
					sign3.setText(2, new StringTextComponent("get a cube then!"));
					sign3.setText(3, new StringTextComponent("(¬_¬)"));
					world.setBlockState(pos.add(0, 0, 0), Blocks.OAK_SIGN.getDefaultState());
					world.setTileEntity(pos.add(0, 0, 0), sign3);
					world.setBlockState(pos.add(-1, 0, 0), Blocks.AIR.getDefaultState());
					world.setBlockState(pos.add(1, 0, 0), Blocks.AIR.getDefaultState());
					Scheduler.removeTask(this);
				}
			}
		});
	}
}