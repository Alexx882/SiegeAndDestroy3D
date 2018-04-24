package at.aau.gloryweapons.siegeanddestroy3d.network;

import java.util.ArrayList;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.GlobalGameSettings;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;


public class ServerGameHandler {

    private List<BattleArea> battleAreas;
    private List<User> users;
    private User currentUser;
    private boolean gameStarted;
    private GlobalGameSettings globalGameSettings;

    public ServerGameHandler(GlobalGameSettings globalGameSettings){
        this.battleAreas = new ArrayList<>();
        this.users = new ArrayList<>();
        this.globalGameSettings = globalGameSettings;
        this.gameStarted = false;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        this.gameStarted = true;
    }

    public void addUser(User user){
        users.add(user);
        if (users.size() == 1){
            currentUser = users.get(0);
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean addBattleArea(BattleArea battleArea){
        if (checktBattleArea(battleArea)){
            battleAreas.add(battleArea);
            return true;
        }else if (!userExists(battleArea.getUserId())){
            //TODO send ERROR
            return false;
        }else {
            //TODO send ERROR
            return false;
        }
    }
        
    public User nextUser(){
        if (currentUser.getId() == users.get(users.size() -1 ).getId()){
            currentUser = users.get(0);
            return currentUser;
        }else {
           currentUser = users.get(users.indexOf(currentUser) +1);
           return currentUser;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private boolean isCurrentUser(User user){
        if (user.getId() == currentUser.getId()){
            return true;
        }else {
            return false;
        }
    }

    //check battlearea
    private boolean checktBattleArea(BattleArea battleArea){
        int requiredTiles = 0;
        int occupiedTiels = 0;

        for (int size: globalGameSettings.getShipSizes()) {
            requiredTiles += size;
        }

        for (int i = 0; i < battleArea.getBattleAreaTiles().length; i++) {
            for (int j = 0; j < battleArea.getBattleAreaTiles().length; j++) {
                if (battleArea.getBattleAreaTiles()[i][j].getType() == BattleAreaTile.TileType.SHIP_HEALTHY){
                    occupiedTiels += 1;
                }
            }
        }

        if (occupiedTiels != requiredTiles){
            return false;
        }else {
            return true;
        }
    }

    private boolean userExists(int id){
        if(users != null) {
            for (User user : users) {
                if (user.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    private BattleArea getBattleAreaByUserId(int id){
        if (battleAreas != null) {
            for (BattleArea battleArea : battleAreas) {
                if (battleArea.getUserId() == id) {
                    return battleArea;
                }
            }
        }
        return null;
    }


    private void checkAndProcessShot(Turn turn){
        if ((getBattleAreaByUserId(turn.getAttacksUserID()) != null) &&userExists(turn.getAttacksUserID())){

            BattleArea attackedArea = getBattleAreaByUserId(turn.getAttacksUserID());
            BattleAreaTile attackedTile = attackedArea.getBattleAreaTiles()[turn.getxCoordinates()][turn.getyCoordinates()];

            switch (attackedTile.getType()){
                case WATER:
                    attackedTile.setType(BattleAreaTile.TileType.NO_HIT);
                    turn.setType(Turn.TurnType.NO_HIT);
                    break;
                case SHIP_HEALTHY:
                    attackedTile.setType(BattleAreaTile.TileType.SHIP_DESTROYED);
                    turn.setType(Turn.TurnType.HIT);
                    break;
                case SHIP_DESTROYED:
                    turn.setType(Turn.TurnType.HIT);
                    break;
                default:
                    turn.setType(Turn.TurnType.ERROR);
                    break;
            }

        }else {
            turn.setType(Turn.TurnType.ERROR);
        }

        //send turn back
        response(turn);
        if (getUserById(turn.getAttacksUserID()) != null && isAlive(getUserById(turn.getAttacksUserID()))){
            nextUser();
        }else {
            Instruction instructionToall = new Instruction(Instruction.InstructionType.USER_DEAD);
            //TODO send to client / all
        }

        sendInstructionRequestToClient();

    }

    private void checkAndProcessPowerUp(Turn turn){
        //TODO do something
    }

    private User getUserById(int id){
        for (User user: users) {
            if (user.getId() == id){
                return user;
            }
        }
        return null;
    }

    //incoming client turn requests
    public void request(Turn turn){
        if (isCurrentUser(turn.getUser())){
            switch (turn.getType()){
                case SHOT:
                    checkAndProcessShot(turn);
                    break;

                case POWERUP:
                    checkAndProcessPowerUp(turn);
                    break;

                default:
                    //TODO Error
                    break;
            }
        }else{
            turn.setType(Turn.TurnType.ERROR);
            response(turn);
        }
    }

    //send Turn back to client
    public void response(Turn turn){
        //TODO return to sender
    }

    public void sendToAll(Instruction instruction){
        //TODO send instruction to all clients
    }

    //checks whether the user still has a ship available
    private boolean isAlive(User user){
        BattleArea area = getBattleAreaByUserId(user.getId());
        if (area != null){

            for (int i = 0; i < area.getBattleAreaTiles().length ; i++) {
                for (int j = 0; j < area.getBattleAreaTiles().length; j++) {
                    if (area.getBattleAreaTiles()[i][j].getType() == BattleAreaTile.TileType.SHIP_HEALTHY){
                        return true;
                    }
                }
            }
            return false;
        }else {
            return false;
        }
    }

    private void sendInstructionRequestToClient(){
        Instruction instruction = new Instruction(Instruction.InstructionType.DO_TURN);
        //TODO send to next client
    }

}
