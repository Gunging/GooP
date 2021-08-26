package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTFilter {

    public String getFilterKey() {
        return filterKey;
    }

    public String getDataPrime() {
        return dataPrime;
    }

    public String getDataDime() {
        return dataDime;
    }

    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer a) {
        amount = a;
    }

    String filterKey;
    String dataPrime;
    String dataDime;
    Integer amount;
    public NBTFilter(@NotNull String filter, @NotNull  String prime, @NotNull  String dime, @Nullable Integer count) {
        filterKey = filter;

        if (filter.equals("m")) {
            prime = prime.toUpperCase().replace(" ","_").replace("-", "_");
            dime = dime.toUpperCase().replace(" ","_").replace("-", "_"); }

        dataDime = dime;
        dataPrime = prime;
        amount = count;
    }


    @Override
    public String toString() {
        String am = "";
        if (amount != null) { am = " " + amount.toString(); }

        return filterKey + " " + dataPrime + " " + dataDime + am;
    }
}
