# GLE - Generic Logistic Enterprise

## Estrutura do Projeto Kotlin + libGDX

Este documento resume a implementação da arquitetura do projeto GLE, seguindo os princípios de Clean Architecture com separação clara entre **Engine** (Motor) e **Content** (Dados/Conteúdo).

---

## A) ESTRUTURA DE PASTAS SUGERIDA (IntelliJ)

```
GLE/
├── engine/                          # MOTOR DO JOGO (Core Logic)
│   ├── core/                        # Núcleo da Simulação
│   │   ├── simulation/              # Sistema de simulação
│   │   │   └── SimulationEngine.kt  # Motor principal de ticks
│   │   ├── map/                     # Sistema de mapa (grid 512x512)
│   │   │   ├── MapTypes.kt          # Tile, TerrainType, RoadType, Direction
│   │   │   └── GameMap.kt           # Classe GameMap com construção de estradas
│   │   ├── vehicle/                 # Classes de veículos
│   │   │   ├── Vehicle.kt           # Classe base abstrata
│   │   │   └── VehicleTypes.kt      # Van, Bus, Truck + VehicleFactory
│   │   ├── infrastructure/          # (Reservado para estradas, garagens)
│   │   └── system/                  # (Reservado para economia, estatísticas)
│   ├── screen/                      # Telas do jogo
│   │   ├── loading/                 # Tela de carregamento
│   │   ├── menu/                    # Menu principal
│   │   ├── game/                    # Tela principal do jogo
│   │   ├── settings/                # Configurações (FPS, Som)
│   │   └── credits/                 # Créditos
│   ├── render/                      # Renderização
│   ├── camera/                      # Controle de câmera
│   ├── input/                       # Processamento de entrada
│   └── audio/                       # Gerenciamento de áudio
│
├── content/                         # CONTEÚDO MODULAR (Data-Driven)
│   ├── vehicles/
│   │   └── VehicleDefinitions.kt    # VehicleDefinition, VehicleType, DefaultVehicles
│   ├── industries/
│   │   └── IndustryDefinitions.kt   # IndustryDefinition, IndustryType, DefaultIndustries
│   ├── cities/
│   │   └── CityDefinitions.kt       # CityCategoryDefinition, CityInstance, DefaultCityCategories
│   ├── cargo/
│   │   └── CargoTypes.kt            # CargoType, CargoLoad, DefaultCargoTypes
│   └── config/                      # (Para arquivos JSON futuros)
│
├── platforms/                       # IMPLEMENTAÇÕES POR PLATAFORMA
│   ├── desktop/                     # DesktopLauncher
│   ├── android/                     # AndroidLauncher
│   ├── ios/                         # IOSLauncher
│   └── web/                         # HtmlLauncher
│
└── shared/                          # CÓDIGO COMPARTILHADO
    ├── utils/                       # Utilitários gerais
    └── extensions/                  # Extensões Kotlin para libGDX
```

---

## B) DATA CLASSES PRINCIPAIS

### 1. Veículos (content/vehicles/VehicleDefinitions.kt)

```kotlin
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
    val fuelCapacity: Int?,
    val fuelConsumption: Float,
    val purchaseCost: Int,
    val runningCost: Int,
    val maintenanceCost: Int,
    val lifespan: Long,
    val spritePath: String
)

enum class VehicleType {
    VAN, BUS, TRUCK, ARTICULATED_TRUCK, PICKUP, DELIVERY
}
```

**Veículos Implementados:**
- `Van` (Small Van, Medium Van) - Ágil, entregas urbanas
- `Bus` (City Bus, Intercity Bus) - Transporte de passageiros
- `Truck` (Light Truck, Heavy Truck, Articulated Truck) - Carga pesada

### 2. Carga (content/cargo/CargoTypes.kt)

```kotlin
data class CargoType(
    val id: String,
    val name: String,
    val description: String,
    val unitType: CargoUnitType,
    val baseValue: Float,
    val isPerishable: Boolean = false,
    val requiredTemperature: Int? = null
)

data class CargoLoad(
    val cargoType: CargoType,
    var quantity: Int,
    val maxQuantity: Int,
    val originTile: Pair<Int, Int>,
    val destinationTile: Pair<Int, Int>,
    val loadedTime: Long
)

enum class CargoUnitType {
    PASSENGERS, TONS, KILOGRAMS, LITERS, CUBIC_METERS, UNITS, CONTAINERS
}
```

**Tipos de Carga:** Passageiros, Mercadorias, Alimentos, Minério, Combustível, Madeira, Contêineres

### 3. Indústrias (content/industries/IndustryDefinitions.kt)

```kotlin
data class IndustryDefinition(
    val id: String,
    val name: String,
    val description: String,
    val industryType: IndustryType,
    val producedCargo: CargoType?,
    val consumedCargo: CargoType?,
    val productionRate: Int,
    val consumptionRate: Int,
    val storageCapacity: Int,
    val spritePath: String
)

data class IndustryInstance(
    val definition: IndustryDefinition,
    val tileX: Int,
    val tileY: Int,
    var currentStorage: Int,
    var isActive: Boolean
)
```

**Tipos:** Mina, Poço de Petróleo, Floresta, Fazenda, Serraria, Refinaria, Fábrica, Usina

### 4. Cidades (content/cities/CityDefinitions.kt)

```kotlin
data class CityCategoryDefinition(
    val id: String,
    val name: String,
    val citySize: CitySize,
    val minPopulation: Int,
    val maxPopulation: Int,
    val baseGrowthRate: Float,
    val passengerGenerationRate: Int,
    val goodsDemandRate: Int
)

data class CityInstance(
    val definition: CityCategoryDefinition,
    val name: String,
    val centerX: Int,
    val centerY: Int,
    val radius: Int,
    var currentPopulation: Int,
    var happinessLevel: Int
)

enum class CitySize {
    VILLAGE, SMALL_TOWN, MEDIUM_CITY, LARGE_CITY, METROPOLIS
}
```

### 5. Mapa (engine/core/map/MapTypes.kt)

```kotlin
data class Tile(
    val x: Int,
    val y: Int,
    var terrain: TerrainType,
    var roadType: RoadType,
    var roadDirection: Direction?,
    var elevation: Int,
    var hasBuilding: Boolean,
    var buildingType: BuildingType?,
    var entityReference: Long?
)

enum class TerrainType { GRASS, DIRT, SAND, SNOW, ROCK, WATER, FOREST, DESERT, TUNDRA }
enum class RoadType { NONE, DIRT_ROAD, PAVED_ROAD, HIGHWAY, BRIDGE, TUNNEL }
enum class Direction { NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST }
```

---

## C) IMPLEMENTAÇÃO DA CLASSE BASE DO MOTOR DE SIMULAÇÃO

### SimulationEngine (engine/core/simulation/SimulationEngine.kt)

**Responsabilidades:**
- Gerenciar loop de ticks (60 ticks/segundo padrão)
- Atualizar todos os veículos
- Processar carregamento/descarregamento
- Sistema de tempo do jogo (hora, dia, ano)
- Notificar eventos via listeners

**Principais Métodos:**

```kotlin
class SimulationEngine {
    // Estado
    var isRunning: Boolean
    var currentTick: Long
    var timeScale: Float  // 0.1x a 5.0x
    
    // Componentes
    val gameMap: GameMap
    private val vehicles: List<Vehicle>
    
    // Controle
    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun update(deltaTime: Float)
    
    // Gerenciamento de Veículos
    fun addVehicle(vehicle: Vehicle)
    fun removeVehicle(vehicle: Vehicle)
    fun createAndAddVehicle(definition, startX, startY): Vehicle
    fun getAllVehicles(): List<Vehicle>
    
    // Tempo do Jogo
    fun getCurrentGameHour(): Int
    fun getCurrentGameDay(): Long
    fun getCurrentGameYear(): Long
    fun getFormattedGameTime(): String
    
    // Events
    fun addListener(listener: VehicleListener)
    fun removeListener(listener: VehicleListener)
}
```

### Vehicle - Classe Base (engine/core/vehicle/Vehicle.kt)

**Características:**
- Classe abstrata com lógica comum de movimentação
- Suporte a carregamento/descarregamento
- Sistema de combustível
- Estatísticas de distância, carga e ganhos

**Métodos Principais:**

```kotlin
abstract class Vehicle(
    val id: Long,
    val definition: VehicleDefinition,
    startX: Int,
    startY: Int
) {
    // Estado
    var tileX: Int, tileY: Int
    var currentSpeed: Float
    var direction: Direction
    var isMoving: Boolean
    var isLoading: Boolean, isUnloading: Boolean
    val cargoLoads: MutableList<CargoLoad>
    
    // Movimento
    open fun update(map: GameMap, deltaTime: Float)
    fun setDirection(newDirection: Direction)
    fun startMoving()
    fun stop(reason: String?)
    
    // Carga
    fun startLoading(cargoType: CargoType, amount: Int): Boolean
    fun updateLoading(loadRate: Int): Boolean
    fun startUnloading(): Boolean
    fun updateUnloading(unloadRate: Int): List<CargoLoad>
    
    // Manutenção
    fun service()
    fun refuel(amount: Int): Int
}
```

### GameMap - Sistema de Mapa (engine/core/map/GameMap.kt)

**Características:**
- Grid 512x512 (inspirado no OpenTTD)
- Construção de estradas horizontal/vertical
- Gerenciamento de terreno e edifícios
- Pathfinding support

**Métodos Principais:**

```kotlin
class GameMap(val width: Int = 512, val height: Int = 512) {
    // Acesso a Tiles
    fun getTile(x: Int, y: Int): Tile
    fun isValidPosition(x: Int, y: Int): Boolean
    
    // Construção de Estradas
    fun buildHorizontalRoad(startX, startY, endX, roadType): Int
    fun buildVerticalRoad(startX, startY, endX, endY, roadType): Int
    fun buildRoad(x1, y1, x2, y2, roadType): Int
    fun removeRoad(x: Int, y: Int): Boolean
    
    // Terreno
    fun setTerrain(x: Int, y: Int, terrain: TerrainType): Boolean
    fun fillTerrain(startX, startY, endX, endY, terrain): Int
    
    // Edifícios
    fun placeBuilding(x: Int, y: Int, type: BuildingType): Boolean
    fun removeBuilding(x: Int, y: Int): Boolean
    
    // Utilitários
    fun getAdjacentTiles(x: Int, y: Int): List<Tile>
    fun manhattanDistance(x1, y1, x2, y2): Int
    fun euclideanDistance(x1, y1, x2, y2): Float
    fun getStatistics(): MapStatistics
}
```

---

## DIRETRIZES DE EXTENSIBILIDADE

### Adicionar Novo Veículo

1. Crie uma nova `VehicleDefinition` em `content/vehicles/`
2. Se precisar de comportamento especial, crie classe estendendo `Vehicle`
3. Registre na `VehicleFactory`

```kotlin
// Exemplo: Nova Van Refrigerada
val REFRIGERATED_VAN = VehicleDefinition(
    id = "vehicle.van.refrigerated",
    name = "Van Refrigerada",
    vehicleType = VehicleType.VAN,
    // ... configurações
    supportedCargoTypes = listOf(DefaultCargoTypes.FOOD) // Apenas alimentos
)
```

### Adicionar Nova Indústria

1. Crie `IndustryDefinition` em `content/industries/`
2. Defina carga produzida/consumida
3. O motor de simulação processa automaticamente

### Adicionar Novo Tipo de Carga

1. Crie `CargoType` em `content/cargo/`
2. Associe a veículos e indústrias
3. O sistema calcula valor automaticamente

---

## PRÓXIMOS PASSOS SUGERIDOS

1. **Telas:** Implementar LoadingScreen, MenuScreen, GameScreen, SettingsScreen, CreditsScreen
2. **Renderização:** Criar SpriteBatch wrappers e TextureAtlasManager
3. **Input:** Implementar InputProcessor para mouse/teclado/touch
4. **Pathfinding:** Algoritmo A* para navegação de veículos
5. **UI:** Interface de usuário com Scene2D
6. **Save/Load:** Sistema de serialização para salvar jogos
7. **JSON Config:** Carregar definições de arquivos JSON externos

---

## RESUMO TÉCNICO

| Componente | Arquivo | Linhas | Descrição |
|------------|---------|--------|-----------|
| CargoTypes | content/cargo/CargoTypes.kt | ~210 | Tipos de carga e instâncias |
| VehicleDefinitions | content/vehicles/VehicleDefinitions.kt | ~375 | Definições de veículos |
| IndustryDefinitions | content/industries/IndustryDefinitions.kt | ~375 | Definições de indústrias |
| CityDefinitions | content/cities/CityDefinitions.kt | ~315 | Definições de cidades |
| MapTypes | engine/core/map/MapTypes.kt | ~190 | Tile, Terrain, Road, Direction |
| GameMap | engine/core/map/GameMap.kt | ~455 | Sistema de mapa 512x512 |
| Vehicle | engine/core/vehicle/Vehicle.kt | ~445 | Classe base de veículos |
| VehicleTypes | engine/core/vehicle/VehicleTypes.kt | ~370 | Van, Bus, Truck + Factory |
| SimulationEngine | engine/core/simulation/SimulationEngine.kt | ~445 | Motor de simulação |
| **TOTAL** | **9 arquivos** | **~3180 linhas** | **Código base completo** |

---

*Documento gerado como parte da estruturação do projeto GLE - Generic Logistic Enterprise*
