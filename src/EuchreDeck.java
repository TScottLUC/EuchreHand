import java.util.*;

/**
 * EuchreDeck uses the EuchreCard class
 * to create a normal Euchre deck (9, 10, Jack,
 * Queen, King, Ace of each Suit) as an ArrayList.
 * The deck can be shuffled, and cards are accessed
 * through the top of the deck.
 *
 * Author: Thomas Scott tscott3@luc.edu
 */
public class EuchreDeck {

    public final ArrayList<EuchreCard> deck; // Main structure for the EuchreDeck class

    /**
     * Constructor for a EuchreDeck
     * that adds one of each card to the deck
     */
    public EuchreDeck(){
        deck = new ArrayList<>(24);
        for (EuchreCard.Suit suit : EuchreCard.Suit.values()){
            for (EuchreCard.Rank rank : EuchreCard.Rank.values()){
                deck.add(new EuchreCard(rank, suit));
            }
        }
    } // EuchreDeck constructor

    /**
     * Shuffle the deck
     */
    public void shuffle(){
        Collections.shuffle(deck);
    } // method shuffle

    /**
     * @return the top card (index 0) of the deck
     */
    public EuchreCard getTopCard(){
        return deck.get(0);
    }// method getTopCard

    /**
     * Remove the top card (index 0) of the deck
     */
    public void removeTopCard(){
        deck.remove(0);
    } // method removeTopCard

} // class EuchreDeck
