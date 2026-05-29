package com.gle.engine.core.simulation

import com.gle.engine.core.map.GameMap
import com.gle.engine.core.vehicle.Vehicle
import com.gle.engine.core.vehicle.VehicleFactory

/**
 * Motor de Simulação do GLE.
 * 
 * Gerencia o loop principal da simulação, incluindo:
 * - Atualização de veículos
 * - Processamento de carregamento/descarregamento
 * - Produção de indústrias
 * - Crescimento de cidades
 * - Sistema de ticks e delta time
 * 
 * Este é o "coração" da simulação que não precisa ser modificado
 * para adicionar novos tipos de veículos ou indústrias.
 */
class SimulationEngine {
    
    companion object {
        const val TICKS_PER_SECOND = 60
        const val MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND
        const val DEFAULT_TIME_SCALE = 1.0f
    }

    // Estado da simulação
    var isRunning: Boolean = false
        private set
    var currentTick: Long = 0
        private set
    var gameTime: Long = 0 // Tempo total em milliseconds
        private set

    // Configurações
    var timeScale: Float = DEFAULT_TIME_SCALE
        set(value) {
            field = value.coerceIn(0.1f, 5.0f) // 0.1x a 5.0x
        }
    
    var maxFPS: Int = 60
        set(value) {
            field = value.coerceIn(30, 240)
        }

    // Componentes
    val gameMap: GameMap = GameMap()
    private val vehicles = mutableListOf<Vehicle>()
    private val vehicleListeners = mutableListOf<VehicleListener>()

    // Estatísticas
    var lastFrameTime: Long = 0
    var frameCount: Int = 0
    var currentFPS: Float = 0f
        private set

    /**
     * Inicia a simulação.
     */
    fun start() {
        if (isRunning) return
        
        isRunning = true
        currentTick = 0
        gameTime = 0
        lastFrameTime = System.currentTimeMillis()
        
        notifySimulationStarted()
    }

    /**
     * Para a simulação.
     */
    fun stop() {
        if (!isRunning) return
        
        isRunning = false
        notifySimulationStopped()
    }

    /**
     * Pausa a simulação.
     */
    fun pause() {
        isRunning = false
        notifySimulationPaused()
    }

    /**
     * Retoma a simulação.
     */
    fun resume() {
        if (!isRunning) {
            isRunning = true
            lastFrameTime = System.currentTimeMillis()
            notifySimulationResumed()
        }
    }

    /**
     * Atualiza a simulação por um tick.
     * Deve ser chamado no loop principal do jogo.
     * 
     * @param deltaTime Tempo decorrido desde o último frame em segundos
     */
    fun update(deltaTime: Float) {
        if (!isRunning) return

        val currentTime = System.currentTimeMillis()
        val elapsedSinceLastFrame = currentTime - lastFrameTime

        // Atualiza FPS
        frameCount++
        if (elapsedSinceLastFrame >= 1000) {
            currentFPS = frameCount.toFloat()
            frameCount = 0
            lastFrameTime = currentTime
        }

        // Calcula ticks a processar baseado no time scale
        val tickInterval = MILLISECONDS_PER_TICK / timeScale
        var accumulator = gameTime % tickInterval

        // Processa todos os ticks pendentes
        while (accumulator >= tickInterval) {
            processTick()
            accumulator -= tickInterval
        }

        gameTime += (deltaTime * 1000).toLong()
    }

    /**
     * Processa um único tick da simulação.
     */
    private fun processTick() {
        currentTick++

        // Atualiza todos os veículos
        updateVehicles()

        // Processa carregamentos/descarregamentos
        processLoadingUnloading()

        // Atualiza indústrias (produção)
        // updateIndustries() // Implementado quando tivermos sistema de indústrias

        // Atualiza cidades (crescimento)
        // updateCities() // Implementado quando tivermos sistema de cidades

        // Verifica eventos periódicos
        checkPeriodicEvents()

        notifyTickProcessed(currentTick)
    }

    /**
     * Atualiza todos os veículos ativos.
     */
    private fun updateVehicles() {
        val deltaTime = 1.0f / TICKS_PER_SECOND // Cada tick tem duração fixa

        for (vehicle in vehicles.filter { it.isActive }) {
            vehicle.update(gameMap, deltaTime)
            
            // Verifica se o veículo completou alguma ação importante
            if (!vehicle.isMoving && !vehicle.isLoading && !vehicle.isUnloading) {
                notifyVehicleStopped(vehicle)
            }
        }

        // Remove veículos inativos (se necessário)
        // vehicles.removeAll { !it.isActive }
    }

    /**
     * Processa operações de carregamento e descarregamento.
     */
    private fun processLoadingUnloading() {
        for (vehicle in vehicles.filter { it.isActive }) {
            if (vehicle.isLoading) {
                val completed = vehicle.updateLoading(getLoadRateForVehicle(vehicle))
                if (completed) {
                    notifyVehicleLoaded(vehicle)
                }
            } else if (vehicle.isUnloading) {
                val unloadedCargo = vehicle.updateUnloading(getUnloadRateForVehicle(vehicle))
                if (unloadedCargo.isNotEmpty()) {
                    notifyCargoUnloaded(vehicle, unloadedCargo)
                }
            }
        }
    }

    /**
     * Obtém a taxa de carregamento para um veículo.
     */
    private fun getLoadRateForVehicle(vehicle: Vehicle): Int {
        // Taxa base de 10 unidades por tick
        // Caminhões são mais lentos (ver VehicleTypes.kt)
        return when (vehicle.vehicleType.displayName) {
            "Caminhão Articulado" -> 5
            "Caminhão" -> 7
            "Ônibus" -> 15 // Passageiros embarcam mais rápido
            "Van" -> 10
            else -> 10
        }
    }

    /**
     * Obtém a taxa de descarregamento para um veículo.
     */
    private fun getUnloadRateForVehicle(vehicle: Vehicle): Int {
        // Similar ao carregamento
        return getLoadRateForVehicle(vehicle)
    }

    /**
     * Verifica eventos periódicos (manutenção, produção, etc.).
     */
    private fun checkPeriodicEvents() {
        // A cada 60 ticks (1 segundo de jogo)
        if (currentTick % 60 == 0) {
            // Verifica veículos que precisam de manutenção
            for (vehicle in vehicles.filter { it.needsMaintenance && it.isActive }) {
                notifyVehicleNeedsMaintenance(vehicle)
            }
        }

        // A cada 3600 ticks (1 minuto de jogo = 1 hora do dia)
        if (currentTick % 3600 == 0) {
            notifyGameHourPassed(getCurrentGameHour())
        }

        // A cada 86400 ticks (1 dia de jogo)
        if (currentTick % 86400 == 0) {
            notifyGameDayPassed()
        }
    }

    // ==================== GERENCIAMENTO DE VEÍCULOS ====================

    /**
     * Adiciona um veículo à simulação.
     */
    fun addVehicle(vehicle: Vehicle) {
        if (vehicle !in vehicles) {
            vehicles.add(vehicle)
            vehicle.creationTick = currentTick
            notifyVehicleAdded(vehicle)
        }
    }

    /**
     * Remove um veículo da simulação.
     */
    fun removeVehicle(vehicle: Vehicle) {
        if (vehicle in vehicles) {
            vehicles.remove(vehicle)
            vehicle.isActive = false
            notifyVehicleRemoved(vehicle)
        }
    }

    /**
     * Remove um veículo pelo ID.
     */
    fun removeVehicleById(id: Long): Boolean {
        val vehicle = vehicles.find { it.id == id }
        return if (vehicle != null) {
            removeVehicle(vehicle)
            true
        } else {
            false
        }
    }

    /**
     * Retorna todos os veículos ativos.
     */
    fun getAllVehicles(): List<Vehicle> = vehicles.toList()

    /**
     * Encontra um veículo pelo ID.
     */
    fun findVehicleById(id: Long): Vehicle? = vehicles.find { it.id == id }

    /**
     * Cria e adiciona um novo veículo usando a fábrica.
     */
    fun createAndAddVehicle(
        definition: com.gle.content.vehicles.VehicleDefinition,
        startX: Int = 0,
        startY: Int = 0
    ): Vehicle {
        val vehicle = VehicleFactory.create(definition, startX, startY)
        addVehicle(vehicle)
        return vehicle
    }

    // ==================== UTILITÁRIOS DE TEMPO ====================

    /**
     * Retorna a hora atual do jogo (0-23).
     */
    fun getCurrentGameHour(): Int {
        return ((currentTick / 3600) % 24).toInt()
    }

    /**
     * Retorna o dia atual do jogo.
     */
    fun getCurrentGameDay(): Long {
        return currentTick / 86400
    }

    /**
     * Retorna o ano atual do jogo.
     */
    fun getCurrentGameYear(): Long {
        return (currentTick / (86400 * 365)) + 1900 // Começa em 1900
    }

    /**
     * Formata o tempo atual do jogo como string.
     */
    fun getFormattedGameTime(): String {
        val year = getCurrentGameYear()
        val day = getCurrentGameDay() % 365
        val hour = getCurrentGameHour()
        val minute = ((currentTick % 3600) / 60).toInt()
        
        return "$year-${String.format("%03d", day)} ${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    // ==================== LISTENERS ====================

    /**
     * Adiciona um listener para eventos da simulação.
     */
    fun addListener(listener: VehicleListener) {
        if (listener !in vehicleListeners) {
            vehicleListeners.add(listener)
        }
    }

    /**
     * Remove um listener.
     */
    fun removeListener(listener: VehicleListener) {
        vehicleListeners.remove(listener)
    }

    private fun notifySimulationStarted() {
        vehicleListeners.forEach { it.onSimulationStarted() }
    }

    private fun notifySimulationStopped() {
        vehicleListeners.forEach { it.onSimulationStopped() }
    }

    private fun notifySimulationPaused() {
        vehicleListeners.forEach { it.onSimulationPaused() }
    }

    private fun notifySimulationResumed() {
        vehicleListeners.forEach { it.onSimulationResumed() }
    }

    private fun notifyTickProcessed(tick: Long) {
        vehicleListeners.forEach { it.onTickProcessed(tick) }
    }

    private fun notifyVehicleAdded(vehicle: Vehicle) {
        vehicleListeners.forEach { it.onVehicleAdded(vehicle) }
    }

    private fun notifyVehicleRemoved(vehicle: Vehicle) {
        vehicleListeners.forEach { it.onVehicleRemoved(vehicle) }
    }

    private fun notifyVehicleStopped(vehicle: Vehicle) {
        vehicleListeners.forEach { it.onVehicleStopped(vehicle) }
    }

    private fun notifyVehicleLoaded(vehicle: Vehicle) {
        vehicleListeners.forEach { it.onVehicleLoaded(vehicle) }
    }

    private fun notifyCargoUnloaded(vehicle: Vehicle, cargo: List<com.gle.content.cargo.CargoLoad>) {
        vehicleListeners.forEach { it.onCargoUnloaded(vehicle, cargo) }
    }

    private fun notifyVehicleNeedsMaintenance(vehicle: Vehicle) {
        vehicleListeners.forEach { it.onVehicleNeedsMaintenance(vehicle) }
    }

    private fun notifyGameHourPassed(hour: Int) {
        vehicleListeners.forEach { it.onGameHourPassed(hour) }
    }

    private fun notifyGameDayPassed() {
        vehicleListeners.forEach { it.onGameDayPassed() }
    }

    /**
     * Limpa toda a simulação.
     */
    fun clear() {
        stop()
        vehicles.clear()
        vehicleListeners.clear()
        gameMap.clear()
        currentTick = 0
        gameTime = 0
    }
}

/**
 * Listener para eventos da simulação.
 * Implemente esta interface para receber notificações de eventos.
 */
interface VehicleListener {
    fun onSimulationStarted() {}
    fun onSimulationStopped() {}
    fun onSimulationPaused() {}
    fun onSimulationResumed() {}
    fun onTickProcessed(tick: Long) {}
    fun onVehicleAdded(vehicle: Vehicle) {}
    fun onVehicleRemoved(vehicle: Vehicle) {}
    fun onVehicleStopped(vehicle: Vehicle) {}
    fun onVehicleLoaded(vehicle: Vehicle) {}
    fun onCargoUnloaded(vehicle: Vehicle, cargo: List<com.gle.content.cargo.CargoLoad>) {}
    fun onVehicleNeedsMaintenance(vehicle: Vehicle) {}
    fun onGameHourPassed(hour: Int) {}
    fun onGameDayPassed() {}
}

/**
 * Adapter vazio para VehicleListener (útil para herança).
 */
abstract class VehicleListenerAdapter : VehicleListener
