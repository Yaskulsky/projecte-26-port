package com.yaskulsky.equivox.client.sound;

import com.yaskulsky.equivox.PECore;
import com.yaskulsky.equivox.gameObjs.registration.impl.SoundEventRegistryObject;
import com.yaskulsky.equivox.gameObjs.registries.PESoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class PESoundProvider extends SoundDefinitionsProvider {

	public PESoundProvider(PackOutput output) {
		super(output, PECore.MODID);
	}

	protected void addSoundEventWithSubtitle(SoundEventRegistryObject<?> soundEventRO, Identifier location) {
		add(soundEventRO.get(), SoundDefinition.definition().subtitle(soundEventRO.getTranslationKey()).with(sound(location)));
	}

	@Override
	public void registerSounds() {
		addSoundEventWithSubtitle(PESoundEvents.WIND_MAGIC, PECore.rl("item/pewindmagic"));
		addSoundEventWithSubtitle(PESoundEvents.WATER_MAGIC, PECore.rl("item/pewatermagic"));
		addSoundEventWithSubtitle(PESoundEvents.POWER, PECore.rl("item/pepower"));
		addSoundEventWithSubtitle(PESoundEvents.HEAL, PECore.rl("item/peheal"));
		addSoundEventWithSubtitle(PESoundEvents.DESTRUCT, PECore.rl("item/pedestruct"));
		addSoundEventWithSubtitle(PESoundEvents.CHARGE, PECore.rl("item/pecharge"));
		addSoundEventWithSubtitle(PESoundEvents.UNCHARGE, PECore.rl("item/peuncharge"));
		addSoundEventWithSubtitle(PESoundEvents.TRANSMUTE, PECore.rl("item/petransmute"));
		//TODO: Evaluate the remaining sounds that we don't actually use anywhere
	}
}
