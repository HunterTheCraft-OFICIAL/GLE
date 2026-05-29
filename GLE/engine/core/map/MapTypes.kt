package com.gle.engine.core.map

/**
 * Tipos de terreno disponíveis no jogo.
 * 
 * Inspirado no OpenTTD, cada tile tem um tipo de terreno que afeta
 * construção, velocidade de veículos e custo de infraestrutura.
 */
enum class TerrainType(
    val displayName: String,
    val movementCost: Float,
    val canBuildRoad: Boolean,
    val canBuildRail: Boolean,
    val canBuildBuilding: Boolean
) {
    GRASS("Grama", 1.0f, true, true, true),
    DIRT("Terra", 1.2f, true, true, true),
    SAND("Areia", 1.5f, true, true, false),
    SNOW("Neve", 2.0f, true, true, false),
    ROCK("Rocha", 3.0f, false, false, false),
    WATER("Água", 999.0f, false, false, false),
    FOREST("Floresta", 2.5f, false, false, false),
    DESERT("Deserto", 1.8f, true, true, false),
    TUNDRA("Tundra", 2.2f, true, true, false)
}

/**
 * Tipos de estrada disponíveis.
 */
enum class RoadType(val displayName: String, val speedLimit: Float, val buildCost: Int) {
    NONE("Nenhuma", 1.0f, 0),
    DIRT_ROAD("Estrada de Terra", 0.5f, 50),
    PAVED_ROAD("Estrada Pavimentada", 1.0f, 150),
    HIGHWAY("Rodovia", 1.5f, 400),
    BRIDGE("Ponte", 1.0f, 800),
    TUNNEL("Túnel", 0.8f, 1000)
}

/**
 * Direção de uma estrada ou veículo.
 */
enum class Direction(val dx: Int, val dy: Int, val displayName: String) {
    NORTH(0, -1, "Norte"),
    SOUTH(0, 1, "Sul"),
    EAST(1, 0, "Leste"),
    WEST(-1, 0, "Oeste"),
    NORTHEAST(1, -1, "Nordeste"),
    NORTHWEST(-1, -1, "Noroeste"),
    SOUTHEAST(1, 1, "Sudeste"),
    SOUTHWEST(-1, 1, "Sudoeste");

    /**
     * Retorna a direção oposta.
     */
    fun opposite(): Direction = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
        NORTHEAST -> SOUTHWEST
        NORTHWEST -> SOUTHEAST
        SOUTHEAST -> NORTHWEST
        SOUTHWEST -> NORTHEAST
    }
}

/**
 * Representa um único tile no mapa do jogo.
 * 
 * O mapa é um grid 512x512 inspirado no OpenTTD. Cada tile contém
 * informações sobre terreno, estradas, edifícios e entidades.
 * 
 * @param x Posição X no mapa (0-511)
 * @param y Posição Y no mapa (0-511)
 * @param terrain Tipo de terreno base
 * @param roadType Tipo de estrada (se houver)
 * @param roadDirection Direção da estrada (para estradas unidirecionais)
 * @param elevation Nível de elevação (0-255)
 * @param hasBuilding Se true, há um edifício neste tile
 * @param buildingType Tipo de edifício (cidade, indústria, garagem)
 * @param entityReference Referência a entidade presente (veículo, etc.)
 */
data class Tile(
    val x: Int,
    val y: Int,
    var terrain: TerrainType = TerrainType.GRASS,
    var roadType: RoadType = RoadType.NONE,
    var roadDirection: Direction? = null,
    var elevation: Int = 0,
    var hasBuilding: Boolean = false,
    var buildingType: BuildingType? = null,
    var entityReference: Long? = null // ID da entidade
) {
    /**
     * Verifica se este tile tem uma estrada transitável.
     */
    val isRoaded: Boolean
        get() = roadType != RoadType.NONE

    /**
     * Verifica se este tile permite construção de estradas.
     */
    val canBuildRoad: Boolean
        get() = terrain.canBuildRoad && !hasBuilding

    /**
     * Verifica se este tile está ocupado por uma entidade.
     */
    val isOccupied: Boolean
        get() = entityReference != null

    /**
     * Calcula o custo de movimento para este tile.
     * 
     * @return Custo de movimento (quanto maior, mais lento)
     */
    fun getMovementCost(): Float {
        var cost = terrain.movementCost
        
        // Estradas reduzem custo de movimento
        if (isRoaded) {
            cost *= 0.5f
        }
        
        return cost
    }

    /**
     * Verifica se este tile é adjacente a outro tile.
     */
    fun isAdjacentTo(other: Tile): Boolean {
        val dx = kotlin.math.abs(x - other.x)
        val dy = kotlin.math.abs(y - other.y)
        return dx <= 1 && dy <= 1 && (dx + dy > 0)
    }

    /**
     * Retorna as coordenadas como Pair.
     */
    fun toPair(): Pair<Int, Int> = x to y

    override fun toString(): String = "Tile($x, $y, ${terrain.displayName})"
}

/**
 * Tipos de edifícios que podem ocupar um tile.
 */
enum class BuildingType(val displayName: String, val category: Category) {
    // Cidades
    HOUSE_RESIDENTIAL("Casa Residencial", Category.CITY),
    APARTMENT("Prédio de Apartamentos", Category.CITY),
    COMMERCIAL("Comércio", Category.CITY),
    CITY_CENTER("Centro da Cidade", Category.CITY),

    // Indústrias
    FACTORY_BUILDING("Fábrica", Category.INDUSTRY),
    MINE_BUILDING("Mina", Category.INDUSTRY),
    FARM_BUILDING("Fazenda", Category.INDUSTRY),
    REFINERY_BUILDING("Refinaria", Category.INDUSTRY),
    SAWMILL_BUILDING("Serraria", Category.INDUSTRY),
    POWER_PLANT_BUILDING("Usina", Category.INDUSTRY),

    // Infraestrutura
    GARAGE("Garagem", Category.INFRASTRUCTURE),
    STATION("Estação", Category.INFRASTRUCTURE),
    DEPOT("Depósito", Category.INFRASTRUCTURE),
    WAREHOUSE("Armazém", Category.INFRASTRUCTURE);

    enum class Category {
        CITY,
        INDUSTRY,
        INFRASTRUCTURE
    }
}

/**
 * Exceções relacionadas ao mapa.
 */
class MapException(message: String) : Exception(message)

class TileOutOfBoundsException(x: Int, y: Int, mapWidth: Int, mapHeight: Int) : MapException(
    "Tile ($x, $y) está fora dos limites do mapa (${mapWidth}x${mapHeight})"
)

class TileOccupiedException(x: Int, y: Int) : MapException(
    "Tile ($x, $y) já está ocupado"
)

class InvalidRoadConstructionException(x: Int, y: Int, reason: String) : MapException(
    "Não foi possível construir estrada em ($x, $y): $reason"
)
