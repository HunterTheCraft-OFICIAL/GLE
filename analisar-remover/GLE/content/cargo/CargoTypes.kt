package com.gle.content.cargo

/**
 * Representa um tipo de carga que pode ser transportada pelos veículos.
 * 
 * Esta data class é parte do módulo Content, permitindo que novos tipos de carga
 * sejam adicionados sem modificar o Motor de Simulação.
 * 
 * @param id Identificador único do tipo de carga
 * @param name Nome exibido da carga
 * @param description Descrição detalhada da carga
 * @param unitType Tipo de unidade (passageiros, toneladas, litros, etc.)
 * @param baseValue Valor base por unidade para cálculo de pagamento
 * @param isPerishable Se true, a carga tem validade e pode se perder com o tempo
 * @param requiredTemperature Temperatura necessária para transporte (null = ambiente)
 */
data class CargoType(
    val id: String,
    val name: String,
    val description: String,
    val unitType: CargoUnitType,
    val baseValue: Float,
    val isPerishable: Boolean = false,
    val requiredTemperature: Int? = null
) {
    /**
     * Calcula o valor total da carga considerando quantidade e distância.
     * 
     * @param quantity Quantidade de unidades
     * @param distance Distância percorrida em tiles
     * @return Valor total em moedas do jogo
     */
    fun calculateValue(quantity: Int, distance: Int): Float {
        val distanceMultiplier = 1.0f + (distance.toFloat() / 100.0f)
        return baseValue * quantity * distanceMultiplier
    }
}

/**
 * Tipos de unidade de carga suportados.
 */
enum class CargoUnitType(val displayName: String, val symbol: String) {
    PASSENGERS("Passageiros", "pax"),
    TONS("Toneladas", "t"),
    KILOGRAMS("Quilogramas", "kg"),
    LITERS("Litros", "L"),
    CUBIC_METERS("Metros Cúbicos", "m³"),
    UNITS("Unidades", "un"),
    CONTAINERS("Contêineres", "cnt")
}

/**
 * Representa uma instância de carga sendo transportada.
 * 
 * @param cargoType Tipo da carga
 * @param quantity Quantidade atual
 * @param maxQuantity Capacidade máxima
 * @param originTile Origem do carregamento (x, y)
 * @param destinationTile Destino para descarregamento (x, y)
 * @param loadedTime Tempo em ticks quando foi carregada
 */
data class CargoLoad(
    val cargoType: CargoType,
    var quantity: Int,
    val maxQuantity: Int,
    val originTile: Pair<Int, Int>,
    val destinationTile: Pair<Int, Int>,
    val loadedTime: Long
) {
    /**
     * Verifica se a carga está cheia.
     */
    val isFull: Boolean
        get() = quantity >= maxQuantity

    /**
     * Verifica se a carga está vazia.
     */
    val isEmpty: Boolean
        get() = quantity <= 0

    /**
     * Adiciona quantidade à carga.
     * 
     * @param amount Quantidade a adicionar
     * @return Quantidade realmente adicionada (pode ser menor se exceder maxQuantity)
     */
    fun add(amount: Int): Int {
        val spaceAvailable = maxQuantity - quantity
        val actualAmount = minOf(amount, spaceAvailable)
        quantity += actualAmount
        return actualAmount
    }

    /**
     * Remove quantidade da carga.
     * 
     * @param amount Quantidade a remover
     * @return Quantidade realmente removida (pode ser menor se não houver suficiente)
     */
    fun remove(amount: Int): Int {
        val actualAmount = minOf(amount, quantity)
        quantity -= actualAmount
        return actualAmount
    }

    /**
     * Esvazia completamente a carga.
     */
    fun clear() {
        quantity = 0
    }
}

/**
 * Define os tipos de carga padrão do jogo.
 * Pode ser estendido via arquivos JSON no módulo content/config.
 */
object DefaultCargoTypes {
    
    // Passageiros
    val PASSENGERS = CargoType(
        id = "cargo.passengers",
        name = "Passageiros",
        description = "Pessoas que precisam de transporte entre cidades",
        unitType = CargoUnitType.PASSENGERS,
        baseValue = 2.5f,
        isPerishable = false
    )

    // Mercadorias gerais
    val GOODS = CargoType(
        id = "cargo.goods",
        name = "Mercadorias",
        description = "Produtos manufaturados para distribuição",
        unitType = CargoUnitType.TONS,
        baseValue = 3.0f,
        isPerishable = false
    )

    // Alimentos perecíveis
    val FOOD = CargoType(
        id = "cargo.food",
        name = "Alimentos",
        description = "Produtos alimentícios que requerem transporte rápido",
        unitType = CargoUnitType.TONS,
        baseValue = 4.0f,
        isPerishable = true
    )

    // Recursos minerais
    val ORE = CargoType(
        id = "cargo.ore",
        name = "Minério",
        description = "Recursos extraídos de minas para processamento",
        unitType = CargoUnitType.TONS,
        baseValue = 1.5f,
        isPerishable = false
    )

    // Combustível
    val FUEL = CargoType(
        id = "cargo.fuel",
        name = "Combustível",
        description = "Derivados de petróleo para indústrias",
        unitType = CargoUnitType.LITERS,
        baseValue = 2.0f,
        isPerishable = false
    )

    // Madeira
    val WOOD = CargoType(
        id = "cargo.wood",
        name = "Madeira",
        description = "Troncos para serrarias e indústrias de papel",
        unitType = CargoUnitType.CUBIC_METERS,
        baseValue = 1.8f,
        isPerishable = false
    )

    // Contêineres
    val CONTAINERS = CargoType(
        id = "cargo.containers",
        name = "Contêineres",
        description = "Carga geral em contêineres padronizados",
        unitType = CargoUnitType.CONTAINERS,
        baseValue = 5.0f,
        isPerishable = false
    )

    /**
     * Retorna todos os tipos de carga padrão.
     */
    fun getAll(): List<CargoType> = listOf(
        PASSENGERS,
        GOODS,
        FOOD,
        ORE,
        FUEL,
        WOOD,
        CONTAINERS
    )

    /**
     * Busca um tipo de carga pelo ID.
     */
    fun getById(id: String): CargoType? = getAll().find { it.id == id }
}
