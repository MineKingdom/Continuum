package net.minekingdom.continuum;

import net.minekingdom.continuum.portal.Portal;
import net.minekingdom.continuum.utils.ConfigUtils;
import net.minekingdom.continuum.world.Dimension;
import net.minekingdom.continuum.world.Universe;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ContinuumListener implements Listener {
	
	private Continuum plugin;

	public ContinuumListener(Continuum plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {		
		final Player player = event.getPlayer();
		final Location target = event.getTo();
		
		Universe toWorld = Universe.get(event.getTo().getWorld());
		Universe fromWorld = Universe.get(event.getFrom().getWorld());
		
		if (toWorld != null && toWorld.equals(fromWorld)) {
			return;
		}
		
		Dimension dimTo = Dimension.get(event.getTo().getWorld());
		
		if (dimTo != null && !dimTo.canAccess(player)) {
			event.setCancelled(true);
			return;
		}
		
		ConfigUtils.savePlayer(player, event.getFrom().getWorld());
		ConfigUtils.loadPlayer(player, event.getTo().getWorld());
	}
	
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		final Player player = event.getPlayer();
		
		Block portalBlock = null;
		portalSearch:
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				for (int k = -1; k < 2; ++k) {
					Block b = player.getLocation().add(i, j, k).getBlock();
					switch (b.getType()) {
						case PORTAL:
						case ENDER_PORTAL:
							portalBlock = b;
							break portalSearch;
						default: break;
					}
				}
			}
		}
		
		if (portalBlock == null) {
			event.setCancelled(true);
			return;
		}
		
		Material frame = null;
		switch (portalBlock.getType()) {
			case PORTAL: {
				Block b = portalBlock;
				while (b.getType().equals(Material.PORTAL)) {
					b = b.getLocation().add(0, -1, 0).getBlock();
				}
				frame = b.getType();
			} break;
			case ENDER_PORTAL: {
				Block b = portalBlock;
				while (b.getType().equals(Material.ENDER_PORTAL)) {
					b = b.getLocation().add(1, 0, 0).getBlock();
				}
				frame = b.getType();
			} break;
			default: break;
		}
		
		
		Universe world = Universe.get(player.getWorld());
		Portal portal = plugin.getWorldManager().getPortal(portalBlock.getType(), frame);
		
		if (world == null || portal == null) {
			event.setCancelled(true);
			return;
		}
		
		Dimension dim = portal.getOtherWorld(world, player.getWorld());
		
		if (dim == null) {
			event.setCancelled(true);
			return;
		}
		
		Location dest = event.getFrom().clone();
		dest.setWorld(dim.getHandle());
		dest = dest.multiply(dim.getScale());
		event.setTo(dest);
	}

}
