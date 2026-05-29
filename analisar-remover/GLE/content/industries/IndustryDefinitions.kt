package com.gle.content.industries

import com.gle.content.cargo.CargoType

/**
 * Define as propriedades de um tipo de indústria.
 * 
 * Indústrias produzem ou consomem cargas específicas, criando cadeias
 * de produção que os veículos devem atender.
 * 
 * @param id Identificador único da indústria
 * @param name Nome exibido da indústria
 * @param description Descrição detalhada
 * @param industryType Tipo da indústria (Fábrica, Fazenda, Mina, etc.)
 * @param producedCargo Carga produzida por esta indústria (null se não produz)
 * @param consumedCargo Carga consumida por esta indústria (null se não consome)
 * @param productionRate Quantidade produzida por tick (quando ativa)
 * @param consumptionRate Quantidade consumida por tick (quando ativa)
 * @param storageCapacity Capacidade máxima de armazenamento
 * @param minTilesToCity Distância mínima de cidades para funcionar
 * @param maxTilesToCity Distância máxima de cidades para eficiência
 * @param spritePath Caminho para o sprite da indústria
 * @param requiresRoadConnection Se true, precisa estar conectada a uma estrada
 */
data class IndustryDefinition(
    val id: String,
    val name: String,
    val description: String,
    val industryType: IndustryType,
    val producedCargo: CargoType? = null,
    val consumedCargo: CargoType? = null,
    val productionRate: Int = 0,
    val consumptionRate: Int = 0,
    val storageCapacity: Int = 1000,
    val minTilesToCity: Int = 5,
    val maxTilesToCity: Int = 50,
    val spritePath: String,
    val requiresRoadConnection: Boolean = true
) {
    /**
     * Verifica se esta indústria é apenas produtora (não consome nada).
     */
    val isProducerOnly: Boolean
        get() = producedCargo != null && consumedCargo == null

    /**
     * Verifica se esta indústria é apenas consumidora (não produz nada).
     */
    val isConsumerOnly: Boolean
        get() = producedCargo == null && consumedCargo != null

    /**
     * Verifica se esta indústria processa carga (produz e consome).
     */
    val isProcessor: Boolean
        get() = producedCargo != null && consumedCargo != null
}

/**
 * Tipos de indústrias disponíveis no jogo.
 */
enum class IndustryType(val displayName: String, val category: String) {
    // Indústrias Extrativas
    MINE("Mina", "Extrativa"),
    QUARRY("Pedreira", "Extrativa"),
    OIL_WELL("Poço de Petróleo", "Extrativa"),
    FOREST("Floresta", "Extrativa"),
    FARM("Fazenda", "Extrativa"),

    // Indústrias de Processamento
    SAWMILL("Serraria", "Processamento"),
    REFINERY("Refinaria", "Processamento"),
    STEEL_MILL("Siderúrgica", "Processamento"),
    FOOD_PROCESSOR("Processadora de Alimentos", "Processamento"),

    // Indústrias Manufatureiras
    FACTORY("Fábrica", "Manufatureira"),
    POWER_PLANT("Usina de Energia", "Manufatureira"),
    BANK("Banco", "Serviços"),
    SHOPPING_CENTER("Shopping Center", "Serviços")
}

/**
 * Representa uma instância de indústria no mapa.
 * 
 * @param definition Definição base da indústria
 * @param tileX Posição X no mapa (tile)
 * @param tileY Posição Y no mapa (tile)
 * @param currentStorage Quantidade atual armazenada
 * @param isActive Se true, a indústria está operando
 * @param lastProductionTick Último tick em que houve produção
 */
data class IndustryInstance(
    val definition: IndustryDefinition,
    val tileX: Int,
    val tileY: Int,
    var currentStorage: Int = 0,
    var isActive: Boolean = true,
    var lastProductionTick: Long = 0
) {
    /**
     * Verifica se há espaço para armazenar mais carga.
     */
    val hasStorageSpace: Boolean
        get() = currentStorage < definition.storageCapacity

    /**
     * Verifica se há carga disponível para retirada.
     */
    val hasAvailableCargo: Boolean
        get() = currentStorage > 0

    /**
     * Adiciona carga ao armazenamento.
     * 
     * @param amount Quantidade a adicionar
     * @return Quantidade realmente adicionada
     */
    fun addStorage(amount: Int): Int {
        if (!isActive || !hasStorageSpace) return 0
        
        val spaceAvailable = definition.storageCapacity - currentStorage
        val actualAmount = minOf(amount, spaceAvailable)
        currentStorage += actualAmount
        return actualAmount
    }

    /**
     * Remove carga do armazenamento.
     * 
     * @param amount Quantidade a remover
     * @return Quantidade realmente removida
     */
    fun removeStorage(amount: Int): Int {
        if (!isActive || !hasAvailableCargo) return 0
        
        val actualAmount = minOf(amount, currentStorage)
        currentStorage -= actualAmount
        return actualAmount
    }

    /**
     * Produz carga baseado na taxa de produção.
     * Deve ser chamado a cada tick pela engine de simulação.
     * 
     * @param currentTick Tick atual do jogo
     * @return Quantidade produzida neste tick
     */
    fun produce(currentTick: Long): Int {
        if (!isActive || definition.producedCargo == null) return 0
        if (!hasStorageSpace) return 0

        val produced = minOf(definition.productionRate, definition.storageCapacity - currentStorage)
        currentStorage += produced
        lastProductionTick = currentTick
        return produced
    }

    /**
     * Calcula a eficiência baseada na distância até a cidade mais próxima.
     * 
     * @param distanceToNearestCity Distância em tiles até a cidade mais próxima
     * @return Fator de eficiência (0.0 a 1.0)
     */
    fun calculateEfficiency(distanceToNearestCity: Int): Float {
        if (distanceToNearestCity < definition.minTilesToCity) {
            return 0.5f // Muito perto, penalidade
        }
        if (distanceToNearestCity > definition.maxTilesToCity) {
            return 0.3f // Muito longe, penalidade maior
        }
        
        // Eficiência ideal quando está dentro do range
        val range = definition.maxTilesToCity - definition.minTilesToCity
        val position = distanceToNearestCity - definition.minTilesToCity
        return 0.7f + (0.3f * (position.toFloat() / range))
    }
}

/**
 * Definições padrão de indústrias do jogo GLE.
 */
object DefaultIndustries {

    // ==================== INDÚSTRIAS EXTRATIVAS ====================

    /**
     * Mina de minério de ferro.
     * Produz minério para siderúrgicas.
     */
    val IRON_MINE = IndustryDefinition(
        id = "industry.mine.iron",
        name = "Mina de Ferro",
        description = "Extrai minério de ferro para produção de aço",
        industryType = IndustryType.MINE,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.ORE,
        productionRate = 5,
        storageCapacity = 2000,
        minTilesToCity = 10,
        maxTilesToCity = 80,
        spritePath = "industries/mines/iron_mine.png"
    )

    /**
     * Poço de petróleo.
     * Produz combustível para refinarias.
     */
    val OIL_WELL = IndustryDefinition(
        id = "industry.oil.well",
        name = "Poço de Petróleo",
        description = "Extrai petróleo bruto para refino",
        industryType = IndustryType.OIL_WELL,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.FUEL,
        productionRate = 8,
        storageCapacity = 3000,
        minTilesToCity = 15,
        maxTilesToCity = 100,
        spritePath = "industries/oil/oil_well.png"
    )

    /**
     * Floresta para extração de madeira.
     * Produz madeira para serrarias.
     */
    val FOREST = IndustryDefinition(
        id = "industry.forest.logging",
        name = "Floresta Madeireira",
        description = "Fonte de madeira para serrarias e indústrias de papel",
        industryType = IndustryType.FOREST,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.WOOD,
        productionRate = 6,
        storageCapacity = 1500,
        minTilesToCity = 5,
        maxTilesToCity = 60,
        spritePath = "industries/forest/logging_camp.png"
    )

    /**
     * Fazenda de grãos.
     * Produz alimentos para processadoras.
     */
    val GRAIN_FARM = IndustryDefinition(
        id = "industry.farm.grain",
        name = "Fazenda de Grãos",
        description = "Produz grãos para processamento de alimentos",
        industryType = IndustryType.FARM,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.FOOD,
        productionRate = 10,
        storageCapacity = 2500,
        minTilesToCity = 3,
        maxTilesToCity = 40,
        spritePath = "industries/farms/grain_farm.png"
    )

    // ==================== INDÚSTRIAS DE PROCESSAMENTO ====================

    /**
     * Serraria para processamento de madeira.
     * Consome madeira bruta, produz madeira processada.
     */
    val SAWMILL = IndustryDefinition(
        id = "industry.sawmill.basic",
        name = "Serraria",
        description = "Processa madeira bruta em tábuas e vigas",
        industryType = IndustryType.SAWMILL,
        consumedCargo = com.gle.content.cargo.DefaultCargoTypes.WOOD,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.GOODS,
        consumptionRate = 4,
        productionRate = 3,
        storageCapacity = 1000,
        minTilesToCity = 5,
        maxTilesToCity = 50,
        spritePath = "industries/processing/sawmill.png"
    )

    /**
     * Refinaria de petróleo.
     * Consome petróleo bruto, produz combustível.
     */
    val REFINERY = IndustryDefinition(
        id = "industry.refinery.basic",
        name = "Refinaria",
        description = "Refina petróleo bruto em combustíveis utilizáveis",
        industryType = IndustryType.REFINERY,
        consumedCargo = com.gle.content.cargo.DefaultCargoTypes.FUEL,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.FUEL,
        consumptionRate = 6,
        productionRate = 5,
        storageCapacity = 4000,
        minTilesToCity = 20,
        maxTilesToCity = 80,
        spritePath = "industries/processing/refinery.png"
    )

    // ==================== INDÚSTRIAS MANUFATUREIRAS ====================

    /**
     * Fábrica genérica de manufaturados.
     * Consome mercadorias básicas, produz produtos acabados.
     */
    val MANUFACTURING_FACTORY = IndustryDefinition(
        id = "industry.factory.manufacturing",
        name = "Fábrica de Manufaturados",
        description = "Produz bens manufaturados para distribuição",
        industryType = IndustryType.FACTORY,
        consumedCargo = com.gle.content.cargo.DefaultCargoTypes.GOODS,
        producedCargo = com.gle.content.cargo.DefaultCargoTypes.GOODS,
        consumptionRate = 5,
        productionRate = 5,
        storageCapacity = 2000,
        minTilesToCity = 5,
        maxTilesToCity = 30,
        spritePath = "industries/factory/manufacturing.png"
    )

    /**
     * Usina de energia.
     * Consome combustível para gerar energia (representado como goods).
     */
    val POWER_PLANT = IndustryDefinition(
        id = "industry.power.plant",
        name = "Usina Termelétrica",
        description = "Gera energia elétrica consumindo combustível",
        industryType = IndustryType.POWER_PLANT,
        consumedCargo = com.gle.content.cargo.DefaultCargoTypes.FUEL,
        consumptionRate = 10,
        storageCapacity = 5000,
        minTilesToCity = 15,
        maxTilesToCity = 50,
        spritePath = "industries/power/power_plant.png"
    )

    /**
     * Retorna todos as indústrias padrão.
     */
    fun getAll(): List<IndustryDefinition> = listOf(
        // Extrativas
        IRON_MINE,
        OIL_WELL,
        FOREST,
        GRAIN_FARM,
        // Processamento
        SAWMILL,
        REFINERY,
        // Manufatureiras
        MANUFACTURING_FACTORY,
        POWER_PLANT
    )

    /**
     * Busca uma indústria pelo ID.
     */
    fun getById(id: String): IndustryDefinition? = getAll().find { it.id == id }

    /**
     * Filtra indústrias por tipo.
     */
    fun getByType(type: IndustryType): List<IndustryDefinition> = getAll().filter { it.industryType == type }

    /**
     * Encontra indústrias que produzem um tipo específico de carga.
     */
    fun findProducersForCargo(cargoType: com.gle.content.cargo.CargoType): List<IndustryDefinition> {
        return getAll().filter { it.producedCargo?.id == cargoType.id }
    }

    /**
     * Encontra indústrias que consomem um tipo específico de carga.
     */
    fun findConsumersForCargo(cargoType: com.gle.content.cargo.CargoType): List<IndustryDefinition> {
        return getAll().filter { it.consumedCargo?.id == cargoType.id }
    }
}
