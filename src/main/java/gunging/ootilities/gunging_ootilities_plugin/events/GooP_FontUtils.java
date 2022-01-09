package gunging.ootilities.gunging_ootilities_plugin.events;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import gunging.ootilities.gunging_ootilities_plugin.misc.NameVariableOperation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

public class GooP_FontUtils implements Listener {

    // Page
    public static final String pageIdentifier = "unicode_page_";
    public static final String[] page37 = { "㜀", "㜁", "㜂", "㜃", "㜄", "㜅", "㜆", "㜇", "㜈", "㜉", "㜊", "㜋", "㜌", "㜍", "㜎", "㜏", "㜐", "㜑", "㜒", "㜓", "㜔", "㜕", "㜖", "㜗", "㜘", "㜙", "㜚", "㜛", "㜜", "㜝", "㜞", "㜟", "㜠", "㜡", "㜢", "㜣", "㜤", "㜥", "㜦", "㜧", "㜨", "㜩", "㜪", "㜫", "㜬", "㜭", "㜮", "㜯", "㜰", "㜱", "㜲", "㜳", "㜴", "㜵", "㜶", "㜷", "㜸", "㜹", "㜺", "㜻", "㜼", "㜽", "㜾", "㜿", "㝀", "㝁", "㝂", "㝃", "㝄", "㝅", "㝆", "㝇", "㝈", "㝉", "㝊", "㝋", "㝌", "㝍", "㝎", "㝏", "㝐", "㝑", "㝒", "㝓", "㝔", "㝕", "㝖", "㝗", "㝘", "㝙", "㝚", "㝛", "㝜", "㝝", "㝞", "㝟", "㝠", "㝡", "㝢", "㝣", "㝤", "㝥", "㝦", "㝧", "㝨", "㝩", "㝪", "㝫", "㝬", "㝭", "㝮", "㝯", "㝰", "㝱", "㝲", "㝳", "㝴", "㝵", "㝶", "㝷", "㝸", "㝹", "㝺", "㝻", "㝼", "㝽", "㝾", "㝿", "㞀", "㞁", "㞂", "㞃", "㞄", "㞅", "㞆", "㞇", "㞈", "㞉", "㞊", "㞋", "㞌", "㞍", "㞎", "㞏", "㞐", "㞑", "㞒", "㞓", "㞔", "㞕", "㞖", "㞗", "㞘", "㞙", "㞚", "㞛", "㞜", "㞝", "㞞", "㞟", "㞠", "㞡", "㞢", "㞣", "㞤", "㞥", "㞦", "㞧", "㞨", "㞩", "㞪", "㞫", "㞬", "㞭", "㞮", "㞯", "㞰", "㞱", "㞲", "㞳", "㞴", "㞵", "㞶", "㞷", "㞸", "㞹", "㞺", "㞻", "㞼", "㞽", "㞾", "㞿", "㟀", "㟁", "㟂", "㟃", "㟄", "㟅", "㟆", "㟇", "㟈", "㟉", "㟊", "㟋", "㟌", "㟍", "㟎", "㟏", "㟐", "㟑", "㟒", "㟓", "㟔", "㟕", "㟖", "㟗", "㟘", "㟙", "㟚", "㟛", "㟜", "㟝", "㟞", "㟟", "㟠", "㟡", "㟢", "㟣", "㟤", "㟥", "㟦", "㟧", "㟨", "㟩", "㟪", "㟫", "㟬", "㟭", "㟮", "㟯", "㟰", "㟱", "㟲", "㟳", "㟴", "㟵", "㟶", "㟷", "㟸", "㟹", "㟺", "㟻", "㟼", "㟽", "㟾", "㟿" };
    public static final String[] page38 = { "㠀", "㠁", "㠂", "㠃", "㠄", "㠅", "㠆", "㠇", "㠈", "㠉", "㠊", "㠋", "㠌", "㠍", "㠎", "㠏", "㠐", "㠑", "㠒", "㠓", "㠔", "㠕", "㠖", "㠗", "㠘", "㠙", "㠚", "㠛", "㠜", "㠝", "㠞", "㠟", "㠠", "㠡", "㠢", "㠣", "㠤", "㠥", "㠦", "㠧", "㠨", "㠩", "㠪", "㠫", "㠬", "㠭", "㠮", "㠯", "㠰", "㠱", "㠲", "㠳", "㠴", "㠵", "㠶", "㠷", "㠸", "㠹", "㠺", "㠻", "㠼", "㠽", "㠾", "㠿", "㡀", "㡁", "㡂", "㡃", "㡄", "㡅", "㡆", "㡇", "㡈", "㡉", "㡊", "㡋", "㡌", "㡍", "㡎", "㡏", "㡐", "㡑", "㡒", "㡓", "㡔", "㡕", "㡖", "㡗", "㡘", "㡙", "㡚", "㡛", "㡜", "㡝", "㡞", "㡟", "㡠", "㡡", "㡢", "㡣", "㡤", "㡥", "㡦", "㡧", "㡨", "㡩", "㡪", "㡫", "㡬", "㡭", "㡮", "㡯", "㡰", "㡱", "㡲", "㡳", "㡴", "㡵", "㡶", "㡷", "㡸", "㡹", "㡺", "㡻", "㡼", "㡽", "㡾", "㡿", "㢀", "㢁", "㢂", "㢃", "㢄", "㢅", "㢆", "㢇", "㢈", "㢉", "㢊", "㢋", "㢌", "㢍", "㢎", "㢏", "㢐", "㢑", "㢒", "㢓", "㢔", "㢕", "㢖", "㢗", "㢘", "㢙", "㢚", "㢛", "㢜", "㢝", "㢞", "㢟", "㢠", "㢡", "㢢", "㢣", "㢤", "㢥", "㢦", "㢧", "㢨", "㢩", "㢪", "㢫", "㢬", "㢭", "㢮", "㢯", "㢰", "㢱", "㢲", "㢳", "㢴", "㢵", "㢶", "㢷", "㢸", "㢹", "㢺", "㢻", "㢼", "㢽", "㢾", "㢿", "㣀", "㣁", "㣂", "㣃", "㣄", "㣅", "㣆", "㣇", "㣈", "㣉", "㣊", "㣋", "㣌", "㣍", "㣎", "㣏", "㣐", "㣑", "㣒", "㣓", "㣔", "㣕", "㣖", "㣗", "㣘", "㣙", "㣚", "㣛", "㣜", "㣝", "㣞", "㣟", "㣠", "㣡", "㣢", "㣣", "㣤", "㣥", "㣦", "㣧", "㣨", "㣩", "㣪", "㣫", "㣬", "㣭", "㣮", "㣯", "㣰", "㣱", "㣲", "㣳", "㣴", "㣵", "㣶", "㣷", "㣸", "㣹", "㣺", "㣻", "㣼", "㣽", "㣾", "㣿" };

    // Load
    public static HashMap<Integer, HashMap<String, String>> fontLinks = new HashMap<>();
    public static HashMap<String, String> reverseLinks = new HashMap<>();

    // Reload
    public static void ReloadFonts(OotilityCeption oots) {

        // Clear
        fontLinks = new HashMap<>();
        reverseLinks = new HashMap<>();

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.fontsPair != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.fontsPair;

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Log da shit
            for(Map.Entry<String, Object> val : (ofgStorage.getValues(false)).entrySet()){

                // Get Glint Name
                String tName = val.getKey();
                String[] crUTF = null;

                // Is it valid?
                if (tName.toLowerCase().startsWith(pageIdentifier)) {

                    // Which page?
                    String pNumber = tName.substring(pageIdentifier.length());

                    // Switch
                    switch (pNumber) {
                        case "37":
                            crUTF = page37;
                            break;
                        case "38":
                            crUTF = page38;
                            break;
                        default:
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog("Error when loading Font Code List \u00a73" + tName + "\u00a77: The only supported pages are 37 and 38"); }
                            break;
                    }

                    // Get String list
                    if (crUTF != null) {

                        // List from config
                        List<String> rawFontLinks = ofgStorage.getStringList(tName);

                        // Is it exist?
                        if (rawFontLinks != null) {

                            // Iterate over contents
                            for (String flink : rawFontLinks) {

                                // Contains spaces?
                                if (flink.contains(" ")) {

                                    // Split
                                    String[] fSplit = flink.split(" ");

                                    // Must parse index
                                    Integer texIndex = null;
                                    if (OotilityCeption.IntTryParse(fSplit[0])) {

                                        // Parse
                                        texIndex = OotilityCeption.ParseInt(fSplit[0]);

                                        // Under constraints?
                                        if (texIndex < 1 || texIndex > 256) {

                                            // Failure
                                            texIndex = null;

                                            // Log
                                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog("Error when loading Font Code List \u00a73" + tName + "\u00a77 entry \u00a7b" + flink + "\u00a77: Texture indexes range from \u00a7e1\u00a77 to \u00a7e256\u00a77, cant use \u00a7e" + fSplit[0]); }
                                        }

                                    // Log
                                    } else {

                                        // Log
                                        if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog("Error when loading Font Code List \u00a73" + tName + "\u00a77 entry \u00a7b" + flink + "\u00a77: Expected integer number instead of \u00a7e" + fSplit[0]); }
                                    }

                                    // Get Code
                                    String code = fSplit[1];

                                    // Success?
                                    if (texIndex != null) {

                                        // Register
                                        RegisterCode(code, crUTF[texIndex - 1]);
                                    }

                                // Doesnt even have a space ffs
                                } else {

                                    // Log
                                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog("Error when loading Font Code List \u00a73" + tName + "\u00a77 entry \u00a7b" + flink + "\u00a77: You must specify at least an [INDEX] and a [CODE]"); }
                                }
                            }

                        // Empty list i guess
                        } else {

                            // Log
                            if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog("Error when loading Font Code List \u00a73" + tName + "\u00a77: No valid entries. Check YML format for string lists."); }
                        }
                    }

                // Note
                } else {
                    if (!Gunging_Ootilities_Plugin.blockImportantErrorFeedback) { oots.CPLog("Error when loading Font Code List \u00a73" + tName + "\u00a77: List name must be \u00a7eUnicode_Page_[Number]"); }
                }
            }
        }
    }

    /**
     * Registers a code in the global code links array.
     * <p></p>
     * The format of the array is kinda unique, thought it'd be faster to filter out by size and then by actual string.
     * Thus it checks for the length of the string, and gives a set of codes of that length, and then the codes link to a character.
     * @param fCode String, character sequence, that will stand for fChar
     * @param fChar Character that will replace fCode
     */
    public static void RegisterCode(String fCode, String fChar) {

        // Get code length
        Integer cLength = fCode.length();

        // Confirm existance
        if (!fontLinks.containsKey(cLength)) { fontLinks.put(cLength, new HashMap<>()); }

        // Set
        fontLinks.get(cLength).put(fCode, fChar);
        //DBG//OotilityCeption. Log("Registered code of length \u00a73" + cLength + "\u00a77 source \u00a7e" + fCode + "\u00a77 as \u00a7e" + fChar);

        // Remember inverse
        reverseLinks.put(fChar, ":" + fCode + ":");
    }

    /**
     * If there is a code linked to this string, it will return the character it encodes for. Otherwise, NULL
     */
    public static String CodeFrom(String source) {
        //DBG//OotilityCeption. Log("Code From \u00a7e" + source + "\u00a77 at \u00a7b" + aft);

        // No spaces
        if (source.contains(" ")) {
            //DBG//OotilityCeption. Log("\u00a7cInvalid Sintax: \u00a77Space");
            return null;
        }

        // Get code length
        Integer cLength = source.length();
        //DBG//OotilityCeption. Log("Length \u00a73" + cLength);

        // Get
        HashMap<String, String> fLinks = fontLinks.get(cLength);

        // Confirm existance
        if (fLinks != null) {

            // EValuate
            for (String fCode : fLinks.keySet()) {
                //DBG//OotilityCeption. Log("Comparing to registerd code \u00a73" + fCode);

                // Does it equal?
                if (fCode.toLowerCase().equals(source.toLowerCase())) {
                    //DBG//OotilityCeption. Log("\u00a7aAccepted: \u00a77Match");

                    // Return it
                    String ret = fLinks.get(fCode);

                    // Ok then go
                    return ret;

                } else {
                    //DBG//OotilityCeption. Log("\u00a76Nope: \u00a77No match");
                }
            }
        } else {
            //DBG//OotilityCeption. Log("\u00a7cInvalid: \u00a77No Codes Registered");
        }

        // Noep
        return null;
    }

    /**
     * Parses all font codes from the message.
     * <p></p>
     * The format must be :[CODE]:
     */
    @NotNull
    public static String ParseFontLinks(@NotNull String source) {

        // Does it have colons bruh
        if (source.contains(":")) {

            // Split
            String[] kodes = source.split(":");

            // Finished rebuilt
            StringBuilder builder = new StringBuilder("");
            boolean kounterAppend = true;

            // Evaluate each ig
            for (String str : kodes) {
                // Has it a code
                String code = null;
                if (!kounterAppend) { code = CodeFrom(str); }

                // Did it exist?
                if (code != null) {

                    // Append as-is
                    builder.append(code);

                    // Counter next colon append
                    kounterAppend = true;

                    // Was not a code, return original colon
                } else {

                    // If last one wasnt a code
                    if (!kounterAppend) {

                        // Return
                        builder.append(":").append(str);

                    } else {

                        // Ok last one was a code, no need to return colon
                        builder.append(str);

                        // NExt one shall again
                        kounterAppend = false;
                    }
                }
            }

            // Solidify
            String finished = builder.toString();

            // Does it have an extra colon?
            if (!source.startsWith(":") && finished.startsWith(":")) { finished = finished.substring(1); }

            // Return colons of ending
            if (source.endsWith(":")) {

                // Identify last colon
                int trailingColons = 0;
                for (int t = (source.length()-1); t >= 0; t--) {

                    // Is it a colon?
                    if (source.charAt(t) == ':') {

                        // This is a trailing colon
                        trailingColons = t;

                    // Otherwise break
                    } else {

                        t = -1;
                    }
                }

                // Add 1 if last one was a counter parse
                if (kounterAppend) { trailingColons++; }

                // Get those
                String tColons = source.substring(trailingColons);
                finished = finished + tColons;
            }

            // Return thay
            return finished;
        }

        // No need for operations
        return source;
    }

    /**
     * Reverts all font codes from the message.
     * <p></p>
     * The format will be :[CODE]:
     */
    @NotNull
    public static String UnparseFontLinks(@NotNull String source) {

        // For each inverse code
        for (String iCode : reverseLinks.keySet()) {

            // Replace in message
            source = source.replace(iCode, reverseLinks.get(iCode));
        }

        // No need for operations
        return source;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPrethreadChat(AsyncPlayerChatEvent event) {
        // Parse Message
        event.setMessage(ParseFontLinks(event.getMessage()));

        // Parse as OP
        if (event.getPlayer().isOp() && OotilityCeption.If(Gunging_Ootilities_Plugin.anvilRenameEnabled)) {

            // Revariablize
            event.setMessage(OotilityCeption.RerenameNameVarialbes(event.getPlayer(), new NameVariableOperation(event.getMessage()), null, null, null));
        }
    }
}
