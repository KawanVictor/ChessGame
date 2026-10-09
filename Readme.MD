# Projeto: Sistema de Xadrez em Java

Aluno: Kawan Victor Cavalcante
Curso: Análise e Desenvolvimento de Sistemas
Instituição: Faculdade Unicesumar
Matéria: Programação Orientada a Objetos

# Descrição do Projeto

Este projeto consiste em um sistema de xadrez com interface gráfica desenvolvido como trabalho prático para a disciplina de Programação Orientada a Objetos.
O sistema permite partidas entre dois jogadores ou jogador contra a IA (níveis aleatório e minimax), aplica as regras oficiais de movimentação (incluindo roque, en passant e promoção), detecta xeque, xeque-mate e empates, e mantém histórico de movimentos com opção de desfazer.

# Tecnologias e Ferramentas Utilizadas

Java 21 ou superior

Swing (javax.swing) para interface gráfica

Orientação a Objetos

Compilação via javac, sem dependências externas

Execução em terminal (ou pelo VSCode/Eclipse)

# Como Executar

Abra o terminal na raiz do projeto.

Compile todos os arquivos:

```
javac -d bin src/model/board/*.java src/model/pieces/*.java src/controller/*.java src/view/*.java
```

Execute com as imagens no classpath:

```
java -cp "bin;resources" view.ChessGUI
```

(No Linux/Mac, troque ; por :)

# Como Rodar os Testes

Os testes ficam em `test/` e não usam bibliotecas externas. Depois de compilar o projeto:

```
javac -d bin -cp bin test/controller/*.java
java -cp bin controller.GameTest
```

Cada verificação imprime `OK` ou `FALHA`, e o programa termina com código 1 se algo falhar.
Os testes cobrem a geração de lances (perft, comparado com valores de referência), xeque e xeque-mate, roque, en passant, promoção, as regras de empate e a IA.

# Como Jogar

Clique em uma peça para selecioná-la: a casa fica com borda azul e os lances legais com borda verde. Clique em uma casa destacada para mover, ou na própria peça para desfazer a seleção.

O menu **Jogo** escolhe o modo:

- Humano vs Humano
- Humano vs Computador (IA Simples): a IA joga de pretas com lances aleatórios
- Humano vs Computador (IA Minimax): a IA joga de pretas usando minimax
- Computador vs Humano: a IA aleatória joga de brancas e faz o primeiro lance

**Novo Jogo** reinicia a partida mantendo o modo escolhido. **Desfazer** volta o último lance; contra a IA, volta também o seu lance, devolvendo a vez a você.

# Funcionalidades Implementadas

Tabuleiro e peças de xadrez gráficos

Jogo entre 2 humanos ou humano vs. computador (IA aleatória ou minimax)

Destacamento visual da peça selecionada e dos lances legais

Validação de lances legais (não é possível deixar o próprio rei em xeque)

Roque (pequeno e grande) e en passant

Promoção de peão com escolha de peça via diálogo visual (a IA promove sempre a rainha)

Aviso visual de xeque (popup) e detecção de xeque-mate

Empate por afogamento, tripla repetição, regra dos 50 lances e material insuficiente

Histórico de movimentos

Painel de status para turno/mensagens/erro

Reiniciar partida e desfazer jogada

# Notação do Histórico

Cada lance aparece como peça, casa de origem e casa de destino, com as letras em inglês: `P` peão, `N` cavalo, `B` bispo, `R` torre, `Q` rainha, `K` rei.

| Exemplo | Significado |
| --- | --- |
| `Pe2-e4` | peão de e2 para e4 |
| `Bc8xg4` | bispo de c8 captura em g4 |
| `O-O` / `O-O-O` | roque pequeno / roque grande |
| `Pa7-a8=Q` | peão promove a rainha |

# Estrutura do Projeto

```
src/model/board    Board, Position e Move (tabuleiro, casas e lances)
src/model/pieces   Piece e as seis peças, cada uma com sua movimentação
src/controller     Game (regras e estado da partida) e ChessAI (IA)
src/view           ChessGUI (interface Swing)
test/controller    GameTest (testes das regras e da IA)
resources          imagens das peças
```

# Detalhes Técnicos

Modelo orientado a objetos com separação clara entre lógica (controller), peças/tabuleiro (model) e interface (view).

Cada peça gera seus próprios movimentos; torre, bispo e rainha compartilham a lógica de movimento em linha da classe `Piece`. A classe `Game` filtra os lances que deixariam o rei em xeque e acrescenta roque e en passant.

IA: dois níveis de dificuldade. O nível minimax busca 2 meios-lances à frente com poda alfa-beta, avalia a posição pela soma do valor das peças e sorteia entre lances de mesmo valor.

Implementação de promoção totalmente personalizável (Rainha, Torre, Bispo ou Cavalo).

Cores, fontes e disposição modernizadas para melhor usabilidade.

# Limitações Conhecidas

A IA minimax avalia apenas material, sem considerar posição das peças, e não leva em conta os empates por repetição ou pela regra dos 50 lances ao escolher o lance.

O empate por tripla repetição e pela regra dos 50 lances é aplicado automaticamente, sem precisar ser reivindicado pelo jogador.
