# Projeto: Sistema de Xadrez em Java

Aluno: Kawan Victor Cavalcante

RA: 24151609-2

Curso: Análise e Desenvolvimento de Sistemas

Instituição: Faculdade Unicesumar

Matéria: Programação Orientada a Objetos

Professora: Thayse Duarte de Oliveira

# Descrição do Projeto

Este projeto consiste em um sistema de xadrez completo com interface gráfica desenvolvido como trabalho prático para a disciplina de Programação Orientada a Objetos.
O sistema permite partidas entre dois jogadores, ou jogador contra a IA (com níveis aleatório e minimax), inclui verificação de xeque, promoção de peão com escolha de peça, histórico de movimentos e interface visual aprimorada.

# Tecnologias e Ferramentas Utilizadas

Java 21

Swing (javax.swing) para interface gráfica

Orientação a Objetos

Compilação via javac

Execução em terminal (ou pelo VSCode/Eclipse)

# Como Executar

Abra o terminal na raiz do projeto.

Compile todos os arquivos:

javac -d bin src/model/board/*.java src/model/pieces/*.java src/controller/*.java src/view/*.java

Execute com as imagens no classpath:

java -cp "bin;resources" view.ChessGUI

(No Linux/Mac, troque ; por :)

# Funcionalidades Implementadas

Tabuleiro e peças de xadrez gráficos

Jogo entre 2 humanos ou humano vs. computador (IA aleatória ou minimax)

Destacamento visual de seleção e movimentos possíveis

Histórico de movimentos

Painel de status para turno/mensagens/erro

Aviso visual de xeque (popup)

Promoção de peão com escolha de peça via diálogo visual

Reiniciar partida e desfazer jogada

# Detalhes Técnicos

Modelo orientado a objetos com separação clara entre lógica (controller), peças/tabuleiro (model) e interface (view).

IA: dois níveis de dificuldade (aleatório e minimax simples com avaliação de peças).

Implementação de promoção totalmente personalizável (Rainha, Torre, Bispo ou Cavalo).

Cores, fontes e disposição modernizadas para melhor usabilidade.