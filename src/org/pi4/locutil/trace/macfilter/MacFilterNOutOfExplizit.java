/*
 * This file is part of Locutil2.
 * 
 * Copyright (c) 2007 Thomas King <king@informatik.uni-mannheim.de>,
 * University of Mannheim, Germany
 * 
 * All rights reserved.
 *
 * Loclib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Loclib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Loclib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.pi4.locutil.trace.macfilter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.pi4.locutil.MACAddress;
import org.pi4.locutil.Random;

public class MacFilterNOutOfExplizit extends MacFilterExplizit {
	
	int n = 0;
	HashSet<MACAddress> selectedN;
	
	public MacFilterNOutOfExplizit() {
		selectedN = new HashSet<MACAddress>();
	}
	
	public boolean contains(MACAddress mac) {
		return selectedN.contains(mac);
	}
	
	public void setN(int n) {
		this.n = n;
	}
	
	public void selectNOutOfExplizit() {
		if ((n != 0) && (macs.size() >= n)) {
			selectedN = new HashSet<MACAddress>();
			Vector<MACAddress> macsVector = new Vector<MACAddress>();
			macsVector.addAll(macs);
			for (int i = 0; i < n; ++i) {
				MACAddress selected = macsVector.remove(Random.nextInt(macsVector.size()));
				selectedN.add(selected);
			}
		} else {
			throw new IllegalArgumentException("n is not inside valid bounderies.");
		}
	}
	
	public HashSet<MACAddress> getMacs() {
		return selectedN;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("MacFilterNOutOfExplizit: ");
		if (selectedN.isEmpty()) {
			sb.append("Empty");
		}
		Iterator<MACAddress> it = selectedN.iterator();
		while (it.hasNext()) {
			MACAddress mac = it.next();
			sb.append(mac);
			if (it.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}
	
	public boolean isEmpty() {
		return selectedN.isEmpty();
	}
	
}
