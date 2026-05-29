package com.gle.content.cities

/**
 * Define as propriedades de uma categoria de cidade.
 * 
 * Cidades geram demanda por transporte de passageiros e mercadorias,
 * crescendo conforme são bem atendidas pelos veículos do jogador.
 * 
 * @param id Identificador único da categoria
 * @param name Nome exibido da categoria
 * @param description Descrição detalhada
 * @param citySize Tamanho da cidade (Pequena, Média, Grande, Metrópole)
 * @param minPopulation População mínima para esta categoria
 * @param maxPopulation População máxima para esta categoria
 * @param baseGrowthRate Taxa base de crescimento populacional (% ao ano)
 * @param passengerGenerationRate Passageiros gerados por tick
 * @param goodsDemandRate Demanda de mercadorias por tick
 * @param buildingDensity Densidade de construções (0.0 a 1.0)
 * @param roadNetworkDensity Densidade da malha viária (0.0 a 1.0)
 * @param spritePathTemplate Template para sprites ({size} será substituído)
 */
data class CityCategoryDefinition(
    val id: String,
    val name: String,
    val description: String,
    val citySize: CitySize,
    val minPopulation: Int,
    val maxPopulation: Int,
    val baseGrowthRate: Float,
    val passengerGenerationRate: Int,
    val goodsDemandRate: Int,
    val buildingDensity: Float,
    val roadNetworkDensity: Float,
    val spritePathTemplate: String
) {
    /**
     * Verifica se uma população se enquadra nesta categoria.
     */
    fun containsPopulation(population: Int): Boolean {
        return population in minPopulation..maxPopulation
    }

    /**
     * Calcula o sprite path baseado no tamanho atual da cidade.
     */
    fun getSpritePath(): String {
        return spritePathTemplate.replace("{size}", citySize.spriteSuffix)
    }
}

/**
 * Tamanhos de cidade disponíveis.
 */
enum class CitySize(val displayName: String, val spriteSuffix: String) {
    VILLAGE("Aldeia", "village"),
    SMALL_TOWN("Cidade Pequena", "small"),
    MEDIUM_CITY("Cidade Média", "medium"),
    LARGE_CITY("Cidade Grande", "large"),
    METROPOLIS("Metrópole", "metropolis")
}

/**
 * Representa uma instância de cidade no mapa.
 * 
 * @param definition Definição da categoria da cidade
 * @param name Nome específico da cidade (ex: "São Paulo", "Nova York")
 * @param centerX Posição X central da cidade no mapa (tile)
 * @param centerY Posição Y central da cidade no mapa (tile)
 * @param radius Raio da cidade em tiles
 * @param currentPopulation População atual
 * @param happinessLevel Nível de felicidade dos cidadãos (0-100)
 * @param connectedStations Número de estações de transporte conectadas
 * @param lastGrowthTick Último tick em que houve crescimento
 */
data class CityInstance(
    val definition: CityCategoryDefinition,
    val name: String,
    val centerX: Int,
    val centerY: Int,
    val radius: Int,
    var currentPopulation: Int = 0,
    var happinessLevel: Int = 75,
    var connectedStations: Int = 0,
    var lastGrowthTick: Long = 0
) {
    /**
     * Verifica se um ponto (tile) está dentro dos limites da cidade.
     */
    fun isInsideCity(tileX: Int, tileY: Int): Boolean {
        val dx = tileX - centerX
        val dy = tileY - centerY
        return (dx * dx + dy * dy) <= (radius * radius)
    }

    /**
     * Calcula a distância do centro da cidade até um ponto.
     */
    fun distanceFromCenter(tileX: Int, tileY: Int): Float {
        val dx = tileX - centerX
        val dy = tileY - centerY
        return kotlin.math.sqrt((dx * dx + dy * dy).toFloat())
    }

    /**
     * Verifica se a cidade pode crescer.
     */
    val canGrow: Boolean
        get() = currentPopulation < definition.maxPopulation && happinessLevel > 50

    /**
     * Obtém a categoria de tamanho atual baseada na população.
     */
    val currentSizeCategory: CitySize
        get() = definition.citySize

    /**
     * Gera passageiros para transporte.
     * Deve ser chamado periodicamente pela engine.
     * 
     * @return Quantidade de passageiros gerados
     */
    fun generatePassengers(): Int {
        // Mais passageiros = mais população e mais felicidade
        val basePassengers = definition.passengerGenerationRate
        val populationFactor = currentPopulation.toFloat() / definition.maxPopulation
        val happinessFactor = happinessLevel / 100.0f
        
        return (basePassengers * populationFactor * happinessFactor).toInt().coerceAtLeast(1)
    }

    /**
     * Calcula a demanda de mercadorias.
     */
    fun calculateGoodsDemand(): Int {
        val baseDemand = definition.goodsDemandRate
        val populationFactor = currentPopulation.toFloat() / definition.maxPopulation
        
        return (baseDemand * populationFactor).toInt().coerceAtLeast(1)
    }

    /**
     * Atualiza a população e verifica mudança de categoria.
     * 
     * @param delta Variação da população (positivo = crescimento, negativo = declínio)
     * @return Nova categoria se houve mudança, null caso contrário
     */
    fun updatePopulation(delta: Int): CitySize? {
        val oldCategory = currentSizeCategory
        currentPopulation = (currentPopulation + delta).coerceIn(0, Int.MAX_VALUE)
        
        // Em um sistema completo, aqui verificaria mudança de categoria
        // e retornaria a nova categoria se aplicável
        
        return if (currentSizeCategory != oldCategory) currentSizeCategory else null
    }

    /**
     * Aplica crescimento populacional baseado em felicidade e conexões.
     * Deve ser chamado anualmente (ou a cada X ticks).
     * 
     * @param currentTick Tick atual do jogo
     */
    fun applyGrowth(currentTick: Long) {
        if (!canGrow) return
        
        // Fatores que afetam crescimento
        val happinessMultiplier = happinessLevel / 100.0f
        val connectionBonus = connectedStations * 0.1f // 10% bonus por estação
        val growthRate = definition.baseGrowthRate * (happinessMultiplier + connectionBonus)
        
        val growthAmount = (currentPopulation * growthRate / 100.0f).toInt().coerceAtLeast(1)
        updatePopulation(growthAmount)
        lastGrowthTick = currentTick
    }

    /**
     * Ajusta felicidade baseado no atendimento de transporte.
     * 
     * @param serviceQuality Qualidade do serviço (0.0 a 1.0)
     * @param waitTimeAverage Tempo médio de espera em ticks
     */
    fun adjustHappiness(serviceQuality: Float, waitTimeAverage: Int) {
        var happinessChange = 0
        
        // Qualidade do serviço
        if (serviceQuality > 0.8f) happinessChange += 2
        else if (serviceQuality < 0.4f) happinessChange -= 3
        
        // Tempo de espera
        if (waitTimeAverage < 100) happinessChange += 1
        else if (waitTimeAverage > 500) happinessChange -= 2
        
        happinessLevel = (happinessLevel + happinessChange).coerceIn(0, 100)
    }
}

/**
 * Definições padrão de categorias de cidades.
 */
object DefaultCityCategories {

    /**
     * Aldeia - Assentamento pequeno e rural.
     */
    val VILLAGE = CityCategoryDefinition(
        id = "city.category.village",
        name = "Aldeia",
        description = "Pequeno assentamento rural com poucas necessidades",
        citySize = CitySize.VILLAGE,
        minPopulation = 0,
        maxPopulation = 500,
        baseGrowthRate = 2.0f,
        passengerGenerationRate = 5,
        goodsDemandRate = 2,
        buildingDensity = 0.3f,
        roadNetworkDensity = 0.2f,
        spritePathTemplate = "cities/village/{size}.png"
    )

    /**
     * Cidade Pequena - Centro urbano em desenvolvimento.
     */
    val SMALL_TOWN = CityCategoryDefinition(
        id = "city.category.small_town",
        name = "Cidade Pequena",
        description = "Centro urbano em desenvolvimento com demanda moderada",
        citySize = CitySize.SMALL_TOWN,
        minPopulation = 501,
        maxPopulation = 5000,
        baseGrowthRate = 3.5f,
        passengerGenerationRate = 20,
        goodsDemandRate = 10,
        buildingDensity = 0.5f,
        roadNetworkDensity = 0.4f,
        spritePathTemplate = "cities/town/{size}.png"
    )

    /**
     * Cidade Média - Centro regional importante.
     */
    val MEDIUM_CITY = CityCategoryDefinition(
        id = "city.category.medium_city",
        name = "Cidade Média",
        description = "Centro regional com significativa demanda de transporte",
        citySize = CitySize.MEDIUM_CITY,
        minPopulation = 5001,
        maxPopulation = 50000,
        baseGrowthRate = 4.0f,
        passengerGenerationRate = 50,
        goodsDemandRate = 25,
        buildingDensity = 0.7f,
        roadNetworkDensity = 0.6f,
        spritePathTemplate = "cities/city/{size}.png"
    )

    /**
     * Cidade Grande - Metrópole regional.
     */
    val LARGE_CITY = CityCategoryDefinition(
        id = "city.category.large_city",
        name = "Cidade Grande",
        description = "Grande metrópole com alta demanda de transporte",
        citySize = CitySize.LARGE_CITY,
        minPopulation = 50001,
        maxPopulation = 200000,
        baseGrowthRate = 3.0f,
        passengerGenerationRate = 100,
        goodsDemandRate = 50,
        buildingDensity = 0.85f,
        roadNetworkDensity = 0.8f,
        spritePathTemplate = "cities/large/{size}.png"
    )

    /**
     * Metrópole - Megacidade com demanda massiva.
     */
    val METROPOLIS = CityCategoryDefinition(
        id = "city.category.metropolis",
        name = "Metrópole",
        description = "Megacidade com demanda massiva de transporte",
        citySize = CitySize.METROPOLIS,
        minPopulation = 200001,
        maxPopulation = 1000000,
        baseGrowthRate = 2.0f,
        passengerGenerationRate = 200,
        goodsDemandRate = 100,
        buildingDensity = 0.95f,
        roadNetworkDensity = 0.9f,
        spritePathTemplate = "cities/metropolis/{size}.png"
    )

    /**
     * Retorna todas as categorias de cidade.
     */
    fun getAll(): List<CityCategoryDefinition> = listOf(
        VILLAGE,
        SMALL_TOWN,
        MEDIUM_CITY,
        LARGE_CITY,
        METROPOLIS
    )

    /**
     * Busca uma categoria pelo ID.
     */
    fun getById(id: String): CityCategoryDefinition? = getAll().find { it.id == id }

    /**
     * Encontra a categoria apropriada para uma população.
     */
    fun getCategoryForPopulation(population: Int): CityCategoryDefinition? {
        return getAll().find { it.containsPopulation(population) }
    }
}
