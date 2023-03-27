package me.whitehatd.Dodgeball.npc;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.utils.FixedLocations;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class NPCUtil {

    private final Core core;
    private NPCRegistry registry;

    private UUID startNPCUUID;

    public NPCUtil(Core core){

        this.core = core;

        registry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
        setupStartNPC();
    }

    private void setupStartNPC(){
        NPC npc = registry.createNPC(EntityType.PLAYER, core.getChatUtil().toColor("&d&lCreate or join a match"));

        npc.getOrAddTrait(SkinTrait.class).setSkinName(core.getConfig().getString("start-npc.skin"));
        npc.spawn(FixedLocations.NPC_SPAWN.get());

        startNPCUUID = npc.getUniqueId();
    }

    public void destroyStartNPC(){
        NPC npc = registry.getByUniqueId(startNPCUUID);
        npc.destroy();
    }

    public UUID getStartNPCUUID() {
        return startNPCUUID;
    }
}
