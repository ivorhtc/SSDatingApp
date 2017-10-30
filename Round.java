package hr.from.kovacevic.ivor.ssdatingapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivor on 20.05.17..
 */

public class Round {

    String name;

    List<Table> tables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }


    public ArrayList<String> getTablesAsStringArrayList() {
        ArrayList<String> tablesAsStringArray = new ArrayList<>();
        for (int i = 0; i < tables.size(); i++) {
            tablesAsStringArray.add((i + 1) + ". " + tables.get(i).getUser1().getName() + " and " + tables.get(i).getUser2().getName());
        }
        return tablesAsStringArray;
    }
}
