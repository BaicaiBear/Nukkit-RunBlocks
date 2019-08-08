package top.bearcabbage.RunBlocks;

import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;


public class RunBlocks extends PluginBase implements Listener {


    //定义全局变量
    public int bt;
    public Block BlockType; //跑酷方块种类
    public int tc;  //游戏时间
    public Location localb;
    public Location locale; //游戏范围起止点对角
    int tmp1;


    @Override
    public void onLoad()
    {
        this.getLogger().info("RunBlocks is loading...");
        saveResource("config.yml",false);
        saveDefaultConfig();

    }

    @Override
    public void onEnable()
    {
        double x;
        double y;
        double z;

        bt = this.getServer().getConfig().getInt("BlockType",-1);
        if(bt==-1)
            BlockType = Block.get(1);
        else
            rbBlockType = Block.get(bt);
        tc = this.getServer().getConfig().getInt("GameTime",-1);

        x = this.getServer().getConfig().getDouble("BeginX",0);
        y = this.getServer().getConfig().getDouble("BeginY",0);
        z = this.getServer().getConfig().getDouble("BeginZ",0);
        localb = new Location(x,y,z);

        x = this.getServer().getConfig().getDouble("EndX",0);
        y = this.getServer().getConfig().getDouble("EndY",0);
        z = this.getServer().getConfig().getDouble("EndZ",0);
        locale = new Location(x,y,z);

        tmp1 = 0;

        this.getLogger().info("RunBlocks has loaded successfully! Bear Cabbage is the cutest bear!");
    }

    @Override
    public void onDisable()
    {
        this.getServer().getConfig().save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String zhiling, String[] liebiao)
    {
        if(zhiling.equals("runblocks")||zhiling.equals("rb"))
        {

            //设置游戏范围
            if(liebiao[0].equals("set"))
            {
                if(!sender.isPlayer())
                {
                    sender.sendMessage("§5[RunBlocks]请在游戏内设置游戏区域。");
                    return false;
                }

                if(liebiao[1].equals("begin"))
                {
                    if(tmp1!=0)
                    {
                        sender.sendMessage("§5[RunBlocks]您已设置起点，请继续使用/rb set end设置终点。");
                        return false;
                    }
                    try
                    {
                        localb = this.getServer().getPlayerExact(sender.getName()).getLocation();
                    }
                    catch (Exception e){
                        sender.sendMessage("§4[RunBlocks]未知错误！");
                        return false;
                    }
                    tmp1 = 1;
                    sender.sendMessage("§6[RunBlocks]起点设置为您的位置成功，请继续使用/rb set end设置终点，否则会引起错误。");
                    return true;
                }
                else if(liebiao[1].equals("end"))
                {
                    if(tmp1!=1)
                    {
                        sender.sendMessage("§5[RunBlocks]请先使用/rb set begin设置起点。");
                        return false;
                    }
                    try
                    {
                        locale = this.getServer().getPlayerExact(sender.getName()).getLocation();
                    }
                    catch (Exception e){
                        sender.sendMessage("§4[RunBlocks]未知错误！");
                        return false;
                    }
                    tmp1 = 0;
                    this.getServer().getConfig().set("BeginX", localb.getX());
                    this.getServer().getConfig().set("BeginY", localb.getY());
                    this.getServer().getConfig().set("BeginZ", localb.getZ());
                    this.getServer().getConfig().set("EndX", locale.getX());
                    this.getServer().getConfig().set("EndY", locale.getY());
                    this.getServer().getConfig().set("EndZ", locale.getZ());
                    this.getServer().getConfig().save();
                    sender.sendMessage("§6[RunBlocks]终点设置为您的位置成功，现在可以使用/rb game start开始游戏啦！");
                    return true;
                }
                else
                {
                    sender.sendMessage("§5[RunBlocks]指令语法有误，请使用/rb set begin开始设置游戏范围。");
                    return false;
                }
            }

            //开始游戏以及游戏进行中
            if(liebiao[1].equals("game"))
            {
                if (liebiao[2].equals("start"))
                {
                    boolean result;
                    result = onGame.start(this.getServer().getPlayer(sender.getName()), localb, locale);
                    if (result == true)
                        return true;
                    sender.sendMessage("§4[RunBlocks]游戏出现异常，请重试。");
                    if (onEnd.clean(localb, locale))
                        return false;
                    this.getLogger().warning("§4[RunBlocks]严重未知错误！建议重装此插件");
                    return false;
                }
            }
        }
        return false;
    }


}
