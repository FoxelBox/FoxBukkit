package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.CacheMap;
import de.doridian.yiffbukkit.main.util.RedisManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class YiffBukkitPermissionHandler {
	public static final YiffBukkitPermissionHandler instance = new YiffBukkitPermissionHandler();

	class GroupWorld {
		public final String group;
		public final String world;
		
		public GroupWorld(String group, String world) {
			this.group = group;
			this.world = world;
		}
		
		@Override
		public boolean equals(Object other) {
			return (other instanceof  GroupWorld) && equals((GroupWorld)other);
		}

		public boolean equals(GroupWorld other) {
			return other.group.equals(this.group) && other.world.equals(this.world);
		}
		
		@Override
		public int hashCode() {
			return (group.hashCode() / 2) + (world.hashCode() / 2);
		}
	}

	private boolean loaded = false;
	private final Map<String,String> playerGroups = RedisManager.createKeptMap("playergroups");
	private final CacheMap<UUID,String> playerGroupCache = new CacheMap<>(10000L);
	private final HashMap<GroupWorld,HashSet<String>> groupPermissions = new HashMap<>();
	private final HashMap<GroupWorld,HashSet<String>> groupProhibitions = new HashMap<>();
	
	private String defaultWorld = "world";

	public void setDefaultWorld(String world) {
		defaultWorld = world;
	}

	public void load() {
		if(loaded) return;
		reload();
	}

	public void reload() {
		loaded = true;
		groupPermissions.clear();
		groupProhibitions.clear();

		final File permissionsDirectory = new File(YiffBukkit.instance.getDataFolder() + "/permissions");
		permissionsDirectory.mkdirs();
		File[] files = permissionsDirectory.listFiles();
		if (files == null)
			return;

		for(File file : files) {
			try {
				String currentWorld;
				GroupWorld currentGroupWorld = null;
				currentWorld = file.getName();
				if(currentWorld.indexOf('.') > 0) {
					currentWorld = currentWorld.substring(0, currentWorld.indexOf('.'));
				}
				HashSet<String> currentPermissions = null;
				HashSet<String> currentProhibitions = null;
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim().toLowerCase();
						if (line.length() < 1) continue;
						char c = line.charAt(0);
						if (c == '-') {
							line = line.substring(1).trim();
							currentPermissions.remove(line);
							currentProhibitions.add(line);
						} else if (c == '+') {
							line = line.substring(1).trim();
							currentPermissions.add(line);
							currentProhibitions.remove(line);
						} else {
							if (currentGroupWorld != null) {
								groupPermissions.put(currentGroupWorld, currentPermissions);
								groupProhibitions.put(currentGroupWorld, currentProhibitions);
							}
							int i = line.indexOf(' ');
							currentPermissions = new HashSet<>();
							currentProhibitions = new HashSet<>();
							if (i > 0) {
								currentGroupWorld = new GroupWorld(line.substring(0, i).trim(), currentWorld);
								GroupWorld tmp = new GroupWorld(line.substring(i + 1).trim(), currentWorld);
								currentPermissions.addAll(groupPermissions.get(tmp));
								currentProhibitions.addAll(groupProhibitions.get(tmp));
							} else {
								currentGroupWorld = new GroupWorld(line, currentWorld);
							}
						}
					}
					if (currentGroupWorld != null) {
						groupPermissions.put(currentGroupWorld, currentPermissions);
						groupProhibitions.put(currentGroupWorld, currentProhibitions);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {

	}

	public boolean has(CommandSender commandSender, String permission) {
		// Console can do everything
		return (!(commandSender instanceof Player)) || has((Player) commandSender, permission);
	}

	public boolean has(Player player, String permission) {
		return has(player.getWorld().getName(), player.getUniqueId(), permission);
	}

	public boolean has(String worldName, UUID uuid, String permission) {
		permission = permission.toLowerCase();
		if (AbusePotentialManager.isBlocked(uuid, permission))
			return false;

		GroupWorld currentGroupWorld = new GroupWorld(getGroup(uuid), worldName);

		HashSet<String> currentPermissions = groupPermissions.get(currentGroupWorld);
		if(currentPermissions == null) {
			currentGroupWorld = new GroupWorld(currentGroupWorld.group, defaultWorld);
			currentPermissions = groupPermissions.get(currentGroupWorld);
			if(currentPermissions == null) return false;
		}
		if(currentPermissions.contains(permission)) return true;

		HashSet<String> currentProhibitions = groupProhibitions.get(currentGroupWorld);
		if(currentProhibitions != null && currentProhibitions.contains(permission)) return false;

		int xpos = 0;
		String tperm = permission;
		while((xpos = tperm.lastIndexOf('.')) > 0) {
			tperm = tperm.substring(0, xpos);
			String tperm2 = tperm + ".*";
			if(currentProhibitions != null && currentProhibitions.contains(tperm2)) { currentProhibitions.add(permission); return false; }
			if(currentPermissions.contains(tperm2)) { currentPermissions.add(permission); return true; }
		}

		if(currentProhibitions != null && currentProhibitions.contains("*")) { currentProhibitions.add(permission); return false; }
		if(currentPermissions.contains("*")) { currentPermissions.add(permission); return true; }

		if(currentProhibitions == null) {
			currentProhibitions = new HashSet<>();
			groupProhibitions.put(currentGroupWorld, currentProhibitions);
		}
		currentProhibitions.add(permission);
		return false;
	}

	public boolean has(UUID uuid, String permission) {
		return has(defaultWorld, uuid, permission);
	}

	public String getGroup(UUID uuid) {
		String group = playerGroupCache.get(uuid);
		if(group == null)
			group = playerGroups.get(uuid.toString());
		if(group == null)
			group = "guest";
		playerGroupCache.put(uuid, group);
		return group;
	}

	public void setGroup(UUID uuid, String group) {
		group = group.toLowerCase();
		playerGroups.put(uuid.toString(), group);
		playerGroupCache.put(uuid, group);
		save();
	}

	public boolean inGroup(String world, UUID uuid, String group) {
		return getGroup(uuid).equalsIgnoreCase(group);
	}

	public boolean inGroup(UUID uuid, String group) {
		return inGroup(defaultWorld, uuid, group);
	}
}