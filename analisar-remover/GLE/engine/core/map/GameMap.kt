package com.gle.engine.core.map

/**
 * Sistema de Mapa do GLE.
 * 
 * Implementa um grid 512x512 inspirado no OpenTTD, gerenciando:
 * - Terreno e elevação
 * - Construção de estradas (vertical/horizontal)
 * - Posicionamento de cidades, indústrias e garagens
 * - Pathfinding para veículos
 * 
 * @param width Largura do mapa em tiles (padrão: 512)
 * @param height Altura do mapa em tiles (padrão: 512)
 */
class GameMap(
    val width: Int = DEFAULT_MAP_WIDTH,
    val height: Int = DEFAULT_MAP_HEIGHT
) {
    companion object {
        const val DEFAULT_MAP_WIDTH = 512
        const val DEFAULT_MAP_HEIGHT = 512
        const val MIN_MAP_SIZE = 64
        const val MAX_MAP_SIZE = 4096
    }

    // Armazenamento do mapa como array 1D para melhor performance
    private val tiles: Array<Tile> = Array(width * height) { index ->
        val x = index % width
        val y = index / width
        Tile(x, y)
    }

    // Cache de tiles para acesso rápido
    private val tileCache = mutableMapOf<Pair<Int, Int>, Tile>()

    /**
     * Inicializa o cache de tiles.
     */
    init {
        initializeTileCache()
    }

    private fun initializeTileCache() {
        tileCache.clear()
        for (tile in tiles) {
            tileCache[tile.x to tile.y] = tile
        }
    }

    /**
     * Obtém um tile pelas coordenadas.
     * 
     * @param x Coordenada X (0 até width-1)
     * @param y Coordenada Y (0 até height-1)
     * @return Tile na posição especificada
     * @throws TileOutOfBoundsException se as coordenadas estiverem fora dos limites
     */
    fun getTile(x: Int, y: Int): Tile {
        validateCoordinates(x, y)
        return tiles[y * width + x]
    }

    /**
     * Obtém um tile por Pair de coordenadas.
     */
    fun getTile(coords: Pair<Int, Int>): Tile = getTile(coords.first, coords.second)

    /**
     * Verifica se as coordenadas estão dentro dos limites do mapa.
     */
    fun isValidPosition(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }

    /**
     * Verifica se um Pair de coordenadas está dentro dos limites.
     */
    fun isValidPosition(coords: Pair<Int, Int>): Boolean {
        return isValidPosition(coords.first, coords.second)
    }

    /**
     * Valida coordenadas e lança exceção se inválidas.
     */
    private fun validateCoordinates(x: Int, y: Int) {
        if (!isValidPosition(x, y)) {
            throw TileOutOfBoundsException(x, y, width, height)
        }
    }

    // ==================== CONSTRUÇÃO DE ESTRADAS ====================

    /**
     * Constrói uma estrada horizontal entre dois pontos.
     * 
     * @param startX Posição X inicial
     * @param startY Posição Y inicial (constante para horizontal)
     * @param endX Posição X final
     * @param roadType Tipo de estrada a construir
     * @return Quantidade de tiles construídos
     * @throws InvalidRoadConstructionException se não for possível construir
     */
    fun buildHorizontalRoad(
        startX: Int,
        startY: Int,
        endX: Int,
        roadType: RoadType = RoadType.PAVED_ROAD
    ): Int {
        validateCoordinates(startX, startY)
        validateCoordinates(endX, startY)

        if (startY != startY) {
            throw InvalidRoadConstructionException(startX, startY, "Construção horizontal requer Y constante")
        }

        val xMin = minOf(startX, endX)
        val xMax = maxOf(startX, endX)
        var builtCount = 0

        for (x in xMin..xMax) {
            val tile = getTile(x, startY)
            if (tile.canBuildRoad && !tile.isOccupied) {
                tile.roadType = roadType
                tile.roadDirection = Direction.EAST
                builtCount++
            } else if (tile.roadType == RoadType.NONE) {
                throw InvalidRoadConstructionException(x, startY, "Terreno não permite construção")
            }
        }

        return builtCount
    }

    /**
     * Constrói uma estrada vertical entre dois pontos.
     * 
     * @param startX Posição X inicial (constante para vertical)
     * @param startY Posição Y inicial
     * @param endX Posição X final
     * @param endY Posição Y final
     * @param roadType Tipo de estrada a construir
     * @return Quantidade de tiles construídos
     * @throws InvalidRoadConstructionException se não for possível construir
     */
    fun buildVerticalRoad(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        roadType: RoadType = RoadType.PAVED_ROAD
    ): Int {
        validateCoordinates(startX, startY)
        validateCoordinates(startX, endY)

        if (startX != endX) {
            throw InvalidRoadConstructionException(startX, startY, "Construção vertical requer X constante")
        }

        val yMin = minOf(startY, endY)
        val yMax = maxOf(startY, endY)
        var builtCount = 0

        for (y in yMin..yMax) {
            val tile = getTile(startX, y)
            if (tile.canBuildRoad && !tile.isOccupied) {
                tile.roadType = roadType
                tile.roadDirection = Direction.SOUTH
                builtCount++
            } else if (tile.roadType == RoadType.NONE) {
                throw InvalidRoadConstructionException(startX, y, "Terreno não permite construção")
            }
        }

        return builtCount
    }

    /**
     * Constrói uma estrada entre dois pontos (auto-detecta horizontal/vertical).
     */
    fun buildRoad(
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        roadType: RoadType = RoadType.PAVED_ROAD
    ): Int {
        return if (y1 == y2) {
            buildHorizontalRoad(x1, y1, x2, roadType)
        } else if (x1 == x2) {
            buildVerticalRoad(x1, y1, x2, y2, roadType)
        } else {
            // Para estradas diagonais, constrói em L
            val horizontal = buildHorizontalRoad(x1, y1, x2, roadType)
            val vertical = buildVerticalRoad(x2, y1, x2, y2, roadType)
            horizontal + vertical
        }
    }

    /**
     * Remove uma estrada de um tile.
     */
    fun removeRoad(x: Int, y: Int): Boolean {
        if (!isValidPosition(x, y)) return false
        
        val tile = getTile(x, y)
        if (tile.roadType != RoadType.NONE) {
            tile.roadType = RoadType.NONE
            tile.roadDirection = null
            return true
        }
        return false
    }

    // ==================== TERRENO ====================

    /**
     * Define o tipo de terreno de um tile.
     */
    fun setTerrain(x: Int, y: Int, terrain: TerrainType): Boolean {
        if (!isValidPosition(x, y)) return false
        
        val tile = getTile(x, y)
        if (!tile.hasBuilding) {
            tile.terrain = terrain
            return true
        }
        return false
    }

    /**
     * Preenche uma área retangular com um tipo de terreno.
     */
    fun fillTerrain(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        terrain: TerrainType
    ): Int {
        val xMin = maxOf(0, minOf(startX, endX))
        val yMin = maxOf(0, minOf(startY, endY))
        val xMax = minOf(width - 1, maxOf(startX, endX))
        val yMax = minOf(height - 1, maxOf(startY, endY))

        var count = 0
        for (y in yMin..yMax) {
            for (x in xMin..xMax) {
                if (setTerrain(x, y, terrain)) {
                    count++
                }
            }
        }
        return count
    }

    // ==================== EDIFÍCIOS E ESTRUTURAS ====================

    /**
     * Coloca um edifício em um tile.
     */
    fun placeBuilding(
        x: Int,
        y: Int,
        buildingType: BuildingType
    ): Boolean {
        if (!isValidPosition(x, y)) return false
        
        val tile = getTile(x, y)
        if (!tile.hasBuilding && !tile.isOccupied) {
            tile.hasBuilding = true
            tile.buildingType = buildingType
            return true
        }
        return false
    }

    /**
     * Remove um edifício de um tile.
     */
    fun removeBuilding(x: Int, y: Int): Boolean {
        if (!isValidPosition(x, y)) return false
        
        val tile = getTile(x, y)
        if (tile.hasBuilding) {
            tile.hasBuilding = false
            tile.buildingType = null
            return true
        }
        return false
    }

    // ==================== ENTIDADES ====================

    /**
     * Registra uma entidade (veículo) em um tile.
     */
    fun registerEntity(x: Int, y: Int, entityId: Long): Boolean {
        if (!isValidPosition(x, y)) return false
        
        val tile = getTile(x, y)
        if (tile.entityReference == null) {
            tile.entityReference = entityId
            return true
        }
        return false
    }

    /**
     * Remove uma entidade de um tile.
     */
    fun unregisterEntity(x: Int, y: Int, entityId: Long): Boolean {
        if (!isValidPosition(x, y)) return false
        
        val tile = getTile(x, y)
        if (tile.entityReference == entityId) {
            tile.entityReference = null
            return true
        }
        return false
    }

    // ==================== UTILITÁRIOS ====================

    /**
     * Retorna todos os tiles adjacentes a um ponto (incluindo diagonais).
     */
    fun getAdjacentTiles(x: Int, y: Int, includeDiagonals: Boolean = true): List<Tile> {
        if (!isValidPosition(x, y)) return emptyList()

        val adjacent = mutableListOf<Tile>()
        val directions = if (includeDiagonals) {
            listOf(
                0 to -1, 1 to -1, 1 to 0, 1 to 1,
                0 to 1, -1 to 1, -1 to 0, -1 to -1
            )
        } else {
            listOf(0 to -1, 1 to 0, 0 to 1, -1 to 0)
        }

        for ((dx, dy) in directions) {
            val nx = x + dx
            val ny = y + dy
            if (isValidPosition(nx, ny)) {
                adjacent.add(getTile(nx, ny))
            }
        }

        return adjacent
    }

    /**
     * Encontra tiles com estrada dentro de um raio.
     */
    fun findRoadsWithinRadius(centerX: Int, centerY: Int, radius: Int): List<Tile> {
        val result = mutableListOf<Tile>()
        val xMin = maxOf(0, centerX - radius)
        val xMax = minOf(width - 1, centerX + radius)
        val yMin = maxOf(0, centerY - radius)
        val yMax = minOf(height - 1, centerY + radius)

        for (y in yMin..yMax) {
            for (x in xMin..xMax) {
                val tile = getTile(x, y)
                if (tile.isRoaded) {
                    val dx = x - centerX
                    val dy = y - centerY
                    if (dx * dx + dy * dy <= radius * radius) {
                        result.add(tile)
                    }
                }
            }
        }

        return result
    }

    /**
     * Calcula a distância de Manhattan entre dois pontos.
     */
    fun manhattanDistance(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        return kotlin.math.abs(x2 - x1) + kotlin.math.abs(y2 - y1)
    }

    /**
     * Calcula a distância Euclidiana entre dois pontos.
     */
    fun euclideanDistance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return kotlin.math.sqrt((dx * dx + dy * dy).toFloat())
    }

    /**
     * Retorna estatísticas do mapa.
     */
    fun getStatistics(): MapStatistics {
        var roadTiles = 0
        var buildingTiles = 0
        var waterTiles = 0

        for (tile in tiles) {
            when {
                tile.isRoaded -> roadTiles++
                tile.hasBuilding -> buildingTiles++
                tile.terrain == TerrainType.WATER -> waterTiles++
            }
        }

        return MapStatistics(
            totalTiles = tiles.size,
            roadTiles = roadTiles,
            buildingTiles = buildingTiles,
            waterTiles = waterTiles,
            roadCoverage = roadTiles.toFloat() / tiles.size,
            buildingCoverage = buildingTiles.toFloat() / tiles.size
        )
    }

    /**
     * Limpa todo o mapa, resetando para o estado inicial.
     */
    fun clear() {
        for (tile in tiles) {
            tile.terrain = TerrainType.GRASS
            tile.roadType = RoadType.NONE
            tile.roadDirection = null
            tile.elevation = 0
            tile.hasBuilding = false
            tile.buildingType = null
            tile.entityReference = null
        }
    }
}

/**
 * Estatísticas do mapa.
 */
data class MapStatistics(
    val totalTiles: Int,
    val roadTiles: Int,
    val buildingTiles: Int,
    val waterTiles: Int,
    val roadCoverage: Float,
    val buildingCoverage: Float
) {
    val buildableTiles: Int
        get() = totalTiles - waterTiles

    val undevelopedTiles: Int
        get() = totalTiles - roadTiles - buildingTiles
}
