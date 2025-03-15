# GAME 5-Card Poker
Java-based network poker game.
Each player has their own console window, which constantly displays the moves they can make and the moves their opponents make.

#### Details:
- Maven based project
- Tested with JUnit - 74% of Code Coverage reported by SonarQube
- Fully documented with JavaDoc
- used java.nio package for Server based communication using Sockets


#### The course of the game:
0. Create or join the game - to game to start the minimum players requirement must be met.
1. Dealing 5 cards to each player
2. First bid - the player can make the following moves: fold, call, raise, all-in
3. Exchanging cards for players who are still in the game (maximum 4 cards can be exchanged)
4. Second bid - the rules are the same as in the first
5. Summary of the game with the winner and a summary of the player's current account balance


#### How to start:
execute the commands:
```shell
mvn clean package
```
first start the server with the command:

```shell
java -jar .\poker-server\target\poker-server-1.0-jar-with-dependencies.jar <number>
```
where number is the argument of how many players are in the game

start the client with the command:

```shell
java -jar .\poker-client\target\poker-client-1.0-jar-with-dependencies.jar
```

#### Communication protocol:
1. Client messages to the server at each stage:

   JoinServer
   - login <username>
   
   PreGame:
   - create <userID>
   - join <gameID> <userID>
   
   PokerGame:
   - handCards <gameID> <userID> request
   - handCards <gameID> <userID> accepted
   
   Auction:
   - bet <gameID> <userID> request <typeOfMove> sum
     (where <typeOfMove> takes the values: call, fold, raise, allIN)
   
   ChangingCards:
   - exchange <gameID> <userID> <info> <cardNumber>
     (where <info> takes the values ​​yes, no, <cardNumber> only when <info> is yes)
   
   Summary:
   - summary <gameID> <userID> request
     (gameFounder sends this request to the server so that the server sends the game results)
   
   `

2. Server messages to client at individual stages:
   JoinServer
   - accepted <userID>
   - denied

   PreGame:
   - acceptedCreate <gameID>
   - acceptedJoin <gameID>
   - rejectedJoin <gameID>

   Waiting:
   - playerJoin <username>                                 (sent to other players in the game that a player has joined)
   - startGame <gameID>                                    (starts the game)

   PokerGame:
   - cards <card1> <card2> <card3> <card4> <card5>
   - startAuction <playerCash> <minimumBet> <stake>        (to the first active player to start bidding)

   Auction:
   - startAuction <playerCash> <minimumBet> <stake>        (for the player to make a move in the auction)
   - lastStage
   - nextStage                                             (to all players to proceed to the next Stage)
   - winner <winnerID> <winnerUserName> <stake>            (when all but one player folded)

   ChangingCards:
   - changeCards                                           (sent to ask the player if they want to trade cards)
   - acceptedChange <cardID> <card>
   - deniedChange
   - acceptedEndChanging                                   (when a player does not want to exchange cards)
   - nextStage                                             (to all players to proceed to the next Stage)
   - startAuction <playerCash> <minimumBet> <stake>      (to the first active player to start bidding)

   Summary:
   - score <playerLayout> <winnerID> <winnerUsername> <winnerLayout> <stake> <playerCash>
      (information about game results and player account status)
   - foldWinner <winnerUsername> <winnerID> <stake> <playerCash>
     (information when everyone has folded except one player)



[//]: # (GRA Poker 5-kartowy)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (Przebieg rozgrywki:)

[//]: # (1. Rozdanie każdemu graczowi 5 kart)

[//]: # (2. Pierwsza licytacja - gracz może wykonać ruchy: fold, call, raise, allIn)

[//]: # (3. Wymiana kart dla graczy, którzy są dalej w grze &#40;maksymalnie 4 karty można wymienić&#41;)

[//]: # (4. Druga licytacja - zasady takie jak w pierwszej)

[//]: # (5. Podsumowanie gry z wskazaniem zwycięzcy oraz podsumowaniem aktualnego stanu konta gracza)

[//]: # ()
[//]: # ()
[//]: # ()

[//]: # ()
[//]: # (Sposób uruchomienia:)

[//]: # (1. wykonać polecenia:)

[//]: # (- mvn clean package)

[//]: # (- najpierw uruchomić serwer polceniem:)

[//]: # ()
[//]: # (```shell)

[//]: # (java -jar .\poker-server\target\poker-server-1.0-jar-with-dependencies.jar <liczba>)

[//]: # (```)

[//]: # (gdzie liczba jest argumentem ilu graczy jest w rozgrywce)

[//]: # ()
[//]: # (- uruchomić klienta poleceniem:)

[//]: # ()
[//]: # (```shell)

[//]: # (java -jar .\poker-client\target\poker-client-1.0-jar-with-dependencies.jar)

[//]: # (```)
         




[//]: # (Protokół komunikacyjny:)

[//]: # (1. Komunikaty klienta do serwera na poszczególnych etapach:)

[//]: # ()
[//]: # (   JoinServer)

[//]: # (    - login <username>)

[//]: # ()
[//]: # (   PreGame:)

[//]: # (    - create <userID>)

[//]: # (    - join <gameID> <userID>)

[//]: # ()
[//]: # (   PokerGame:)

[//]: # (    - handCards <gameID> <userID> request)

[//]: # (    - handCards <gameID> <userID> accepted)

[//]: # ()
[//]: # (   Auction:)

[//]: # (    - bet <gameID> <userID> request <typeOfMove> sum)

[//]: # (      &#40;gdzie <typeOfMove> przyjmuje wartości: call, fold, raise, allIN&#41;)

[//]: # ()
[//]: # (   ChangingCards:)

[//]: # (    - exchange <gameID> <userID> <info> <cardNumber>)

[//]: # (      &#40;gdzie <info> przyjmuje wartości yes, no, <cardNumber> tylko gdy <info> jest yes&#41;)

[//]: # ()
[//]: # (   Summary:)

[//]: # (    - summary <gameID> <userID> request)

[//]: # (      &#40;gameFounder wysyła ten request do serwera, żeby serwer wysłał wyniki gry&#41;)

[//]: # ()



[//]: # ()
[//]: # (2. Komunikaty serwera do klienta na poszczególnych etapach:)

[//]: # ()
[//]: # (   JoinServer)

[//]: # (    - accepted <userID>)

[//]: # (    - denied)

[//]: # ()
[//]: # (   PreGame:)

[//]: # (    - acceptedCreate <gameID>)

[//]: # (    - acceptedJoin <gameID>)

[//]: # (    - rejectedJoin <gameID>)

[//]: # ()
[//]: # (   Waiting:)

[//]: # (    - playerJoin <username>                                 &#40;wysyłany do pozostałych graczy w grze, że gracz dołączył&#41;)

[//]: # (    - startGame <gameID>                                    &#40;rozpoczyna grę&#41;)

[//]: # ()
[//]: # (   PokerGame:)

[//]: # (    - cards <card1> <card2> <card3> <card4> <card5>)

[//]: # (    - startAuction <playerCash> <minimumBet> <stake>        &#40;do pierwszego aktywnego gracza aby rozpoczął licytacje&#41;)

[//]: # ()
[//]: # (   Auction:)

[//]: # (    - startAuction <playerCash> <minimumBet> <stake>        &#40;do gracza aby wykonał ruch w licytacji licytacje&#41;)

[//]: # (    - lastStage)

[//]: # (    - nextStage                                             &#40;do wszystkich graczy, aby przeszli do następnego Stage'a&#41;)

[//]: # (    - winner <winnerID> <winnerUserName> <stake>            &#40;gdy wszyscy poza jednym graczem sfoldowali&#41;)

[//]: # ()
[//]: # (   ChangingCards:)

[//]: # (    - changeCards                                           &#40;wysyłane żeby zapytać gracza czy chce wymienić karty&#41;)

[//]: # (    - acceptedChange <cardID> <card>)

[//]: # (    - deniedChange)

[//]: # (    - acceptedEndChanging                                   &#40;gdy gracz nie chce wymienić kart&#41;)

[//]: # (    - nextStage                                             &#40;do wszystkich graczy, aby przeszli do następnego Stage'a&#41;)

[//]: # (    - startAuction <playerCash> <minimumBet> <stake>        &#40;do pierwszego aktywnego gracza aby rozpoczął licytacje&#41;)

[//]: # ()
[//]: # (   Summary:)

[//]: # (    - score <playerLayout> <winnerID> <winnerUsername> <winnerLayout> <stake> <playerCash>)

[//]: # (      &#40;informacja o wynikach gry i stanie konta gracza&#41;)
[//]: # (    - foldWinner <winnerUsername> <winnerID> <stake> <playerCash>)

[//]: # (      &#40;informacja gdy wszyscy sfoldowali poza jednym graczem&#41;)

[//]: # ()
[//]: # ()
