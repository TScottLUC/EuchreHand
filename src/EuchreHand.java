import java.util.*;

public class EuchreHand {

    private EuchreDeck deck = new EuchreDeck();
    private Player[] players = new Player[4];

    private ArrayList<CardInPlay> cardsInPlay = new ArrayList<>(4);

    class CardInPlay{
        public EuchreCard card;
        public Player playerWhoPlayed;

        public CardInPlay(EuchreCard c, Player p){
            card = c;
            playerWhoPlayed = p;
        }
    }

    public Player dealer;
    private Player orderer;
    private Player trickWinner;
    private EuchreCard kittyCard;
    private EuchreCard.Suit trump;
    private EuchreCard.Suit led;

    private int team1Tricks = 0;
    private int team2Tricks = 0;

    private static final int playerHandSize = 5;

    public void initializePlayers() {
        System.out.println("What is your name?");
        Scanner keyboard = new Scanner(System.in);
        String response = keyboard.nextLine();
        players[0] = new Player(1, false, response);
        players[1] = new Player(2, true, "Opponent 1");
        players[2] = new Player(1, true, "Partner");
        players[3] = new Player(2, true, "Opponent 2");

        players[0].setNextTo(players[1]);
        players[1].setNextTo(players[2]);
        players[2].setNextTo(players[3]);
        players[3].setNextTo(players[0]);
    }

    public void deal() {
        deck.shuffle();
        for (Player player : players) {
            for (int i = 0; i < playerHandSize; i++) {
                player.addCardToHand(deck.getTopCard());
                deck.removeTopCard();
            }
        }
        Random r = new Random();
        dealer = players[r.nextInt((3-1) + 1) + 1];
        System.out.println(dealer.getName() + " deals 5 cards to each player");
    }

    public void findKittyCard(){
        kittyCard = deck.getTopCard();
        System.out.println("The " + kittyCard.toString() + " is up.");
    }

    public void decideTrump(){
        findKittyCard();
        Player currentPlayer = dealer.getNextTo();
        int decided = 0;
        boolean trumpDecided = false;
        trump = kittyCard.getSuit();
        ArrayList<EuchreCard.Suit> possibleTrumps = new ArrayList<>(4);
        for (EuchreCard.Suit suit : EuchreCard.Suit.values()){
            possibleTrumps.add(suit);
        }
        while (decided < 4 && !trumpDecided){
            if (!currentPlayer.computer){
                System.out.println("You have the following hand: ");
                currentPlayer.printHand();
                ArrayList<EuchreCard.Suit> suitsToCall = new ArrayList<>(4);
                for (EuchreCard card : currentPlayer.hand){
                    if (card.getSuit() == kittyCard.getSuit() && !suitsToCall.contains(card.getSuit())){
                        suitsToCall.add(card.getSuit());
                    }
                }
                if (suitsToCall.contains(kittyCard.getSuit())) {
                    System.out.println("Order the " + kittyCard.toString() + " to " + dealer.getName() + "? Y/N");
                    Scanner keyboard = new Scanner(System.in);
                    boolean continueReading = false;
                    do {
                        String response = keyboard.nextLine();
                        if (response.equalsIgnoreCase("y")){
                            orderer = currentPlayer;
                            cardOrdered();
                            trumpDecided = true;
                            continueReading = false;
                        } else if (response.equalsIgnoreCase("n")){
                            continueReading = false;
                        } else{
                            System.out.println("Invalid response entered");
                            continueReading = true;
                        }
                    }while (continueReading);
                } else{
                    System.out.println("You must pass because you do not have any trump cards");
                }
            } else{
                setAIValuesWithTrump(currentPlayer);
                int sumOfValues = 0;
                for (EuchreCard card : currentPlayer.hand){
                    sumOfValues += card.getAIValue();
                }
                if (sumOfValues/5 >= 14){ // Computer orders card if they have a high average card value for the trump.
                    orderer = currentPlayer;
                    cardOrdered();
                    trumpDecided = true;
                } else{
                    System.out.println(currentPlayer.getName() + " passes.");
                }
            }
            currentPlayer = currentPlayer.getNextTo();
            decided += 1;
        }
        possibleTrumps.remove(kittyCard.getSuit());
        while (4 <= decided && decided < 8 && !trumpDecided){
            if (!currentPlayer.computer){
                System.out.println("Set trump? Cards in hand: ");
                currentPlayer.printHand();
                System.out.println("Suits available to call: ");
                ArrayList<EuchreCard.Suit> suitsToCall = new ArrayList<>(4);
                for (EuchreCard card : currentPlayer.hand){
                    if (card.getSuit() != kittyCard.getSuit() && !suitsToCall.contains(card.getSuit())){
                        suitsToCall.add(card.getSuit());
                    }
                }
                System.out.println(suitsToCall + " or enter N for no");
                Scanner keyboard = new Scanner(System.in);
                boolean continueReading = false;
                do {
                    String response = keyboard.nextLine();
                    for (EuchreCard.Suit suit : suitsToCall){
                        if (response.equalsIgnoreCase(suit.getName())){
                            trump = suit;
                            trumpDecided = true;
                            continueReading = false;
                            System.out.println(currentPlayer.getName() + " calls " + trump.getName() + " as trump");
                        }
                    }
                    if (!trumpDecided){
                        if (response.equalsIgnoreCase("n")) {
                            continueReading = false;
                        } else{
                            System.out.println("Invalid response entered");
                            continueReading = true;
                        }
                    }
                } while(continueReading);
            } else{
                int largestAverage = 0; // anything will be higher
                EuchreCard.Suit largestAvgSuit = null;
                ArrayList<EuchreCard.Suit> suitsToCall = new ArrayList<>(4);
                for (EuchreCard card : currentPlayer.hand) {
                    if (card.getSuit() != kittyCard.getSuit() && !suitsToCall.contains(card.getSuit())) {
                        suitsToCall.add(card.getSuit());
                    }
                }
                for (EuchreCard.Suit suit : suitsToCall){
                    trump = suit;
                    int sum = 0;
                    setAIValuesWithTrump(currentPlayer);
                    for (EuchreCard card : currentPlayer.hand){
                        sum += card.getAIValue();
                    }
                    if (sum/5 > largestAverage){
                        largestAverage = sum/5;
                        largestAvgSuit = suit;
                    }
                }
                if (largestAverage > 15){
                    trump = largestAvgSuit;
                    trumpDecided = true;
                    System.out.println(currentPlayer.getName() + " calls " + trump.getName() + " as trump");
                } else {
                    System.out.println(currentPlayer.getName() + " passes.");
                    trump = null;
                }
            }
            currentPlayer = currentPlayer.getNextTo();
            decided += 1;
        }
        if (!trumpDecided){
            System.out.println("No one called trump. A new hand will be dealt.");
            trump = null;
        } else{
            for (Player player : players) {
                for (EuchreCard card : player.hand) {
                    boolean cardIsLeft = card.getSuit().getColor().equals(trump.getColor()) && card.getRank().getValue() == 11;
                    if (cardIsLeft) {
                        card.setTempName(card.getSuit().getName().toUpperCase());
                        card.setSuit(trump);
                    }
                }
            }
        }
    }

    public void playCards(boolean afterFirstRound){
        Player currentPlayer = dealer.getNextTo();
        if (afterFirstRound){
            currentPlayer = trickWinner;
        }
        if (!currentPlayer.computer){
            System.out.println("Choose a card to lead: ");
            currentPlayer.printHand();
            System.out.println("Type a number to select your card.");
            Scanner keyboard = new Scanner(System.in);
            int response;
            do{
                response = keyboard.nextInt();
            } while(response < 1 || response > 5);
            EuchreCard cardToPlay = currentPlayer.playCard(response-1);
            led = cardToPlay.getSuit();
            setOneCardValue(cardToPlay);
            cardsInPlay.add(new CardInPlay(cardToPlay, currentPlayer));
        } else{
            if (currentPlayer.hand.size() == 1){
                EuchreCard cardToPlay = currentPlayer.playCard(0);
                led = cardToPlay.getSuit();
                setOneCardValue(cardToPlay);
                cardsInPlay.add(new CardInPlay(cardToPlay, currentPlayer));
            }
            else{
                int highestValue = -1;
                int indexOfNonTrump = -1;
                int highestTrumpValue = -1;
                int indexOfHTrump = -1;
                EuchreCard cardToPlay = null;
                EuchreCard highestNonTrump = null;
                EuchreCard highestTrump = null;
                setAIValuesWithTrump(currentPlayer);
                for (EuchreCard card : currentPlayer.hand) {
                    if (card.getAIValue() > highestValue && card.getSuit() != trump) {
                        highestValue = card.getAIValue();
                        highestNonTrump = card;
                        indexOfNonTrump = currentPlayer.hand.indexOf(card);
                    } else if (card.getAIValue() > highestTrumpValue && card.getSuit() == trump) {
                        highestTrumpValue = card.getAIValue();
                        highestTrump = card;
                        indexOfHTrump = currentPlayer.hand.indexOf(card);
                    }
                }
                int indexOfCard = -1;
                if (highestValue == 14) {
                    cardToPlay = highestNonTrump;
                    indexOfCard = indexOfNonTrump;
                } else if (highestTrumpValue > 20) {
                    cardToPlay = highestTrump;
                    indexOfCard = indexOfHTrump;
                } else {
                    cardToPlay = highestNonTrump;
                    indexOfCard = indexOfNonTrump;
                }
                currentPlayer.playCard(indexOfCard);
                led = cardToPlay.getSuit();
                setOneCardValue(cardToPlay);
                cardsInPlay.add(new CardInPlay(cardToPlay, currentPlayer));
            }
        }
        setCardValuesForAllPlayers();
        for (int i=0;i<3;i++) {
            currentPlayer = currentPlayer.getNextTo();
            ArrayList<EuchreCard> cardsToPlay = new ArrayList<>(5);
            for (EuchreCard card : currentPlayer.hand){
                if (card.getSuit() == led){ // Must follow suit
                    cardsToPlay.add(card);
                }
            }
            if (cardsToPlay.size() == 0){ // Cannot follow suit
                cardsToPlay.addAll(currentPlayer.hand);
            }
            if (!currentPlayer.computer){
                System.out.println();
                System.out.println("The current cards in play are: ");
                for (CardInPlay card : cardsInPlay){
                    System.out.println("[" + card.card.toString() + "] - " + card.playerWhoPlayed.getName());
                }
                System.out.println();
                System.out.println("Trump is " + trump.getName() + ".");
                System.out.println();
                System.out.println("Your current cards are: ");
                currentPlayer.printHand();
                System.out.println("You can play: ");
                for (int j=0;j<cardsToPlay.size();j++){
                    System.out.print(cardsToPlay.get(j).toString() + "[" + (j+1) + "] ");
                }
                System.out.println();
                System.out.println();
                System.out.println("Type a number to select your card.");
                Scanner keyboard = new Scanner(System.in);
                int response;
                do{
                    response = keyboard.nextInt();
                } while(response < 1 || response > cardsToPlay.size());
                EuchreCard cardToPlay = cardsToPlay.get(response - 1);
                int indexOfCard = -1;
                for (EuchreCard card : currentPlayer.hand){
                    if (card.getSuit() == cardToPlay.getSuit() && card.getRank() == cardToPlay.getRank()){
                        indexOfCard = currentPlayer.hand.indexOf(card);
                    }
                }
                currentPlayer.playCard(indexOfCard);
                cardsInPlay.add(new CardInPlay(cardToPlay, currentPlayer));
            } else{
                if (inWinningPosition(currentPlayer)){
                    int lowestValue = 23; // anything will be lower
                    EuchreCard lowestCard = null;
                    for (EuchreCard card : cardsToPlay){
                        if (card.getEuchreValue() < lowestValue){
                            lowestValue = card.getEuchreValue();
                            lowestCard = card;
                        }
                    }
                    int indexOfCard = currentPlayer.hand.indexOf(lowestCard);
                    currentPlayer.playCard(indexOfCard);
                    cardsInPlay.add(new CardInPlay(lowestCard, currentPlayer));
                } else{
                    int highestValue = -1; // anything will be higher
                    EuchreCard highestCard = null;
                    for (EuchreCard card : cardsToPlay){
                        if (card.getEuchreValue() > highestValue){
                            highestValue = card.getEuchreValue();
                            highestCard = card;
                        }
                    }
                    if (highestCard.getEuchreValue() > bestValue()){
                        int indexOfCard = currentPlayer.hand.indexOf(highestCard);
                        currentPlayer.playCard(indexOfCard);
                        cardsInPlay.add(new CardInPlay(highestCard, currentPlayer));
                    } else{
                        int lowestValue = 23; // anything will be lower
                        EuchreCard lowestCard = null;
                        for (EuchreCard card : cardsToPlay){
                            if (card.getEuchreValue() < lowestValue){
                                lowestValue = card.getEuchreValue();
                                lowestCard = card;
                            }
                        }
                        int indexOfCard = currentPlayer.hand.indexOf(lowestCard);
                        currentPlayer.playCard(indexOfCard);
                        cardsInPlay.add(new CardInPlay(lowestCard, currentPlayer));
                    }
                }
            }
        }
        compareCardsInPlay();
        resetLeftSuit();
    }

    public void resetLeftSuit(){
        for (EuchreCard card : deck.deck){
            if (card.getTempName() != null){
                card.setTempName(null);
            }
        }
    }

    public int bestValue(){
        int highest = -1;
        for (CardInPlay card : cardsInPlay){
            if (card.card.getEuchreValue() > highest){
                highest = card.card.getEuchreValue();
            }
        }
        return highest;
    }

    public void cardOrdered(){
        System.out.println(dealer.getName() + " was ordered the " + kittyCard.toString() + " by " + orderer.getName());
        if (!dealer.computer){
            System.out.println("Choose a card to replace with the " + kittyCard.toString());
            dealer.printHand();
            Scanner keyboard = new Scanner(System.in);
            boolean continueReading = false;
            do {
                int response = keyboard.nextInt();
                if (1 <= response && response <= 5) {
                    dealer.replaceCard(response - 1, kittyCard);
                    continueReading = false;
                } else {
                    System.out.println("Invalid response entered.");
                    continueReading = true;
                }
            } while (continueReading);
        } else{
            setAIValuesWithTrump(dealer);
            int lowestValue = 23; // anything will be lower
            int indexToReplace = -1;
            for (int i=0;i<playerHandSize;i++){
                if (dealer.hand.get(i).getAIValue() < lowestValue){
                    lowestValue = dealer.hand.get(i).getAIValue();
                    indexToReplace = i;
                }
            }
            deck.addCard(dealer.hand.get(indexToReplace));
            dealer.replaceCard(indexToReplace, kittyCard);
            System.out.println(dealer.getName() + " replaced a card with the " + kittyCard.toString());
            System.out.println();
        }
    }

    public void compareCardsInPlay(){
        int highestValue = 0; // anything will be higher
        CardInPlay winningCard = null;
        for (CardInPlay card : cardsInPlay){
            if (card.card.getEuchreValue() > highestValue){
                highestValue = card.card.getEuchreValue();
                winningCard = card;
            }
        }
        trickWinner = winningCard.playerWhoPlayed;
        int winningTeam = winningCard.playerWhoPlayed.team;
        if (winningTeam == 1){
            System.out.println("Team 1 wins the trick");
            team1Tricks += 1;
        } else{
            System.out.println("Team 2 wins the trick");
            team2Tricks += 1;
        }
        System.out.println();
    }

    public boolean inWinningPosition(Player p){
        int highestValue = 0;
        CardInPlay winningCard = cardsInPlay.get(0);
        for (CardInPlay card : cardsInPlay){
            if (card.card.getEuchreValue() > highestValue){
                highestValue = card.card.getEuchreValue();
                winningCard = card;
            }
        }
        if (winningCard.playerWhoPlayed.team == p.team){
            return true;
        }
        return false;
    }

    public void whoWinsHand(){
        if (team1Tricks > team2Tricks){
            System.out.println(players[0].getName() + " and " + players[2].getName() + " win the hand");
        } else{
            System.out.println(players[1].getName() + " and " + players[3].getName() + " win the hand");
        }
    }

    public void resetCardsInPlay(){
        cardsInPlay = new ArrayList<>(4);
    }

    public void resetDeck(){
        deck = new EuchreDeck();
    }

    public void setAIValuesWithTrump(Player player){
        for (EuchreCard card : player.hand){
            boolean cardIsTrump = card.getSuit() == trump;
            boolean cardIsJack = card.getRank().getValue() == 11;
            boolean cardIsLeft = card.getSuit().getColor().equals(trump.getColor()) && card.getRank().getValue() == 11;

            if (cardIsTrump) {
                if (cardIsJack) {
                    card.setAIValue(22);
                } else {
                    card.setAIValue(card.getRank().getValue() + 6);
                }
            } else if (cardIsLeft) {
                card.setAIValue(21);
            } else {
                card.setAIValue(card.getRank().getValue());
            }
        }
    }

    public void setOneCardValue(EuchreCard card){
        boolean trumpLed = led == trump;
        boolean cardIsTrump = card.getSuit() == trump;
        boolean cardIsJack = card.getRank().getValue() == 11;
        boolean cardIsLeft = card.getSuit().getColor().equals(trump.getColor()) && card.getRank().getValue() == 11;

        if (trumpLed) { // Trump suit is led -> No other suit has value (except Jack of the same color as trump)
            if (cardIsTrump) { // Card has the same suit as trump
                if (cardIsJack) { // Card is a Jack of the trump suit
                    card.setGameValue(16); // Jack of the trump suit has the highest value ("the right")
                } else {
                    card.setGameValue(card.getRank().getValue()); // Other trump cards will have their normal value
                }
            } else if (cardIsLeft) {
                card.setGameValue(15); // Jack of suit that is same color of trump suit has 2nd highest value ("the left")
            } else {
                card.setGameValue(0); // All cards that aren't trump when trump is led have no value
            }
        } else { // Trump is not led

            boolean cardSuitIsLed = card.getSuit() == led;

            if (cardIsTrump) { // Trump cards have higher value than all other cards
                if (cardIsJack) { // Card is a Jack of the trump suit
                    card.setGameValue(22); // Jack of trump suit has highest value ("the right")
                } else {
                    card.setGameValue(card.getRank().getValue() + 6); // Other trump cards get a higher than normal value so that they are higher than all other suits
                }
            } else if (cardIsLeft) {
                card.setGameValue(21); // Jack of suit that is same color of trump suit has 2nd highest value ("the left"
            } else if (cardSuitIsLed) {
                card.setGameValue(card.getRank().getValue()); // Cards that match the led suit have normal value
            }else {
                card.setGameValue(0); // Cards that aren't trump or don't match the led suit have no value
            }
        }
    }

    public void setCardValuesForAllPlayers () {
        boolean trumpLed = led == trump;
        for (Player player : players) {
            for (EuchreCard card : player.hand) {
                boolean cardIsTrump = card.getSuit() == trump;
                boolean cardIsJack = card.getRank().getValue() == 11;
                boolean cardIsLeft = card.getSuit().getColor().equals(trump.getColor()) && card.getRank().getValue() == 11;

                if (trumpLed) { // Trump suit is led -> No other suit has value (except Jack of the same color as trump)
                    if (cardIsTrump) { // Card has the same suit as trump
                        if (cardIsJack) { // Card is a Jack of the trump suit
                            card.setGameValue(16); // Jack of the trump suit has the highest value ("the right")
                        } else {
                            card.setGameValue(card.getRank().getValue()); // Other trump cards will have their normal value
                        }
                    } else if (cardIsLeft) {
                        card.setGameValue(15); // Jack of suit that is same color of trump suit has 2nd highest value ("the left")
                    } else {
                        card.setGameValue(0); // All cards that aren't trump when trump is led have no value
                    }
                } else { // Trump is not led

                    boolean cardSuitIsLed = card.getSuit() == led;

                    if (cardIsTrump) { // Trump cards have higher value than all other cards
                        if (cardIsJack) { // Card is a Jack of the trump suit
                            card.setGameValue(22); // Jack of trump suit has highest value ("the right")
                        } else {
                            card.setGameValue(card.getRank().getValue() + 6); // Other trump cards get a higher than normal value so that they are higher than all other suits
                        }
                    } else if (cardIsLeft) {
                        card.setGameValue(21); // Jack of suit that is same color of trump suit has 2nd highest value ("the left"
                    } else if (cardSuitIsLed) {
                        card.setGameValue(card.getRank().getValue()); // Cards that match the led suit have normal value
                    } else {
                        card.setGameValue(0); // Cards that aren't trump or don't match the led suit have no value
                    }
                }
            }
        }
    }



    public static void main(String[] args) {
        EuchreHand game = new EuchreHand();
        game.initializePlayers();
        boolean afterFirstRound = false;
        game.deal();
        game.decideTrump();
        for (int i=0;i<5;i++) {
            game.playCards(afterFirstRound);
            game.resetCardsInPlay();
            afterFirstRound = true;
        }
        game.whoWinsHand();
        game.resetDeck();
    }
}
