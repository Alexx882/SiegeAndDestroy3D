package at.aau.gloryweapons.siegeanddestroy3d.server;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.validation.ValidationHelperClass;


public class ServerController {

    private AtomicInteger id = new AtomicInteger(1);
    private ArrayList<String> names = new ArrayList<>();

    public User checkName(String name) {
        //check name
        if (ValidationHelperClass.isUserNameValid(name)) {

        } else {
            return null;
        }

        //check if name is unique
        for (String n : names) {
            if (n.equals(name)) {
                return null;

            }
        }

        //name is unique, return user object
        names.add(name);
        User user = new User();
        user.setName(name);
        user.setId(getId());
        return user;
    }

    //getID and always add 1
    private int getId() {
        return id.getAndAdd(1);
    }


}

