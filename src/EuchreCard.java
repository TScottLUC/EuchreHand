/**
 * EuchreCard is a class used bu EuchreHand
 * to create playing card objects. Each card has a Rank (i.e. "Nine, "Jack")
 * and a Suit (i.e. "Clubs", "Hearts") and can be assigned values for
 * determining which card wins a trick (euchreValue) and what decision a computer player
 * will make (AIValue). The "left" in Euchre (Jack of same color as trump, but not trump suit)
 * can also be assigned a tempName, as it can be played as if it were trump.
 *
 * Author: Thomas Scott tscott3@luc.edu
 */
public class EuchreCard {

    /**
     * Rank holds normal card values and names for Ranks
     */
    public enum Rank {
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);
        // Euchre is not played with cards lower than 9

        private final int value; // Normal value of the card (9-14)

        public final int getValue(){
            return value;
        } // method getValue

        private Rank(int i){
            value = i;
        } // Rank constructor
    } // enum Rank

    /**
     * Suit holds normal card Suits and colors
     */
    public enum Suit {
        CLUBS("Clubs", "Black"),
        DIAMONDS("Diamonds", "Red"),
        HEARTS("Hearts", "Red"),
        SPADES("Spades", "Black");

        private final String color; // Red or black
        private final String name; // Suit name (i.e. "Clubs")

        public String getColor(){
            return color;
        } // method getColor

        public String getName(){
            return name;
        } // method getName

        private Suit(String n, String c){
            this.name = n;
            this.color = c;
        } // Suit constructor
    } // enum Suit

    // Each card has a Suit and Rank
    private Suit suit;
    private Rank rank;

    // Cards can also hold temporary values for during the game
    private int euchreValue; // Value that determines what card wins a trick
    private int AIValue; // Value that determines a computer's decision
    private String tempName = null; // Cards have null tempName unless they are the "left"

    public EuchreCard(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    } // EuchreCard constructor

    public Suit getSuit(){
        return suit;
    } // method getSuit

    public void setSuit(Suit s){
        suit = s;
    } // method setSuit

    public Rank getRank(){
        return rank;
    } // method getRank

    public int getEuchreValue(){
        return euchreValue;
    } // method getEuchreValue

    public void setEuchreValue(int n){
        euchreValue = n;
    } // method setEuchreValue

    public int getAIValue(){
        return AIValue;
    } // method getAIValue

    public void setAIValue(int n){
        AIValue = n;
    } // method setAIValue

    public String getTempName(){
        return tempName;
    } // method getTempName

    public void setTempName(String s){
        tempName = s;
    } // method setTempName

    /**
     * Returns the name of a card as a String (using tempName if it is the "left")
     * @return rank + " of " + suit
     */
    public String toString(){
        if (tempName != null){
            return rank + " of " + tempName;
        } else return rank + " of " + suit;
    } // method toString

} // class EuchreCard
