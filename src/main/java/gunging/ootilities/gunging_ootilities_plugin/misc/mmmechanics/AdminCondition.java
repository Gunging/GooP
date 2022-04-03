package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class AdminCondition extends CustomMMCondition implements IEntityCondition {
    boolean requireOP;
    @NotNull ArrayList<GameMode> requiredGamemodes = new ArrayList<>();
    @NotNull ArrayList<GameMode> blockedGamemodes = new ArrayList<>();
    @NotNull ArrayList<String> requiredNames = new ArrayList<>();

    public AdminCondition(MythicLineConfig mlc) {
        super(mlc);
        requireOP = mlc.getBoolean(new String[]{"op", "requireop", "rop"}, false);
        PlaceholderString rGM = mlc.getPlaceholderString(new String[]{"requiredgamemodes", "gm", "rgm"}, null);
        PlaceholderString bGM = mlc.getPlaceholderString(new String[]{"blockedgamemodes", "bgm"}, null);
        PlaceholderString rNm = mlc.getPlaceholderString(new String[]{"requirednames", "name", "names"}, null);

        if (rGM != null) { requiredGamemodes.addAll(fromList(rGM.get())); }
        if (bGM != null) { blockedGamemodes.addAll(fromList(bGM.get())); }
        if (rNm != null) { String s = rNm.get(); if (s != null) { requiredNames.addAll(Arrays.asList(s.split(","))); } }

        //MM//OotilityCeption.Log("\u00a7aRegistered \u00a77ASMIN Condition: \u00a7bOP:" + requireOP);
        //MM//OotilityCeption.Log("\u00a7bRGM:" + requiredGamemodes.size());
        //MM//OotilityCeption.Log("\u00a7bBGM:" + blockedGamemodes.size());
        //MM//OotilityCeption.Log("\u00a7bRNames:" + requiredNames.size());
    }

    @NotNull ArrayList<GameMode> fromList(@Nullable String str) {

        ArrayList<GameMode> ret = new ArrayList<>();
        if (str == null) { return ret; }
        if (str.contains(",")) {

            for (String s : str.split(",")) {


                try {
                    GameMode m = GameMode.valueOf(s.toUpperCase());
                    ret.add(m);
                } catch (IllegalArgumentException ignored) { }
            }

        } else {

            try {
                GameMode m = GameMode.valueOf(str.toUpperCase());
                ret.add(m);
            } catch (IllegalArgumentException ignored) { }
        }
        return ret;
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {

        // Must be player
        if (abstractEntity == null) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c No entity");
            return false; }
        if (!(abstractEntity.getBukkitEntity() instanceof Player)) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Not a player");
            return false; }
        Player p = (Player) abstractEntity.getBukkitEntity();

        if (requireOP && !p.isOp()) {
            //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Not OP");
            return false; }

        if (requiredGamemodes.size() > 0) {

            boolean success = false;
            for (GameMode gm : requiredGamemodes) {

                if (p.getGameMode().equals(gm)) { success = true; }
            }
            if (!success) {

                //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Incorrect GameMode \u00a76" + p.getGameMode().toString());
                return false;
            }
        }

        for (GameMode gm : blockedGamemodes) {

            if (p.getGameMode().equals(gm)) {

                //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Blocked Gamemode \u00a76" + p.getGameMode().toString());
                return false;
            }
        }


        if (requiredNames.size() > 0) {

            boolean success = false;
            for (String name : requiredNames) {

                if (p.getName().equals(name)) { success = true; }
            }
            if (!success) {

                //MM//OotilityCeption.Log("\u00a76AC\u00a77 Failed:\u00a7c Incorrect Name \u00a76" + p.getName());
                return false;
            }
        }

        //MM//OotilityCeption.Log("\u00a76AC\u00a77 Passed:\u00a7a Player valid");
        return true;
    }
}
