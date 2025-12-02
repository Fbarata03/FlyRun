#  FlyRun – Loja de Itens para Jogos

Aplicação móvel desenvolvida como projeto da disciplina de **Programação para Dispositivos Móveis**, seguindo rigorosamente as diretrizes do enunciado E01. O app simula uma loja digital para compra de conteúdos adicionais (DLCs, expansões e itens cosméticos) para dois jogos de corrida: **Fórmula 1®** e **MotoGP™**.


---

##  Funcionalidades

- **Tela inicial (`MainActivity`)**: lista de jogos em cards.
- **Tela de detalhes (`GameDetailActivity`)**: exibe informações do jogo e lista de itens compráveis.
- **Modal Bottom Sheet**: aparece ao selecionar um item; permite confirmar ou cancelar a compra.
- **Feedback de compra**: exibe um `Toast` com a mensagem:  
  *“Acabou de comprar o item [NOME] por $[PREÇO]”*.
- Navegação entre telas usando **`Intent`** (sem Navigation Component).
- Dados de exemplo embutidos (simulando resposta de backend).

---

## Jogos e Itens Disponíveis

###  Fórmula 1®
1. **Pacote de Pinturas das Equipes** – $12,99  
2. **Expansão de Carros Clássicos** – $9,99  
3. **DLC de Circuitos Históricos** – $14,99  

###  MotoGP™
1. **Pacote de Equipamento do Piloto** – $11,99  
2. **Coleção de Motos Clássicas** – $8,99  
3. **Expansão de Circuitos do GP** – $15,99  


---

##  Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Arquitetura**: MVC (Model-View-Controller)
- **Navegação**: `Intent` entre Activities
- **Passagem de dados**: `Parcelable` (objetos serializados via `Intent.putExtra`)
- **Sem bibliotecas externas** — apenas dependências padrão do Android Studio.

---

## Estrutura do Projeto

app/
├── src/main/java/
│ └── com/example/flyrun/
│ ├── model/
│ │ ├── Game.kt
│ │ └── PurchasableItem.kt
│ ├── MainActivity.kt
│ └── GameDetailActivity.kt
│
├── src/main/res/
│ ├── drawable/ # Capas dos jogos e ícones dos itens
│ │ ├── cover_f1.png
│ │ ├── cover_motogp.png
│ │ ├── ic_f1_liveries.png
│ │ └── ... (todos os ícones dos itens)
│ └── ... # Outros recursos (strings, themes, etc.)
│
└── src/main/java/.../ui/ # (opcional) Composables organizados
├── GameCard.kt
├── PurchasableItemRow.kt
└── PurchaseBottomSheet.kt
