# OpenTTD Road MVP - LibGDX Kotlin

Este é um projeto MVP (Minimum Viable Product) focado no **modal rodoviário** usando a engine **LibGDX** com a linguagem **Kotlin**.

## Objetivo

Criar uma versão simplificada do OpenTTD focada exclusivamente em transporte rodoviário, utilizando LibGDX como engine gráfica e Kotlin como linguagem de programação.

## Estrutura do Projeto

```
libgdx-kotlin-road-mvp/
├── build.gradle.kts          # Configuração do Gradle com dependências LibGDX
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
└── src/main/kotlin/com/openttd/roadmvp/
    ├── desktop/
    │   └── DesktopLauncher.kt    # Ponto de entrada da aplicação desktop
    └── core/
        ├── RoadMVPGame.kt        # Classe principal do jogo
        ├── RoadScreen.kt         # Tela principal com renderização
        └── RoadEntities.kt       # Entidades (veículos e estradas)
```

## Funcionalidades do MVP

### Implementadas
- ✅ Configuração do projeto LibGDX com Kotlin
- ✅ Sistema básico de renderização 2D
- ✅ Entidades de veículos rodoviários (Ônibus, Caminhões, Carros)
- ✅ Segmentos de estrada básicos
- ✅ Movimento automático de veículos
- ✅ UI básica com informações do jogo
- ✅ Camera ortográfica com viewport adaptável

### Próximos Passos (Sugestões)
- [ ] Sistema de pathfinding para veículos seguirem estradas
- [ ] Criação de estradas interativa (mouse/toque)
- [ ] Sistema de passageiros/carga
- [ ] Economias básicas (custos, receitas)
- [ ] Construção de paradas de ônibus/estações
- [ ] Sprites personalizados para veículos e estradas
- [ ] Sistema de compras de veículos
- [ ] Mapas maiores com scroll

## Requisitos

- JDK 17 ou superior
- Gradle 8.5 (incluído via wrapper)

## Como Executar

### No Linux/Mac:
```bash
cd libgdx-kotlin-road-mvp
chmod +x gradlew
./gradlew run
```

### No Windows:
```bash
cd libgdx-kotlin-road-mvp
gradlew.bat run
```

## Build

### Criar JAR executável:
```bash
./gradlew jar
```

O JAR será gerado em `build/libs/`

## Tecnologias Utilizadas

- **LibGDX 1.12.1** - Engine de jogos multiplataforma
- **Kotlin 1.9.20** - Linguagem de programação
- **Gradle 8.5** - Sistema de build
- **LWJGL3** - Backend desktop para LibGDX

## Arquitetura

O projeto segue uma arquitetura simples baseada em componentes:

1. **DesktopLauncher**: Ponto de entrada que configura a janela e inicia o jogo
2. **RoadMVPGame**: Classe principal que gerencia o ciclo de vida do jogo
3. **RoadScreen**: Tela principal que contém a lógica de renderização e atualização
4. **RoadEntities**: Classes de dados para veículos e estradas

## Contribuição

Este é um MVP inicial focado em demonstrar a viabilidade da migração para LibGDX/Kotlin com foco no modal rodoviário.

## Licença

Mesma licença do OpenTTD original (GPL v2)
