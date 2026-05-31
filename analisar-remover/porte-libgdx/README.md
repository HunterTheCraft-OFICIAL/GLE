# Porte libGDX do OpenTTD

## Visão Geral

Esta pasta contém os arquivos e recursos relacionados ao porte do OpenTTD para a framework **libGDX**.

### Sobre o libGDX

[libGDX](https://libgdx.com/) é um framework de desenvolvimento de jogos multiplataforma escrito em Java. Ele permite que desenvolvedores criem jogos que rodam em:

- **Desktop**: Windows, macOS, Linux
- **Mobile**: Android, iOS
- **Web**: Navegadores via WebGL/HTML5

### Objetivo deste Porte

O objetivo deste projeto é adaptar o OpenTTD (originalmente escrito em C/C++) para rodar sobre a framework libGDX, permitindo:

- ✅ Execução multiplataforma simplificada
- ✅ Aproveitamento da infraestrutura Java/Kotlin
- ✅ Facilitar o desenvolvimento para dispositivos móveis
- ✅ Possibilidade de execução web via HTML5

### Estrutura Esperada

```
porte libGDX/
├── README.md           # Este arquivo
├── core/               # Código principal do jogo (lógica compartilhada)
├── android/            # Projeto Android específico
├── desktop/            # Projeto Desktop específico
├── ios/                # Projeto iOS específico
├── html/               # Projeto HTML5/Web específico
└── assets/             # Recursos do jogo (imagens, sons, etc.)
```

### Status do Projeto

⚠️ **Em Desenvolvimento**

Este porte está em fase experimental/planejamento. Consulte a documentação principal do projeto para mais detalhes sobre o progresso.

### Dependências

- **JDK 8+** (Java Development Kit)
- **libGDX** (framework principal)
- **Kotlin** (opcional, para módulos Kotlin)
- **Gradle** (sistema de build)

### Como Contribuir

Se você deseja contribuir com este porte:

1. Familiarize-se com a [documentação do libGDX](https://libgdx.com/wiki/)
2. Entenda a arquitetura atual do OpenTTD
3. Consulte as issues abertas relacionadas ao porte
4. Siga as diretrizes de contribuição do projeto principal

### Links Úteis

- [Site oficial do libGDX](https://libgdx.com/)
- [Wiki do libGDX](https://libgdx.com/wiki/)
- [Repositório do libGDX no GitHub](https://github.com/libgdx/libgdx)
- [Documentação Principal do OpenTTD](../README.md)
- [Guia de Compilação](../COMPILING.md)

---

**Nota:** Para informações gerais sobre o OpenTTD, consulte o [README principal](../README.md) na raiz do projeto.
