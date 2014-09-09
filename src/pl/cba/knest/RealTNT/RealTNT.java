package pl.cba.knest.RealTNT;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RealTNT extends JavaPlugin implements Listener{
	private Random r = new Random();
	private final static double s = 3;
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH)
	public void onExplode(EntityExplodeEvent e){
		if(e.isCancelled()) return;
		World w = e.getEntity().getWorld();
		Entity en = e.getEntity();
		Location center = en.getLocation();
		Iterator<Block> i = e.blockList().iterator();
		//double chance = 1/e.getYield();
		
		while(i.hasNext()){
			Block b = i.next();
			Material type = b.getType();
			if(r.nextDouble()>0.5 && type != Material.TNT) continue;
			byte data = b.getData();
			switch(type){
			case PISTON_BASE:
			case PISTON_STICKY_BASE:
			case PISTON_EXTENSION:
			case LEAVES:
			case LEAVES_2:
			case OBSIDIAN:
			case ENCHANTMENT_TABLE:
			case REDSTONE_ORE:
			case GLOWING_REDSTONE_ORE:
			case DIAMOND_ORE:
			case LAPIS_ORE:
			case COAL_ORE:
			case EMERALD_ORE:
				continue;
			case STONE:
				if(data==0) type = Material.COBBLESTONE;
				break;
			case LOG:
			case LOG_2:
				data = (byte) ((data & 3) + (r.nextInt(3)<<2));
				break;
			case GRASS:
				type = Material.DIRT;
				data = 0;
				break;
			default:
				if(b.isLiquid()) continue;
				if(!type.isBlock()) continue;
				if(!type.isSolid()) continue;
				if(type.isTransparent()) continue;
				if(b.getState() instanceof InventoryHolder) continue;
			}
			Entity fe = null;
			if(type == Material.TNT){
				TNTPrimed tnt = w.spawn(b.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);
				tnt.setFuseTicks(5+r.nextInt(35));
				fe = tnt;
			}else{
				FallingBlock fw = w.spawnFallingBlock(b.getLocation().add(0, 0.5, 0), type, data);
				fw.setDropItem(r.nextDouble()<0.5);
				fe = fw;
			}
			Vector v = b.getLocation().add(0.5, 0.5, 0.5).toVector().subtract(center.toVector());
			double l = 3d+v.length()*0.1;
			v.normalize();
			v.multiply(s/l);
			v.setY(v.getY()+0.2);
			v.add(en.getVelocity());
			fe.setVelocity(v);
			b.setType(Material.AIR);
			i.remove();
		}
		
		
	}
}
