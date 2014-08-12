package com.comphenix.attribute;

import static org.junit.Assert.assertEquals;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemFactory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.comphenix.attribute.mocking.BukkitInitialization;

/**
 * Unit test for simple App.
 */
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(CraftItemFactory.class)
public class StorageTest {
	private static final UUID TEST_UUID = UUID.fromString("72a1eaa6-a4dd-4d15-8a9b-c975d417c766");
	
	@BeforeClass
	public static void initializeMeta() throws IllegalAccessException {
		BukkitInitialization.initializeItemMeta();
	}
	
	@Test
	public void testStorage() {
		ItemStack stack = new ItemStack(Material.IRON_AXE);
		
		// Always get the result stack
		stack = AttributeStorage.newTarget(stack, TEST_UUID).
			setData("test").
			getTarget();
		
		assertEquals("test", 
			AttributeStorage.newTarget(stack, TEST_UUID).getData(null));
	}
}
