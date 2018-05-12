package at.aau.gloryweapons.siegeanddestroy3d.network.dto;

import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;

/**
 *Objects are automatically packed into a wrapper class and converted
 * into a json object. When converting a json string into an object,
 * the "target" class must be implemented in the "jsonToObject(String wrapperJson)" method.
 * Otherwise the json string cannot be converted into an object.
 */
public class WrapperHelper {

    private static WrapperHelper instance;

    public static WrapperHelper getInstance() {
        if (instance == null) {
            instance = new WrapperHelper();
        }
        return instance;
    }

    /**
     *
     * @param wrapperJson   String
     * @return              Object
     */
    public Object jsonToObject(String wrapperJson) {
        //json to wrapper object
        DtoWrapper dtoWrapper = null;
        try {
            dtoWrapper = LoganSquare.parse(wrapperJson, DtoWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get real object
        try {
            switch (dtoWrapper.getClassName()) {
                case "HandshakeDTO":
                    HandshakeDTO handshakeDTO = LoganSquare.parse(dtoWrapper.getJsonObject(), HandshakeDTO.class);
                    return handshakeDTO;
                case "UserNameRequestDTO":
                    UserNameRequestDTO userNameRequestDTO = LoganSquare.parse(dtoWrapper.getJsonObject(), UserNameRequestDTO.class);
                    return userNameRequestDTO;
                case "User":
                    User user = LoganSquare.parse(dtoWrapper.getJsonObject(), User.class);
                    return user;
                default:
                    Log.e(this.getClass().getName(), "class name does not exist");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Objects are automatically packed into a wrapper class and
     * converted into a json object.
     * @param object
     * @return
     */
    public String ObjectToWrappedJson(Object object) {
        //get classname
        String fullClassName[] = object.getClass().getName().split("\\.");
        String classname = fullClassName[fullClassName.length - 1];

        //object to json string
        String jsonObject = null;
        try {
            jsonObject = LoganSquare.serialize(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //build wrapper
        DtoWrapper wrapper = new DtoWrapper();
        wrapper.setClassName(classname);
        wrapper.setJsonObject(jsonObject);

        //wrapper object to json
        String wrapperJson = "";
        try {
            wrapperJson = LoganSquare.serialize(wrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wrapperJson;
    }
}
