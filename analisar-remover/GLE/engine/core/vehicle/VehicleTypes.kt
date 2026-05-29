package com.gle.engine.core.vehicle

import com.gle.content.vehicles.VehicleDefinition
import com.gle.content.vehicles.VehicleType
import com.gle.engine.core.map.GameMap

/**
 * Implementação de veículo do tipo Van.
 * 
 * Vans são veículos leves, ágeis e ideais para entregas urbanas
 * de curta distância e cargas menores.
 * 
 * @param id Identificador único da van
 * @param definition Definição base (deve ser de um veículo tipo VAN)
 * @param startX Posição X inicial
 * @param startY Posição Y inicial
 */
class Van(
    id: Long,
    definition: VehicleDefinition,
    startX: Int,
    startY: Int
) : Vehicle(id, definition, startX, startY) {

    init {
        require(definition.vehicleType in listOf(
            VehicleType.VAN,
            VehicleType.PICKUP,
            VehicleType.DELIVERY
        )) { "Van requer VehicleType.VAN, PICKUP ou DELIVERY" }
    }

    override val vehicleType: VehicleType
        get() = definition.vehicleType

    /**
     * Vans têm maior agilidade em áreas urbanas.
     * Podem entrar em tiles com edifícios comerciais mais facilmente.
     */
    override fun canEnterTile(tile: com.gle.engine.core.map.Tile): Boolean {
        // Vans podem entrar em qualquer estrada
        if (!tile.isRoaded) return false
        
        // Vans são pequenas e podem estacionar perto de edifícios comerciais
        if (tile.hasBuilding && tile.buildingType?.category == com.gle.engine.core.map.BuildingType.Category.CITY) {
            return !tile.isOccupied
        }
        
        return !tile.isOccupied
    }

    /**
     * Vans aceleram mais rápido em baixas velocidades.
     */
    override fun update(map: GameMap, deltaTime: Float) {
        // Bônus de aceleração em baixa velocidade para simular agilidade urbana
        if (isMoving && currentSpeed < 0.3f) {
            currentSpeed = minOf(
                currentSpeed + (definition.acceleration * 1.2f) * deltaTime,
                definition.speed
            )
        } else {
            super.update(map, deltaTime)
        }
    }

    override fun toString(): String {
        return "Van: $displayName @ ($tileX, $tileY) - Carga: $currentCargoAmount/$totalCargoCapacity"
    }
}

/**
 * Implementação de veículo do tipo Ônibus.
 * 
 * Ônibus são especializados em transporte de passageiros,
 * com capacidade média-alta e paradas frequentes.
 * 
 * @param id Identificador único do ônibus
 * @param definition Definição base (deve ser de um veículo tipo BUS)
 * @param startX Posição X inicial
 * @param startY Posição Y inicial
 */
class Bus(
    id: Long,
    definition: VehicleDefinition,
    startX: Int,
    startY: Int
) : Vehicle(id, definition, startX, startY) {

    init {
        require(definition.vehicleType == VehicleType.BUS) {
            "Bus requer VehicleType.BUS"
        }
    }

    override val vehicleType: VehicleType
        get() = definition.vehicleType

    /**
     * Número de passageiros atualmente no ônibus.
     */
    val passengerCount: Int
        get() = currentCargoAmount

    /**
     * Capacidade máxima de passageiros.
     */
    val maxPassengers: Int
        get() = totalCargoCapacity

    /**
     * Fator de conforto baseado na lotação.
     * Retorna valor entre 0.0 (vazio) e 1.0 (superlotado).
     */
    val comfortFactor: Float
        get() = passengerCount.toFloat() / maxPassengers

    /**
     * Verifica se está confortavelmente lotado (até 80%).
     */
    val isComfortable: Boolean
        get() = comfortFactor <= 0.8f

    /**
     * Verifica se está superlotado (acima de 100% - standing room).
     */
    val isOvercrowded: Boolean
        get() = comfortFactor > 1.0f

    /**
     * Ônibus têm prioridade em tiles próximos a cidades.
     */
    override fun canEnterTile(tile: com.gle.engine.core.map.Tile): Boolean {
        if (!tile.isRoaded) return false
        
        // Ônibus podem usar faixas exclusivas (se implementado)
        // e têm acesso facilitado a áreas de cidade
        
        return !tile.isOccupied
    }

    /**
     * Ônibus desaceleram mais suavemente para conforto dos passageiros.
     */
    override fun update(map: GameMap, deltaTime: Float) {
        super.update(map, deltaTime)
        
        // Desaceleração mais suave para conforto
        if (!isMoving && currentSpeed > 0) {
            currentSpeed = maxOf(currentSpeed - (definition.braking * 0.8f) * deltaTime, 0f)
        }
    }

    /**
     * Calcula a receita baseada no conforto e distância.
     */
    fun calculatePassengerRevenue(distanceTraveled: Int): Float {
        var revenue = calculateTotalCargoValue(distanceTraveled)
        
        // Bônus por conforto
        if (isComfortable) {
            revenue *= 1.2f // 20% bônus por viagem confortável
        }
        
        // Penalidade por superlotação
        if (isOvercrowded) {
            revenue *= 0.7f // 30% penalidade por superlotação
        }
        
        return revenue
    }

    override fun toString(): String {
        return "Ônibus: $displayName @ ($tileX, $tileY) - Passageiros: $passengerCount/$maxPassengers"
    }
}

/**
 * Implementação de veículo do tipo Caminhão.
 * 
 * Caminhões são veículos pesados para transporte de carga
 * de longa distância e grande volume.
 * 
 * @param id Identificador único do caminhão
 * @param definition Definição base (deve ser de um veículo tipo TRUCK ou ARTICULATED_TRUCK)
 * @param startX Posição X inicial
 * @param startY Posição Y inicial
 */
class Truck(
    id: Long,
    definition: VehicleDefinition,
    startX: Int,
    startY: Int
) : Vehicle(id, definition, startX, startY) {

    init {
        require(definition.vehicleType in listOf(
            VehicleType.TRUCK,
            VehicleType.ARTICULATED_TRUCK
        )) { "Truck requer VehicleType.TRUCK ou ARTICULATED_TRUCK" }
    }

    override val vehicleType: VehicleType
        get() = definition.vehicleType

    /**
     * Caminhões articulados requerem mais espaço para manobras.
     */
    val isArticulated: Boolean
        get() = definition.vehicleType == VehicleType.ARTICULATED_TRUCK

    /**
     * Peso total da carga em toneladas (estimado).
     */
    val estimatedWeightTons: Float
        get() = currentCargoAmount * 0.5f // Estimativa grosseira

    /**
     * Caminhões têm restrições em certas estradas.
     * Não podem usar pontes muito pequenas ou túneis baixos.
     */
    override fun canEnterTile(tile: com.gle.engine.core.map.Tile): Boolean {
        if (!tile.isRoaded) return false
        
        // Caminhões articulados não podem fazer curvas muito fechadas
        // (em uma implementação completa, verificaria o tile anterior)
        
        // Caminhões pesados não podem usar estradas de terra em mau estado
        if (tile.roadType == com.gle.engine.core.map.RoadType.DIRT_ROAD && 
            definition.vehicleType == VehicleType.ARTICULATED_TRUCK) {
            return false
        }
        
        return !tile.isOccupied
    }

    /**
     * Caminhões consomem mais combustível quando carregados.
     */
    override fun update(map: GameMap, deltaTime: Float) {
        super.update(map, deltaTime)
        
        // Consumo adicional baseado no peso
        if (definition.fuelCapacity != null && isMoving) {
            val loadFactor = currentCargoAmount.toFloat() / totalCargoCapacity
            val extraConsumption = (definition.fuelConsumption * loadFactor * 0.5f * deltaTime).toInt()
            currentFuel = maxOf(0, currentFuel - extraConsumption)
        }
    }

    /**
     * Caminhões levam mais tempo para carregar/descarregar.
     */
    fun getLoadingTimeMultiplier(): Float {
        return when (vehicleType) {
            VehicleType.ARTICULATED_TRUCK -> 2.0f // 2x mais lento
            VehicleType.TRUCK -> 1.5f // 1.5x mais lento
            else -> 1.0f
        }
    }

    /**
     * Verifica se a carga é adequada para este caminhão.
     */
    fun isCargoSuitable(cargoType: com.gle.content.cargo.CargoType): Boolean {
        return definition.canCarry(cargoType)
    }

    /**
     * Caminhões podem transportar múltiplos tipos de carga simultaneamente.
     * Retorna os tipos de carga atualmente transportados.
     */
    fun getCargoTypes(): List<com.gle.content.cargo.CargoType> {
        return cargoLoads.map { it.cargoType }.distinct()
    }

    override fun toString(): String {
        val type = if (isArticulated) "Articulado" else "Pesado"
        return "Caminhão $type: $displayName @ ($tileX, $tileY) - Carga: $currentCargoAmount/$totalCargoCapacity"
    }
}

/**
 * Fábrica para criação de veículos.
 * Permite criar instâncias apropriadas baseadas na definição.
 */
object VehicleFactory {

    private var nextId: Long = 1L

    /**
     * Cria um veículo baseado na definição fornecida.
     * 
     * @param definition Definição do veículo
     * @param startX Posição X inicial
     * @param startY Posição Y inicial
     * @return Instância do veículo apropriada
     */
    fun create(
        definition: VehicleDefinition,
        startX: Int = 0,
        startY: Int = 0
    ): Vehicle {
        val id = nextId++
        
        return when (definition.vehicleType) {
            VehicleType.VAN, VehicleType.PICKUP, VehicleType.DELIVERY -> {
                Van(id, definition, startX, startY)
            }
            VehicleType.BUS -> {
                Bus(id, definition, startX, startY)
            }
            VehicleType.TRUCK, VehicleType.ARTICULATED_TRUCK -> {
                Truck(id, definition, startX, startY)
            }
            else -> {
                // Fallback para classe base anônima
                object : Vehicle(id, definition, startX, startY) {
                    override val vehicleType: VehicleType = definition.vehicleType
                }
            }
        }
    }

    /**
     * Cria uma van padrão.
     */
    fun createVan(
        startX: Int = 0,
        startY: Int = 0
    ): Van {
        val definition = com.gle.content.vehicles.DefaultVehicles.SMALL_VAN
        return Van(nextId++, definition, startX, startY)
    }

    /**
     * Cria um ônibus padrão.
     */
    fun createBus(
        startX: Int = 0,
        startY: Int = 0
    ): Bus {
        val definition = com.gle.content.vehicles.DefaultVehicles.CITY_BUS
        return Bus(nextId++, definition, startX, startY)
    }

    /**
     * Cria um caminhão padrão.
     */
    fun createTruck(
        startX: Int = 0,
        startY: Int = 0,
        articulated: Boolean = false
    ): Truck {
        val definition = if (articulated) {
            com.gle.content.vehicles.DefaultVehicles.ARTICULATED_TRUCK
        } else {
            com.gle.content.vehicles.DefaultVehicles.LIGHT_TRUCK
        }
        return Truck(nextId++, definition, startX, startY)
    }

    /**
     * Reseta o contador de IDs (útil para testes).
     */
    fun resetIdCounter() {
        nextId = 1L
    }
}
