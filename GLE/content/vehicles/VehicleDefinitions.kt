package com.gle.content.vehicles

import com.gle.content.cargo.CargoType

/**
 * Define as estatísticas e propriedades de um veículo.
 * 
 * Esta data class é parte do módulo Content, permitindo que novos veículos
 * sejam definidos via código ou arquivos JSON sem modificar o Motor.
 * 
 * @param id Identificador único do veículo
 * @param name Nome exibido do veículo
 * @param description Descrição detalhada
 * @param vehicleType Tipo do veículo (Van, Ônibus, Caminhão)
 * @param speed Velocidade máxima em tiles por tick
 * @param acceleration Aceleração (tiles/tick²)
 * @param braking Freio (desaceleração em tiles/tick²)
 * @param cargoCapacity Capacidade de carga em unidades
 * @param supportedCargoTypes Tipos de carga que este veículo pode transportar
 * @param fuelCapacity Capacidade do tanque de combustível (null = elétrico/infinito)
 * @param fuelConsumption Consumo de combustível por tile
 * @param purchaseCost Custo de compra do veículo
 * @param runningCost Custo de operação por tick
 * @param maintenanceCost Custo de manutenção mensal
 * @param lifespan Vida útil em ticks antes de precisar de substituição
 * @param spritePath Caminho para o sprite do veículo
 * @param soundEnginePath Caminho para o som do motor
 * @param soundHornPath Caminho para o som da buzina
 */
data class VehicleDefinition(
    val id: String,
    val name: String,
    val description: String,
    val vehicleType: VehicleType,
    val speed: Float,
    val acceleration: Float,
    val braking: Float,
    val cargoCapacity: Int,
    val supportedCargoTypes: List<CargoType>,
    val fuelCapacity: Int? = null,
    val fuelConsumption: Float = 0.0f,
    val purchaseCost: Int,
    val runningCost: Int,
    val maintenanceCost: Int,
    val lifespan: Long,
    val spritePath: String,
    val soundEnginePath: String? = null,
    val soundHornPath: String? = null
) {
    /**
     * Verifica se este veículo pode transportar um tipo específico de carga.
     */
    fun canCarry(cargoType: CargoType): Boolean {
        return supportedCargoTypes.any { it.id == cargoType.id }
    }

    /**
     * Calcula o custo total de propriedade por ano (365 dias de jogo).
     */
    fun calculateAnnualCost(): Int {
        // Custo de operação + manutenção
        return (runningCost * 24 * 365) + (maintenanceCost * 12)
    }

    /**
     * Retorna o tempo estimado para percorrer uma distância.
     * 
     * @param distanceTiles Distância em tiles
     * @return Tempo estimado em ticks
     */
    fun estimateTravelTime(distanceTiles: Int): Long {
        if (speed <= 0) return Long.MAX_VALUE
        return (distanceTiles / speed).toLong()
    }
}

/**
 * Tipos de veículos suportados.
 * Cada tipo tem características distintas de capacidade e uso.
 */
enum class VehicleType(val displayName: String, val category: String) {
    VAN("Van", "Leve"),
    BUS("Ônibus", "Passageiros"),
    TRUCK("Caminhão", "Pesado"),
    ARTICULATED_TRUCK("Caminhão Articulado", "Pesado"),
    PICKUP("Picape", "Leve"),
    DELIVERY("Veículo de Entrega", "Leve");

    /**
     * Verifica se este tipo de veículo é considerado leve.
     */
    val isLightVehicle: Boolean
        get() = category == "Leve"

    /**
     * Verifica se este tipo de veículo transporta passageiros.
     */
    val carriesPassengers: Boolean
        get() = this == BUS
}

/**
 * Especificação de portas para carregamento/descarregamento.
 * 
 * @param side Lado do veículo onde a porta está localizada
 * @param doorType Tipo de porta (corrediça, basculante, etc.)
 * @param openTime Tempo em ticks para abrir/fechar a porta
 * @param loadRate Taxa de carregamento em unidades por tick
 */
data class DoorSpecification(
    val side: DoorSide,
    val doorType: DoorType,
    val openTime: Int,
    val loadRate: Int
)

/**
 * Lados do veículo onde portas podem estar localizadas.
 */
enum class DoorSide(val displayName: String) {
    LEFT("Esquerda"),
    RIGHT("Direita"),
    REAR("Traseira"),
    BOTH_SIDES("Ambos os lados")
}

/**
 * Tipos de portas suportadas.
 */
enum class DoorType(val displayName: String, val requiresSpace: Boolean) {
    SLIDING("Corrediça", false),
    SWING("Basculante", true),
    ROLL_UP("Enrolável", false),
    LIFT_GATE("Plataforma Elevatória", true)
}

/**
 * Definições padrão de veículos do jogo GLE.
 * Pode ser estendido via arquivos JSON no módulo content/config.
 */
object DefaultVehicles {

    // ==================== VANS ====================

    /**
     * Van pequena para entregas urbanas rápidas.
     * Ideal para cargas leves e curtas distâncias.
     */
    val SMALL_VAN = VehicleDefinition(
        id = "vehicle.van.small",
        name = "Van Pequena",
        description = "Van compacta para entregas rápidas em áreas urbanas",
        vehicleType = VehicleType.VAN,
        speed = 0.8f,
        acceleration = 0.15f,
        braking = 0.3f,
        cargoCapacity = 15,
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.GOODS,
            com.gle.content.cargo.DefaultCargoTypes.FOOD,
            com.gle.content.cargo.DefaultCargoTypes.CONTAINERS
        ),
        fuelCapacity = 60,
        fuelConsumption = 0.02f,
        purchaseCost = 25000,
        runningCost = 5,
        maintenanceCost = 150,
        lifespan = 50000,
        spritePath = "vehicles/vans/small_van.png"
    )

    /**
     * Van média com maior capacidade.
     * Versátil para diversos tipos de carga.
     */
    val MEDIUM_VAN = VehicleDefinition(
        id = "vehicle.van.medium",
        name = "Van Média",
        description = "Van de tamanho médio com boa capacidade de carga",
        vehicleType = VehicleType.VAN,
        speed = 0.7f,
        acceleration = 0.12f,
        braking = 0.25f,
        cargoCapacity = 30,
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.GOODS,
            com.gle.content.cargo.DefaultCargoTypes.FOOD,
            com.gle.content.cargo.DefaultCargoTypes.WOOD,
            com.gle.content.cargo.DefaultCargoTypes.CONTAINERS
        ),
        fuelCapacity = 80,
        fuelConsumption = 0.03f,
        purchaseCost = 40000,
        runningCost = 8,
        maintenanceCost = 250,
        lifespan = 60000,
        spritePath = "vehicles/vans/medium_van.png"
    )

    // ==================== ÔNIBUS ====================

    /**
     * Ônibus urbano para transporte de passageiros.
     * Projetado para rotas dentro de cidades.
     */
    val CITY_BUS = VehicleDefinition(
        id = "vehicle.bus.city",
        name = "Ônibus Urbano",
        description = "Ônibus para transporte público em áreas urbanas",
        vehicleType = VehicleType.BUS,
        speed = 0.6f,
        acceleration = 0.1f,
        braking = 0.25f,
        cargoCapacity = 50, // Passageiros
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.PASSENGERS
        ),
        fuelCapacity = 150,
        fuelConsumption = 0.05f,
        purchaseCost = 120000,
        runningCost = 15,
        maintenanceCost = 500,
        lifespan = 40000,
        spritePath = "vehicles/buses/city_bus.png"
    )

    /**
     * Ônibus intermunicipal para longas distâncias.
     * Mais rápido e confortável.
     */
    val INTERCITY_BUS = VehicleDefinition(
        id = "vehicle.bus.intercity",
        name = "Ônibus Interestadual",
        description = "Ônibus confortável para viagens entre cidades",
        vehicleType = VehicleType.BUS,
        speed = 0.9f,
        acceleration = 0.08f,
        braking = 0.2f,
        cargoCapacity = 60, // Passageiros
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.PASSENGERS
        ),
        fuelCapacity = 300,
        fuelConsumption = 0.06f,
        purchaseCost = 200000,
        runningCost = 25,
        maintenanceCost = 800,
        lifespan = 50000,
        spritePath = "vehicles/buses/intercity_bus.png"
    )

    // ==================== CAMINHÕES ====================

    /**
     * Caminhão leve para distribuição regional.
     * Bom equilíbrio entre capacidade e agilidade.
     */
    val LIGHT_TRUCK = VehicleDefinition(
        id = "vehicle.truck.light",
        name = "Caminhão Leve",
        description = "Caminhão de pequeno porte para distribuição regional",
        vehicleType = VehicleType.TRUCK,
        speed = 0.65f,
        acceleration = 0.08f,
        braking = 0.18f,
        cargoCapacity = 80,
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.GOODS,
            com.gle.content.cargo.DefaultCargoTypes.FOOD,
            com.gle.content.cargo.DefaultCargoTypes.WOOD,
            com.gle.content.cargo.DefaultCargoTypes.FUEL
        ),
        fuelCapacity = 200,
        fuelConsumption = 0.08f,
        purchaseCost = 80000,
        runningCost = 18,
        maintenanceCost = 400,
        lifespan = 70000,
        spritePath = "vehicles/trucks/light_truck.png"
    )

    /**
     * Caminhão pesado para transporte de grande volume.
     * Ideal para longas distâncias e cargas pesadas.
     */
    val HEAVY_TRUCK = VehicleDefinition(
        id = "vehicle.truck.heavy",
        name = "Caminhão Pesado",
        description = "Caminhão de grande capacidade para transporte de longa distância",
        vehicleType = VehicleType.TRUCK,
        speed = 0.55f,
        acceleration = 0.05f,
        braking = 0.15f,
        cargoCapacity = 150,
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.GOODS,
            com.gle.content.cargo.DefaultCargoTypes.ORE,
            com.gle.content.cargo.DefaultCargoTypes.WOOD,
            com.gle.content.cargo.DefaultCargoTypes.FUEL,
            com.gle.content.cargo.DefaultCargoTypes.CONTAINERS
        ),
        fuelCapacity = 400,
        fuelConsumption = 0.12f,
        purchaseCost = 180000,
        runningCost = 35,
        maintenanceCost = 900,
        lifespan = 80000,
        spritePath = "vehicles/trucks/heavy_truck.png"
    )

    /**
     * Caminhão articulado para máximo volume de carga.
     * Requer estradas adequadas para manobras.
     */
    val ARTICULATED_TRUCK = VehicleDefinition(
        id = "vehicle.truck.articulated",
        name = "Caminhão Articulado",
        description = "Caminhão com carreta articulada para máximo volume",
        vehicleType = VehicleType.ARTICULATED_TRUCK,
        speed = 0.5f,
        acceleration = 0.04f,
        braking = 0.12f,
        cargoCapacity = 250,
        supportedCargoTypes = listOf(
            com.gle.content.cargo.DefaultCargoTypes.GOODS,
            com.gle.content.cargo.DefaultCargoTypes.ORE,
            com.gle.content.cargo.DefaultCargoTypes.WOOD,
            com.gle.content.cargo.DefaultCargoTypes.FUEL,
            com.gle.content.cargo.DefaultCargoTypes.CONTAINERS
        ),
        fuelCapacity = 600,
        fuelConsumption = 0.18f,
        purchaseCost = 300000,
        runningCost = 55,
        maintenanceCost = 1500,
        lifespan = 90000,
        spritePath = "vehicles/trucks/articulated_truck.png"
    )

    /**
     * Retorna todos os veículos padrão.
     */
    fun getAll(): List<VehicleDefinition> = listOf(
        // Vans
        SMALL_VAN,
        MEDIUM_VAN,
        // Ônibus
        CITY_BUS,
        INTERCITY_BUS,
        // Caminhões
        LIGHT_TRUCK,
        HEAVY_TRUCK,
        ARTICULATED_TRUCK
    )

    /**
     * Busca um veículo pelo ID.
     */
    fun getById(id: String): VehicleDefinition? = getAll().find { it.id == id }

    /**
     * Filtra veículos por tipo.
     */
    fun getByType(type: VehicleType): List<VehicleDefinition> = getAll().filter { it.vehicleType == type }

    /**
     * Encontra veículos que podem transportar um tipo específico de carga.
     */
    fun findVehiclesForCargo(cargoType: com.gle.content.cargo.CargoType): List<VehicleDefinition> {
        return getAll().filter { it.canCarry(cargoType) }
    }
}
