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
package de.doridian.yiffbukkit.yiffpoints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YBAccount {
	private final YBBank bank;

	private double balance;

	public YBAccount(YBBank bank, Map<String, List<String>> section) {
		this(bank);
		balance = Double.parseDouble(section.get("balance").get(0));
	}


	public YBAccount(YBBank bank) {
		this.bank = bank;
	}


	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
		bank.save();
	}


	public void addFunds(double cents) {
		balance += cents;
		bank.save();
	}

	public void useFunds(double cents) throws NotEnoughFundsException {
		if (balance < cents)
			throw new NotEnoughFundsException(cents - balance);

		balance -= cents;
		bank.save();
	}


	public Map<String, List<String>> save() {
		if (balance == 0.0)
			return null;

		Map<String, List<String>> section = new HashMap<>();
		section.put("balance", Arrays.asList(""+balance));
		return section;
	}
}
