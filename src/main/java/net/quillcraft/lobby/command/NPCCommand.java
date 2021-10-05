package net.quillcraft.lobby.command;

import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.manager.ConfigurationManager;
import net.quillcraft.lobby.npc.NPC;
import net.quillcraft.lobby.npc.NPCManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class NPCCommand implements CommandExecutor {

    private final QuillCraftLobby main;

    public NPCCommand(QuillCraftLobby main){
        this.main = main;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args){
        if(cmds instanceof final Player player){
            if(args.length == 2 || args.length == 3){
                if(args[1].length() > 16){
                    player.sendMessage("Name max size is 15 characters");
                    return true;
                }

                if(args[0].equalsIgnoreCase("add")){
                    final String npcName = args[1];
                    if(args.length == 3){

                        if(args[2].length() > 16){
                            player.sendMessage("Name max size is 15 characters");
                            return true;
                        }


                        main.getNpcManager().createNPC(npcName, null, player.getLocation());
                        player.sendMessage("NPC CREER AVEC SUCCES");

                        return true;
                    }

                    main.getNpcManager().createNPC(npcName, npcName, player.getLocation());

                    player.sendMessage("NPC CREER AVEC SUCCES");
                    return true;
                }else if(args.length == 3){
                    if(args[0].equalsIgnoreCase("remove")){
                        final String npcName = args[1];
                        final int reference = Integer.parseInt(args[2]);

                        final NPCManager npcManager = main.getNpcManager();
                        if(npcManager.exists(npcName, reference)){
                            npcManager.removeNPC(npcName, reference);
                            player.sendMessage("NPC SUPPRIMER AVEC SUCCES");
                            return true;
                        }
                        player.sendMessage("Le NPC n'existe pas");
                        return true;
                    }
                }
            }else if(args.length == 5){
                if(args[0].equalsIgnoreCase("rotate")){
                    final String npcName = args[2];
                    final int reference = Integer.parseInt(args[3]);

                    final NPCManager npcManager = main.getNpcManager();
                    if(npcManager.exists(npcName, reference)){
                        final NPC npc = npcManager.getNPCList().stream().parallel().filter(npcTarget -> npcTarget.getName().equalsIgnoreCase(npcName) && npcTarget.getReference() == reference).toList().get(0);
                        final ConfigurationManager configurationManager = ConfigurationManager.NPC;
                        final String path = npcName+"."+reference+".location.yaw."+args[1].toLowerCase();
                        if(args[1].equalsIgnoreCase("head")){
                            final float yaw = npc.getYawHead()+Integer.parseInt(args[4]);
                            npc.setYawHeadRotation(yaw);
                            npc.updateHeadRotation();
                            configurationManager.getConfiguration().set(path, yaw);
                            configurationManager.saveFile();
                            player.sendMessage("NPC Head rotate");
                            return true;
                        }else if(args[1].equalsIgnoreCase("body")){
                            final float yaw = npc.getLocation().getYaw()+Integer.parseInt(args[4]);
                            npc.setBodyRotation(yaw);
                            npc.updateBodyRotation();
                            configurationManager.getConfiguration().set(path, yaw);
                            configurationManager.saveFile();
                            player.sendMessage("NPC body rotate");
                            return true;
                        }
                        System.out.println("§CHEAD OR BODY");
                        return true;
                    }
                    player.sendMessage("Le NPC n'existe pas");
                    return true;
                }
            }else if(args.length == 1 && args[0].equalsIgnoreCase("list")){
                final StringBuilder stringBuilder = new StringBuilder("List des NPCs :");
                main.getNpcManager().getNPCList().forEach(npc -> {
                    final Location location = npc.getLocation();
                    stringBuilder.append("\n").append(npc.getName()).append(":").append(npc.getReference()).append(" (x=").append(location.getBlockX()).append(", ").append("y=").
                            append(location.getBlockY()).append(", ").append("z=").append(location.getBlockZ()).append(", ").append("world:\"").append(location.getWorld().getName()).append("\")");
                });
                player.sendMessage(stringBuilder.toString());
                return true;
            }
        }
        return false;
    }
}