package com.gle.engine.core.vehicle

import com.gle.content.cargo.CargoLoad
import com.gle.content.cargo.CargoType
import com.gle.content.vehicles.VehicleDefinition
import com.gle.content.vehicles.VehicleType
import com.gle.engine.core.map.Direction
import com.gle.engine.core.map.GameMap
import com.gle.engine.core.map.Tile

/**
 * Classe base para todos os veículos do jogo GLE.
 * 
 * Esta classe abstrai a lógica comum de movimentação, carregamento
 * e descarregamento, permitindo especializações para Van, Ônibus e Caminhão.
 * 
 * @param id Identificador único do veículo
 * @param definition Definição base do veículo (stats, capacidades)
 * @param startX Posição X inicial no mapa
 * @param startY Posição Y inicial no mapa
 */
abstract class Vehicle(
    val id: Long,
    val definition: VehicleDefinition,
    startX: Int,
    startY: Int
) {
    // Posição atual
    var tileX: Int = startX
        protected set
    var tileY: Int = startY
        protected set

    // Estado de movimento
    var currentSpeed: Float = 0f
        protected set
    var direction: Direction = Direction.NORTH
        protected set
    var isMoving: Boolean = false
        protected set

    // Estado operacional
    var isLoading: Boolean = false
        protected set
    var isUnloading: Boolean = false
        protected set
    var loadProgress: Int = 0
        protected set
    var unloadProgress: Int = 0
        protected set

    // Carga transportada
    val cargoLoads: MutableList<CargoLoad> = mutableListOf()

    // Combustível (se aplicável)
    var currentFuel: Int = definition.fuelCapacity ?: Int.MAX_VALUE
        protected set

    // Estatísticas
    var totalDistanceTraveled: Int = 0
        protected set
    var totalCargoDelivered: Int = 0
        protected set
    var totalEarnings: Float = 0f
        protected set
    var creationTick: Long = 0
        protected set
    var lastServiceTick: Long = 0
        protected set

    // Estado
    var isActive: Boolean = true
        protected set
    var isBroken: Boolean = false
        protected set

    /**
     * Nome exibido do veículo (pode ser customizado pelo jogador).
     */
    var displayName: String = definition.name

    /**
     * Tipo específico do veículo (implementado pelas subclasses).
     */
    abstract val vehicleType: VehicleType

    /**
     * Capacidade total de carga deste veículo.
     */
    val totalCargoCapacity: Int
        get() = definition.cargoCapacity

    /**
     * Quantidade total de carga atualmente transportada.
     */
    val currentCargoAmount: Int
        get() = cargoLoads.sumOf { it.quantity }

    /**
     * Espaço disponível para carga.
     */
    val availableCargoSpace: Int
        get() = totalCargoCapacity - currentCargoAmount

    /**
     * Verifica se o veículo está cheio.
     */
    val isFullyLoaded: Boolean
        get() = availableCargoSpace <= 0

    /**
     * Verifica se o veículo está vazio.
     */
    val isEmpty: Boolean
        get() = cargoLoads.isEmpty() || currentCargoAmount <= 0

    /**
     * Verifica se precisa de combustível.
     */
    val needsFuel: Boolean
        get() = definition.fuelCapacity != null && currentFuel <= 0

    /**
     * Verifica se precisa de manutenção.
     */
    val needsMaintenance: Boolean
        get() {
            val ticksSinceService = creationTick - lastServiceTick
            return ticksSinceService > (definition.lifespan * 0.8f).toLong()
        }

    // ==================== MOVIMENTAÇÃO ====================

    /**
     * Atualiza o estado de movimento do veículo.
     * Deve ser chamado a cada tick pela engine de simulação.
     * 
     * @param map Mapa do jogo para verificação de terreno
     * @param deltaTime Tempo decorrido desde o último update
     */
    open fun update(map: GameMap, deltaTime: Float) {
        if (!isActive || isBroken || isLoading || isUnloading) return

        // Verifica combustível
        if (needsFuel) {
            stop("Sem combustível")
            return
        }

        // Aplica aceleração se estiver movendo
        if (isMoving && currentSpeed < definition.speed) {
            currentSpeed = minOf(currentSpeed + definition.acceleration * deltaTime, definition.speed)
        } else if (!isMoving && currentSpeed > 0) {
            currentSpeed = maxOf(currentSpeed - definition.braking * deltaTime, 0f)
        }

        // Move na direção atual se houver velocidade
        if (currentSpeed > 0) {
            move(map, deltaTime)
        }
    }

    /**
     * Executa o movimento na direção atual.
     */
    protected open fun move(map: GameMap, deltaTime: Float) {
        if (currentSpeed <= 0) return

        // Calcula distância a mover neste tick
        val moveDistance = currentSpeed * deltaTime
        
        // Para simplificação, movemos tile por tile
        // Em uma implementação completa, usaríamos interpolação
        val newTileX = tileX + direction.dx
        val newTileY = tileY + direction.dy

        if (map.isValidPosition(newTileX, newTileY)) {
            val newTile = map.getTile(newTileX, newTileY)
            
            // Verifica se pode entrar no tile
            if (canEnterTile(newTile)) {
                // Remove do tile antigo
                map.unregisterEntity(tileX, tileY, id)
                
                // Atualiza posição
                tileX = newTileX
                tileY = newTileY
                totalDistanceTraveled++
                
                // Registra no novo tile
                map.registerEntity(tileX, tileY, id)
                
                // Consome combustível
                consumeFuel()
            } else {
                // Não pode entrar, para o veículo
                currentSpeed = 0f
                isMoving = false
            }
        } else {
            // Borda do mapa, para o veículo
            currentSpeed = 0f
            isMoving = false
        }
    }

    /**
     * Verifica se o veículo pode entrar em um tile.
     * Pode ser sobrescrito por subclasses para regras específicas.
     */
    protected open fun canEnterTile(tile: Tile): Boolean {
        // Veículos precisam de estrada para se mover eficientemente
        // (em uma implementação mais complexa, poderia permitir off-road com penalidade)
        return tile.isRoaded && !tile.isOccupied
    }

    /**
     * Consome combustível baseado no consumo do veículo.
     */
    protected fun consumeFuel() {
        if (definition.fuelCapacity != null) {
            currentFuel = maxOf(0, currentFuel - (definition.fuelConsumption * 100).toInt())
        }
    }

    /**
     * Define a direção do veículo.
     */
    fun setDirection(newDirection: Direction) {
        if (newDirection != direction) {
            direction = newDirection
            currentSpeed = 0f // Precisa parar para virar
        }
    }

    /**
     * Inicia o movimento.
     */
    fun startMoving() {
        if (!isBroken && !needsFuel) {
            isMoving = true
        }
    }

    /**
     * Para o veículo.
     */
    fun stop(reason: String? = null) {
        isMoving = false
        currentSpeed = 0f
    }

    // ==================== CARREGAMENTO/DESCARREGAMENTO ====================

    /**
     * Inicia o processo de carregamento.
     * 
     * @param cargoType Tipo de carga a carregar
     * @param amount Quantidade a carregar
     * @return true se o carregamento foi iniciado
     */
    fun startLoading(cargoType: CargoType, amount: Int): Boolean {
        if (!definition.canCarry(cargoType)) return false
        if (availableCargoSpace <= 0) return false
        if (isLoading || isUnloading) return false

        // Verifica se já tem carga deste tipo
        val existingLoad = cargoLoads.find { it.cargoType.id == cargoType.id }
        
        if (existingLoad != null) {
            if (existingLoad.isFull) return false
        } else {
            // Cria nova carga
            cargoLoads.add(CargoLoad(
                cargoType = cargoType,
                quantity = 0,
                maxQuantity = minOf(amount, availableCargoSpace),
                originTile = tileX to tileY,
                destinationTile = tileX to tileY, // Será definido pelo jogador
                loadedTime = System.currentTimeMillis()
            ))
        }

        isLoading = true
        loadProgress = 0
        return true
    }

    /**
     * Atualiza o processo de carregamento.
     * Deve ser chamado a cada tick durante o carregamento.
     * 
     * @param loadRate Taxa de carregamento em unidades por tick
     * @return true se o carregamento foi completado
     */
    fun updateLoading(loadRate: Int): Boolean {
        if (!isLoading) return false

        loadProgress += loadRate
        
        // Encontra cargas que podem ser carregadas
        for (load in cargoLoads.filter { !it.isFull }) {
            val spaceInThisLoad = load.maxQuantity - load.quantity
            val toAdd = minOf(loadRate, spaceInThisLoad, availableCargoSpace)
            
            if (toAdd > 0) {
                load.add(toAdd)
            }
        }

        // Verifica se completou
        if (cargoLoads.all { it.isFull } || availableCargoSpace <= 0) {
            isLoading = false
            loadProgress = 0
            return true
        }

        return false
    }

    /**
     * Inicia o processo de descarregamento.
     * 
     * @return true se o descarregamento foi iniciado
     */
    fun startUnloading(): Boolean {
        if (isEmpty || isLoading || isUnloading) return false

        isUnloading = true
        unloadProgress = 0
        return true
    }

    /**
     * Atualiza o processo de descarregamento.
     * 
     * @param unloadRate Taxa de descarregamento em unidades por tick
     * @return Cargas descarregadas neste tick
     */
    fun updateUnloading(unloadRate: Int): List<CargoLoad> {
        if (!isUnloading) return emptyList()

        val unloadedCargo = mutableListOf<CargoLoad>()
        unloadProgress += unloadRate

        val iterator = cargoLoads.iterator()
        while (iterator.hasNext()) {
            val load = iterator.next()
            val toRemove = minOf(unloadRate, load.quantity)
            
            if (toRemove > 0) {
                val removed = load.remove(toRemove)
                if (removed > 0) {
                    val partialLoad = CargoLoad(
                        cargoType = load.cargoType,
                        quantity = removed,
                        maxQuantity = removed,
                        originTile = load.originTile,
                        destinationTile = load.destinationTile,
                        loadedTime = load.loadedTime
                    )
                    unloadedCargo.add(partialLoad)
                }

                if (load.isEmpty) {
                    iterator.remove()
                }
            }
        }

        if (cargoLoads.isEmpty() || currentCargoAmount <= 0) {
            isUnloading = false
            unloadProgress = 0
        }

        return unloadedCargo
    }

    /**
     * Cancela o carregamento/descarregamento atual.
     */
    fun cancelLoadingUnloading() {
        isLoading = false
        isUnloading = false
        loadProgress = 0
        unloadProgress = 0
    }

    // ==================== MANUTENÇÃO E SERVIÇO ====================

    /**
     * Realiza manutenção no veículo.
     */
    fun service() {
        currentFuel = definition.fuelCapacity ?: Int.MAX_VALUE
        lastServiceTick = System.currentTimeMillis()
        isBroken = false
    }

    /**
     * Abastece o veículo.
     * 
     * @param amount Quantidade de combustível a adicionar
     * @return Quantidade realmente adicionada
     */
    fun refuel(amount: Int): Int {
        if (definition.fuelCapacity == null) return 0
        
        val spaceAvailable = (definition.fuelCapacity - currentFuel)
        val actualAmount = minOf(amount, spaceAvailable)
        currentFuel += actualAmount
        return actualAmount
    }

    // ==================== UTILITÁRIOS ====================

    /**
     * Retorna a posição atual como Pair.
     */
    fun getPosition(): Pair<Int, Int> = tileX to tileY

    /**
     * Calcula o valor total da carga transportada.
     */
    fun calculateTotalCargoValue(distanceTraveled: Int = 0): Float {
        return cargoLoads.sumOf { load ->
            load.cargoType.calculateValue(load.quantity, distanceTraveled)
        }
    }

    /**
     * Limpa todas as cargas (usado para debug ou reset).
     */
    fun clearCargo() {
        cargoLoads.clear()
    }

    override fun toString(): String {
        return "$displayName (${vehicleType.displayName}) @ ($tileX, $tileY)"
    }
}
