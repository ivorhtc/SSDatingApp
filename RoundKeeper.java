package hr.from.kovacevic.ivor.ssdatingapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivor on 20.05.17..
 */

public final class RoundKeeper {

    private RoundKeeper() {}

    private static List<Round> rounds = new ArrayList<>();

    public static List<Round> getRounds() {
        return rounds;
    }

    public static void setRounds(List<Round> rounds) {
        RoundKeeper.rounds = rounds;
    }
}
