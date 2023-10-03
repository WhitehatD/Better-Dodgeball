package me.whitehatd.Dodgeball.npc;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.utils.listener.ListenerBase;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;

public class NPCListener extends ListenerBase {

    public NPCListener(Core core) {
        super(core);
    }

    @EventHandler
    public void onNPCClick(NPCRightClickEvent e) {
        if (e.getNPC().getUniqueId().equals(core.getNpcUtil().getStartNPCUUID()))
            e.getClicker().openInventory(new StartNPCMenu(core).getInventory());
    }
}
