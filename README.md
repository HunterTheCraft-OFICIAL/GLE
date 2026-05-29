# Road Transport MVP - LibGDX & Kotlin

Este repositório contém o desenvolvimento de um **novo jogo de simulação de transporte rodoviário**, construído do zero utilizando **LibGDX** como engine gráfica e **Kotlin** como linguagem principal.

> **Nota Importante:** Este projeto representa uma transição completa de tecnologia. O código legado relacionado ao *OpenTTD Mobile* e outras versões anteriores foi movido para a pasta `analisar-remover/` para fins de auditoria e será removido futuramente. A raiz deste repositório contém **apenas** o novo projeto funcional.

## 🚀 Visão Geral

O objetivo é criar um MVP (Minimum Viable Product) focado exclusivamente no **modal rodoviário**, aproveitando a produtividade do Kotlin e a flexibilidade multiplataforma do LibGDX. Diferente do OpenTTD original (escrito em C++), esta versão é nativa para a stack moderna de jogos Java/Kotlin.

## 📁 Estrutura do Repositório

```text
/
├── src/                  # Código fonte principal (Core + Desktop)
├── build.gradle.kts      # Configuração de build (Gradle Kotlin DSL)
├── gradlew               # Wrapper do Gradle
├── README.md             # Este arquivo
└── analisar-remover/     # ⚠️ LEGADO: Código antigo do OpenTTD/Mobile (para revisão)
```

## ✨ Funcionalidades Atuais (MVP)

- ✅ **Engine:** LibGDX 1.12.1 com backend LWJGL3.
- ✅ **Linguagem:** Kotlin 1.9.20+ com corrotinas e sintaxe moderna.
- ✅ **Renderização:** Sistema 2D ortográfico com câmera dinâmica.
- ✅ **Entidades:** Veículos (Ônibus, Caminhões, Carros) e Segmentos de Estrada.
- ✅ **Simulação:** Loop de atualização e movimento básico de veículos.
- ✅ **UI:** Interface básica sobreposta para debug e informações.

## 🛠️ Requisitos

Para compilar e rodar este projeto, você precisa de:
- **JDK 17** ou superior (Recomendado: JDK 17 ou 21).
- Não é necessário instalar o Gradle manualmente (o wrapper está incluído).

## ▶️ Como Executar

A compilação e execução são feitas via Gradle Wrapper.

### Linux / macOS
```bash
chmod +x gradlew
./gradlew run
```

### Windows
```powershell
gradlew.bat run
```

## 📦 Build e Distribuição

Para gerar um JAR executável ou empacotar o jogo:

```bash
# Gerar JAR
./gradlew jar

# O artefato estará em: build/libs/
```

## 🏗️ Arquitetura do Código

O projeto segue a estrutura padrão do LibGDX separando o núcleo (`core`) da implementação específica da plataforma (`desktop`):

1.  **`src/main/kotlin/.../core`**: Contém a lógica do jogo independente de plataforma (`RoadMVPGame`, `RoadScreen`, entidades).
2.  **`src/main/kotlin/.../desktop`**: Contém o launcher específico para PC (`DesktopLauncher`), configurando a janela e o contexto OpenGL.

## 🔮 Próximos Passos (Roadmap)

- [ ] Implementar sistema de *Pathfinding* real para veículos.
- [ ] Ferramentas de construção de estradas interativas (mouse/touch).
- [ ] Sistema de economia (custos de construção, receita por passageiro/carga).
- [ ] Sprites personalizados e animações.
- [ ] Geração procedural de mapas ou carregamento de cenários.
- [ ] Empacotamento para Android (futuro).

## 🤝 Contribuição

Este é um projeto ativo de reescrita/engine swap. Foco total na nova implementação em Kotlin.
- **Não** submita PRs misturando código legado da pasta `analisar-remover`.
- Sinta-se à vontade para sugerir melhorias na arquitetura LibGDX/Kotlin.

## 📄 Licença

Este novo projeto mantém a filosofia open-source, herdando a licença **GPL v2** do conceito original do OpenTTD, aplicada agora a esta nova base de código em Kotlin.
