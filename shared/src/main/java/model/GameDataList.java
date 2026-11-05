package model;

import java.util.Collection;

public record GameDataList(
        Collection<GameData> games
) { }
