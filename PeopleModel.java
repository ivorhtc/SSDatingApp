package hr.from.kovacevic.ivor.ssdatingapp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import hr.from.kovacevic.ivor.ssdatingapp.db.AppDatabase;
import hr.from.kovacevic.ivor.ssdatingapp.db.entities.User;

public class PeopleModel extends AndroidViewModel {

    private static AppDatabase db;

    public PeopleModel(Application application) {
        super(application);
        db = AppDatabase.getInMemoryDatabase(this.getApplication());
    }


    public User addUser(String newUser) {
     //   int id = 1;
     //   User lastUser = db.userDao().getLast();
     //   if (lastUser != null) id = lastUser.getUid() + 1;

        User user = new User();
     //   user.setUid(id);
        user.setName(newUser);
        db.userDao().insertAll(user);
        return user;
    }

    public void removeUser(String userName) {
        if (userName == null) return;
        User user = db.userDao().findByName(userName);
        db.userDao().delete(user);
    }

    public static List<String> getUserNames() {
        List<String> people = new ArrayList<>();
        List<User> users = db.userDao().getAll();
        for (User user : users) {
            people.add(user.getName());
        }

        return people;
    }

    public List<Round> getRounds() {
        List<User> userList = db.userDao().getAll();
        if (userList.isEmpty())
            return null;

        List<User> users = new ArrayList<>();
        users.addAll(userList);

        if(users.size() % 2 != 0) {
            User dummyUser = new User();
            dummyUser.setName("PAUSE");
            users.add(0, dummyUser);
        }

        int numTeams = users.size();

        int numRounds = (numTeams - 1); // Days needed to complete tournament
        int halfSize = numTeams / 2;

        User staticUser = users.get(0); // Add people to List and remove the first team
        users.remove(0);


        int peopleSize = users.size();
        List<Round> rounds = new ArrayList<>();
        for (int day = 0; day < numRounds; day++) {
            Round round = new Round();
            round.setName("Round " + (day + 1) + "/" + numRounds);
            List<Table> tables = new ArrayList<>();

            int teamIdx = day % peopleSize;

            tables.add(new Table(users.get(teamIdx), staticUser));

            for (int idx = 1; idx < halfSize; idx++) {
                int firstPerson = (day + idx) % peopleSize;
                int secondPerson = (day + peopleSize - idx) % peopleSize;
                tables.add(new Table(users.get(firstPerson), users.get(secondPerson)));
            }
            round.setTables(tables);
            rounds.add(round);
        }
        return rounds;

    }

    public String getPairs() {
        List<String> peopleList = getUserNames();
        if (peopleList.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();

        List<String> pList = new ArrayList<>();
        pList.addAll(peopleList);


        if (pList.size() % 2 != 0) {
            pList.add(0, "PAUSE one round"); // If odd number of people add a dummy
        }
        int numTeams = pList.size();

        int numRounds = (numTeams - 1); // Days needed to complete tournament
        int halfSize = numTeams / 2;

        List<String> people = new ArrayList<>();

        people.addAll(pList); // Add people to List and remove the first team
        people.remove(0);

        int peopleSize = people.size();

        for (int day = 0; day < numRounds; day++) {
            sb.append("Round " + (day + 1) + "\n");

            int teamIdx = day % peopleSize;

            sb.append("1. " + people.get(teamIdx) + " and " + pList.get(0) + "\n");

            for (int idx = 1; idx < halfSize; idx++) {
                int firstPerson = (day + idx) % peopleSize;
                int secondPerson = (day + peopleSize - idx) % peopleSize;
                sb.append((idx + 1) + ". " + people.get(firstPerson) + " and " + people.get(secondPerson) + "\n");
            }
        }
        return sb.toString();
    }

    public void removeAll() {
        db.userDao().truncate();
    }

    public void updateUserName(String oldName, String newName) {
        if (oldName == null || newName == null) return;
        if ("".equals(oldName) || "".equals(newName)) return;
        User oldUser = db.userDao().findByName(oldName);
        db.userDao().updateUserName(oldUser.getUid(), newName);
    }
}
