/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@ICommand.Names("sethomelimit")
@ICommand.BooleanFlags("a")
@ICommand.Help("Sets home location limit for a player")
@ICommand.Permission("yiffbukkit.teleport.admin.sethomelocationlimit")
public class SetHomeLocationLimit extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);
		int newLimit = Integer.parseInt(args[1]);

		UUID playerUUID = UUID.fromString(args[0]);

		if(booleanFlags.contains('a')) {
			newLimit += playerHelper.getPlayerHomePositionLimit(playerUUID);
		}
		playerHelper.setPlayerHomePositionLimit(playerUUID, newLimit);
	}
}
