package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.misc.PlusMinusPercent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class GooPVault {

    public static void CompatibilityCheck() { Economy snooze = null; }

    public static Economy econ = null;
    public Economy getEcon() { return econ; }

    public GooPVault() {}
    public boolean SetupEconomy(Server srv) {

        // Fail by missing vault
        Plugin vault = srv.getPluginManager().getPlugin("Vault");
        if (vault == null) {
            Gunging_Ootilities_Plugin.theOots.CPLog(ChatColor.GRAY + "Interrupted -\u00a7c Unloaded");
            return false; }
        if (!vault.isEnabled()) {
            Gunging_Ootilities_Plugin.theOots.CPLog(ChatColor.GRAY + "Interrupted -\u00a7c Disabled");
            return false; }

        // Fail due to invalid economy registration
        RegisteredServiceProvider<Economy> rsp = srv.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Gunging_Ootilities_Plugin.theOots.CPLog(ChatColor.GRAY + "Interrupted -\u00a7c Unregistered");
            return false; }

        //Final
        econ = rsp.getProvider();
        return econ != null;
    }

    public static double GetPlayerBalance(OfflinePlayer player) {

        // Simple!
        return econ.getBalance(player);
    }

    /**
     * Will perform a PlusMinusPercent operation on some player's balance.
     * @param player Player to target
     * @param operation Operation to apply to the player's balance
     * @return True if the operation was a success. False otherwise.
     */
    public static boolean SetPlayerBalance(OfflinePlayer player, PlusMinusPercent operation) {
        return SetPlayerBalance(player, operation, false);
    }

    /**
     * Will perform a PlusMinusPercent operation on some player's balance.
     * @param player Player to target
     * @param operation Operation to apply to the player's balance
     * @param quietly ONLY SET TO TRUE if you really expect that the operation may be cancelled by some other plugin, so it wont spamm the failure.
     * @return True if the operation was a success. False otherwise.
     */
    public static boolean SetPlayerBalance(OfflinePlayer player, PlusMinusPercent operation, boolean quietly) {

        // Get balance lma0
        double balance = GetPlayerBalance(player);

        // Apply operation
        double finalBalance = operation.apply(balance);

        // Simple!
        EconomyResponse clear = Withdraw(player, balance);
        if (clear.transactionSuccess()) {

            // I guess add-yo!
            EconomyResponse readd = Deposit(player, finalBalance);
            if (readd.transactionSuccess()) {

                // Oh yea!
                return true;

            // Someone's balance just got cleared, that sucks!
            } else {

                // Notify
                Gunging_Ootilities_Plugin.theOots.ECPLog("GooP Vault", "\u00a7cA fatal error occurred when performing balance operations.\u00a77 Player \u00a73" + player.getName() + "\u00a77's balance was cleared in the process. Originally had \u00a7a" + balance + "\u00a77, was supposed to end up with \u00a7a" + finalBalance + "\u00a77. \u00a78This is not really a GooP error, another plugin interfered with the process and cancelled GooP's operations.");
            }

        // Bruh?
        } else {

            // Notify
            if (!quietly) Gunging_Ootilities_Plugin.theOots.ECPLog("GooP Vault", "\u00a7cAn unknown error prevented a balance operation.\u00a77 Player \u00a73" + player.getName() + "\u00a77 was supposed to end up with a balance of \u00a7a" + finalBalance + "\u00a77. Should still have the initial \u00a7a" + balance + "\u00a77. \u00a78This is not really a GooP error, another plugin interfered with the process and cancelled GooP's operations.");
        }

        return false;
    }

    /**
     * Economy deposit but supports negative amounts lma0
     */
    public static EconomyResponse Deposit(OfflinePlayer player, double amount) {

        // Is it positive?
        if (amount >= 0) {

            // Will deposit as positive amount
            return econ.depositPlayer(player, amount);

        // Its negative -.-
        } else {

            // Will withdraw its positive amount instaed lol
            return econ.withdrawPlayer(player, -amount);
        }
    }

    /**
     * Economy withdraw but supports negative amounts lma0
     */
    public static EconomyResponse Withdraw(OfflinePlayer player, double amount) {

        // Is it positive?
        if (amount >= 0) {

            // Will withdraw as positive amount
            return econ.withdrawPlayer(player, amount);

        // Its negative -.-
        } else {

            // Will deposit its positive amount instaed lol
            return econ.depositPlayer(player, -amount);
        }
    }
}
