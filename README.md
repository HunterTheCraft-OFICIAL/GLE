# OpenTTD

OpenTTD é um jogo de simulação de transporte de código aberto, baseado no clássico Transport Tycoon Deluxe de Chris Sawyer. 
Escrito principalmente em C++, o projeto mantém a jogabilidade original enquanto adiciona recursos modernos, 
multiplayer, IA personalizada e extensibilidade através de NewGRF (mods).

## Table of contents

- 1.0) [About](#10-about)
    - 1.1) [Downloading OpenTTD](#11-downloading-openttd)
    - 1.2) [OpenTTD gameplay manual](#12-openttd-gameplay-manual)
    - 1.3) [Supported platforms](#13-supported-platforms)
    - 1.4) [Installing and running OpenTTD](#14-installing-and-running-openttd)
    - 1.5) [Add-on content / mods](#15-add-on-content--mods)
    - 1.6) [OpenTTD directories](#16-openttd-directories)
    - 1.7) [Compiling OpenTTD](#17-compiling-openttd)
- 2.0) [Contact and community](#20-contact-and-community)
    - 2.1) [Contributing to OpenTTD](#21-contributing-to-openttd)
    - 2.2) [Reporting bugs](#22-reporting-bugs)
    - 2.3) [Translating](#23-translating)
- 3.0) [Licensing](#30-licensing)
- 4.0) [Credits](#40-credits)

## 1.0) About

OpenTTD é um simulador de negócios de transporte onde os jogadores competem para obter lucro transportando passageiros e mercadorias 
por terra, mar e ar. O objetivo é expandir sua empresa de transporte enquanto compete com outras empresas e IA.

**Principais características:**
- Suporte a multiplayer (até 255 jogadores)
- Inteligência Artificial personalizável (AI/Game Scripts)
- Sistema de modding NewGRF para veículos, estações, indústrias e muito mais
- Editor de cenários integrado
- Mais de 40 idiomas suportados
- Atualizações constantes pela comunidade ativa

OpenTTD está licenciado sob a GNU General Public License versão 2.0, mas inclui alguns softwares de terceiros sob licenças diferentes.
Veja a seção ["Licensing"](#30-licensing) abaixo para detalhes.

## 1.1) Downloading OpenTTD

OpenTTD pode ser baixado do [site oficial do OpenTTD](https://www.openttd.org/).

Tanto versões 'stable' (estáveis) quanto 'nightly' (noturnas) estão disponíveis para download:

- a maioria dos usuários deve escolher a versão 'stable', pois esta foi mais extensivamente testada
- a versão 'nightly' inclui as últimas mudanças e recursos, mas às vezes pode ser menos confiável

OpenTTD também está disponível gratuitamente na [Steam](https://store.steampowered.com/app/1536610/OpenTTD/), [GOG.com](https://www.gog.com/game/openttd), e na [Microsoft Store](https://www.microsoft.com/p/openttd-official/9ncjg5rvrr1c). Em algumas plataformas, o OpenTTD estará disponível através do gerenciador de pacotes do seu sistema operacional ou serviço similar.


## 1.2) Manual de jogo do OpenTTD

OpenTTD possui um [wiki mantido pela comunidade](https://wiki.openttd.org/), incluindo um manual de jogo e dicas.


## 1.3) Plataformas suportadas

OpenTTD foi portado para várias plataformas e sistemas operacionais.

As plataformas atualmente suportadas são:

- Linux (SDL (OpenGL e non-OpenGL))
- macOS (universal) (Cocoa)
- Windows (Win32 GDI / OpenGL)

Outras plataformas também podem funcionar (em particular vários sistemas BSD), mas não testamos ou mantemos ativamente estas.

### 1.3.1) Suporte legado
Plataformas, linguagens e compiladores mudam.
Manteremos o suporte em plataformas antigas enquanto houver alguém interessado em mantê-las, exceto quando isso significar que o projeto não possa avançar para acompanhar os recursos de linguagem e compilador.

Garantimos que cada revisão do OpenTTD será capaz de carregar savegames de cada revisão mais antiga (exceto quando o savegame estiver corrompido).
Por favor, reporte um bug se encontrar um save que não carrega.

## 1.4) Instalando e executando OpenTTD

OpenTTD é geralmente simples de instalar, mas para mais ajuda o wiki [inclui um guia de instalação](https://wiki.openttd.org/en/Manual/Installation).

OpenTTD precisa de alguns arquivos gráficos e de som adicionais para funcionar.

Para algumas plataformas, estes serão baixados durante o processo de instalação se necessário.

Para algumas plataformas, você precisará consultar [o guia de instalação](https://wiki.openttd.org/en/Manual/Installation).


### 1.4.1) Arquivos gratuitos de gráficos e som

Os arquivos de dados gratuitos, divididos em OpenGFX para gráficos, OpenSFX para sons e
OpenMSX para música podem ser encontrados em:

- https://www.openttd.org/downloads/opengfx-releases/latest para OpenGFX
- https://www.openttd.org/downloads/opensfx-releases/latest para OpenSFX
- https://www.openttd.org/downloads/openmsx-releases/latest para OpenMSX

Por favor, siga o readme destes pacotes sobre o procedimento de instalação.
O instalador do Windows pode opcionalmente baixar e instalar estes pacotes.


### 1.4.2) Arquivos originais de gráficos e som do Transport Tycoon Deluxe

Se você quiser jogar com os arquivos de dados originais do Transport Tycoon Deluxe, você precisa copiar os arquivos de dados do CD-ROM para o diretório baseset/.
Não importa se você copia da versão DOS ou Windows do Transport Tycoon Deluxe.
A instalação do Windows pode opcionalmente copiar estes arquivos.

Você precisa copiar os seguintes arquivos:
- sample.cat
- trg1r.grf ou TRG1.GRF
- trgcr.grf ou TRGC.GRF
- trghr.grf ou TRGH.GRF
- trgir.grf ou TRGI.GRF
- trgtr.grf ou TRGT.GRF


### 1.4.3) Música original do Transport Tycoon Deluxe

Se você quiser a música do Transport Tycoon Deluxe, copie os arquivos apropriados do jogo original para a pasta baseset.
- TTD para Windows: Todos os arquivos na pasta gm/ (gm_tt00.gm até gm_tt21.gm)
- TTD para DOS: O arquivo GM.CAT
- Transport Tycoon Original: O arquivo GM.CAT, mas renomeie para GM-TTO.CAT


## 1.5) Conteúdo adicional / mods

OpenTTD possui múltiplos tipos de conteúdo adicional, que modificam a jogabilidade de diferentes maneiras.

A maioria dos tipos de conteúdo adicional pode ser baixada dentro do OpenTTD através do botão 'Check Online Content' no menu principal.

Conteúdo adicional também pode ser instalado manualmente, mas isso é mais complicado; o [wiki do OpenTTD](https://wiki.openttd.org/) pode oferecer ajuda com isso, ou o [guia de estrutura de diretórios do OpenTTD](./docs/directory_structure.md).


### 1.6) Diretórios do OpenTTD

OpenTTD usa sua própria estrutura de diretórios para armazenar dados do jogo, conteúdo adicional, etc.

Para mais informações, veja o [guia de estrutura de diretórios](./docs/directory_structure.md).

### 1.7) Compilando OpenTTD

Se você quer compilar o OpenTTD a partir do código fonte, as instruções podem ser encontradas em [COMPILING.md](./COMPILING.md).


## 2.0) Contato e comunidade

Canais 'oficiais'

- [Site oficial do OpenTTD](https://www.openttd.org)
- [Discord oficial do OpenTTD](https://discord.gg/openttd)
- Chat IRC usando #openttd no irc.oftc.net [mais informações sobre nosso canal IRC](https://wiki.openttd.org/en/Development/IRC%20channel)
- [OpenTTD no Github](https://github.com/OpenTTD/) para repositórios de código e para reportar problemas
- [forum.openttd.org](https://forum.openttd.org/) - o site primário de fórum da comunidade para discutir OpenTTD e jogos relacionados
- [Wiki do OpenTTD](https://wiki.openttd.org/) wiki mantida pela comunidade, incluindo tópicos como guia de jogabilidade, explicações detalhadas de algumas mecânicas do jogo, como usar conteúdo adicional (mods) e muito mais

Canais 'não oficiais'

- O wiki do OpenTTD tem uma [página listando comunidades do OpenTTD](https://wiki.openttd.org/en/Community/Community) incluindo algumas em idiomas diferentes do inglês


### 2.1) Contribuindo para o OpenTTD

Damos boas-vindas a contribuidores para o OpenTTD. Mais informações para contribuidores podem ser encontradas em [CONTRIBUTING.md](./CONTRIBUTING.md)


### 2.2) Reportando bugs

Bons relatórios de bugs são muito úteis. Temos um [guia para reportar bugs](./CONTRIBUTING.md#bug-reports) para ajudar com isso.

Desyncs em multiplayer são complexos de debugar e reportar (algumas habilidades de desenvolvimento de software são necessárias).
Instruções podem ser encontradas em [debugando e reportando desyncs](./docs/debugging_desyncs.md).


### 2.3) Traduzindo

OpenTTD é traduzido para muitos idiomas. Traduções são adicionadas e atualizadas via [ferramenta de tradução online](https://translator.openttd.org).


## 3.0) Licenciamento

OpenTTD é licenciado sob a GNU General Public License versão 2.0.
Para o texto completo da licença, veja o arquivo '[COPYING.md](./COPYING.md)'.
Esta licença se aplica a todos os arquivos nesta distribuição, exceto quando indicado abaixo.

A implementação do squirrel em `src/3rdparty/squirrel` é licenciada sob a licença Zlib.
Veja `src/3rdparty/squirrel/COPYRIGHT` para o texto completo da licença.

A implementação do md5 em `src/3rdparty/md5` é licenciada sob a licença Zlib.
Veja os comentários nos arquivos fonte em `src/3rdparty/md5` para o texto completo da licença.

As implementações de Posix `getaddrinfo` e `getnameinfo` para OS/2 em `src/3rdparty/os2` são distribuídas parcialmente sob a GNU Lesser General Public License 2.1, e parcialmente sob a licença BSD (3 cláusulas).
Os termos exatos da licença podem ser encontrados em `src/3rdparty/os2/getaddrinfo.c` e `src/3rdparty/os2/getnameinfo.c`, respectivamente.

A implementação do fmt em `src/3rdparty/fmt` é licenciada sob a licença MIT.
Veja `src/3rdparty/fmt/LICENSE.rst` para o texto completo da licença.


## 4.0 Créditos

Veja [CREDITS.md](./CREDITS.md)
