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

package com.comphenix.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.minecraft.server.v1_7_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemFactory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.StreamOptions;
import com.comphenix.attribute.mocking.BukkitInitialization;
import com.google.common.io.ByteStreams;
import com.google.common.io.OutputSupplier;

/**
 * Unit test for simple App.
 */
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(CraftItemFactory.class)
public class NbtFactoryTest {
	@BeforeClass
	public static void initializeMeta() throws IllegalAccessException {
		BukkitInitialization.initializeItemMeta();
	}
	
	@Test
	public void testCompound() {
		NbtCompound compound = createTestCompound();
		
		// Simple test
		verifyCompound(compound);
	}
	
	private void verifyCompound(NbtCompound compound) {
		// Verify the NBT content
		assertEquals(NBTTagCompound.class, compound.getHandle().getClass());
		assertEquals(2009, (int)compound.getInteger("released", 0));
		assertEquals("Minecraft", compound.getString("game", ""));
		assertEquals(Arrays.asList(1, 2, 3), compound.getList("list", false));
		assertEquals("Markus Persson", compound.getPath("author.name"));
		assertEquals("Kristian Stangeland", compound.getPath("fan.name"));
		
		assertNull("Missing root path was not NULL", compound.getPath("missing.test"));
	}
	
	private NbtCompound createTestCompound() {
		// Use NbtFactory.fromCompound(obj); to load from a NBTCompound class.
		NbtCompound compound = NbtFactory.createCompound();
		NbtCompound author = NbtFactory.createCompound();
		 
		compound.put("released", 2009);
		compound.put("game", "Minecraft");
		compound.put("author", author);
		compound.put("bytes", new byte[] { 1, 2, 3 });
		compound.put("integers", new int[] { 1, 2, 3});
		compound.put("list", NbtFactory.createList(1, 2, 3));

		author.put("name", "Markus Persson");
		compound.putPath("fan.name", "Kristian Stangeland");
		return compound;
	}
	
	@Test
	public void testSaving() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final NbtCompound compound = createTestCompound();
		
		// Save the compound with compression
	    compound.saveTo(new OutputSupplier<OutputStream>() {
	    	public OutputStream getOutput() throws IOException {
	    		return output;
	    	}
		}, StreamOptions.GZIP_COMPRESSION);
	     
	    // Load the compound
	    NbtCompound loaded = NbtFactory.fromStream(
	    	ByteStreams.newInputStreamSupplier(output.toByteArray()), StreamOptions.GZIP_COMPRESSION);
	    verifyCompound(loaded);
	}
	
	@Test
	public void testItemMeta() {
	    ItemStack stack = NbtFactory.getCraftItemStack(new ItemStack(Material.GOLD_AXE));
	    NbtCompound other = NbtFactory.fromItemTag(stack);
	     
	    // Do whatever
	    other.putPath("display.Name", "New display");
	    other.putPath("display.Lore", NbtFactory.createList("Line 1", "Line 2"));
	    
	    ItemMeta meta = stack.getItemMeta();
	    assertEquals("New display", meta.getDisplayName());
	    assertEquals(Arrays.asList("Line 1", "Line 2"), meta.getLore());
	}
}
