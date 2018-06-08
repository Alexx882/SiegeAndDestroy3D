package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import at.aau.gloryweapons.siegeanddestroy3d.network.interfaces.CallbackObject;

/**
 * Used to remember the client data for communication
 */
public abstract class ClientDataCallbackObject<T> implements CallbackObject<T> {
    protected ClientData clientData;

    public ClientDataCallbackObject(ClientData clientData) {
        this.clientData = clientData;
    }
}
