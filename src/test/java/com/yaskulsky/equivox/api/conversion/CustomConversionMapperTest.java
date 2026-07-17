package com.yaskulsky.equivox.api.conversion;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import com.yaskulsky.equivox.api.EquivoxAPI;
import com.yaskulsky.equivox.api.nss.NSSFake;
import com.yaskulsky.equivox.api.nss.NSSItem;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.impl.codec.CodecTestHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
@DisplayName("Test Custom Conversion Mappers")
class CustomConversionMapperTest {

	private static CustomConversionFile parseJson(HolderLookup.Provider registryAccess, String json) {
		return CodecTestHelper.parseJson(registryAccess, CustomConversionFile.CODEC, "custom conversion test", json);
	}

	@Test
	@DisplayName("Test conversion file that only contains a comment")
	void testCommentOnlyCustomFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"comment": "A very simple Example"
				}""");
		Assertions.assertEquals("A very simple Example", conversionFile.comment());
	}

	@Test
	@DisplayName("Test conversion file with empty group")
	void testSingleEmptyGroupFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"groups": {
						"groupa": {
							"comment": "A conversion group for something",
							"conversions": [
							]
						}
					}
				}""");
		Assertions.assertEquals(1, conversionFile.groups().size());
		Assertions.assertTrue(conversionFile.groups().containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = conversionFile.groups().get("groupa");
		Assertions.assertEquals("A conversion group for something", group.comment(), "Group contains specific comment");
		Assertions.assertEquals(0, group.size());
	}

	@Test
	@DisplayName("Test simple conversion file")
	void testSimpleFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"groups": {
						"groupa": {
							"conversions": [
								{
									"output": {
										"type": "equivox:item",
										"id": "iron_ingot"
									},
									"ingredients": [
										{
											"type": "equivox:item",
											"id": "stone"
										},
										{
											"type": "equivox:item",
											"id": "granite",
											"amount": 2
										},
										{
											"type": "equivox:item",
											"id": "diorite",
											"amount": 3
										}
									]
								},
								{
									"output": {
										"type": "equivox:item",
										"id": "gold_ingot"
									},
									"ingredients": [
										{
											"type": "equivox:item",
											"id": "stone"
										},
										{
											"type": "equivox:item",
											"id": "granite"
										},
										{
											"type": "equivox:item",
											"id": "diorite"
										}
									]
								},
								{
									"output": {
										"type": "equivox:item",
										"id": "copper_ingot"
									},
									"count": 3,
									"ingredients": [
										{
											"type": "equivox:item",
											"id": "stone"
										},
										{
											"type": "equivox:item",
											"id": "stone"
										},
										{
											"type": "equivox:item",
											"id": "stone"
										}
									]
								}
							]
						}
					}
				}""");
		Assertions.assertEquals(1, conversionFile.groups().size());
		Assertions.assertTrue(conversionFile.groups().containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = conversionFile.groups().get("groupa");
		Assertions.assertEquals(3, group.size());
		List<CustomConversion> conversions = group.conversions();
		{
			CustomConversion conversion = conversions.getFirst();
			Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(Items.GOLD_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(2);
			Assertions.assertEquals(NSSItem.createItem(Items.COPPER_INGOT), conversion.output());
			Assertions.assertEquals(3, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(1, ingredients.size());
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.STONE)));
		}
	}

	@Test
	@DisplayName("Test conversion file setting value")
	void testSetValueFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"before": [
							{
								"type": "equivox:item",
								"id": "minecraft:stone",
								"emc_value": 1
							},
							{
								"type": "equivox:item",
								"id": "granite",
								"emc_value": 2
							},
							{
								"type": "equivox:item",
								"id": "diorite",
								"emc_value": "free"
							}
						],
						"after": [
							{
								"type": "equivox:item",
								"id": "andesite",
								"emc_value": 3
							}
						]
					}
				}""");
		FixedValues values = conversionFile.values();
		Assertions.assertEquals(1, values.setValueBefore().getLong(NSSItem.createItem(Items.STONE)));
		Assertions.assertEquals(2, values.setValueBefore().getLong(NSSItem.createItem(Items.GRANITE)));
		Assertions.assertEquals(EquivoxAPI.FREE_ARITHMETIC_VALUE, values.setValueBefore().getLong(NSSItem.createItem(Items.DIORITE)));
		Assertions.assertEquals(3, values.setValueAfter().getLong(NSSItem.createItem(Items.ANDESITE)));
	}

	@Test
	@DisplayName("Test conversion file skipping invalid keys for setting value")
	void testInvalidKeySetValueFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"before": [
							{
								"type": "equivox:item",
								"id": "INVALID|stone",
								"emc_value": 1
							},
							{
								"type": "equivox:item",
								"id": "granite",
								"emc_value": 2
							}
						],
						"after": [
							{
								"type": "equivox:item",
								"id": "INVALID|andesite",
								"emc_value": 3
							}
						]
					}
				}""");
		FixedValues values = conversionFile.values();
		Assertions.assertEquals(2, values.setValueBefore().getLong(NSSItem.createItem(Items.GRANITE)));
		Assertions.assertTrue(values.setValueAfter().isEmpty());
	}

	@Test
	@DisplayName("Test set value from conversion")
	void testSetValueFromConversion(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "equivox:item",
									"id": "iron_ingot"
								},
								"ingredients": [
									{
										"type": "equivox:item",
										"id": "minecraft:stone",
										"amount": 1
									},
									{
										"type": "equivox:item",
										"id": "granite",
										"amount": 2
									},
									{
										"type": "equivox:item",
										"id": "minecraft:diorite",
										"amount": 3
									}
								]
							}
						]
					}
				}""");
		Assertions.assertEquals(1, conversionFile.values().conversions().size());
		CustomConversion conversion = conversionFile.values().conversions().getFirst();
		Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
		Assertions.assertEquals(1, conversion.count());
		Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
		Assertions.assertEquals(3, ingredients.size());
		Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
		Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
		Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
	}

	@Test
	@DisplayName("Test explicit format in conversions")
	void testConversionExplicitFormat(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "equivox:item",
									"id": "iron_ingot"
								},
								"ingredients": [
									{
										"type": "equivox:item",
										"id": "minecraft:stone",
										"amount": 1
									},
									{
										"type": "equivox:item",
										"id": "granite",
										"amount": 2
									},
									{
										"type": "equivox:item",
										"id": "minecraft:diorite",
										"amount": 3
									}
								]
							},
							{
								"output": {
									"type": "equivox:item",
									"id": "gold_ingot",
									"data": {
										"custom_data": {
											"my": "tag"
										}
									}
								},
								"ingredients": [
									{
										"type": "equivox:item",
										"id": "stone"
									},
									{
										"type": "equivox:item",
										"id": "granite"
									},
									{
										"type": "equivox:item",
										"id": "diorite",
										"data": {
											"custom_data": "{my: \\"tag\\"}"
										}
									}
								]
							}
						]
					}
				}""");
		List<CustomConversion> conversions = conversionFile.values().conversions();
		Assertions.assertEquals(2, conversions.size());
		{
			CustomConversion conversion = conversions.getFirst();
			Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(Items.GOLD_INGOT, CodecTestHelper.MY_TAG_PATCH), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.DIORITE, CodecTestHelper.MY_TAG_PATCH)));
		}
	}

	@Test
	@DisplayName("Test to make sure FAKE values in conversions don't break things")
	void testNonInterferingFakes(MinecraftServer server) {
		String file1 = """
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "equivox:fake",
									"description": "FOO"
								},
								"ingredients": [
									{
										"type": "equivox:fake",
										"description": "BAR"
									}
								]
							}
						]
					}
				}""";

		NSSFake.setCurrentNamespace("file1");
		CustomConversionFile conversionFile1 = parseJson(server.registryAccess(), file1);
		CustomConversionFile conversionFile2 = parseJson(server.registryAccess(), file1);
		NSSFake.setCurrentNamespace("file2");
		CustomConversionFile conversionFile3 = parseJson(server.registryAccess(), file1);

		CustomConversion conversion1 = conversionFile1.values().conversions().getFirst();
		CustomConversion conversion2 = conversionFile2.values().conversions().getFirst();
		CustomConversion conversion3 = conversionFile3.values().conversions().getFirst();

		Assertions.assertEquals(conversion1.output(), conversion2.output());
		Assertions.assertNotEquals(conversion1.output(), conversion3.output());
		Assertions.assertNotEquals(conversion2.output(), conversion3.output());
	}

	@Test
	@DisplayName("Test ignore invalid conversions")
	void testIgnoreInvalidConversions(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"groups": {
						"groupa": {
							"conversions": [
								{
									"output": {
										"type": "equivox:item",
										"id": "iron_ingot"
									},
									"ingredients": [
										{
											"type": "equivox:item",
											"id": "minecraft:stone",
											"amount": 1
										},
										{
											"type": "equivox:item",
											"id": "granite",
											"amount": 2
										},
										{
											"type": "equivox:item",
											"id": "minecraft:diorite",
											"amount": 3
										}
									]
								},
								{
									"output": {
										"type": "equivox:item",
										"id": "gold_ingot"
									}
								},
								{
									"output": {
										"type": "equivox:item",
										"id": "copper_ingot"
									},
									"count": 3,
									"ingredients": [
										{
											"type": "equivox:item",
											"id": "minecraft:stone",
											"amount": 3
										}
									]
								},
								{
									"output": {
										"type": "equivox:item",
										"id": "diamond"
									},
									"ingredients": [
										{
											"type": "equivox:item",
											"id": "granite",
											"amount": 2
										},
										{
											"type": "equivox:item",
											"id": "minecraft:INVALID|stone",
											"amount": 1
										}
									]
								}
							]
						}
					}
				}""");
		Assertions.assertEquals(1, conversionFile.groups().size());
		Assertions.assertTrue(conversionFile.groups().containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = conversionFile.groups().get("groupa");
		Assertions.assertEquals(2, group.size());
		List<CustomConversion> conversions = group.conversions();
		{
			CustomConversion conversion = conversions.getFirst();
			Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(Items.COPPER_INGOT), conversion.output());
			Assertions.assertEquals(3, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(1, ingredients.size());
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.STONE)));
		}
	}
}