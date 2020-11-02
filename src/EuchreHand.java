import java.util.*;

/**
 * EuchreHand utilizes Player, EuchreCard, and EuchreDeck to
 * play a hand of Euchre with 1 human player and 3 computer
 * players.
 *
 * Author: Thomas Scott tscott3@luc.edu
 */
public class EuchreHand {

    private static final int playerHandSize = 5; // Each player can only have 5 cards in their hand
    private static final int PLAYER_COUNT = 4; // Euchre is played with 4 players

    private EuchreDeck deck = new EuchreDeck(); // deck consisting of 9, 10, Jack, Queen, King, and Ace of each suit
    private Player[] players = new Player[PLAYER_COUNT]; // holds the 4 player objects

    public Player dealer; // keeps track of the player who dealt a hand
    private Player orderer; // keeps track of a player if they order someone the kittyCard
    private Player trickWinner; // keeps track of who won the last trick
    private EuchreCard kittyCard; // card that is turned up during dealing
    private EuchreCard.Suit trump; // keeps track of trump for the hand
    private EuchreCard.Suit led; // keeps track of what suit is led for each trick

    private int team1Tricks = 0; // keep track of how many tricks each team has won in a hand
    private int team2Tricks = 0; // A team needs a majority of tricks to win the hand

    private ArrayList<CardInPlay> cardsInPlay = new ArrayList<>(PLAYER_COUNT); // used during a trick to determine which card wins

    /**
     * CardInPlay is an inner class that
     * keeps track of which players play
     * what cards during a trick. This is used
     * with the cardsInPlay array to determine what card
     * wins a trick.
     */
    class CardInPlay{
        private EuchreCard card;
        private Player playerWhoPlayed;

        public EuchreCard getCard() { return card; }
        public Player getPlayerWhoPlayed(){
            return playerWhoPlayed;
        }

        public CardInPlay(EuchreCard c, Player p){
            card = c;
            playerWhoPlayed = p;
        }
    }

    /**
     * initializePlayers creates 4 Player
     * objects (1 human and 3 computer). The human
     * chooses their name, while the human's partner and the
     * opponents are assigned names. Each player is assigned
     * their team, and the players get linked to who is sitting
     * next to them.
     */
    private void initializePlayers() {
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

    /**
     * deal first shuffles the deck, chooses a random
     * Player to be the dealer, then adds 5 cards
     * to each Player's hand from the deck.
     */
    private void deal() {
        deck.shuffle();
        Random r = new Random();
        dealer = players[r.nextInt((3-1) + 1) + 1];
        for (Player player : players) {
            for (int i = 0; i < playerHandSize; i++) {
                player.addCardToHand(deck.getTopCard());
                deck.removeTopCard();
            }
        }
        System.out.println(dealer.getName() + " deals 5 cards to each player");
    }

    /**
     * findKittyCard gets the top card of the deck
     * (after dealing cards) to determine which card is up
     * for trump.
     */
    private void findKittyCard(){
        kittyCard = deck.getTopCard();
        System.out.println("The " + kittyCard.toString() + " is up.");
    }

    /**
     * decideTrumpAsHumanWithKitty takes input from a human
     * player to see if they want to order (or, if they are the dealer, pick up)
     * the kittyCard.
     * @param currentPlayer Human player of interest
     * @return true if trump is decided, false if not
     */
    private boolean decideTrumpAsHumanWithKitty(Player currentPlayer){
        System.out.println("You have the following hand: ");
        currentPlayer.printHand();
        ArrayList<EuchreCard.Suit> suitsToCall = new ArrayList<>(4);
        boolean trumpDecided = false;
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
        return trumpDecided;
    }

    /**
     * decideTrumpAsAIWithKitty determines whether a computer player
     * orders (or, if they are the dealer, picks up) the kittyCard.
     * @param currentPlayer AI player of interest
     * @return true if trump is decided, false if not
     */
    private boolean decideTrumpAsAIWithKitty(Player currentPlayer){
        setAIValuesWithTrump(currentPlayer);
        int sumOfValues = 0;
        boolean trumpDecided = false;
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
        return trumpDecided;
    }

    /**
     * decideTrumpAsHumanNoKitty takes input from a human
     * player to see if they want to call trump after the kittyCard
     * has been turned down.
     * @param currentPlayer Human player of interest
     * @return true if trump is decided, false if not
     */
    private boolean decideTrumpAsHumanNoKitty(Player currentPlayer){
        boolean trumpDecided = false;
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
        return trumpDecided;
    }

    /**
     * decideTrumpAsAINoKitty determines whether a computer player
     * calls trump after the kittyCard has been turned down.
     * @param currentPlayer AI player of interest
     * @return true if trump decided, false if not
     */
    private boolean decideTrumpAsAINoKitty(Player currentPlayer){
        boolean trumpDecided = false;
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
        return trumpDecided;
    }

    /**
     * changeTheLeft temporarily changes the suit of "the
     * left" after trump has been decided, and sets a temporary name
     * for it so that the card can still be referenced
     * as its original suit.
     */
    private void changeTheLeft(){
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

    /**
     * decideTrump cycles through each Player twice, first determining
     * if the Player wants to order the kittyCard, then determining if
     * they want to call trump, until trump is decided. Once trump is decided,
     * "the left" has its values changed. If trump is not
     * decided, nothing happens and a new hand would be dealt.
     */
    private void decideTrump(){
        findKittyCard();
        Player currentPlayer = dealer.getNextTo();
        int decided = 0;
        boolean trumpDecided = false;
        trump = kittyCard.getSuit();

        while (decided < 4 && !trumpDecided){
            if (!currentPlayer.computer){
               trumpDecided = decideTrumpAsHumanWithKitty(currentPlayer);
            } else{
                trumpDecided = decideTrumpAsAIWithKitty(currentPlayer);
            }
            currentPlayer = currentPlayer.getNextTo();
            decided += 1;
        }

        while (4 <= decided && decided < 8 && !trumpDecided){
            if (!currentPlayer.computer){
                trumpDecided = decideTrumpAsHumanNoKitty(currentPlayer);
            } else{
                trumpDecided = decideTrumpAsAINoKitty(currentPlayer);
            }
            currentPlayer = currentPlayer.getNextTo();
            decided += 1;
        }
        if (!trumpDecided){
            System.out.println("No one called trump. A new hand will be dealt.");
            trump = null;
        } else{
            changeTheLeft();
        }
    }

    /**
     * leadCardAsAI determines what card a computer
     * Player leads if they are the first to play
     * during a trick.
     * @param currentPlayer AI Player of interest
     */
    private void leadCardAsAI(Player currentPlayer){
        if (currentPlayer.hand.size() == 1){
            EuchreCard cardToPlay = currentPlayer.playCard(0);
            led = cardToPlay.getSuit();
            setOneCardValue(cardToPlay);
            cardsInPlay.add(new CardInPlay(cardToPlay, currentPlayer));
        }
        else {
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
                Random r = new Random();
                indexOfCard = r.nextInt((currentPlayer.hand.size()-1) + 1) + 1;
                cardToPlay = currentPlayer.hand.get(indexOfCard);
            }
            currentPlayer.playCard(indexOfCard);
            led = cardToPlay.getSuit();
            setOneCardValue(cardToPlay);
            cardsInPlay.add(new CardInPlay(cardToPlay, currentPlayer));
        }
    }

    /**
     * leadCardAsHuman takes input from a human Player
     * to determine what card they lead if they are the first
     * to play during a trick.
     * @param currentPlayer Human Player of interest
     */
    private void leadCardAsHuman(Player currentPlayer){
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
    }

    /**
     * playCardAsAI determines what card a computer Player
     * plays during a trick.
     * @param currentPlayer AI Player of interest
     * @param cardsToPlay cards available for the player to play
     */
    private void playCardAsAI(Player currentPlayer, ArrayList<EuchreCard> cardsToPlay){
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
        } else {
            int highestValue = -1; // anything will be higher
            EuchreCard highestCard = null;
            for (EuchreCard card : cardsToPlay) {
                if (card.getEuchreValue() > highestValue) {
                    highestValue = card.getEuchreValue();
                    highestCard = card;
                }
            }
            if (highestCard.getEuchreValue() > bestValue()) {
                int indexOfCard = currentPlayer.hand.indexOf(highestCard);
                currentPlayer.playCard(indexOfCard);
                cardsInPlay.add(new CardInPlay(highestCard, currentPlayer));
            } else {
                int lowestValue = 23; // anything will be lower
                EuchreCard lowestCard = null;
                for (EuchreCard card : cardsToPlay) {
                    if (card.getEuchreValue() < lowestValue) {
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

    /**
     * playCardAsHuman takes input from a human Player
     * to determine what card they play during a trick.
     * @param currentPlayer Human Player of interest
     * @param cardsToPlay cards available for the player to play
     */
    private void playCardAsHuman(Player currentPlayer, ArrayList<EuchreCard> cardsToPlay){
        System.out.println();
        System.out.println("The current cards in play are: ");
        for (CardInPlay card : cardsInPlay){
            System.out.println("[" + card.card.toString() + "] - " + card.getPlayerWhoPlayed().getName());
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
    }

    /**
     * cardsAbleToPlay determines what cards a
     * Player can play depending on what was led
     * during a trick.
     * @param currentPlayer Player of interest
     * @return an ArrayList containing the cards the player can play
     */
    private ArrayList<EuchreCard> cardsAbleToPlay(Player currentPlayer){
        ArrayList<EuchreCard> cardsToPlay = new ArrayList<>(5);
        for (EuchreCard card : currentPlayer.hand){
            if (card.getSuit() == led){ // Must follow suit
                cardsToPlay.add(card);
            }
        }
        if (cardsToPlay.size() == 0){ // Cannot follow suit
            cardsToPlay.addAll(currentPlayer.hand);
        }
        return cardsToPlay;
    }

    /**
     * playCards cycles through each Player and allows each to
     * play a card. Then, the cards in play are compared
     * to see who wins the trick.
     * @param afterFirstRound true if the first trick has already been played
     */
    private void playCards(boolean afterFirstRound){
        Player currentPlayer = dealer.getNextTo();
        if (afterFirstRound){
            currentPlayer = trickWinner;
        }
        if (!currentPlayer.computer){
            leadCardAsHuman(currentPlayer);
        } else{
            leadCardAsAI(currentPlayer);
        }
        setCardValuesForAllPlayers();
        for (int i=0;i<3;i++) {
            currentPlayer = currentPlayer.getNextTo();
            ArrayList<EuchreCard> cardsToPlay = cardsAbleToPlay(currentPlayer);
            if (!currentPlayer.computer){
                playCardAsHuman(currentPlayer, cardsToPlay);
            } else{
                playCardAsAI(currentPlayer, cardsToPlay);
            }
        }
        compareCardsInPlay();
    }

    /**
     * bestValue determines which card
     * out of the cards that are in play
     * has the highest value, so that
     * an AI player may choose what card
     * to play accordingly.
     * @return int game value of the best card in play
     */
    private int bestValue(){
        int highest = -1;
        for (CardInPlay card : cardsInPlay){
            if (card.card.getEuchreValue() > highest){
                highest = card.card.getEuchreValue();
            }
        }
        return highest;
    }

    /**
     * cardOrderedToHuman takes input from a human
     * Player to determine what card they replace with the
     * kitty card when a player orders it to them.
     * @param dealer Human dealer of interest
     */
    private void cardOrderedToHuman(Player dealer){
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
    }

    /**
     * cardOrderedToAI determines what card a computer
     * Player replaces with the kittyCard when another player orders
     * it to them.
     * @param dealer AI dealer of interest
     */
    private void cardOrderedToAI(Player dealer){
        setAIValuesWithTrump(dealer);
        int lowestValue = 23; // anything will be lower
        int indexToReplace = -1;
        for (int i=0;i<playerHandSize;i++){
            if (dealer.hand.get(i).getAIValue() < lowestValue){
                lowestValue = dealer.hand.get(i).getAIValue();
                indexToReplace = i;
            }
        }
        dealer.replaceCard(indexToReplace, kittyCard);
        System.out.println(dealer.getName() + " replaced a card with the " + kittyCard.toString());
        System.out.println();
    }

    /**
     * cardOrdered determines what card the dealer
     * replaces with the kittyCard when it is ordered,
     * depending on whether the dealer is human or AI.
     */
    private void cardOrdered(){
        System.out.println(dealer.getName() + " was ordered the " + kittyCard.toString() + " by " + orderer.getName());
        if (!dealer.computer){
            cardOrderedToHuman(dealer);
        } else{
            cardOrderedToAI(dealer);
        }
    }

    /**
     * compareCardsInPlay compares each card
     * that is in play for a trick to
     * determine what team wins the trick.
     */
    private void compareCardsInPlay(){
        int highestValue = 0; // anything will be higher
        CardInPlay winningCard = null;
        for (CardInPlay card : cardsInPlay){
            if (card.card.getEuchreValue() > highestValue){
                highestValue = card.card.getEuchreValue();
                winningCard = card;
            }
        }
        trickWinner = winningCard.getPlayerWhoPlayed();
        int winningTeam = winningCard.getPlayerWhoPlayed().team;
        if (winningTeam == 1){
            System.out.println("Team 1 wins the trick");
            team1Tricks += 1;
        } else{
            System.out.println("Team 2 wins the trick");
            team2Tricks += 1;
        }
        System.out.println();
    }

    /**
     * inWinningPosition determines whether a Player
     * is in a winning position during a trick so that
     * AI may make decisions on what card to play.
     * @param p AI player of interest
     * @return true if team is in a winning position, false if not
     */
    private boolean inWinningPosition(Player p){
        int highestValue = 0;
        CardInPlay winningCard = cardsInPlay.get(0);
        for (CardInPlay card : cardsInPlay){
            if (card.card.getEuchreValue() > highestValue){
                highestValue = card.card.getEuchreValue();
                winningCard = card;
            }
        }
        if (winningCard.getPlayerWhoPlayed().team == p.team){
            return true;
        }
        return false;
    }

    /**
     * whoWinsHand determines what team wins a hand
     * based on how many tricks they won.
     */
    private void whoWinsHand(){
        if (team1Tricks > team2Tricks){
            System.out.println(players[0].getName() + " and " + players[2].getName() + " win the hand");
        } else{
            System.out.println(players[1].getName() + " and " + players[3].getName() + " win the hand");
        }
    }

    /**
     * resetCardsInPlay resets the cardsInPlay ArrayList
     * so that a new trick may be played.
     */
    private void resetCardsInPlay(){
        cardsInPlay = new ArrayList<>(4);
    }

    /**
     * resetDeck resets the deck so that a new hand may be
     * played.
     */
    private void resetDeck(){
        deck = new EuchreDeck();
    }

    /**
     * setAIValuesWithTrump sets temporary values to each
     * card in an AI player's hand so that they may make a decision
     * based on these assigned values.
     * @param player AI Player of interest
     */
    private void setAIValuesWithTrump(Player player){
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

    /**
     * setOneCardValue is used to set the value
     * of the card that is led
     * @param card Card of interest
     */
    private void setOneCardValue(EuchreCard card){
        boolean trumpLed = led == trump;
        boolean cardIsTrump = card.getSuit() == trump;
        boolean cardIsJack = card.getRank().getValue() == 11;
        boolean cardIsLeft = card.getSuit().getColor().equals(trump.getColor()) && card.getRank().getValue() == 11;

        if (trumpLed) { // Trump suit is led -> No other suit has value (except Jack of the same color as trump)
            if (cardIsTrump) { // Card has the same suit as trump
                if (cardIsJack) { // Card is a Jack of the trump suit
                    card.setEuchreValue(16); // Jack of the trump suit has the highest value ("the right")
                } else {
                    card.setEuchreValue(card.getRank().getValue()); // Other trump cards will have their normal value
                }
            } else if (cardIsLeft) {
                card.setEuchreValue(15); // Jack of suit that is same color of trump suit has 2nd highest value ("the left")
            } else {
                card.setEuchreValue(0); // All cards that aren't trump when trump is led have no value
            }
        } else { // Trump is not led

            boolean cardSuitIsLed = card.getSuit() == led;

            if (cardIsTrump) { // Trump cards have higher value than all other cards
                if (cardIsJack) { // Card is a Jack of the trump suit
                    card.setEuchreValue(22); // Jack of trump suit has highest value ("the right")
                } else {
                    card.setEuchreValue(card.getRank().getValue() + 6); // Other trump cards get a higher than normal value so that they are higher than all other suits
                }
            } else if (cardIsLeft) {
                card.setEuchreValue(21); // Jack of suit that is same color of trump suit has 2nd highest value ("the left"
            } else if (cardSuitIsLed) {
                card.setEuchreValue(card.getRank().getValue()); // Cards that match the led suit have normal value
            }else {
                card.setEuchreValue(0); // Cards that aren't trump or don't match the led suit have no value
            }
        }
    }

    /**
     * setCardValuesForAllPlayers sets the game value
     * of each card in each player's hand, depending
     * on what card was led and what suit is trump.
     */
    private void setCardValuesForAllPlayers(){
        boolean trumpLed = led == trump;
        for (Player player : players) {
            for (EuchreCard card : player.hand) {
                boolean cardIsTrump = card.getSuit() == trump;
                boolean cardIsJack = card.getRank().getValue() == 11;
                boolean cardIsLeft = card.getSuit().getColor().equals(trump.getColor()) && card.getRank().getValue() == 11;

                if (trumpLed) { // Trump suit is led -> No other suit has value (except Jack of the same color as trump)
                    if (cardIsTrump) { // Card has the same suit as trump
                        if (cardIsJack) { // Card is a Jack of the trump suit
                            card.setEuchreValue(16); // Jack of the trump suit has the highest value ("the right")
                        } else {
                            card.setEuchreValue(card.getRank().getValue()); // Other trump cards will have their normal value
                        }
                    } else if (cardIsLeft) {
                        card.setEuchreValue(15); // Jack of suit that is same color of trump suit has 2nd highest value ("the left")
                    } else {
                        card.setEuchreValue(0); // All cards that aren't trump when trump is led have no value
                    }
                } else { // Trump is not led

                    boolean cardSuitIsLed = card.getSuit() == led;

                    if (cardIsTrump) { // Trump cards have higher value than all other cards
                        if (cardIsJack) { // Card is a Jack of the trump suit
                            card.setEuchreValue(22); // Jack of trump suit has highest value ("the right")
                        } else {
                            card.setEuchreValue(card.getRank().getValue() + 6); // Other trump cards get a higher than normal value so that they are higher than all other suits
                        }
                    } else if (cardIsLeft) {
                        card.setEuchreValue(21); // Jack of suit that is same color of trump suit has 2nd highest value ("the left"
                    } else if (cardSuitIsLed) {
                        card.setEuchreValue(card.getRank().getValue()); // Cards that match the led suit have normal value
                    } else {
                        card.setEuchreValue(0); // Cards that aren't trump or don't match the led suit have no value
                    }
                }
            }
        }
    }

    /**
     * playAgain takes user input to
     * determine if the user wants
     * to play another hand.
     * @return true if user wants to play again, false if not
     */
    private boolean playAgain(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Play another hand? Y/N");
        String response = keyboard.nextLine();
        boolean continueReading = false;
        boolean playAgain = false;
        do {
            if (response.equalsIgnoreCase("Y")) {
                playAgain = true;
            } else if (response.equalsIgnoreCase("N")) {
                playAgain = false;
            } else {
                System.out.println("Invalid response entered.");
                continueReading = true;
            }
        } while (continueReading);
        return playAgain;
    }

    // main method that plays a hand of Euchre
    public static void main(String[] args) {
        EuchreHand game = new EuchreHand();
        game.initializePlayers();
        boolean playAgain;
        do {
            boolean afterFirstRound = false;
            do {
                game.deal();
                game.decideTrump();
            } while (game.trump == null);
            for (int i = 0; i < 5; i++) {
                game.playCards(afterFirstRound);
                game.resetCardsInPlay();
                afterFirstRound = true;
            }
            game.whoWinsHand();
            game.resetDeck();
            playAgain = game.playAgain();
        } while (playAgain);
    }
}
