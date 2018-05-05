package at.aau.gloryweapons.siegeanddestroy3d.network.interfaces;

import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;

/**
 * Simple dummy implementation for the network communicator.
 */
public class DummyNetworkCommunicator implements NetworkCommunicator {

    private User user;

    @Override
    public void sendNameToServer(String username, CallbackObject<User> callback) {
        // simulate sending
        Thread t = new Thread(new ServerMessageSimulator(username, callback));
        t.start();
    }

    @Override
    public void sendGameConfigurationToServer(User user, BattleArea userBoard, List<BasicShip> placedShips, CallbackObject<GameConfiguration> callback) {
        // simulate sending
        Thread t = new Thread(new ServerConfigurationSimulator(callback));
        t.start();
    }

    @Override
    public void receiveServerMessages(CallbackObject<InstructionDTO> callback) {

    }

    @Override
    public void getUserId(CallbackObject<User> callback) {
        int id = (1000 + (int) (Math.random() * (1000000)));
        this.user = new User(id,null,null);
        return ;
    }

    /**
     * Class used for simulating delay.
     */
    private class ServerMessageSimulator implements Runnable {
        String name;
        CallbackObject callback;

        ServerMessageSimulator(String name, CallbackObject callback) {
            this.name = name;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // ignore
            }

            if (name == null || name.equals("alex"))
                callback.callback(null);
            else
                callback.callback(new User(10, "10.0.0.1", name));
        }
    }

    private class ServerConfigurationSimulator implements Runnable {
        private CallbackObject callback;

        ServerConfigurationSimulator(CallbackObject callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }

            callback.callback(new GameConfiguration());
        }
    }
}


