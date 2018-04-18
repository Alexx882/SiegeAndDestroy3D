package at.aau.gloryweapons.siegeanddestroy3d.network.interfaces;

/**
 * Used for enabling callbacks.
 * Alternative to java.util.function.Consumer<T> which is available from API 24 upwards.
 */
public interface CallbackObject<T> {
    public void callback(T param);
}
