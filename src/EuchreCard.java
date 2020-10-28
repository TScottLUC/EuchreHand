import java.util.*;

public class EuchreCard {

    public enum Rank {
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);

        private final int value;

        public final int getValue(){
            return value;
        }

        private Rank(int i){
            value = i;
        }
    }

    public enum Suit {
        CLUBS("Clubs", "Black"),
        DIAMONDS("Diamonds", "Red"),
        HEARTS("Hearts", "Red"),
        SPADES("Spades", "Black");

        private final String color;
        private final String name;

        public String getColor(){
            return color;
        }

        public String getName(){
            return name;
        }

        private Suit(String n, String c){
            this.name = n;
            this.color = c;
        }
    }

    private final Rank rank;

    public Suit getSuit(){
        return suit;
    }

    public void setSuit(Suit s){
        suit = s;
    }

    public Rank getRank(){
        return rank;
    }

    private Suit suit;
    private int euchreValue;
    private int AIValue;
    private String tempName = null;

    public EuchreCard(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }


    public String toString(){
        if (tempName != null){
            return rank + " of " + tempName;
        } else return rank + " of " + suit;

    }

    public void setGameValue(int n){
        euchreValue = n;
    }

    public int getEuchreValue(){
        return euchreValue;
    }

    public void setAIValue(int n){
        AIValue = n;
    }

    public int getAIValue(){
        return AIValue;
    }

    public void setTempName(String s){
        tempName = s;
    }

    public String getTempName(){
        return tempName;
    }
}
