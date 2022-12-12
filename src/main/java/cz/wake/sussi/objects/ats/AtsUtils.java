package cz.wake.sussi.objects.ats;

import cz.wake.sussi.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AtsUtils {

    @NotNull
    public String getRankByID(int rank) {
        if (rank == 12) {
            return "Majitel";
        } else if (rank == 11) {
            return "Manager";
        } else if (rank == 10) {
            return "Hl.Admin";
        } else if (rank == 9) {
            return "Developer";
        } else if (rank == 2) {
            return "Helper";
        } else if (rank == 3) {
            return "Helperka";
        } else if (rank == 4) {
            return "Admin";
        } else if (rank == 5) {
            return "Adminka";
        } else if (rank == 7) {
            return "Eventer";
        } else if (rank == 8) {
            return "Moderátor";
        } else if (rank == 6) {
            return "Builder";
        } else if (rank == 1) {
            return "Discord Moderátor";
        } else {
            return "Hajzlík s chybným ID!";
        }
    }

    @NotNull
    public Color getColorByRank(int rank) {
        if (rank == 12) {
            return Constants.MAJITEL;
        } else if (rank == 11) {
            return Constants.MANAGER;
        } else if (rank == 10) {
            return Constants.HL_ADMIN;
        } else if (rank == 9) {
            return Constants.DEV;
        } else if (rank == 2 || rank == 3) {
            return Constants.HELPER;
        } else if (rank == 4 || rank == 5) {
            return Constants.ADMIN;
        } else if (rank == 7) {
            return Constants.EVENTER;
        } else if (rank == 8) {
            return Constants.MOD;
        } else if (rank == 6) {
            return Constants.BUILDER;
        } else {
            return Constants.GRAY;
        }
    }
}
