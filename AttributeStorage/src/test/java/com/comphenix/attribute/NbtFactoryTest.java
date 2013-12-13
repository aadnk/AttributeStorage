package com.comphenix.attribute;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.util.org.apache.commons.io.output.ByteArrayOutputStream;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemFactory;
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
