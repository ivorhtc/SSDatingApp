package hr.from.kovacevic.ivor.ssdatingapp.model;

import hr.from.kovacevic.ivor.ssdatingapp.db.entities.User;

/**
 * Created by ivor on 20.05.17..
 */

class Table {

    User user1;

    User user2;

    public Table(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }
}
