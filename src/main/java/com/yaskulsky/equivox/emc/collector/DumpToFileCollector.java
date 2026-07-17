package com.yaskulsky.equivox.emc.collector;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.nio.file.Path;
import com.yaskulsky.equivox.api.conversion.CustomConversion;
import com.yaskulsky.equivox.api.conversion.CustomConversionFile;
import com.yaskulsky.equivox.api.mapper.arithmetic.IValueArithmetic;
import com.yaskulsky.equivox.api.mapper.collector.IExtendedMappingCollector;
import com.yaskulsky.equivox.api.nss.NormalizedSimpleStack;
import com.yaskulsky.equivox.impl.codec.PECodecHelper;
import net.minecraft.core.HolderLookup;

public class DumpToFileCollector<A extends IValueArithmetic<?>> extends AbstractMappingCollector<NormalizedSimpleStack, Long, A> {

	public static String currentGroupName = "default";
	private final CustomConversionFile out = new CustomConversionFile();
	private final IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner;
	private final Path path;

	public DumpToFileCollector(Path path, IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner) {
		super(inner.getArithmetic());
		this.path = path;
		this.inner = inner;
	}

	@Override
	public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Object2IntMap<NormalizedSimpleStack> ingredientsWithAmount) {
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
		if (something != null && !ingredientsWithAmount.containsKey(null)) {
			out.values().addConversion(CustomConversion.getFor(outnumber, something, ingredientsWithAmount));
		}
	}

	@Override
	public void addConversion(int outnumber, NormalizedSimpleStack output, Object2IntMap<NormalizedSimpleStack> ingredientsWithAmount, A arithmeticForConversion) {
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
		if (output != null && !ingredientsWithAmount.containsKey(null)) {
			out.getOrAddGroup(currentGroupName).addConversion(CustomConversion.getFor(outnumber, output, ingredientsWithAmount));
		}
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, Long value) {
		inner.setValueBefore(something, value);
		if (something != null) {
			out.values().setValueBefore().put(something, value.longValue());
		}
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, Long value) {
		inner.setValueAfter(something, value);
		if (something != null) {
			out.values().setValueAfter().put(something, value.longValue());
		}
	}

	@Override
	public void finishCollection(HolderLookup.Provider registries) {
		PECodecHelper.writeToFile(registries, path, CustomConversionFile.CODEC, out, "custom conversion");
		inner.finishCollection(registries);
	}
}