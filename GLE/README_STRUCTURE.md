/**
 * GLE - Generic Logistic Enterprise
 * 
 * Estrutura de Pastas do Projeto (IntelliJ/Kotlin + libGDX)
 * =========================================================
 * 
 * Este projeto segue os princípios da Clean Architecture, separando claramente:
 * - Engine (Motor): Lógica de simulação, renderização, sistemas
 * - Content (Conteúdo): Dados modulares de veículos, indústrias, cidades, cargas
 * - Platforms: Implementações específicas por plataforma (Desktop, Android, iOS, Web)
 * - Shared: Utilitários e extensões compartilhadas
 * 
 * ESTRUTURA COMPLETA:
 * 
 * GLE/
 * ├── engine/                      # MOTOR DO JOGO (Core Logic)
 * │   ├── core/                    # Núcleo da Simulação
 * │   │   ├── simulation/          # Sistema de simulação (tick, delta time, estado)
 * │   │   ├── map/                 # Sistema de mapa (grid 512x512, tiles, terreno)
 * │   │   ├── vehicle/             # Classes base e especializações de veículos
 * │   │   ├── infrastructure/      # Estradas, garagens, pontos de carga/descarga
 * │   │   └── system/              # Sistemas auxiliares (economia, estatísticas)
 * │   ├── screen/                  # Telas do jogo
 * │   │   ├── loading/             # Tela de carregamento
 * │   │   ├── menu/                # Menu principal
 * │   │   ├── game/                # Tela principal do jogo
 * │   │   ├── settings/            # Configurações (FPS, Som, etc.)
 * │   │   └── credits/             # Créditos
 * │   ├── render/                  # Renderização (sprites, UI, efeitos)
 * │   ├── camera/                  # Controle de câmera (zoom, pan, follow)
 * │   ├── input/                   # Processamento de entrada (mouse, teclado, touch)
 * │   └── audio/                   # Gerenciamento de áudio (SFX, música)
 * │
 * ├── content/                     # CONTEÚDO MODULAR (Data-Driven)
 * │   ├── vehicles/                # Definições de veículos (Van, Ônibus, Caminhão)
 * │   ├── industries/              # Tipos de indústrias (Fábrica, Fazenda, Mina)
 * │   ├── cities/                  # Categorias de cidades (Pequena, Média, Grande)
 * │   ├── cargo/                   # Tipos de carga (Passageiros, Mercadorias, Recursos)
 * │   └── config/                  # Arquivos JSON de configuração
 * │
 * ├── platforms/                   # IMPLEMENTAÇÕES POR PLATAFORMA
 * │   ├── desktop/                 # Desktop (Windows, Linux, macOS)
 * │   ├── android/                 # Android
 * │   ├── ios/                     # iOS
 * │   └── web/                     # WebGL/HTML5
 * │
 * └── shared/                      # CÓDIGO COMPARTILHADO
 *     ├── utils/                   # Utilitários gerais
 *     └── extensions/              # Extensões Kotlin para libGDX
 * 
 * PACOTES KOTLIN SUGERIDOS:
 * 
 * engine.core.simulation        -> SimulationEngine, TickSystem, GameState
 * engine.core.map               -> GameMap, Tile, TerrainType, MapRenderer
 * engine.core.vehicle           -> Vehicle, Van, Bus, Truck, VehicleFactory
 * engine.core.infrastructure    -> Road, Garage, Station, InfrastructureManager
 * engine.core.system            -> EconomySystem, StatisticsSystem, EventSystem
 * engine.screen.*               -> Screen implementations (LoadingScreen, MenuScreen, etc.)
 * engine.render                 -> SpriteBatchWrapper, TextureAtlasManager, UIRenderer
 * engine.camera                 -> GameCamera, CameraController
 * engine.input                  -> InputProcessor, GestureHandler
 * engine.audio                  -> AudioManager, MusicPlayer, SoundEffectPlayer
 * 
 * content.vehicles              -> VehicleDefinitions, VehicleStats
 * content.industries            -> IndustryDefinitions, ProductionChain
 * content.cities                -> CityDefinitions, PopulationSystem
 * content.cargo                 -> CargoType, CargoLoad, CargoDelivery
 * content.config                -> ConfigLoader, JsonParser
 * 
 * platforms.desktop             -> DesktopLauncher, DesktopConfig
 * platforms.android             -> AndroidLauncher, AndroidConfig
 * platforms.ios                 -> IOSLauncher, IOSConfig
 * platforms.web                 -> HtmlLauncher, WebConfig
 * 
 * shared.utils                  -> MathUtils, FileUtils, Logger
 * shared.extensions             -> LibGDXExtensions, KotlinExtensions
 * 
 * BENEFÍCIOS DESTA ARQUITETURA:
 * 
 * 1. Separação clara entre Engine e Content permite adicionar novos veículos/indústrias
 *    sem modificar o núcleo da simulação.
 * 2. Data Classes e arquivos JSON facilitam a criação de mods personalizados.
 * 3. Multi-plataforma nativa com libGDX.
 * 4. Testabilidade: cada módulo pode ser testado isoladamente.
 * 5. Escalabilidade: novos sistemas podem ser adicionados sem quebrar código existente.
 */

// Este arquivo é apenas documentação da estrutura de pastas.
// Os arquivos reais serão criados nos próximos passos.

package com.gle.structure

/**
 * Documentação da Estrutura de Pastas do Projeto GLE
 */
object ProjectStructureDocumentation {
    const val DESCRIPTION = "Generic Logistic Enterprise - Estrutura Clean Architecture"
}
