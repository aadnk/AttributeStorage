// The MIT License (MIT)
//
// Copyright (c) 2015 Kristian Stangeland
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
// files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
// modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
// Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
// ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.comphenix.attribute.mocking;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.Item;
import net.minecraft.server.v1_7_R3.StatisticList;

// Will have to be updated for every version though
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Used to ensure that ProtocolLib and Bukkit is prepared to be tested.
 * 
 * @author Kristian
 */
public class BukkitInitialization {
	private static boolean initialized;
	
	/**
	 * Initialize Bukkit and ProtocolLib such that we can perfrom unit testing.
	 * @throws IllegalAccessException If we are unable to initialize Bukkit.
	 */
	public static void initializeItemMeta() throws IllegalAccessException {
		if (!initialized) {
			// Denote that we're done
			initialized = true;

			try {
				Block.p();
				Item.l();
				StatisticList.a();
			} catch (Exception e) {
				// Swallow
				e.printStackTrace();
			}
			
			// Mock the server object
			Server mockedServer = mock(Server.class);
			ItemFactory mockedFactory = mock(CraftItemFactory.class);
			ItemMeta mockedMeta = mock(ItemMeta.class);
	
			when(mockedServer.getItemFactory()).thenReturn(mockedFactory);
			when(mockedServer.isPrimaryThread()).thenReturn(true);
			when(mockedFactory.getItemMeta(any(Material.class))).thenReturn(mockedMeta);
	
			// Inject this fake server
			setStaticField(Bukkit.class, "server", mockedServer);
			
			// And the fake item factory
			setStaticField(CraftItemFactory.class, "instance", mockedFactory);
		}
	}
	
	private static void setStaticField(Class<?> parent, String name, Object value) {
		try {
			Field field = parent.getDeclaredField(name);
			field.setAccessible(true);
			field.set(null, value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot set static field " + name + ".", e);
		}
	}
}
