package gunging.ootilities.gunging_ootilities_plugin.misc.sqlutils;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.misc.FileConfigPair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GooPSQL {

    public static String getHost() { return host; }
    public static String getPort() { return port; }

    static String host = null;
    static String port = null;
    static String database = null;
    static String username = null;
    static String password = null;
    static boolean isEnabled = false;
    static Connection mySQLBase = null;
    static Statement mySQLStatement = null;

    /**
     * Gets target column values from a query. May be presented with errors
     */
    @Nullable
    public static ArrayList<String> GetFromQuery(String query, String field) {

        // Disconnection cancel
        if (!IsConnected()) { return null; }

        try {

            // Does thisse
            ResultSet result = mySQLStatement.executeQuery(query);

            // Value to return
            ArrayList<String> ret = new ArrayList<String>();

            // Iterates for every row
            while (result.next()) {

                // Get the target column
                String name = result.getString(field);
                ret.add(name);
            }

            // Return thay
            return ret;

        } catch (SQLException e) { return null; }
    }

    public static void ExecuteUpdate(String query) {

        // Disconnection cancel
        if (!IsConnected()) { return; }

        try {

            // Does thisse
            mySQLStatement.executeUpdate(query);

        } catch (SQLException ignored) { }
    }

    // Reload stats
    public static void Reload() {

        // Reset
        host = null;
        port = null;
        database = null;
        username = null;
        password = null;

        // If there were no parsing errors
        if (Gunging_Ootilities_Plugin.theMain.mySQLHostInfo != null) {

            // Read the file yeet
            FileConfigPair ofgPair = Gunging_Ootilities_Plugin.theMain.mySQLHostInfo;

            // Get the latest version of the storage
            ofgPair = Gunging_Ootilities_Plugin.theMain.GetLatest(ofgPair);

            // Modify Storage
            YamlConfiguration ofgStorage = ofgPair.getStorage();

            // Get values
            host = ofgStorage.getString("Host");
            port = ofgStorage.getString("Port");
            database = ofgStorage.getString("Database");
            username = ofgStorage.getString("Username");
            password = ofgStorage.getString("Password");

            //Enability
            Boolean iEN = ofgStorage.getBoolean("Enable");
            isEnabled = OotilityCeption.If(iEN);
        }

        // Success?
        if (isEnabled && host != null && port != null && database != null && username != null && password != null) {

            // Connect Async
            Bukkit.getScheduler().runTaskAsynchronously(Gunging_Ootilities_Plugin.getPlugin(), () -> {

                // Attempt to connect
                GooPSQL.ConnectionAttempt();

                // If connected
                if (GooPSQL.IsConnected()) {

                    // Mention
                    Gunging_Ootilities_Plugin.theOots.CPLog(ChatColor.DARK_AQUA + "Established mySQL Connection to \u00a7b" + GooPSQL.getHost() + ":" + GooPSQL.getPort());
                }
            });

        // Disconnecting perhaps?
        } else if (!isEnabled && IsConnected()) {

            // Disconnect
            Disconnect();

            // Mention
            Gunging_Ootilities_Plugin.theOots.CPLog(ChatColor.BLUE + "Terminated mySQL Connection to \u00a73" + GooPSQL.getHost() + ":" + GooPSQL.getPort());
        }
    }

    /**
     * Connection attempt
     */
    public static void ConnectionAttempt() {

        // Try I gusess
        try {

            // Open, Oregano
            OpenConnection();

            // Worked?
            if (IsConnected()) {

                // Mention
                mySQLStatement = mySQLBase.createStatement();
            }

            // Well yeet
        } catch (Exception ignored) {
        }
    }

    /**
     * Is there an active connection?
     */
    public static boolean IsConnected() {

        try {

            // Return Status
            return mySQLBase != null && !mySQLBase.isClosed();

        } catch (SQLException e) {

            // Not Connected
            return false;
        }
    }

    /**
     * Is there an active connection?
     */
    public static void Disconnect() {

        // Dont waste time
        if (!IsConnected()) { return; }

        try {
            // Return Status
            mySQLBase.close();
            mySQLStatement = null;

        } catch (SQLException ignored) { }
    }

    /**
     * Attempts to connect.
     */
    static void OpenConnection() throws SQLException, ClassNotFoundException {

        // Keep Open Connection
        if (IsConnected()) { return; }

        // Actually Connect
        Class.forName("com.mysql.cj.jdbc.Driver");
        mySQLBase = DriverManager.getConnection("jdbc:mysql://" + host+ ":" + port + "/" + database, username, password);
    }
}
