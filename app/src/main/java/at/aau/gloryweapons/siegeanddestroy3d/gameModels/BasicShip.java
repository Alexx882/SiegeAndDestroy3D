package at.aau.gloryweapons.siegeanddestroy3d.gameModels;

/**
 * Created by Alexander on 05.04.2018.
 */

public class BasicShip {

    private int _size = 0;
    private BattleAreaTile[] _tiles = new BattleAreaTile[0];

    public BasicShip(int size){
        _size = size;
        _tiles = new BattleAreaTile[_size];
    }

    public int getSize(){
        return _size;
    }

    public BattleAreaTile[] getTiles(){
        return _tiles;
    }
}
