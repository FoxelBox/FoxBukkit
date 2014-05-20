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
package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.RemoteEntities;
import de.kumpelblase2.remoteentities.api.RemoteEntity;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import org.bukkit.Location;

public class SpawnUtilsNPCDependency {
	public static EntityManager entityManager = null;

	public static RemoteEntity makeNPC(String name, Location location) {
		if(entityManager == null)
			entityManager = RemoteEntities.createManager(YiffBukkit.instance);
		RemoteEntity entity = entityManager.createNamedEntity(RemoteEntityType.Human, location, name);
		entity.setPushable(false);
		entity.setStationary(true, true);
		return entity;
	}
}
