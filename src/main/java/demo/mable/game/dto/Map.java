package demo.mable.game.dto;

import demo.mable.game.enums.GroupCode;
import demo.mable.game.enums.TileType;

import java.util.List;

public final class Map {

    public static final List<BoardTileDefinition> TILES = List.of(
            new BoardTileDefinition(0,"START", TileType.START,null,0),
            new BoardTileDefinition(1,"강릉",TileType.CITY, GroupCode.LIGHT_GREEN,250000),
            new BoardTileDefinition(2,"함정",TileType.EVENT,null,0),
            new BoardTileDefinition(3,"춘천",TileType.CITY,GroupCode.LIGHT_GREEN,260000),
            new BoardTileDefinition(4,"북한산",TileType.TOURIST_SPOT,null,300000),
            new BoardTileDefinition(5,"경주",TileType.CITY,GroupCode.DARK_GREEN,320000),
            new BoardTileDefinition(6,"안동",TileType.CITY,GroupCode.DARK_GREEN,330000),
            new BoardTileDefinition(7,"천안",TileType.CITY,GroupCode.DARK_GREEN,340000),
            new BoardTileDefinition(8,"감옥",TileType.PRISON,null,0),
            new BoardTileDefinition(9,"서울대공원",TileType.TOURIST_SPOT,null,450000),
            new BoardTileDefinition(10,"인천",TileType.CITY,GroupCode.LIGHT_BLUE,500000),
            new BoardTileDefinition(11,"대전",TileType.CITY,GroupCode.LIGHT_BLUE,520000),
            new BoardTileDefinition(12,"함정",TileType.EVENT,null,0),
            new BoardTileDefinition(13,"청주",TileType.CITY,GroupCode.DARK_BLUE,550000),
            new BoardTileDefinition(14,"설악산",TileType.TOURIST_SPOT,null,550000),
            new BoardTileDefinition(15,"세종",TileType.CITY,GroupCode.DARK_BLUE,590000),
            new BoardTileDefinition(16,"엑스포",TileType.EXPO,null,0),
            new BoardTileDefinition(17,"여수",TileType.CITY,GroupCode.PINK,820000),
            new BoardTileDefinition(18,"지리산",TileType.TOURIST_SPOT,null,500000),
            new BoardTileDefinition(19,"제주",TileType.CITY,GroupCode.PINK,840000),
            new BoardTileDefinition(20,"함정",TileType.EVENT,null,0),
            new BoardTileDefinition(21,"포항",TileType.CITY,GroupCode.PURPLE,880000),
            new BoardTileDefinition(22,"울산",TileType.CITY,GroupCode.PURPLE,890000),
            new BoardTileDefinition(23,"대구",TileType.CITY,GroupCode.PURPLE,900000),
            new BoardTileDefinition(24,"기차여행",TileType.TRAIN,null,0),
            new BoardTileDefinition(25,"어린이 대공원",TileType.TOURIST_SPOT,null,500000),
            new BoardTileDefinition(26,"전주",TileType.CITY,GroupCode.ORANGE,900000),
            new BoardTileDefinition(27,"광주",TileType.CITY,GroupCode.ORANGE,910000),
            new BoardTileDefinition(28,"함정",TileType.EVENT,null,0),
            new BoardTileDefinition(29,"부산",TileType.CITY,GroupCode.RED,920000),
            new BoardTileDefinition(30,"함정",TileType.EVENT,null,0),
            new BoardTileDefinition(31,"서울",TileType.CITY,GroupCode.RED,930000)
    );
}
