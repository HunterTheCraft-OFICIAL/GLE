# Road Transport MVP - LibGDX & Kotlin (Multi-módulo)

Este repositório contém o desenvolvimento de um **novo jogo de simulação de transporte rodoviário**, construído do zero utilizando **LibGDX** como engine gráfica e **Kotlin** como linguagem principal.

> **Nota Importante:** Este projeto representa uma transição completa de tecnologia. O código legado relacionado ao *OpenTTD Mobile* e outras versões anteriores foi movido para a pasta `analisar-remover/` para fins de auditoria e será removido futuramente. A raiz deste repositório contém **apenas** o novo projeto funcional.

## 🚀 Visão Geral

O objetivo é criar um MVP (Minimum Viable Product) focado exclusivamente no **modal rodoviário**, aproveitando a produtividade do Kotlin e a flexibilidade multiplataforma do LibGDX. Diferente do OpenTTD original (escrito em C++), esta versão é nativa para a stack moderna de jogos Java/Kotlin.

## 📁 Estrutura do Repositório

```text
/
├── core/                 # Módulo principal: lógica do jogo (compartilhado)
├── desktop/              # Módulo Desktop: launcher para PC (LWJGL3)
├── android/              # Módulo Android: launcher para dispositivos móveis
├── assets/               # Recursos do jogo (sprites, sons, mapas)
├── build.gradle.kts      # Configuração de build root
├── settings.gradle.kts   # Configuração dos módulos
├── gradle.properties     # Propriedades e versões das dependências
├── gradlew               # Wrapper do Gradle
├── README.md             # Este arquivo
└── analisar-remover/     # ⚠️ LEGADO: Código antigo do OpenTTD/Mobile (para revisão)
```

## ✨ Funcionalidades Atuais (MVP)

- ✅ **Engine:** LibGDX 1.12.1 com backend LWJGL3 (Desktop) e Android.
- ✅ **Linguagem:** Kotlin 1.9.20+ com corrotinas e sintaxe moderna.
- ✅ **Multi-plataforma:** Desktop (Windows, Linux, macOS) e Android.
- ✅ **Renderização:** Sistema 2D ortográfico com câmera dinâmica.
- ✅ **Entidades:** Veículos (Ônibus, Caminhões, Carros) e Segmentos de Estrada.
- ✅ **Simulação:** Loop de atualização e movimento básico de veículos.
- ✅ **UI:** Interface básica sobreposta para debug e informações.

## 🛠️ Requisitos

Para compilar e rodar este projeto, você precisa de:
- **JDK 17** ou superior (Recomendado: JDK 17 ou 21).
- **Android SDK** (apenas para build Android, opcional para Desktop).
- Não é necessário instalar o Gradle manualmente (o wrapper está incluído).

## ▶️ Como Executar

A compilação e execução são feitas via Gradle Wrapper.

### Desktop (PC)

#### Linux / macOS
```bash
chmod +x gradlew
./gradlew desktop:run
```

#### Windows
```powershell
gradlew.bat desktop:run
```

### Android (Dispositivo ou Emulador)

```bash
# Conecte um dispositivo ou inicie um emulador
./gradlew android:installDebug
./gradlew android:run
```

## 📦 Build e Distribuição

Para gerar os artefatos finais:

```bash
# Gerar JAR Desktop
./gradlew desktop:jar
# Artefato: desktop/build/libs/road-mvp-desktop.jar

# Gerar APK Android (Debug)
./gradlew android:assembleDebug
# Artefato: android/build/outputs/apk/debug/android-debug.apk

# Gerar APK Android (Release - requer assinatura)
./gradlew android:assembleRelease
```

## 🏗️ Arquitetura do Código

O projeto segue a estrutura padrão multi-módulo do LibGDX:

1.  **`core/`**: Contém a lógica do jogo independente de plataforma (`RoadMVPGame`, `RoadScreen`, entidades, sistemas). Este módulo é compartilhado entre todas as plataformas.
2.  **`desktop/`**: Contém o launcher específico para PC (`DesktopLauncher`), configurando a janela e o contexto OpenGL via LWJGL3.
3.  **`android/`**: Contém o launcher específico para Android (`AndroidLauncher`), configurando a Activity e o ciclo de vida mobile.
4.  **`assets/`**: Recursos compartilhados (imagens, sons, fontes, dados) carregados pelo `core`.

## 🔮 Próximos Passos (Roadmap)

- [ ] Implementar sistema de *Pathfinding* real para veículos.
- [ ] Ferramentas de construção de estradas interativas (mouse/touch).
- [ ] Sistema de economia (custos de construção, receita por passageiro/carga).
- [ ] Sprites personalizados e animações.
- [ ] Geração procedural de mapas ou carregamento de cenários.
- [ ] Otimizações para mobile (controles touch, resolução adaptativa).
- [ ] Publicação na Google Play Store.

## 🤝 Contribuição

Este é um projeto ativo de reescrita/engine swap. Foco total na nova implementação em Kotlin.
- **Não** submita PRs misturando código legado da pasta `analisar-remover`.
- Sinta-se à vontade para sugerir melhorias na arquitetura LibGDX/Kotlin.
- Para contribuições Android, certifique-se de testar em dispositivo real ou emulador.

## 📄 Licença

Este novo projeto mantém a filosofia open-source, herdando a licença **GPL v2** do conceito original do OpenTTD, aplicada agora a esta nova base de código em Kotlin.
