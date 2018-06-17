package at.aau.gloryweapons.siegeanddestroy3d.network.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.aau.gloryweapons.siegeanddestroy3d.game.models.BasicShip;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleArea;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.BattleAreaTile;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.GameConfiguration;
import at.aau.gloryweapons.siegeanddestroy3d.game.models.User;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.FinishRoundDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.FirstUserDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.GameConfigurationRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.HandshakeDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.InstructionDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.RequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.ResponseDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.TurnInfoDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameRequestDTO;
import at.aau.gloryweapons.siegeanddestroy3d.network.dto.UserNameResponseDTO;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;

public class KryonetHelper {
    private KryonetHelper() {
    }

    public static void registerClassesForEndpoint(EndPoint kryoEndpoint) {
        Kryo kryo = kryoEndpoint.getKryo();

        kryo.register(ArrayList.class);
        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(List.class);
        kryo.register(HandshakeDTO.class);
        kryo.register(UserNameRequestDTO.class);
        kryo.register(UserNameResponseDTO.class);
        kryo.register(GameConfigurationRequestDTO.class);
        kryo.register(GameConfiguration.class);
        kryo.register(TurnDTO.class);
        kryo.register(InstructionDTO.class);
        kryo.register(RequestDTO.class);
        kryo.register(ResponseDTO.class);
        kryo.register(User.class);
        kryo.register(BasicShip.class);
        kryo.register(BasicShip[].class);
        kryo.register(BattleArea.class);
        kryo.register(BattleAreaTile.class);
        kryo.register(BattleAreaTile[][].class);
        kryo.register(BattleAreaTile[].class);
        kryo.register(TurnDTO.TurnType.class);
        kryo.register(BattleAreaTile.TileType.class);
        kryo.register(TurnInfoDTO.class);
        kryo.register(FinishRoundDTO.class);
        kryo.register(FirstUserDTO.class);
    }
}
