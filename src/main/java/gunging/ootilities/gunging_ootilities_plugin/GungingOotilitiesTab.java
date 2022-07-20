package gunging.ootilities.gunging_ootilities_plugin;

import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPE_Shrubs;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMMOItems;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.versions.GooP_MinecraftVersions;
import gunging.ootilities.gunging_ootilities_plugin.containers.loader.*;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.ContainerTypes;
import gunging.ootilities.gunging_ootilities_plugin.containers.options.KindRestriction;
import gunging.ootilities.gunging_ootilities_plugin.containers.restriction.RestrictedBehaviour;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CustomStructure;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CustomStructureTriggers;
import gunging.ootilities.gunging_ootilities_plugin.customstructures.CustomStructures;
import gunging.ootilities.gunging_ootilities_plugin.misc.*;
import gunging.ootilities.gunging_ootilities_plugin.misc.mmoitemstats.ApplicableMask;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GungingOotilitiesTab implements TabCompleter {

    public static List<String> commandsTab = new ArrayList<>(), cnTypesTab = new ArrayList<>(), csTriggersTab = new ArrayList<>(), vanillaMaterialsTab = new ArrayList<>(), vanillaEntitiesTab = new ArrayList<>(), enchantmentsTab = new ArrayList<>();

    public static List<String> gemstoneColours = new ArrayList<String>();

    public static void Start() {
        // Get Loaded Commands
        for (Object goopAcommand : GooP_Commands.class.getEnumConstants()) { commandsTab.add(goopAcommand.toString()); commandsTab.add("sudop"); }

        // Get Loaded Triggers
        for (Object goopAcsTrigger : CustomStructureTriggers.class.getEnumConstants()) { csTriggersTab.add(goopAcsTrigger.toString()); }
        for (Object goopAcnType : ContainerTypes.class.getEnumConstants()) { cnTypesTab.add(goopAcnType.toString()); }

        // Get Materials
        for (Object goopAMaterial : Material.class.getEnumConstants()) { vanillaMaterialsTab.add(goopAMaterial.toString()); }
        for (Object goopAEntity : EntityType.class.getEnumConstants()) { vanillaEntitiesTab.add(goopAEntity.toString()); }

        // Git ench
        for (Enchantment enchantment : Enchantment.values()) { enchantmentsTab.add(enchantment.getKey().getKey()); }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args) {

        // If the command relates to this plugin
        if (command.getName().equals("goop") || command.getName().equals("gungingootilities")) {

            if (args.length >= 1) {
                RefSimulator<String> fmFind = new RefSimulator<>(null);
                args[0] = OotilityCeption.ProcessFailMessageOfCommand(args[0], fmFind);
                String locData = args[0];
                if (locData.startsWith("<")) {

                    // Does it end in >
                    int senderRelativeLocationIDX = locData.indexOf(">");
                    if (senderRelativeLocationIDX > 4) {

                        // Strip
                        args[0] = locData.substring(senderRelativeLocationIDX + 1); } } }

            // Players only.
            if (!(sender instanceof Player)) { return null; }

            // Array to return
            List<String> tabM = new ArrayList<String>();

            // Starts with compare
            String tabSt = args[Math.round(OotilityCeption.MathClamp(args.length - 1, 0, args.length))];

            // Will it be a supported plugin command?
            boolean supp = false;

            // Should not care about caps?
            boolean ignoreCaps = true;
            boolean startWith = true;

            // If there are any arguments
            GooP_Commands cmd = null;
            if (args.length > 1) {

                // Is it a supported plugin command?
                try {
                    // Yes, it seems to be
                    cmd = GooP_Commands.valueOf(args[0].toLowerCase());

                    // Well it is supported then
                    supp = true;

                    // Not recognized
                } catch (IllegalArgumentException ex) {


                    // Yes it is! Lets go
                    if (args[0].toLowerCase().equals("sudop")) {
                        cmd = GooP_Commands.sudo;
                        supp = true;
                    }

                    // Nothing to do on tab completer
                }

                // Otherwise, its just all the commands
            } else if (args.length == 1) { tabM = commandsTab; }

            //region Crop OnSuccess

            // If it is supported by GooP to begin with
            if (supp) {
                //OST//OotilityCeption.Log("   \u00a7e- \u00a77" + alias);
                //OST//for (int a = 0; a < args.length; a++) {  OotilityCeption.Log("\u00a78[\u00a7b" + (a) + "\u00a78]\u00a73- \u00a77" + args[a]); }

                // Find the last onSuccess
                String croppedCommand = null;   // Starts at args.length-2 so that it doesnt count stuff ending in oS= as a chained command, this begins after the space.
                boolean sp = args[args.length-1].length() == 0;
                for (int i = args.length - 2; i >= 0; i--) {

                    // Get the observed
                    String obs = args[i];
                    //OST//OotilityCeption.Log("\u00a78Arg \u00a7e#" + i + "\u00a77 " + obs);

                    // Is it onSuccess?
                    if (OotilityCeption.IsChainedKey(obs)) {
                        //OST//OotilityCeption.Log("\u00a7b    + \u00a77Identified as chaining arg");

                        // Will get all args after thay
                        StringBuilder cropCBuilder = new StringBuilder();

                        // Build the original command, then
                        //OST//OotilityCeption.Log("\u00a7b    + \u00a77Building command");
                        for (int c = i + 1; c < args.length; c++) {

                            // If it happens to be 'gungingootilities' ffs no
                            String obz = args[c];
                            if (obz.toLowerCase().equals("gungingootilities")) { obz = "goop"; }

                            // Append
                            if (!(cropCBuilder.length() == 0)) { cropCBuilder.append(" "); }
                            cropCBuilder.append(obz);
                            //OST//OotilityCeption.Log("\u00a7b    + \u00a77Added \u00a73" + obz);;
                        }

                        // Build
                        croppedCommand = cropCBuilder.toString();
                        //OST//OotilityCeption.Log("\u00a73    + \u00a7eResult \u00a77" + croppedCommand);

                        // Break
                        i = -1;
                    } }

                // Was there any cropped command so far?
                if (croppedCommand != null) {

                    // Extract args
                    String[] argzExtended = croppedCommand.split(" ");
                    int min = 1; if (sp) { min = 0; }
                    String[] argz = new String[argzExtended.length - min];
                    //OST//OotilityCeption.Log("\u00a78[\u00a76C\u00a78]\u00a7e- \u00a77" + argzExtended[0]);
                    for (int a = 1; a < argzExtended.length; a++) {

                        // Put
                        argz[a - 1] = argzExtended[a];
                        //OST//OotilityCeption.Log("\u00a78[\u00a7b" + (a - 1) + "\u00a78]\u00a73- \u00a77" + argz[a - 1]);
                    }
                    if (sp && argzExtended.length > 1) {
                        argz[argz.length - 1] = "";
                        //OST//OotilityCeption.Log("\u00a78[\u00a7b" + (argz.length - 1) + "\u00a78]\u00a73- \u00a77" + argz[argz.length - 1]);
                    }

                    // Finish
                    args = argz;
                    alias = argzExtended[0].toLowerCase();

                    // GooP supported?
                    supp = false;
                    if (args.length == 0) { tabM.add("goop"); }
                    if (alias.equals("goop")) {

                        if (args.length == 1) {
                            tabM = commandsTab;

                        } else if (args.length > 1) {

                            // Is it a supported plugin command?
                            try {
                                // Yes, it seems to be
                                cmd = GooP_Commands.valueOf(args[0].toLowerCase());

                                // Well it is supported then
                                supp = true;

                                // Not recognized
                            } catch (IllegalArgumentException ex) {


                                // Yes it is! Lets go
                                if (args[0].toLowerCase().equals("sudop")) {
                                    cmd = GooP_Commands.sudo;
                                    supp = true;
                                }

                                // Nothing to do on tab completer
                            }
                        }
                    }

                } else {

                    // Will get all args after thay
                    StringBuilder cropCBuilder = new StringBuilder("goop");

                    // Build the original command, then
                    for (String arg : args) { cropCBuilder.append(" ").append(arg); }

                    // Build
                    croppedCommand = cropCBuilder.toString();
                    //OST//OotilityCeption.Log("\u00a78Original \u00a77 Cropped not found, built original \u00a7e" + croppedCommand);
                }

                //OST//OotilityCeption.Log("\u00a78Tab Complete \u00a77Identified Command As: \u00a7e" + croppedCommand);

                // Still supported?
                if (supp) {
                    int gIndex = -1; boolean gtc = false; boolean gtl = false;
                    // Is it a goop containers config commands?
                    if (cmd.equals(GooP_Commands.containers) && croppedCommand.contains("commands")) {
                        //OST//OotilityCeption.Log("\u00a7b    + \u00a77Identified as \u00a7eContainers Commands Config");

                        // IF args is at least 6
                        if (args.length >= 6) {

                            // Gtc yo
                            gtc = true;
                            //OST//OotilityCeption.Log("\u00a7e    + \u00a77Yes");

                            // Suggest command as GooP maybe?
                            if (args.length == 6 && OotilityCeption.IntTryParse(args[4])) {

                                // If its an int, suggest the onTake and onStore thing
                                Collections.addAll(tabM, "onStore", "onTake", "goop");

                                // Gtc no
                                gtc = false;
                                //OST//OotilityCeption.Log("\u00a7c    + \u00a77Actually no");
                            }

                            if (args.length > 7) {
                                //OST//OotilityCeption.Log("\u00a7e    + \u00a77Actually yes");
                                gtl = true; }

                            if (args[5].equals("goop")) { gIndex = 5; } else if (args.length >= 7 && args[6].equals("goop")) { gIndex = 6; }

                        }
                    } else if (cmd.equals(GooP_Commands.customstructures) && croppedCommand.contains("edit actions")) {
                        //OST//OotilityCeption.Log("\u00a7b    + \u00a77Identified as \u00a7eCustom Structures Actions");

                        // Is it at leas 6 lengh
                        if (args.length >= 6) {

                            // Is it add or edit?
                            if (args[3].toLowerCase().equals("add")) {

                                // yep
                                gtc = true;
                                //OST//OotilityCeption.Log("\u00a7e    + \u00a77 Yes");

                                // However, only args[5] can suggest goop
                                if (args.length >= 7) { gtl = true; }

                                // Establish gIndex
                                if (args[5].equals("goop")) { gIndex = 5; }

                            } else if (args[3].toLowerCase().equals("edit") && args.length >= 7) {

                                // yep
                                gtc = true;
                                //OST//OotilityCeption.Log("\u00a7e    + \u00a77 Yes");

                                // However, only args[6] can suggest goop
                                if (args.length >= 8) { gtl = true; }

                                // Establish gIndex
                                if (args[6].equals("goop")) { gIndex = 6; }
                            }
                        }

                    } else if (cmd.equals(GooP_Commands.delay) || cmd.equals(GooP_Commands.sudo)) {

                        if (args.length >= 3) {

                            // Establish gIndex
                            if (args[2].equals("goop")) { gIndex = 2; }
                            gtc = true;

                            if (args.length >= 4) { gtl = true; }
                        }
                    }

                        // If subcommand management
                    if (gtc) {

                        // If it is either six or seven, and the sixth is not 'goop'
                        if (!gtl) {

                            // Introduce goop
                            Collections.addAll(tabM, "goop");
                        }

                        // If it is a goop command (with supported tab complete of self)
                        if (gIndex >= 0) {

                            // Alr now get the index of goop
                            StringBuilder troueCommand = new StringBuilder("");
                            for (int t = gIndex; t < args.length; t++) {

                                // Append the current
                                troueCommand.append(" ").append(args[t]);
                            }

                            // Build
                            String trueCommand = troueCommand.substring(1);

                            //DBG//OotilityCeption.Log("Prime Command: \u00a7b" + trueCommand);

                            // Extract args
                            String[] argzExtended = trueCommand.split(" ");
                            int min = 1; if (sp) { min = 0; }
                            String[] argz = new String[argzExtended.length - min];
                            //OST//OotilityCeption.Log("\u00a78[\u00a76C\u00a78]\u00a7e- \u00a77" + argzExtended[0]);
                            for (int a = 1; a < argzExtended.length; a++) {

                                // Put
                                argz[a - 1] = argzExtended[a];
                                //OST//OotilityCeption.Log("\u00a78[\u00a7b" + (a - 1) + "\u00a78]\u00a73- \u00a77" + argz[a - 1]);
                            }
                            if (sp && argzExtended.length > 1) {
                                argz[argz.length - 1] = "";
                                //OST//OotilityCeption.Log("\u00a78[\u00a7b" + (argz.length - 1) + "\u00a78]\u00a73- \u00a77" + argz[argz.length - 1]);
                            }

                            // Finish
                            args = argz;
                            alias = argzExtended[0];

                            // GooP supported?
                            supp = false;
                            if (args.length == 0) { tabM.add("goop"); }
                            if (alias.equals("goop")) {

                                if (args.length == 1) {
                                    tabM = commandsTab;

                                } else if (args.length > 1) {

                                    // Is it a supported plugin command?
                                    try {
                                        // Yes, it seems to be
                                        cmd = GooP_Commands.valueOf(args[0].toLowerCase());

                                        // Well it is supported then
                                        supp = true;

                                        // Not recognized
                                    } catch (IllegalArgumentException ex) {


                                        // Yes it is! Lets go
                                        if (args[0].toLowerCase().equals("sudop")) {
                                            cmd = GooP_Commands.sudo;
                                            supp = true;
                                        }

                                        // Nothing to do on tab completer
                                    }
                                }
                            }

                        } else {

                            // Not goop anymore
                            supp = false;
                        }
                    }
                }

                //OST//OotilityCeption.Log("\u00a73-\u00a7e-\u00a73-\u00a7e-\u00a73-\u00a7e- \u00a77Final \u00a73-\u00a7e-\u00a73-\u00a7e-\u00a73-\u00a7e-");
                //OST//OotilityCeption.Log("   \u00a7e- \u00a77" + alias);
                //OST//for (int a = 0; a < args.length; a++) {  OotilityCeption.Log("\u00a78[\u00a7b" + (a) + "\u00a78]\u00a73- \u00a77" + args[a]); }
            }
            //endregion

            if (supp) {

                // Useful tab vars
                Block tBlock = null;
                ScoreboardManager scoreboardManager;
                Scoreboard scoreboard;

                // Which command thoi?
                switch (cmd) {
                    //region consumeitem
                    case consumeitem:
                        //   0      1            2     3 4 5      6     args.Length
                        // /goop consumeitem <player> {n b t} <amount>
                        //   -      0            1     2 3 4      5     args[n]

                        switch (args.length) {
                            case 2: tabM = null; break;
                            case 3:
                                Collections.addAll(tabM, OotilityCeption.itemNBTcharKeys);
                                if (!Gunging_Ootilities_Plugin.foundMMOItems) { tabM.remove("m"); }
                                if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("mm"); }
                                break;
                            case 4:
                                switch (args[2]){
                                    case "m":
                                        if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_TypeNames(); } else { tabM = new ArrayList<>(); }
                                        break;
                                    case "mm":
                                        if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM = GooPMythicMobs.GetMythicItemTypes(); } else { tabM = new ArrayList<>(); }
                                        break;
                                    case "e":
                                        tabM = enchantmentsTab;
                                        break;
                                    case "v":
                                        tabM = vanillaMaterialsTab;
                                        break;
                                    case "i":
                                        tabM = GooPIngredient.GetLoadedIngrs();
                                        break;
                                }
                                break;
                            case 5:
                                switch (args[2]){
                                    case "m":
                                        tabM.addAll(GooPMMOItems.GetMMOItem_IDNames(args[3]));
                                        //if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_IDNames(args[4]); } else { tabM =  = new ArrayList<String>(); }
                                        break;
                                    case "e":
                                        Collections.addAll(tabM, "1", "2", "3", "4", "5", "6");
                                        break;
                                    case "v":
                                    case "i":
                                    case "mm":
                                        Collections.addAll(tabM, "0");
                                        break;
                                }
                                break;
                            case 6:
                                Collections.addAll(tabM, "1", "16", "32", "64", "all");
                                break;
                        }

                        break;
                        //endregion
                    //region testinventory
                    case testinventory:
                        //   0      1              2       3    4 5 6       7         8     args.Length
                        // /goop testinventory <player> <slot> {n b t} <objective> <score>
                        //   -      0              1       2    3 4 5       6         7     args[n]

                        switch (args.length){
                            case 2: tabM = null; break;
                            case 3: tabM.addAll(OotilityCeption.getSlotKeywords()); break;
                            case 4:
                                Collections.addAll(tabM, OotilityCeption.itemNBTcharKeys);
                                if (!Gunging_Ootilities_Plugin.foundMMOItems) { tabM.remove("m"); }
                                if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("mm"); }
                                break;
                            case 5:
                                switch (args[3]){
                                    case "m":
                                        if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_TypeNames(); } else { tabM = new ArrayList<String>(); }
                                        break;
                                    case "mm":
                                        if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM = GooPMythicMobs.GetMythicItemTypes(); } else { tabM = new ArrayList<>(); }
                                        break;
                                    case "e":
                                        tabM = enchantmentsTab;
                                        break;
                                    case "v":
                                        tabM = vanillaMaterialsTab;
                                        break;
                                    case "i":
                                        tabM = GooPIngredient.GetLoadedIngrs();
                                        break;
                                }
                                break;
                            case 6:
                                switch (args[3]){
                                    case "m":
                                        tabM.addAll(GooPMMOItems.GetMMOItem_IDNames(args[4]));
                                        //if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_IDNames(args[4]); } else { tabM = new ArrayList<String>(); }
                                        break;
                                    case "e":
                                        Collections.addAll(tabM, "1", "2", "3", "4", "5", "6");
                                        break;
                                    case "v":
                                    case "i":
                                    case "mm":
                                        Collections.addAll(tabM, "0");
                                        break;
                                }
                                break;
                            case 7:
                                scoreboardManager = Bukkit.getScoreboardManager();
                                scoreboard = scoreboardManager.getMainScoreboard();
                                        
                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }

                                Collections.addAll(tabM,  "1..", "8..", "10-32", "48", "..64");
                                break;
                            case 8:
                                        
                                // Can it generate a QNR from args[6]?
                                if (QuickNumberRange.FromString(args[6]) != null) {
                                    tabM.add("comp");
                                    
                                    scoreboardManager = Bukkit.getScoreboardManager();
                                    scoreboard = scoreboardManager.getMainScoreboard();

                                    for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                    
                                } else {

                                    Collections.addAll(tabM, "amount", "1", "2", "3", "4", "5", "6");
                                }
                                break;
                            case 9:
                                        
                                // Can it generate a QNR from args[6]?
                                if (QuickNumberRange.FromString(args[6]) != null) {
                                    Collections.addAll(tabM, "amount", "1", "2", "3", "4", "5", "6");
                                    
                                } else if (!args[7].equalsIgnoreCase("amount")) {
                                    
                                    // Not amount? suggest comp
                                    tabM.add("comp");
                                }
                                
                                break;
                            case 10:
                                        
                                // So it included a number
                                if (QuickNumberRange.FromString(args[6]) != null
                                    && !args[8].equalsIgnoreCase("amount")) { tabM.add("comp"); }
                                
                                break;
                        }

                        break;
                        //endregion
                    //region gamerule
                    case gamerule:
                        //   0      1       2      3    args.Length
                        // /goop gamerule <rule> value
                        //   -      0       1      2    args[n]

                        switch (args.length) {
                            case 2:
                                tabM.add("sff");
                                tabM.add("ssf");
                                tabM.add("sf");
                                tabM.add("sendFeedback");
                                tabM.add("sendSuccessFeedback");
                                tabM.add("sendFailFeedback");
                                tabM.add("blockErrorFeedback");
                                tabM.add("saveGameruleChanges");
                                tabM.add("griefBreaksBedrock");
                                tabM.add("anvilRename");
                                startWith = false;
                                break;
                            case 3:
                                tabM.add("true");
                                tabM.add("false");
                                break;
                        }

                        break;
                    //endregion
                    //region stasis
                    case stasis:
                        //   0      1       2         3     args.Length
                        // /goop stasis <entity> <duration>
                        //   -      0       1         2     args[n]

                        switch (args.length) {
                            case 2: tabM = null; break;
                            case 3: Collections.addAll(tabM, "20", "40", "60", "100", "200", "600"); break;
                        }
                        break;
                        //endregion
                    //region mmoitems
                    case mmoitems:
                        //   0      1        2      args.Length
                        // /goop mmoitems {action}
                        //   -      0        1      args[n]

                        if (Gunging_Ootilities_Plugin.foundMMOItems) {

                            if (args.length == 2) {
                                tabM.add("addGemSlot");
                                tabM.add("countGems");
                                tabM.add("regenerate");
                                tabM.add("setTier");
                                tabM.add("modifier");
                                tabM.add("getTier");
                                tabM.add("fixStacks");
                                tabM.add("stat");
                                tabM.add("upgrade");
                                tabM.add("equipmentUpdate");
                                tabM.add("identify");
                                if (Gunging_Ootilities_Plugin.usingMMOItemShrubs) {
                                    tabM.add("newShrub");
                                    tabM.add("listShrubTypes");
                                    tabM.add("reloadShrubTypes");
                                }

                            } else {
                                switch (args[1].toLowerCase()) {
                                    case "addgemslot":
                                        //   0      1        2          3       4       5     args.Length
                                        // /goop mmoitems addGemSlot <color> <player> <slot>
                                        //   -      0        1          2       3       4     args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = gemstoneColours;
                                                startWith = false;
                                                break;
                                            case 4:
                                                tabM = null;
                                                break;
                                            case 5:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            default: break;
                                        }

                                        break;
                                    case "countgems":
                                        //   0      1        2            3           4        5        6        args.Length
                                        // /goop mmoitems countGems <includeSlots> <player> <slot> <scoreboard>
                                        //   -      0        1            2           3        4        5        args[n]

                                        switch (args.length) {
                                            case 3:
                                                Collections.addAll(tabM, "true", "false", "(also-count-empty?)");
                                                break;
                                            case 4:
                                                tabM = null;
                                                break;
                                            case 5:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 6:
                                                Collections.addAll(tabM, "2..6", "1..", "4..", "..30");
                                            case 7:
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();

                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                                break;
                                            default: break;
                                        }
                                        break;
                                    case "fixstacks":
                                    case "equipmentupdate":
                                        //   0      1        2          3       args.Length
                                        // /goop mmoitems fixStacks [player]
                                        //   -      0        1         2        args[n]

                                        if (args.length == 3) { tabM = null; }

                                        break;
                                    case "modifier":
                                        //   0       1      2          3       4      5         args.Length
                                        // /goop mmoitems modifier <player> <slot> <name>
                                        //   -       0      1          2       3      4         args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 5:
                                                Collections.addAll(tabM,"random", "none");
                                                tabM.addAll(GooPMMOItems.getGlobalModifierNames());
                                                break;
                                            case 6:
                                                Collections.addAll(tabM,"true", "false", "(use-global-modifiers?)");
                                                break;
                                            case 7:
                                                Collections.addAll(tabM,"true", "false", "(use-modifier-chances?)");
                                                break;
                                            default: break;
                                        }

                                        break;
                                    case "stat":
                                        //   0      1       2    3       4       5       6        7         args.Length
                                        // /goop mmoitems stat <stat> <player> <slot> <value> [objective]
                                        //   -      0       1    2       3       4       5        6         args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = GooPMMOItems.GetMMOItem_StatNames();
                                                break;
                                            case 4:
                                                tabM = null;
                                                break;
                                            case 5:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 6:
                                                Collections.addAll(tabM, "read", "+5", "n3", "-8", "+30%", "140%", "-10%", "true", "false", "toggle", "Rogue", "-Cleric", "-all");
                                                break;
                                            case 7:
                                                Collections.addAll(tabM, "2..3", "10..", "..-3", "Uncolored", "true", "false", "Rogue", "3..");
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();
                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                                break;
                                            case 8:
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();
                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                                Collections.addAll(tabM, "read", "+5", "n3", "-8", "+30%", "140%", "-10%");
                                                break;
                                            case 9:
                                            Collections.addAll(tabM, "read", "+5", "n3", "-8", "+30%", "140%", "-10%");
                                            default: break;
                                        }

                                        break;
                                    case "settier":
                                        //   0    1      2      3      4       5        args.Length
                                        // /goop mmi gettier <player> <slot> <value>
                                        //   -    0     1      2       3       4        args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 5:
                                                tabM.addAll(GooPMMOItems.GetTierNames());
                                                tabM.add("none");
                                                break;
                                            default: break;
                                        }

                                        break;
                                    case "regenerate":
                                        //   0    1      2      3      4       5        args.Length
                                        // /goop mmi gettier <player> <slot> <value> [reroll] [name lore ench upgr gems soul exsh]
                                        //   -    0     1      2       3       4        args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 5:
                                                Collections.addAll(tabM, "true", "false", "(reroll-rng-stats?)");
                                                break;
                                            default:
                                                boolean name = false, lore = false, ench = false, upgr = false, gems = false, soul = false, exsh = false, mods = false, skin = false;
                                                for (int i = 5; i < args.length; i++) {
                                                    String str = args[i].toLowerCase();
                                                    if (str.contains("name")) { name = true; }
                                                    if (str.contains("lore")) { lore = true; }
                                                    if (str.contains("ench")) { ench = true; }
                                                    if (str.contains("upgr")) { upgr = true; }
                                                    if (str.contains("gems")) { gems = true; }
                                                    if (str.contains("soul")) { soul = true; }
                                                    if (str.contains("skin")) { skin = true; }
                                                    if (str.contains("ex") && str.contains("sh")) { exsh = true; }
                                                    if (str.contains("mod")) { mods = true; }
                                                }

                                                if (!name) { Collections.addAll(tabM, "name", "keepName"); }
                                                if (!lore) { Collections.addAll(tabM, "lore", "keepLore"); }
                                                if (!ench) { Collections.addAll(tabM, "ench", "keepEnchantments"); }
                                                if (!upgr) { Collections.addAll(tabM, "upgr", "keepUpgrades"); }
                                                if (!gems) { Collections.addAll(tabM, "gems", "keepGems"); }
                                                if (!soul) { Collections.addAll(tabM, "soul", "keepSoulbound"); }
                                                if (!exsh) { Collections.addAll(tabM, "exsh", "keepExternalSH"); }
                                                if (!mods) { Collections.addAll(tabM, "mods", "keepModifiers"); }
                                                if (!skin) { Collections.addAll(tabM, "skin", "keepSkin"); }
                                                break;
                                        }
                                        break;
                                    case "upgrade":
                                        //   0       1     2         3       4          5             6           7              8            args.Length
                                        // /goop mmoitems upgrade <player> <slot> [±]<levels>[%] [break max] [objective] [±][score][%]
                                        //   -       0     1         2       3          4             5           6              7            args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 5:
                                                Collections.addAll(tabM, "read", "1", "2", "3", "n2", "-1", "+4");
                                                break;
                                            case 6:
                                                Collections.addAll(tabM, "true", "false", "(break-max-upgrades?)");
                                                break;
                                            case 7:
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();

                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                                break;
                                            case 8:
                                                Collections.addAll(tabM,"level", "0", "+1", "8", "-12", "n3");
                                                break;
                                            default: break;
                                        }
                                        break;
                                    case "identify":
                                        //   0       1       2         3       4     args.Length
                                        // /goop mmoitems identify <player> <slot>
                                        //   -       0       1         2       3     args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            default: break;
                                        }
                                        break;
                                    case "gettier":
                                        //   0    1      2      3      4       5         6          7   args.Length
                                        // /goop mmi gettier <player> <slot> <value> <objective> <score>
                                        //   -    0     1      2       3       4         5          6    args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                tabM.addAll(OotilityCeption.getSlotKeywords());
                                                break;
                                            case 5:
                                                tabM.addAll(GooPMMOItems.GetTierNames());
                                                tabM.add("none");
                                                break;
                                            case 6:
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();

                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                                break;
                                            case 7:
                                                Collections.addAll(tabM, "-1", "0", "1", "8", "-12");
                                                break;
                                            default: break;
                                        }
                                        break;
                                    case "newshrub":
                                        if (Gunging_Ootilities_Plugin.usingMMOItemShrubs) {

                                            //   0      1        2       3     4   5   6   7     args.Length
                                            // /goop mmoitems newShrub <type> [w] [x] [y] [z]
                                            //   -      0        1       2     3   4   5   6     args[n]

                                            if (args.length >= 4) {
                                                // Just target location I guess?
                                                tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                                // If exists
                                                if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                                    // Git Target Location
                                                    tBlock = null;
                                                }
                                            }

                                            switch (args.length) {
                                                case 3:
                                                    // Suggests some innovative names
                                                    tabM = GooPE_Shrubs.getLoadedShrubTypes();
                                                    break;
                                                case 4:
                                                    //region w
                                                    if (tBlock != null) {

                                                        // Block exists, use thay
                                                        tabM.add(tBlock.getWorld().getName());

                                                    } else {

                                                        // Default Shit: All
                                                        for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                                    }
                                                    //endregion
                                                    break;
                                                case 5:
                                                    //region x
                                                    if (tBlock != null) {

                                                        // Block exists, use thay
                                                        tabM.add(((Integer)tBlock.getX()).toString());

                                                    } else {

                                                        // Default Shit: Player's Own X
                                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                                    }
                                                    //endregion
                                                    break;
                                                case 6:
                                                    //region y
                                                    if (tBlock != null) {

                                                        // Block exists, use thay
                                                        tabM.add(((Integer)tBlock.getY()).toString());

                                                    } else {

                                                        // Default Shit: Player's Own Y
                                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                                    }
                                                    //endregion
                                                    break;
                                                case 7:
                                                    //region z
                                                    if (tBlock != null) {

                                                        // Block exists, use thay
                                                        tabM.add(((Integer)tBlock.getZ()).toString());

                                                    } else {

                                                        // Default Shit: Player's Own Z
                                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                                    }
                                                    //endregion
                                                    break;
                                                default: break;
                                            }
                                        }
                                        break;
                                    default: break;
                                }
                                break;
                            }
                        }
                        break;
                    //endregion
                    //region optifine
                    case optifine:
                        //   0      1        2      args.Length
                        // /goop optifine {action}
                        //   -      0        1      args[n]

                        if (args.length == 2) {
                            tabM.add("glintEnchant");
                            tabM.add("glintDefine");
                            tabM.add("toggleJSON");
                            startWith = false;

                        } else if (args.length > 2) {
                            switch (args[1].toLowerCase()) {
                                case "glintenchant":
                                    //   0      1        2            3             4       5     args.Length
                                    // /goop optifine glintEnchant <glint name> <player> <slot>
                                    //   -      0        1            2             3       4     args[n]

                                    switch (args.length) {
                                        case 3:
                                            for (OptiFineGlint glnt : OptiFineGlint.loadedGlints) { tabM.add(glnt.getGlintName()); }
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "glintdefine":
                                    //   0      1        2            3             4                 5               6         args.Length
                                    // /goop optifine glintDefine <glint name> <enchantment> <enchantment level> [lore line]
                                    //   -      0        1            2             3                 4               5         args[n]

                                    switch (args.length) {
                                        case 3:
                                            Collections.addAll(tabM, "Vortex", "Treasure", "Flames", "Rainbow");
                                            break;
                                        case 4:
                                            for (Enchantment enchantment : Enchantment.values()) {
                                                String enchantmentName = enchantment.getKey().getKey();
                                                tabM.add(enchantmentName); }
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "1", "2", "3", "4", "none");
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "&e< Glint: ");
                                            break;
                                        case 7:
                                            Collections.addAll(tabM, "&6Treasure", "&aVortex", "&cR&6a&ei&an&bb&9o&5w", "&cF&4l&ca&6m&ee&fs&c!&6!&e!");
                                            break;
                                        default:
                                            Collections.addAll(tabM, "&e>");
                                            break;
                                    }
                                    break;
                                default: break;
                            }
                            break;
                        }
                        break;
                    //endregion
                    //region nbt
                    case nbt:
                        //   0    1     2      args.Length
                        // /goop nbt {action}
                        //   -    0     1      args[n]

                        if (args.length == 2 && (args[0].toLowerCase()).equals("nbt")) {
                            tabM.add("removeLore");
                            tabM.add("addLore");
                            tabM.add("rename");
                            tabM.add("revariable");
                            tabM.add("mHealth");
                            tabM.add("mSpeed");
                            tabM.add("aDamage");
                            tabM.add("aSpeed");
                            tabM.add("Armour");
                            tabM.add("aToughness");
                            tabM.add("kResistance");
                            tabM.add("luck");
                            tabM.add("amount");
                            if (GooP_MinecraftVersions.GetMinecraftVersion() >= 14.0) { tabM.add("cModelData"); }
                            tabM.add("enchantment");
                            tabM.add("setMaterial");
                            tabM.add("copy");
                            tabM.add("setItem");
                            tabM.add("damage");

                        } else if (args.length > 2) {
                            scoreboardManager = null;
                            scoreboard = null;

                            switch (args[1].toLowerCase()) {
                                case "addlore":
                                    //   0    1      2      3       4        5     6+       args.Length
                                    // /goop nbt addLore <index> <player> <slot> [fv] <lore line>
                                    //   -    0      1       2       3       4     5+       args[n]

                                    switch (args.length) {
                                        case 3:
                                            Collections.addAll(tabM, "top", "bottom", "0", "1", "2", "-1", "-2");
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "true", "false", "&4F&ci&6l&el&a t&bh&9e&5 w&4o&cr&6l&ed&a w&bi&9t&5h&4 c&co&6l&eo&ar&b!&9!&5!", "&7Property of " + sender.getName(), "&8Gunging was here.", "&7Tier: ");
                                            break;
                                        case 7:
                                            startWith = false;
                                            if ((args[5].toLowerCase()).contains("tier")) { Collections.addAll(tabM, "&fCommon", "&eUncommon", "&9Rare", "&aEpic", "&6Legendary"); }
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "rename":
                                    //   0    1      2      3       4        5+  args.Length
                                    // /goop nbt addLore <player> <slot> <nmae>
                                    //   -    0      1       2       3       4+  args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "Caladbold, Arc of Rainbows", "Dyrnwyn, Bearer of Flames", "&6&lJoyeuse, &eHerald of Victory", "Sord", "Meowmere", "@prefix=&9&l⊱&b✧&9&l⊰@@s= &f&l@@name=Polaris@@desc=, &7Soul Energy taken Form @@prefix@");
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "revar":
                                case "revariable":
                                    //   0    1      2      3       4        5+  args.Length
                                    // /goop nbt addLore <player> <slot> <nmae>
                                    //   -    0      1       2       3       4+  args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "PAPI", "name=Porituiri Shovel", "prefix=&3ᗕ&9࿅;suffix=&9࿅&3ᗒ", "signature=&8Gunging was here");
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "removelore":
                                    //   0    1      2         3       4        5     6   args.Length
                                    // /goop nbt removeLore <index> <player> <slot> [fv]
                                    //   -    0      1         2       3        4     5   args[n]

                                    switch (args.length) {
                                        case 3:
                                            Collections.addAll(tabM, "top", "bottom", "0", "1", "2", "-1", "-2", "all");
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "true", "false");
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "adamage":
                                case "aspeed":
                                case "mhealth":
                                case "mspeed":
                                case "armor":
                                case "armour":
                                case "atoughness":
                                case "kresistance":
                                case "luck":
                                case "cmodeldata":
                                case "amount":
                                case "damage":
                                    //   0    1      2      3      4       5         6       args.Length
                                    // /goop nbt adamage <player> <slot> <value> [objective]
                                    //   -    0     1      2       3       4         5       args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "+5", "n3", "-8", "+30%", "140%", "-10%");
                                            break;
                                        case 6:
                                            scoreboardManager = Bukkit.getScoreboardManager();
                                            scoreboard = scoreboardManager.getMainScoreboard();

                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }

                                            if (args[1].toLowerCase().equals("damage")) { Collections.addAll(tabM, "(prevent-breaking?)", "true", "false"); }
                                            break;
                                        case 7:
                                            if (args[1].toLowerCase().equals("damage")) { Collections.addAll(tabM, "(use-max-durability?)", "true", "false"); }
                                        case 8:
                                            if (args[1].toLowerCase().equals("damage")) {
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();

                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            }
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "enchantment":
                                    //   0    1      2           3      4       5      6        7         args.Length
                                    // /goop nbt enchantment <player> <slot> <ench> <value> [objective]
                                    //   -    0     1            2       3       4     5        6         args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 5:
                                            for (Enchantment enchantment : Enchantment.values()) {
                                                String enchantmentName = enchantment.getKey().getKey();
                                                tabM.add(enchantmentName); }
                                            tabM.add("all");
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "+5", "n3", "-8", "+30%", "140%", "-10%");
                                            break;
                                        case 7:
                                            scoreboardManager = Bukkit.getScoreboardManager();
                                            scoreboard = scoreboardManager.getMainScoreboard();

                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "setmaterial":
                                    //   0    1      2      3       4        5+  args.Length
                                    // /goop nbt addLore <player> <slot> <nmae>
                                    //   -    0      1       2       3       4+  args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 5:
                                            tabM = vanillaMaterialsTab;
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "setitem":
                                    //   0    1      2      3       4       5 6 7        8       9      args.Length
                                    // /goop nbt setitem <player> <slot> {nbt string} <amount> [score]
                                    //   -    0     1      2        3       4 5 6        7       8       args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, OotilityCeption.itemNBTcharKeys); tabM.remove("e");
                                            if (!Gunging_Ootilities_Plugin.foundMMOItems) { tabM.remove("m"); }
                                            if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("mm"); }
                                            break;
                                        case 6:
                                            switch (args[4]){
                                                case "m":
                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_TypeNames(); } else { tabM = new ArrayList<String>(); }
                                                    break;
                                                case "mm":
                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM = GooPMythicMobs.GetMythicItemTypes(); } else { tabM = new ArrayList<>(); }
                                                    break;
                                                case "v":
                                                    tabM = vanillaMaterialsTab;
                                                    break;
                                            }
                                            break;
                                        case 7:
                                            switch (args[4]){
                                                case "m":
                                                    tabM.addAll(GooPMMOItems.GetMMOItem_IDNames(args[5]));
                                                    //if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_IDNames(args[4]); } else { tabM = new ArrayList<String>(); }
                                                    break;
                                                case "v":
                                                case "mm":
                                                    Collections.addAll(tabM, "0");
                                                    break;
                                            }
                                            break;
                                        case 8:
                                            Collections.addAll(tabM, "+5", "n3", "-8", "+30%", "140%", "-10%");
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "copy":
                                    //   0    1    2      3       4          5               6        args.Length
                                    // /goop nbt copy <player> <slots> <source-slot> [±][amount][%]
                                    //   -    0    1      2       3          4               5          args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            Collections.addAll(tabM, "ec*", "4-5", "c<RESULT>");
                                        case 5:
                                            Collections.addAll(tabM, "ec2", "14", "c29");
                                            tabM.addAll(OotilityCeption.getSlotKeywords());
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "+5", "n3", "-8", "+30%", "140%", "-10%");
                                            break;
                                        default: break;
                                    }
                                    break;
                                default: break;
                            }
                            break;
                        }
                        break;
                        //endregion
                    //region customstructures
                    case customstructures:
                        //   0          1             2    args.Length
                        // /goop customstructures {action}
                        //   -          0             1     args[n]
                        if (args.length == 2) {
                            tabM.add("build");
                            tabM.add("edit");
                            tabM.add("import");
                            tabM.add("list");

                        } else {
                            switch (args[1].toLowerCase()) {
                                //region edit
                                case "edit":
                                    //   0           1           2     3     args.Length
                                    // /goop customstructures  edit {action}
                                    //   -           0           1     2     args[n]

                                    if (args.length == 3) {
                                        tabM.add("composition");
                                        tabM.add("triggers");
                                        tabM.add("actions");

                                    } else {

                                        switch (args[2].toLowerCase()) {
                                            //region triggers
                                            case "triggers":
                                                //   0           1           2     3       4          5            args.Length
                                                // /goop customstructures  edit actions {action} <structure name>
                                                //   -           0           1     2       3          4            args[n]

                                                if (args.length == 4) {
                                                    tabM.add("list");
                                                    tabM.add("add");
                                                    tabM.add("remove");
                                                    tabM.add("parameter");

                                                } else {

                                                    switch (args[3].toLowerCase()) {
                                                        //region parameter
                                                        case "parameter":
                                                            //   0           1          2       3       4        5          6                7           8       args.Length
                                                            // /goop customstructures edit triggers parameter {action} <structure name> <trigger name> {action}
                                                            //   -           0          1       2       3        4          5                6           7       args[n]

                                                            if (args.length < 8) {

                                                                switch (args.length) {
                                                                    case 5:
                                                                        tabM.add("list");
                                                                        tabM.add("add");
                                                                        tabM.add("remove");
                                                                        tabM.add("edit");
                                                                        break;
                                                                    case 6:
                                                                        // Adds all loaded structures
                                                                        for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                                        break;
                                                                    case 7:
                                                                        // The suggestions are the Structure Triggers
                                                                        tabM = csTriggersTab;
                                                                        startWith = false;
                                                                        break;
                                                                    default: break;
                                                                }

                                                            } else {
                                                                // Is it an actual trigger
                                                                CustomStructureTriggers trigr = null;
                                                                try {
                                                                    // Yes, it seems to be
                                                                    trigr = CustomStructureTriggers.valueOf(args[6]);

                                                                    // Not recognized
                                                                } catch (IllegalArgumentException ex) { }

                                                                // Depends on parameter action
                                                                switch (args[4].toLowerCase()) {
                                                                    case "add":
                                                                        //   0           1          2       3        4      5          6                7        8 9 [10]       args.Length
                                                                        // /goop customstructures edit triggers parameter add <structure name> <trigger name> <parameter args>
                                                                        //   -           0          1       2        3      4          5                6        7 8 [9]        args[n]

                                                                        switch (args.length) {
                                                                            case 8:
                                                                                if (trigr != null) {
                                                                                    switch (trigr) {
                                                                                        case BREAK:
                                                                                        case COMPLETE:
                                                                                        case INTERACT:
                                                                                        case PUNCH:
                                                                                        case PRESSUREPLATE_PLAYERS:
                                                                                        case PRESSUREPLATE_ITEMS:
                                                                                            Collections.addAll(tabM, OotilityCeption.itemNBTcharKeys);
                                                                                            if (!Gunging_Ootilities_Plugin.foundMMOItems) { tabM.remove("m"); }
                                                                                            if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("mm"); }
                                                                                            break;
                                                                                        case PRESSUREPLATE_MONSTERS:
                                                                                        case PRESSUREPLATE_ANIMALS:
                                                                                            Collections.addAll(tabM, OotilityCeption.entityNBTcharKeys);
                                                                                            if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("m"); }
                                                                                            break;
                                                                                        default: break;
                                                                                    }
                                                                                } else { tabM = new ArrayList<>(); }
                                                                                break;
                                                                            case 9:
                                                                                if (trigr != null) {
                                                                                    switch (trigr) {

                                                                                        case BREAK:
                                                                                        case COMPLETE:
                                                                                        case INTERACT:
                                                                                        case PUNCH:
                                                                                        case PRESSUREPLATE_PLAYERS:
                                                                                        case PRESSUREPLATE_ITEMS:
                                                                                            switch (args[7].toLowerCase()){
                                                                                                case "m":
                                                                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_TypeNames(); } else { tabM = new ArrayList<String>(); }
                                                                                                    break;
                                                                                                case "mm":
                                                                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM = GooPMythicMobs.GetMythicItemTypes(); } else { tabM = new ArrayList<>(); }
                                                                                                    break;
                                                                                                case "e":
                                                                                                    tabM = enchantmentsTab;
                                                                                                    break;
                                                                                                case "v":
                                                                                                    tabM = vanillaMaterialsTab;
                                                                                                    break;
                                                                                                case "i":
                                                                                                    tabM = GooPIngredient.GetLoadedIngrs();
                                                                                                    break;
                                                                                            }
                                                                                            break;
                                                                                        case PRESSUREPLATE_MONSTERS:
                                                                                        case PRESSUREPLATE_ANIMALS:
                                                                                            switch (args[7].toLowerCase()){
                                                                                                case "m":
                                                                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) {

                                                                                                        tabM = GooPMythicMobs.GetMythicMobTypes();

                                                                                                    } else { tabM = new ArrayList<String>(); }
                                                                                                    break;
                                                                                                case "v":
                                                                                                    tabM = vanillaEntitiesTab;
                                                                                                    break;
                                                                                            }
                                                                                            break;
                                                                                        default: break;
                                                                                    }
                                                                                } else { tabM = new ArrayList<>(); }
                                                                                break;
                                                                            case 10:
                                                                                if (trigr != null) {
                                                                                    switch (trigr) {

                                                                                        case BREAK:
                                                                                        case COMPLETE:
                                                                                        case INTERACT:
                                                                                        case PUNCH:
                                                                                        case PRESSUREPLATE_PLAYERS:
                                                                                        case PRESSUREPLATE_ITEMS:
                                                                                            switch (args[7]) {
                                                                                                case "m":
                                                                                                    tabM.addAll(GooPMMOItems.GetMMOItem_IDNames(args[6]));
                                                                                                    //if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_IDNames(args[4]); } else { tabM = new ArrayList<String>(); }
                                                                                                    break;
                                                                                                case "e":
                                                                                                    Collections.addAll(tabM, "1", "2", "3", "4", "5", "6");
                                                                                                    break;
                                                                                                case "v":
                                                                                                case "i":
                                                                                                case "mm":
                                                                                                    Collections.addAll(tabM, "0");
                                                                                                    break;
                                                                                            }
                                                                                            break;
                                                                                        default: break;
                                                                                    }
                                                                                } else { tabM = new ArrayList<>(); }
                                                                                break;
                                                                            default: break;
                                                                        }

                                                                        break;
                                                                    case "remove":
                                                                        //   0           1          2       3        4      5          6                7           8       args.Length
                                                                        // /goop customstructures edit triggers parameter remove <structure name> <trigger name> <parameter index>
                                                                        //   -           0          1       2        3      4          5                6           7       args[n]

                                                                        if (args.length == 8) {
                                                                            Collections.addAll(tabM, "0", "1", "2", "3");

                                                                        }

                                                                        break;
                                                                    case "edit":
                                                                        //   0           1          2       3        4      5          6                7             8               9 10 [11]     args.Length
                                                                        // /goop customstructures edit triggers parameter edit <structure name> <trigger name> <parameter index> <parameter args>
                                                                        //   -           0          1       2        3      4          5                6             7               8  9 [10]     args[n]

                                                                        switch (args.length) {
                                                                            case 8:
                                                                                Collections.addAll(tabM, "0", "1", "2", "3");
                                                                                break;
                                                                            case 9:
                                                                                if (trigr != null) {
                                                                                    switch (trigr) {

                                                                                        case BREAK:
                                                                                        case COMPLETE:
                                                                                        case INTERACT:
                                                                                        case PUNCH:
                                                                                        case PRESSUREPLATE_PLAYERS:
                                                                                        case PRESSUREPLATE_ITEMS:
                                                                                            Collections.addAll(tabM, OotilityCeption.itemNBTcharKeys);
                                                                                            if (!Gunging_Ootilities_Plugin.foundMMOItems) { tabM.remove("m"); }
                                                                                            if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("mm"); }
                                                                                            break;
                                                                                        case PRESSUREPLATE_MONSTERS:
                                                                                        case PRESSUREPLATE_ANIMALS:
                                                                                            Collections.addAll(tabM, OotilityCeption.entityNBTcharKeys);
                                                                                            if (!Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.remove("m"); }
                                                                                            break;
                                                                                        default: break;
                                                                                    }
                                                                                } else { tabM = new ArrayList<>(); }
                                                                                break;
                                                                            case 10:
                                                                                if (trigr != null) {
                                                                                    switch (trigr) {

                                                                                        case BREAK:
                                                                                        case COMPLETE:
                                                                                        case INTERACT:
                                                                                        case PUNCH:
                                                                                        case PRESSUREPLATE_PLAYERS:
                                                                                        case PRESSUREPLATE_ITEMS:
                                                                                            switch (args[8]) {
                                                                                                case "m":
                                                                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_TypeNames(); } else { tabM = new ArrayList<String>(); }
                                                                                                    break;
                                                                                                case "mm":
                                                                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM = GooPMythicMobs.GetMythicItemTypes(); } else { tabM = new ArrayList<>(); }
                                                                                                    break;
                                                                                                case "e":
                                                                                                    tabM = enchantmentsTab;
                                                                                                    break;
                                                                                                case "v":
                                                                                                    tabM = vanillaMaterialsTab;
                                                                                                    break;
                                                                                                case "i":
                                                                                                    tabM = GooPIngredient.GetLoadedIngrs();
                                                                                                    break;
                                                                                            }
                                                                                            break;
                                                                                        case PRESSUREPLATE_MONSTERS:
                                                                                        case PRESSUREPLATE_ANIMALS:
                                                                                            switch (args[8]) {
                                                                                                case "m":
                                                                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) {

                                                                                                        tabM = GooPMythicMobs.GetMythicMobTypes();

                                                                                                    } else { tabM = new ArrayList<String>(); }
                                                                                                    break;
                                                                                                case "v":
                                                                                                case "i":
                                                                                                    tabM = vanillaEntitiesTab;
                                                                                                    break;
                                                                                            }
                                                                                            break;
                                                                                        default: break;
                                                                                    }
                                                                                } else { tabM = new ArrayList<>(); }
                                                                                break;
                                                                            case 11:
                                                                                if (trigr != null) {
                                                                                    switch (trigr) {

                                                                                        case BREAK:
                                                                                        case COMPLETE:
                                                                                        case INTERACT:
                                                                                        case PUNCH:
                                                                                        case PRESSUREPLATE_PLAYERS:
                                                                                        case PRESSUREPLATE_ITEMS:
                                                                                            switch (args[8]) {
                                                                                                case "m":
                                                                                                    tabM.addAll(GooPMMOItems.GetMMOItem_IDNames(args[7]));
                                                                                                    //if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM = GooPMMOItems.GetMMOItem_IDNames(args[4]); } else { tabM = new ArrayList<String>(); }
                                                                                                    break;
                                                                                                case "e":
                                                                                                    Collections.addAll(tabM, "1", "2", "3", "4", "5", "6");
                                                                                                    break;
                                                                                                case "v":
                                                                                                case "i":
                                                                                                case "mm":
                                                                                                    Collections.addAll(tabM, "0");
                                                                                                    break;
                                                                                            }
                                                                                            break;
                                                                                        default: break;
                                                                                    }
                                                                                } else { tabM = new ArrayList<>(); }
                                                                                break;
                                                                            default: break;
                                                                        }

                                                                        break;
                                                                    default: break;
                                                                }
                                                            }
                                                            break;
                                                        //endregion
                                                        //region add & remove
                                                        case "add":
                                                        case "remove":
                                                            //   0           1          2       3    4          5             6          args.Length
                                                            // /goop customstructures edit triggers add <structure name> <trigger name>
                                                            //   -           0          1       2    3          4             5           args[n]

                                                            switch (args.length) {
                                                                case 5:
                                                                    // Adds all loaded structures
                                                                    for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                                    break;
                                                                case 6:
                                                                    // The suggestions are the Structure Triggers
                                                                    tabM = csTriggersTab;
                                                                    startWith = false;
                                                                    break;
                                                                default: break;
                                                            }

                                                            break;
                                                        //endregion
                                                        //region list
                                                        case "list":
                                                            //   0           1          2       3    4          5         args.Length
                                                            // /goop customstructures edit triggers list [structure name]
                                                            //   -           0          1       2    3          4          args[n]

                                                            if (args.length == 5) {
                                                                // Adds all loaded structures
                                                                for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                            }

                                                            break;
                                                        //endregion
                                                        default: break;
                                                    }

                                                }

                                                break;
                                            //endregion
                                            //region actions
                                            case "actions":
                                                //   0           1           2     3       4          5            args.Length
                                                // /goop customstructures  edit actions {action} <structure name>
                                                //   -           0           1     2       3          4            args[n]

                                                if (args.length == 4) {
                                                    tabM.add("list");
                                                    tabM.add("add");
                                                    tabM.add("remove");
                                                    tabM.add("edit");

                                                } else {

                                                    switch (args[3].toLowerCase()) {
                                                        //region add
                                                        case "add":
                                                            //   0           1          2     3      4         5             6+      args.Length
                                                            // /goop customstructures edit actions edit <structure name> <action...>
                                                            //   -           0          1     2      3         4             5+      args[n]

                                                            switch (args.length) {
                                                                case 5:
                                                                    // Adds all loaded structures
                                                                    for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                                    break;
                                                                case 6:
                                                                    // Suggests some reasonable radii
                                                                    Collections.addAll(tabM, "give %player% cooked_beef 1", "clear %player% beef 1", "goop consumeitem %player% v BEEF 0 1");
                                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.add("mm mobs spawn SkeletonKing 1 %world%,%structure-center-comma%"); }
                                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM.add("mmoitems stations open arcane-forge %player%"); }
                                                                    break;
                                                                default: break;
                                                            }

                                                            break;
                                                        //endregion
                                                        //region remove
                                                        case "remove":
                                                            //   0           1          2     3       4         5             6    args.Length
                                                            // /goop customstructures edit actions remove <structure name> <index>
                                                            //   -           0          1     2       3         4             5    args[n]

                                                            switch (args.length) {
                                                                case 5:
                                                                    // Adds all loaded structures
                                                                    for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                                    break;
                                                                case 6:
                                                                    // Suggests some reasonable radii
                                                                    Collections.addAll(tabM, "0", "1", "2", "3");
                                                                    break;
                                                                default: break;
                                                            }

                                                            break;
                                                        //endregion
                                                        //region edit
                                                        case "edit":
                                                            //   0           1          2     3      4         5             6       7+      args.Length
                                                            // /goop customstructures edit actions edit <structure name> <index> <action...>
                                                            //   -           0          1     2      3         4             5       6+      args[n]

                                                            switch (args.length) {
                                                                case 5:
                                                                    // Adds all loaded structures
                                                                    for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                                    break;
                                                                case 6:
                                                                    // Suggests some reasonable radii
                                                                    Collections.addAll(tabM, "0", "1", "2", "3");
                                                                    break;
                                                                case 7:
                                                                    // Suggests some reasonable radii
                                                                    Collections.addAll(tabM, "give %player% cooked_beef 1", "clear %player% beef 1");
                                                                    if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.add("mm mobs spawn SkeletonKing 1 %world%,%structure-center-comma%"); }
                                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM.add("mmoitems stations open arcane-forge %player%"); }
                                                                    break;
                                                                default: break;
                                                            }

                                                            break;
                                                        //endregion
                                                        //region list
                                                        case "list":
                                                            //   0           1          2     3     4          5         args.Length
                                                            // /goop customstructures edit actions list <structure name>
                                                            //   -           0          1     2     3          4        args[n]

                                                            if (args.length == 5) {
                                                                // Adds all loaded structures
                                                                for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }

                                                            }

                                                            break;
                                                        //endregion
                                                        default: break;
                                                    }

                                                }

                                                break;
                                            //endregion
                                            //region composition
                                            case "composition":
                                                //   0           1          2       3              4               5         6   7   8   9  args.Length
                                                // /goop customstructures edit composition <structure name> <cuboid radius> [w] [x] [y] [z]
                                                //   -           0          1       2              3               4         5   6   7   8  args[n]

                                                // Gets block player is pointing at. AIR = NULL
                                                if (args.length >= 6) {
                                                    // Just target location I guess?
                                                    tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                                    // If exists
                                                    if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                                        // Git Target Location
                                                        tBlock = null;
                                                    }
                                                }

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                                        break;
                                                    case 5:
                                                        // Suggests some reasonable radii
                                                        Collections.addAll(tabM, "1", "2", "3");
                                                        if (Gunging_Ootilities_Plugin.foundWorldEdit) { tabM.add("selection"); }
                                                        break;
                                                    case 6:
                                                        //region w
                                                        // Assuming it IS a player and not the console, console wont be considered.
                                                        if (sender instanceof Player) {
                                                            if (tBlock != null) {

                                                                // Block exists, use thay
                                                                tabM.add(tBlock.getWorld().getName());

                                                            } else {

                                                                // Default Shit: All
                                                                for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                                            }
                                                        }
                                                        //endregion
                                                        break;
                                                    case 7:
                                                        //region x
                                                        if (tBlock != null) {

                                                            // Block exists, use thay
                                                            tabM.add(((Integer)tBlock.getX()).toString());

                                                        } else {

                                                            // Default Shit: Player's Own X
                                                            tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                                        }
                                                        //endregion
                                                        break;
                                                    case 8:
                                                        //region y
                                                        if (tBlock != null) {

                                                            // Block exists, use thay
                                                            tabM.add(((Integer)tBlock.getY()).toString());

                                                        } else {

                                                            // Default Shit: Player's Own Y
                                                            tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                                        }
                                                        //endregion
                                                        break;
                                                    case 9:
                                                        //region z
                                                        if (tBlock != null) {

                                                            // Block exists, use thay
                                                            tabM.add(((Integer)tBlock.getZ()).toString());

                                                        } else {

                                                            // Default Shit: Player's Own Z
                                                            tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                                        }
                                                        //endregion
                                                        break;
                                                    default: break;
                                                }
                                                break;
                                            //endregion
                                        }

                                    }

                                    break;
                                //endregion
                                //region import
                                case "import":
                                    //   0           1           2         3                4          5   6   7   8    args.Length
                                    // /goop customstructures import <structure name> <cuboid radius> [w] [x] [y] [z]
                                    //   -           0           1          2               3          4   5   6   9  args[n]

                                    // Gets block player is pointing at. AIR = NULL
                                    if (args.length >= 5) {
                                        // Just target location I guess?
                                        tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                        // If exists
                                        if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                            // Git Target Location
                                            tBlock = null;
                                        }
                                    }

                                    switch (args.length) {
                                        case 3:
                                            // Suggests some innovative names
                                            Collections.addAll(tabM, "Fridge_Top", "Fridge_Bottom", "Baking_Oven", "Gold-Diamond_Transmutator", "Demon_Altar", "Stove");

                                            // Removes all loaded structures
                                            for (CustomStructure struct : CustomStructures.loadedStructures) {
                                                tabM.remove(struct.getStructureName());
                                            }
                                            break;
                                        case 4:
                                            // Suggests some reasonable radii
                                            Collections.addAll(tabM, "1", "2", "3");
                                            if (Gunging_Ootilities_Plugin.foundWorldEdit) { tabM.add("selection"); }
                                            break;
                                        case 5:
                                            //region w
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(tBlock.getWorld().getName());

                                            } else {

                                                // Default Shit: All
                                                for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                            }
                                            //endregion
                                            break;
                                        case 6:
                                            //region x
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getX()).toString());

                                            } else {

                                                // Default Shit: Player's Own X
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 7:
                                            //region y
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getY()).toString());

                                            } else {

                                                // Default Shit: Player's Own Y
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 8:
                                            //region z
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getZ()).toString());

                                            } else {

                                                // Default Shit: Player's Own Z
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                            }
                                            //endregion
                                            break;
                                        default: break;
                                    }
                                    break;
                                //endregion
                                //region build
                                case "build":
                                    //   0           1           2         3          4   5   6   7    args.Length
                                    // /goop customstructures build <structure name> [w] [x] [y] [z] [frw]
                                    //   -           0           1          2         3   4   5   6    args[n]

                                    // Gets block player is pointing at. AIR = NULL
                                    if (args.length >= 4) {
                                        // Just target location I guess?
                                        tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                        // If exists
                                         if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                            // Git Target Location
                                            tBlock = null;
                                        }
                                    }

                                    switch (args.length) {
                                        case 3:
                                            // Adds all loaded structures
                                            for (CustomStructure struct : CustomStructures.loadedStructures) { tabM.add(struct.getStructureName()); }
                                            break;
                                        case 4:
                                            //region w
                                            // Assuming it IS a player and not the console, console wont be considered.
                                            if (sender instanceof Player) {
                                                if (tBlock != null) {

                                                    // Block exists, use thay
                                                    tabM.add(tBlock.getWorld().getName());

                                                } else {

                                                    // Default Shit: All
                                                    for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                                }
                                            }
                                            //endregion
                                            break;
                                        case 5:
                                            //region x
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getX()).toString());

                                            } else {

                                                // Default Shit: Player's Own X
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 6:
                                            //region y
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getY()).toString());

                                            } else {

                                                // Default Shit: Player's Own Y
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                            }
                                            //endregion
                                        break;
                                        case 7:
                                            //region z
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getZ()).toString());

                                            } else {

                                                // Default Shit: Player's Own Z
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 8:
                                            for (Orientations or : Orientations.values()) { tabM.add(or.toString()); }
                                            break;
                                        default: break;
                                    }
                                    break;
                                    //endregion
                                default: break;
                            }
                            break;
                        }
                        break;
                        //endregion
                    //region mythicmobs
                    case mythicmobs:
                        //   0      1        2      args.Length
                        // /goop mythicmobs {action}
                        //   -      0        1      args[n]

                        if (args.length == 2) {
                            tabM.add("runSkillAs");
                            tabM.add("minion");
                            tabM.add("damageTakenLink");

                        } else if (args.length > 2) {
                            switch (args[1].toLowerCase()) {
                                case "damagetakenlink":
                                    //   0      1              2              3             4               5                   6              7        args.Length
                                    // /goop mythicmobs damagetakenlink <source uuid> <receiver uuid> [transfer percent] [prevent percent] [duration]
                                    //   -      0              1              2             3               4                   5              6        args[n]

                                    switch (args.length) {
                                        case 3:
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            Collections.addAll(tabM,"<Transfer-Fraction>", "0.4", "40%");
                                            break;
                                        case 6:
                                            Collections.addAll(tabM,"<Protection-Fraction>", "0.2", "20%");
                                            break;
                                        case 7:
                                            Collections.addAll(tabM,"true", "false");
                                            break;
                                        case 8:
                                            Collections.addAll(tabM,"20s", "5m", "3h");
                                            break;
                                    }
                                    break;
                                case "runskillas":
                                    //   0      1           2         3       4    args.Length
                                    // /goop mythicmobs runskillas <skill> <uuid>
                                    //   -      0            1        2       3    args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM.add("<Mythic Skill Internal Name>");
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8:
                                            Collections.addAll(tabM, "@<target>", "~<trigger>", "vDAMAGE=30");
                                            if (args[args.length - 1].startsWith("@")) {
                                                for (Player p : Bukkit.getOnlinePlayers()) { tabM.add("@" + p.getName()); }
                                                if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                                    tabM.add("@<caster.name>");
                                                    tabM.add("@<trigger.name>");
                                                    tabM.add("@<target.name>"); }

                                            } else if (args[args.length - 1].equals("~")) {
                                                for (Player p : Bukkit.getOnlinePlayers()) { tabM.add("~" + p.getName()); }
                                                if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                                    tabM.add("~<caster.name>");
                                                    tabM.add("~<trigger.name>");
                                                    tabM.add("~<target.name>"); }
                                            }
                                            break;
                                        default: break;
                                    }

                                    break;
                                case "minion":
                                case "minions":

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM.add("<Minion UUID>");
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "20", "30", "10.5");
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "<Mythic Skill Internal Name>");
                                            break;
                                        case 7:
                                            Collections.addAll(tabM, "(pvp-block?)", "true", "false");
                                            break;
                                        default: break;
                                    }

                                    break;
                                default: break;
                            }
                            break;
                        }
                    break;
                    //endregion
                    //region scoreboard
                    case scoreboard:
                        //   0      1        2      args.Length
                        // /goop scoreboard {action}
                        //   -      0        1      args[n]

                        if (args.length == 2) {
                            tabM.add("top");
                            tabM.add("last");
                            tabM.add("damageTakenLink");
                            tabM.add("range");

                        } else if (args.length > 2) {

                            scoreboardManager = Bukkit.getScoreboardManager();
                            scoreboard = scoreboardManager.getMainScoreboard();

                            switch (args[1].toLowerCase()) {
                                case "top":
                                case "last":
                                    //   0        1      2           3                4            5       args.Length
                                    // /goop scoreboard top <objective source> <objective target> <n>
                                    //   -        0      1           2                3            4       args[n]

                                    switch (args.length) {
                                        case 3:
                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            break;
                                        case 4:
                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "5", "3", "1");
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "200", "300", "600");
                                            for (EntityType eType : EntityType.values()) { tabM.add(eType.toString()); }
                                            break;
                                        default: break;
                                    }

                                    break;
                                case "range":
                                    //   0        1      2           3                4            5       args.Length
                                    // /goop scoreboard top <objective source> <objective target> <n>
                                    //   -        0      1           2                3            4       args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "0..6", "3", "..20", "-3..", "-100..-60");
                                            break;
                                        default: break;
                                    }

                                    break;
                                case "damagetakenlink":
                                    //   0      1           2                3             4            5             6         args.Length
                                    // /goop scoreboard damagetakenlink <entity uuid> <objective> [players only] [dont delete]
                                    //   -      0           1                2             3            4             5         args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM.add("<Entity UUID Here>");
                                            break;
                                        case 4:
                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            break;
                                        case 5:
                                        case 6:
                                            Collections.addAll(tabM,"true", "false");
                                            break;
                                        default:  break;
                                    }
                                    break;
                                default:  break;
                            }
                            break;
                        }
                        break;
                    //endregion
                    //region containers
                    case containers:

                        if (args.length == 2) {
                            tabM.add("open");
                            tabM.add("see");
                            tabM.add("close");
                            tabM.add("access");
                            if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM.add("tempEquip"); }
                            tabM.add("config");
                            tabM.add("unregister");
                            tabM.add("playerinventory");

                        } else {

                            switch (args[1].toLowerCase()) {

                                //region close
                                case "close":
                                    //   0        1      2       3       args.Length
                                    // /goop containers close <player>
                                    //   -        0      1       2       args[n]

                                    if (args.length == 3) { tabM = null; }
                                    break;
                                //endregion
                                //region open
                                case "open":
                                    //   0        1      2         3             4           args.Length
                                    // /goop containers open <container type> [player]
                                    //   -        0      1         2              3           args[n]


                                    switch (args.length) {
                                        case 3:
                                            tabM.addAll(GCL_Personal.getByInternalName().keySet());
                                            tabM.addAll(GCL_Station.getByInternalName().keySet());
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            tabM.add("USAGE");
                                            tabM.add("PREVIEW");
                                            break;
                                        default:  break;
                                    }
                                    break;
                                    //endregion
                                //region tempequip
                                case "tempequip":
                                    //   0        1         2           3         4     5       6 7 8    9         args.Length
                                    // /goop containers tempeEquip <container> <slot> <player> <m T I> [time]
                                    //   -        0         1           2         3     4       5 6 7    8         args[n]


                                    switch (args.length) {
                                        case 3:
                                            tabM.addAll(GCL_Personal.getByInternalName().keySet());
                                            break;
                                        case 4:
                                            tabM.add("0");
                                            tabM.add("1");
                                            tabM.add("2");
                                            tabM.add("23");
                                            tabM.add("34");
                                            tabM.add("45");
                                            tabM.add("56");
                                            break;
                                        case 5:
                                            tabM = null;
                                            break;
                                        case 6:
                                            tabM.add("m");
                                            break;
                                        case 7:
                                            switch (args[5]){
                                                case "m":
                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) tabM = GooPMMOItems.GetMMOItem_TypeNames();
                                                    break;
                                            }
                                            break;
                                        case 8:
                                            switch (args[5]){
                                                case "m":
                                                    if (Gunging_Ootilities_Plugin.foundMMOItems) tabM.addAll(GooPMMOItems.GetMMOItem_IDNames(args[6]));
                                                    break;
                                            }
                                            break;
                                        case 9:
                                            Collections.addAll(tabM, "(timer)", "20s", "120s", "5m" ,"72h", "180d", "1y");
                                            break;
                                        default:  break;
                                    }
                                    break;
                                    //endregion
                                //region player inventory
                                case "playerinventory":
                                    //   0        1             2           3          4           args.Length
                                    // /goop containers playerInventory <container> [player]
                                    //   -        0             1           2           3           args[n]


                                    switch (args.length) {
                                        case 3:
                                            tabM.addAll(GCL_Player.getByInternalName().keySet());
                                            tabM.add("CLEAR");
                                            break;
                                        case 4:
                                            for (Player p : Bukkit.getOnlinePlayers()) { tabM.add(p.getName()); }
                                            tabM.add("%player%");
                                            if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                                tabM.add("<caster.name>");
                                                tabM.add("<trigger.name>");
                                                tabM.add("<target.name>"); }
                                            tabM.add("DEFAULT");
                                            break;
                                        default:  break;
                                    }
                                    break;
                                    //endregion
                                //region see
                                case "see":
                                    //   0        1      2         3             4           args.Length
                                    // /goop containers see <container type> [who] <player> [preview]
                                    //   -        0      1         2              3           args[n]


                                    switch (args.length) {
                                        case 3:
                                            tabM.addAll(GCL_Personal.getByInternalName().keySet());
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            for (Player p : Bukkit.getOnlinePlayers()) { tabM.add(p.getName()); }
                                            tabM.add("%player%");
                                            if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                                tabM.add("<caster.name>");
                                                tabM.add("<trigger.name>");
                                                tabM.add("<target.name>"); }
                                        case 6:
                                            tabM.add("USAGE");
                                            tabM.add("PREVIEW");
                                            break;
                                        default:  break;
                                    }
                                    break;
                                //endregion
                                //region unregister
                                case "unregister":
                                    //   0        1         2           3        4      5 6 7 8      args.Length
                                    // /goop containers unregister <container> <owner> [w x y z]
                                    //   -        0         1           2        3      4 5 6 7      args[n]

                                    if (args.length >= 5 && args.length <= 8) {
                                        // Just target location I guess?
                                        tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                        // If exists
                                        if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                            // Git Target Location
                                            tBlock = null;
                                        }
                                    }

                                    switch (args.length) {
                                        case 3:
                                            tabM.addAll(GCL_Personal.getByInternalName().keySet());
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            //region w
                                            // Assuming it IS a player and not the console, console wont be considered.
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(tBlock.getWorld().getName());

                                            } else {

                                                // Default Shit: All
                                                for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                            }
                                            //endregion
                                            break;
                                        case 6:
                                            //region x
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getX()).toString());

                                            } else {

                                                // Default Shit: Player's Own X
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 7:
                                            //region y
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getY()).toString());

                                            } else {

                                                // Default Shit: Player's Own Y
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 8:
                                            //region z
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getZ()).toString());

                                            } else {

                                                // Default Shit: Player's Own Z
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                            }
                                            //endregion
                                            break;
                                        default:  break;
                                    }
                                    break;
                                //endregion
                                //region access
                                case "access":
                                    //   0         1       2           3           4      5 6 7 8    args.Length
                                    // /goop containers access <container type> [player] [w x y z]
                                    //   -         0       1           2           3      4 5 6 7   args[n]

                                    // Gets block player is pointing at. AIR = NULL
                                    if (args.length >= 5) {
                                        // Just target location I guess?
                                        tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                                        // If exists
                                        if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                            // Git Target Location
                                            tBlock = null;
                                        }
                                    }

                                    switch (args.length) {
                                        case 3:
                                            tabM.addAll(GCL_Physical.getByInternalName().keySet());
                                            tabM.add("open_only");
                                            break;
                                        case 4:
                                            tabM = null;
                                            break;
                                        case 5:
                                            //region w
                                            // Assuming it IS a player and not the console, console wont be considered.
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(tBlock.getWorld().getName());

                                            } else {

                                                // Default Shit: All
                                                for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                            }
                                            //endregion
                                            break;
                                        case 6:
                                            //region x
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getX()).toString());

                                            } else {

                                                // Default Shit: Player's Own X
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 7:
                                            //region y
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getY()).toString());

                                            } else {

                                                // Default Shit: Player's Own Y
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                            }
                                            //endregion
                                            break;
                                        case 8:
                                            //region z
                                            if (tBlock != null) {

                                                // Block exists, use thay
                                                tabM.add(((Integer)tBlock.getZ()).toString());

                                            } else {

                                                // Default Shit: Player's Own Z
                                                tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                            }
                                            //endregion
                                            break;
                                        default:  break;
                                    }
                                    break;
                                    //endregion
                                //region config
                                case "config":
                                    //   0         1       2      3+    args.Length
                                    // /goop containers config {action}
                                    //   -         0       1      2+    args[n]

                                    if (args.length == 3) {
                                        tabM.add("new");
                                        tabM.add("title");
                                        tabM.add("rows");
                                        tabM.add("contents");
                                        tabM.add("commands");
                                        tabM.add("aliases");
                                        tabM.add("equipment");
                                        tabM.add("masks");
                                        tabM.add("edgeMaterial");
                                        tabM.add("restrictions");
                                        tabM.add("view");
                                        tabM.add("options");
                                        tabM.add("station");

                                    } else {

                                        switch (args[2].toLowerCase()) {
                                            //region New
                                            case "new":
                                                //   0      1          2    3         4                 5          6        args.Length
                                                // /goop containers config new <container name> <container type> [rows]
                                                //   -      0          1    2         3                 4          5        args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.add("<Original Name With No Spaces>");

                                                        break;
                                                    case 5:
                                                        // The suggestions are the Container TYpes
                                                        tabM = cnTypesTab;
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "1", "2", "3", "4", "5", "6");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Title
                                            case "title":
                                                //   0      1          2     3          4            5+       args.Length
                                                // /goop containers config title <container name> <title>
                                                //   -      0          1     2          3            4+       args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        tabM.add("<Amazing title to display to players>");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Aliases
                                            case "aliases":
                                                //   0      1          2     3          4              5       6    args.Length
                                                // /goop containers config aliases <container name> <slots> [alias]
                                                //   -      0          1     2          3              4       5    args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "0", "1-4", "2", "3-5", "0-8,18-26", "0,2,4,6,8", "0,2-6,8");
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "CONTOUR", "ActionSlot", "Whatever_That_Has_No_Spaces", "(Leave blanc to remove Aliases)");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Equipment
                                            case "equipment":
                                                //   0      1          2     3          4              5       6    args.Length
                                                // /goop containers config aliases <container name> <slots> [alias]
                                                //   -      0          1     2          3              4       5    args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "0", "1-4", "2", "3-5", "0-8,18-26", "0,2,4,6,8", "0,2-6,8");
                                                    case 6:
                                                        Collections.addAll(tabM, "true", "false");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Options
                                            case "options":
                                                //   0      1          2     3       4         5        6       args.Length
                                                // /goop containers config view <container> <option> <value>
                                                //   -      0          1     2       3         4        5       args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "MythicLibStation", "AllowDrag", "DragOverflow", "DuplicateEquipment", "EdgeFormations");
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "true", "false");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Station Options
                                            case "station":
                                                //   0      1          2      3          4          5        6      args.Length
                                                // /goop containers config station <container> resultSlot [slot]
                                                //   -      0          1      2          3          4        5      args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "resultSlot");
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "0", "1", "2", "4", "8", "16", "32");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Masks
                                            case "masks":
                                                //   0      1          2     3          4              5       6      7     args.Length
                                                // /goop containers config masks <container name> <slots> [<type/id> <name>]
                                                //   -      0          1     2          3              4       5      6     args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "0", "1-4", "2", "3-5", "0-8,18-26", "0,2,4,6,8", "0,2-6,8");
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "type", "id", "(Leave blanc to remove masks)");
                                                        break;
                                                    case 7:
                                                        if (args[5].toLowerCase().equals("type")) {
                                                            tabM.addAll(ApplicableMask.getLoadedMaskNames());
                                                            if (Gunging_Ootilities_Plugin.foundMMOItems) { tabM.addAll(GooPMMOItems.GetMMOItem_TypeNames()); }
                                                        }
                                                        else if (args[5].toLowerCase().equals("id")) {
                                                            Collections.addAll(tabM, "<MMOItem ID to allow to be placed>");
                                                            for (KindRestriction kr : KindRestriction.values()) { tabM.add(kr.toString()); }
                                                        }
                                                        else { tabM = new ArrayList<>();}
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Contents
                                            case "contents":
                                            case "view":
                                                //   0      1          2     3              4           args.Length
                                                // /goop containers config contents <container name>
                                                //   -      0          1     2              3           args[n]

                                                if (args.length == 4) {// Adds all loaded structures
                                                    tabM.addAll(GCL_Templates.getByInternalName().keySet());
                                                }

                                                break;
                                            //endregion
                                            //region Commands
                                            case "commands":
                                                //   0      1          2     3          4               5           6           7+       args.Length
                                                // /goop containers config commands <container name> <slot>     <command>
                                                // /goop containers config commands <container name> <slot> <onStore/onTake> <command>
                                                // /goop containers config commands <container name> slots
                                                //   -      0          1     2          3              4            5           6+       args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "0", "1", "2", "3", "5", "36", "53", "slots", "onClose", "onOpen");
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "onStore", "onTake");
                                                    case 7:
                                                        Collections.addAll(tabM, "(Leave blanc to remove command)", "clear", "remove.1", "remove.2", "goop nbt setMaterial %player% %provided-slot% nether_star", "minecraft:effect give %player% strength 2 20");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Edge Material
                                            case "edgematerial":
                                                //   0      1          2        3       args.Length
                                                // /goop containers config edgeMaterial
                                                //   -      0          1        2       args[n]
                                                tabM = new ArrayList<String>();

                                                break;
                                            //endregion
                                            //region Rows
                                            case "rows":
                                                //   0      1          2     3          4            5       args.Length
                                                // /goop containers config rows <container name> <amount>
                                                //   -      0          1     2          3            4       args[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "1", "2", "3", "4", "5", "6");
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                            //region Restrictions
                                            case "restrictions":
                                            case "restrict":
                                                //   0      1          2      3          4                5         6       7       args.Length
                                                // /goop containers config restrictions <container name> <slots> <kind> <value>
                                                //   -      0          1      2          3                4         5      6        rgs[n]

                                                switch (args.length) {
                                                    case 4:
                                                        // Adds all loaded structures
                                                        tabM.addAll(GCL_Templates.getByInternalName().keySet());

                                                        break;
                                                    case 5:
                                                        Collections.addAll(tabM, "0", "1-4", "2", "3-5", "0-8,18-26", "0,2,4,6,8", "0,2-6,8");
                                                        break;
                                                    case 6:
                                                        Collections.addAll(tabM, "behaviour", "class", "permission", "level", "unlockable");
                                                        break;
                                                    case 7:
                                                        switch (args[5].toLowerCase().replace(" ", "_").replace("-", "_")) {
                                                            default: break;
                                                            case "behaviour":
                                                                for (RestrictedBehaviour rb : RestrictedBehaviour.values()) { tabM.add(rb.toString()); }
                                                                break;
                                                            case "class":
                                                                Collections.addAll(tabM, "Mage", "Rogue", "Paladin", "Mage,Rogue,Paladin", "North__Warrior");
                                                                break;
                                                            case "unlockable":
                                                                Collections.addAll(tabM, "thay", "orThat", "andThis", "thay&&andThis,orThat");
                                                                break;
                                                            case "permission":
                                                                Collections.addAll(tabM, "permission", "parmesan", "provolone", "cheese.blue_cheese.eat", "permission&&parmesan,provolone");
                                                                break;
                                                            case "level":
                                                                Collections.addAll(tabM, "1..", "6..", "10..20", "..6");
                                                                break;
                                                        }
                                                        break;
                                                    default:  break;
                                                }

                                                break;
                                            //endregion
                                        }

                                    }
                                //endregion
                            }
                            break;
                        }
                        break;
                    //endregion
                    //region grief
                    case grief:
                        //   0     1     2       3     4 5 6 7      8        9              args.Length
                        // /goop grief <b|l> <player> <w x y z> <radius> [asCuboid = false] [bedrock Break]
                        //   -     0     1       2     3 4 5 6      7        8              args[n]

                        if (args.length >= 4 && args.length <= 7) {
                            // Just target location I guess?
                            tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                            // If exists
                            if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                // Git Target Location
                                tBlock = null;
                            }
                        }

                        switch (args.length) {
                            case 2: Collections.addAll(tabM, "b", "l", "bl", "lb"); break;
                            case 3: tabM = null; break;
                            case 4:
                                //region w
                                // Assuming it IS a player and not the console, console wont be considered.
                                if (tBlock != null) {

                                    // Block exists, use thay
                                    tabM.add(tBlock.getWorld().getName());

                                } else {

                                    // Default Shit: All
                                    for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                }

                                if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.add("<caster.l.w>"); }
                                //endregion
                                break;
                            case 5:
                                //region x
                                if (tBlock != null) {

                                    // Block exists, use thay
                                    tabM.add(((Integer)tBlock.getX()).toString());

                                } else {

                                    // Default Shit: Player's Own X
                                    tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                }
                                if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.add("<goop.projectile.origin.x>"); }
                                //endregion
                                break;
                            case 6:
                                //region y
                                if (tBlock != null) {

                                    // Block exists, use thay
                                    tabM.add(((Integer)tBlock.getY()).toString());

                                } else {

                                    // Default Shit: Player's Own Y
                                    tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                }
                                if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.add("<goop.projectile.origin.y>"); }
                                //endregion
                                break;
                            case 7:
                                //region z
                                if (tBlock != null) {

                                    // Block exists, use thay
                                    tabM.add(((Integer)tBlock.getZ()).toString());

                                } else {

                                    // Default Shit: Player's Own Z
                                    tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                }
                                if (Gunging_Ootilities_Plugin.foundMythicMobs) { tabM.add("<goop.projectile.origin.z>"); }
                                //endregion
                                break;
                            case 8: Collections.addAll(tabM, "0", "1", "2", "3", "(break-radius)"); break;
                            case 9: Collections.addAll(tabM, "true", "false", "(as-cuboid?)"); break;
                            case 10: Collections.addAll(tabM, "0 (Hand)", "1 (Wood)", "2 (Stone)", "3 (Iron)", "4 (Diamond)", "5 (Netherite)"); break;
                            case 11: Collections.addAll(tabM, "true", "false", "(break-bedrock?)"); break;
                            case 12: Collections.addAll(tabM, ((Player) sender).getWorld().getName(), "(reboot-key)"); break;
                        }
                        break;

                    //endregion
                    //region compare
                    case compare:
                        //   0     1        2           3        4        5...                                 args.Length
                        // /goop compare <player> <objective> <score> {Something} GC_<operator> {Anything}
                        //   -     0        1           2        3        4...                                 args[n]

                        switch (args.length) {
                            case 2: tabM = null; break;
                            case 3:
                                scoreboardManager = Bukkit.getScoreboardManager();
                                scoreboard = scoreboardManager.getMainScoreboard();

                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                break;
                            case 4:
                                Collections.addAll(tabM, "1", "2", "3", "4", "5", "sum", "difference", "product", "division", "power", "root", "cos", "sin", "tan", "arcCos", "arcSin", "arcTan");
                                break;
                            case 5:
                                tabM.add("{Something (May even contain spaces. Will parse PAPI Placeholders)}");
                                break;
                            case 6:
                                Collections.addAll(tabM, "{Something (May even contain spaces. Will parse PAPI Placeholders)}", "GC_=", "GC_E>", "GC_<=>", "GC_S=", "GC_!>");
                                break;
                            default:
                                Collections.addAll(tabM, "{Anything (May even contain spaces. Will parse PAPI Placeholders)}", "{Something (May even contain spaces. Will parse PAPI Placeholders)}", "GC_=", "GC_>E", "GC_<=>", "GC_S=", "GC_!>");
                                break;
                        }
                        break;

                    //endregion
                    //region tell
                    case tell:
                        //   0    1       2        3     args.Length
                        // /goop tell <players> <message...>
                        //   -    0       1        2     args[n]

                        switch (args.length) {
                            case 2: tabM = null; break;
                            case 3: Collections.addAll(tabM, "&9Some &dcool &amessage &bwith &ecolour &ccodes.", "&7Hey &a%player%&7! thanks for &3parsing&7 these &2placeholders&7!"); break;
                        }
                        break;
                    //endregion
                    //region tp
                    case tp:
                        //   0    1     2     3 4 5 6     args.Length
                        // /goop tp [player] <w x y z>
                        //   -   0     1      2 3 4 5      args[n]

                        if (args.length >= 2 && args.length <= 6) {
                            // Just target location I guess?
                            tBlock = ((Player)sender).getTargetBlockExact(30, FluidCollisionMode.NEVER);

                            // If exists
                            if (OotilityCeption.IsAirNullAllowed(tBlock)) {
                                // Git Target Location
                                tBlock = null;
                            }
                        }
                        boolean firstWorld = false;
                        if (args.length > 2) { firstWorld = (Bukkit.getWorld(args[1]) != null); }

                        switch (args.length) {
                            case 2:
                                //region w
                                // Assuming it IS a player and not the console, console wont be considered.
                                if (sender instanceof Player) {
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(tBlock.getWorld().getName());

                                    } else {

                                        // Default Shit: All
                                        for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                    }
                                }
                                //endregion

                                // Adds online players
                                for (Player p : Bukkit.getOnlinePlayers()) { tabM.add(p.getName()); }
                                tabM.add("%player%");
                                if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                    tabM.add("<caster.name>");
                                    tabM.add("<trigger.name>");
                                    tabM.add("<target.name>"); }
                                break;
                            case 3:
                                if (firstWorld) {
                                    //region x
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(((Integer)tBlock.getX()).toString());

                                    } else {

                                        // Default Shit: Player's Own X
                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                    }
                                    //endregion
                                } else {
                                    //region w
                                    // Assuming it IS a player and not the console, console wont be considered.
                                    if (sender instanceof Player) {
                                        if (tBlock != null) {

                                            // Block exists, use thay
                                            tabM.add(tBlock.getWorld().getName());

                                        } else {

                                            // Default Shit: All
                                            for (World wrld : Bukkit.getWorlds()) { tabM.add(wrld.getName()); }
                                        }
                                    }
                                    //endregion
                                }

                                // Adds online players
                                for (Player p : Bukkit.getOnlinePlayers()) { tabM.add(p.getName()); }
                                tabM.add("%player%");
                                if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                    tabM.add("<caster.name>");
                                    tabM.add("<trigger.name>");
                                    tabM.add("<target.name>"); }
                                break;
                            case 4:
                                if (firstWorld) {
                                    //region y
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(((Integer)tBlock.getY()).toString());

                                    } else {

                                        // Default Shit: Player's Own Y
                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                    }
                                    //endregion
                                } else {
                                    //region x
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(((Integer)tBlock.getX()).toString());

                                    } else {

                                        // Default Shit: Player's Own X
                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockX()).toString());
                                    }
                                    //endregion
                                }
                                break;
                            case 5:
                                if (firstWorld) {
                                    //region z
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(((Integer)tBlock.getZ()).toString());

                                    } else {

                                        // Default Shit: Player's Own Z
                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                    }
                                    //endregion
                                } else {
                                    //region y
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(((Integer)tBlock.getY()).toString());

                                    } else {

                                        // Default Shit: Player's Own Y
                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockY()).toString());
                                    }
                                    //endregion
                                }
                                break;
                            case 6:
                                if (!firstWorld) {
                                    //region z
                                    if (tBlock != null) {

                                        // Block exists, use thay
                                        tabM.add(((Integer)tBlock.getZ()).toString());

                                    } else {

                                        // Default Shit: Player's Own Z
                                        tabM.add(((Integer)((Player)sender).getLocation().getBlockZ()).toString());
                                    }
                                    //endregion
                                }
                                break;
                            default: break;
                        }
                        break;

                    //endregion
                    //region delay
                    case delay:
                        //   0    1       2        3     args.Length
                        // /goop delay <ticks> <command>
                        //   -    0       1        2     args[n]

                        if (args.length == 2) { Collections.addAll(tabM, "1", "10", "20", "200"); }
                        break;
                    //endregion
                    //region vault
                    case vault:
                        //   0      1        2      args.Length
                        // /goop vault {action}
                        //   -      0        1      args[n]

                        if (Gunging_Ootilities_Plugin.foundVault) {

                            if (args.length == 2) {
                                tabM.add("charge");
                                tabM.add("operation");
                                tabM.add("checkBalance");

                            } else if (args.length > 2) {
                                switch (args[1].toLowerCase()) {
                                    case "charge":
                                        //   0      1     2       3       4         args.Length
                                        // /goop vault charge <player> <amount>
                                        //   -      0     1       2       3         args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                Collections.addAll(tabM, "5", "10", "0.99", "100", "299.99");
                                                break;
                                            default: break;
                                        }

                                        break;
                                    case "operation":
                                        //   0      1     2       3       4         args.Length
                                        // /goop operation charge <player> <value>
                                        //   -      0     1       2       3         args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                Collections.addAll(tabM, "+5", "n3", "-8", "+30%", "140%", "-10%");
                                                break;
                                            default: break;
                                        }

                                        break;
                                    case "checkbalance":
                                        //   0      1     2             3       4         5         6       args.Length
                                        // /goop vault checkbalance <player> <range> [objective] [score]
                                        //   -      0     1             2       3         4         5       args[n]

                                        switch (args.length) {
                                            case 3:
                                                tabM = null;
                                                break;
                                            case 4:
                                                Collections.addAll(tabM, "5..10", "..9.999", "0..300", "100..", "299.99..", "4000..");
                                                break;
                                            case 5:
                                                scoreboardManager = Bukkit.getScoreboardManager();
                                                scoreboard = scoreboardManager.getMainScoreboard();

                                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                                break;
                                            case 6:
                                                Collections.addAll(tabM, "amount", "1", "2", "3", "4", "5", "6", "+5", "n3", "-8", "+30%", "140%", "-10%");
                                                break;
                                            default: break;
                                        }

                                        break;
                                    default: break;
                                }
                            }
                        }
                        break;
                    //endregion
                    //region sudo
                    case sudo:
                        //   0    1       2        3     args.Length
                        // /goop sudo <whio> <command>
                        // /goop sudop <whio> <command>
                        //   -    0       1        2     args[n]

                        if (args.length == 2) {
                            Collections.addAll(tabM, "console", "@p", "<Some UUID>");
                            for (Player p : Bukkit.getOnlinePlayers()) { tabM.add(p.getName()); }
                            tabM.add("%player%");
                            if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                                tabM.add("<caster.name>");
                                tabM.add("<trigger.name>");
                                tabM.add("<target.name>"); }
                        }
                        break;
                    //endregion
                    //region permission
                    case permission:
                        //   0      1          2        3       4           5   args.Length
                        // /goop permission <player> <perm> [objective] [score]
                        //   -      0          1        2       3           4   args[n]

                        switch (args.length) {
                            case 2:
                                tabM = null;
                                break;
                            case 3:
                                // Get Perms
                                ArrayList<Player> permissive = OotilityCeption.GetPlayers(null, args[2], null);

                                // Get perms I guess
                                for (Player p : permissive) {

                                    // For each permission
                                    for (PermissionAttachmentInfo per : p.getEffectivePermissions()) {

                                        // Add name I guess
                                        if (!tabM.contains(per.getPermission())) {

                                            // Add
                                            tabM.add(per.getPermission());
                                        }
                                    }
                                }
                                break;
                            case 4:
                                scoreboardManager = Bukkit.getScoreboardManager();
                                scoreboard = scoreboardManager.getMainScoreboard();

                                for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                break;
                            case 5:
                                Collections.addAll(tabM, "1", "2", "3", "4", "5", "6", "+5", "n3", "-8", "+30%", "140%", "-10%");
                                break;
                            default: break;
                        }
                        break;
                    //endregion
                    //region unlock
                    case unlockables:
                        //   0      1        2      args.Length
                        // /goop optifine {action}
                        //   -      0        1      args[n]

                        if (args.length == 2) {
                            tabM.add("unlock");
                            tabM.add("lock");
                            tabM.add("lockState");
                            tabM.add("check");

                        } else if (args.length > 2) {
                            switch (args[1].toLowerCase()) {
                                case "unlock":
                                    //   0    1        2       3      4     5       args.Length
                                    // /goop unlock unlock <player> <goal> [x]
                                    //   -    0        1       2      3     4       args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM = GooPUnlockables.GetKnownGoals();
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "(value)", "true", "+5", "n3", "-8", "+30%", "140%", "-10%", "2.5", "3.2");
                                            break;
                                        case 6:
                                            Collections.addAll(tabM, "(override-timer?)", "true", "false");
                                            break;
                                        case 7:
                                            Collections.addAll(tabM, "(timer)", "20s", "120s", "5m" ,"72h", "180d", "1y");
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "lock":
                                    //   0    1        2       3      4     5       args.Length
                                    // /goop unlock unlock <player> <goal>
                                    //   -    0        1       2      3     4       args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM = GooPUnlockables.GetKnownGoals();
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "lockstate":
                                    //   0    1        2       3      4     5       args.Length
                                    // /goop unlock unlock <player> <goal> <x>
                                    //   -    0        1       2      3     4       args[n]

                                    switch (args.length) {
                                        case 3:
                                            tabM = null;
                                            break;
                                        case 4:
                                            tabM = GooPUnlockables.GetKnownGoals();
                                            break;
                                        case 5:
                                            Collections.addAll(tabM, "true", "false", "5", "8", "30", "140", "2.5", "3.2", "0..3", "2.5..3.2", "10..20");
                                            break;
                                        case 6:
                                            scoreboardManager = Bukkit.getScoreboardManager();
                                            scoreboard = scoreboardManager.getMainScoreboard();

                                            for (Objective objectiv : scoreboard.getObjectives()) { tabM.add(objectiv.getName()); }
                                            break;
                                        case 7:
                                            Collections.addAll(tabM, "read", "1", "2", "3", "4", "5", "6", "+5", "n3", "-8", "+30%", "140%", "-10%");
                                            break;
                                        default: break;
                                    }
                                    break;
                                case "check":
                                    //   0    1        2       3      4     5       args.Length
                                    // /goop unlock unlock <player> <goal> <x>
                                    //   -    0        1       2      3     4       args[n]
                                    tabM = null;
                                    break;
                                default: break;
                            }
                            break;
                        }
                    //endregion
                    default:  break;
                }
            }

            // If its not null
            if (tabM == null) {

                // Nonullify as player names
                tabM = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) { tabM.add(p.getName()); }
                tabM.add("%player%");
                if (Gunging_Ootilities_Plugin.foundMythicMobs) {
                    tabM.add("<caster.name>");
                    tabM.add("<trigger.name>");
                    tabM.add("<target.name>"); }
            }

            // If there is anything!
            if (tabM.size() > 0){

                // If there is a filtering key
                if (tabSt.length() > 0){
                    // True List
                    List<String> tabMCT = new ArrayList<String>();

                    // Filter the original list and build into the returned one
                    for (String stComp : tabM) {

                        // By default, the tab complete ignores caps
                        //noinspection ConstantConditions
                        if (ignoreCaps) {

                            // Will ignore caps
                            if (startWith) {

                                if (stComp.toLowerCase().startsWith(tabSt.toLowerCase())) { tabMCT.add(stComp); }
                            } else {

                                if (stComp.toLowerCase().contains(tabSt.toLowerCase())) { tabMCT.add(stComp); }
                            }

                            // Ignore Caps is in FALSE = consider caps
                        } else {

                            // Will account for caps
                            if (startWith) {

                                if (stComp.startsWith(tabSt)) { tabMCT.add(stComp); }
                            } else {

                                if (stComp.contains(tabSt)) { tabMCT.add(stComp); }
                            }
                        }
                    }

                    // Filtered
                    return tabMCT;

                    // If there is nothing to filter from
                } else {

                    // Normal
                    return  tabM;
                }

            } else {

                return tabM;
            }
        }

        return null;
    }
}
