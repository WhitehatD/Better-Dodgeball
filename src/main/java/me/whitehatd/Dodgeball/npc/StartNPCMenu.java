package me.whitehatd.Dodgeball.npc;

import io.netty.util.internal.MathUtil;
import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.utils.menu.Button;
import me.whitehatd.Dodgeball.utils.menu.ItemBuilder;
import me.whitehatd.Dodgeball.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StartNPCMenu extends Menu {

    private Menu availableGames;

    public StartNPCMenu(Core core) {
        super(core, 36, "&dSelect an option");

        this
                .setButton(0, 11,
                        Button.of(
                                new ItemBuilder(Material.COMPASS)
                                        .setName("&eBrowse available games")
                                        .setLore(
                                                List.of("&7Click to view available games"))
                                        .build(),
                                e -> {

                                    if(core.getGameManager().getGame((Player) e.getWhoClicked()) != null) {

                                        core.getChatUtil().message(e.getWhoClicked(), "&cYou have already created a game.");
                                        return;

                                    }

                                    availableGames = new Menu(core, 36, "&dSelect a game to join");
                                    availableGames.autoPaginate();

                                    availableGames.setStickyButton(31,
                                            Button.of(
                                                    new ItemBuilder(Material.BARRIER)
                                                            .setName("&cGo back to the main menu")
                                                            .build(),
                                                    e2 -> {

                                                        Bukkit.getScheduler().runTaskLater(core, () -> {
                                                            e2.getWhoClicked().openInventory(this.getInventory());
                                                        }, 2L);
                                                    }));

                                    List<Button> availableButtons = new ArrayList<>();

                                    for(Game game : core.getGameManager().getWaitingGames()) {


                                        ItemStack skull = core.getMiscUtil().getSkull(game.getCreator());

                                        ItemStack item = new ItemBuilder(skull)
                                                .setName("&a" + game.getCreator().getName() + "'s Game")
                                                .setLore(List.of(
                                                        "&eClick to join"
                                                ))
                                                .build();

                                        availableButtons.add(
                                                Button.of(
                                                        item,
                                                        e2 -> {

                                                            if(game.getRedTeam().size() > game.getBlueTeam().size())
                                                                game.addPlayerToTeam((Player) e2.getWhoClicked(), false, false);

                                                            else if(game.getBlueTeam().size() > game.getRedTeam().size())
                                                                game.addPlayerToTeam((Player) e2.getWhoClicked(), false, true);
                                                            else {
                                                                boolean nextToRed = new Random().nextBoolean();

                                                                game.addPlayerToTeam((Player) e2.getWhoClicked(), false, nextToRed);
                                                            }

                                                        })
                                        );
                                    }

                                    availableGames.addAll(0, availableButtons);

                                    e.getWhoClicked().openInventory(availableGames.getInventory());
                                }
                        ))
                .setButton(0,15,
                        Button.of(
                                new ItemBuilder(Material.DIAMOND_SWORD)
                                        .setName("&aStart a dodgeball game")
                                        .setLore(
                                                List.of("&7Click to start a dodgeball game"))
                                        .build(),
                                e -> {

                                    if(core.getGameManager().getGame((Player) e.getWhoClicked()) != null) {

                                        core.getChatUtil().message(e.getWhoClicked(), "&cYou have already created a game.");
                                        return;

                                    }

                                    new Game((Player) e.getWhoClicked(), core);

                                    core.getChatUtil().message(e.getWhoClicked(), "&aCreated a dodgeball lobby.");
                                    e.getWhoClicked().closeInventory();

                                }
                        ));


    }
}
