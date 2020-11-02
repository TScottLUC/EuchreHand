import java.util.*;

/**
 * Player holds information for each player in
 * the EuchreHand game, including their hand of 5
 * EuchreCard objects, what team they are on, whether
 * they are human or not, their name, and the Player
 * sitting next to them.
 *
 * Author: Thomas Scott tscott3@luc.edu
 *
 */
public class Player {

    public ArrayList<EuchreCard> hand = new ArrayList<>(5); // Each player's hand can hold 5 cards
    public int team; // 1 or 2 in Euchre
    public boolean computer; // True if player is a computer player, false if human
    private final String name; // Player name
    private Player nextTo; // Determines player turn order

    /**
     * Player constructor
     * @param t team number
     * @param c true if player is a computer player, false if human player
     * @param n player name
     */
    public Player(int t, boolean c, String n){
        team = t;
        computer = c;
        name = n;
    } // Player constructor

    public void setNextTo(Player p){
        nextTo = p;
    } // method setNextTo

    public Player getNextTo(){
        return nextTo;
    } // method getNextTo

    public String getName(){
        return name;
    } // method getName

    /**
     * Removes a card from the player's hand
     * and prints a message saying which card
     * the player played.
     * @param i index of card to remove
     * @return card that was played (so that values can still be accessed)
     */
    public EuchreCard playCard(int i){
        EuchreCard cardToPlay = hand.get(i);
        System.out.println(getName() +  " played the " + cardToPlay.toString());
        hand.remove(i);
        return cardToPlay;
    } // method playCard

    /**
     * Prints the player's hand in a
     * readable format
     */
    public void printHand(){
        for (int i=0;i<hand.size();i++){
            System.out.println(hand.get(i).toString() + "[" + (i+1) + "] ");
        }
        System.out.println();
    } // method printHand

    /**
     * Replaces a card in the player's hand
     * with a different card
     * @param i index of card to replace
     * @param card card to replace removed card with
     */
    public void replaceCard(int i, EuchreCard card){
        hand.set(i, card);
    } // method replaceCard

    /**
     * Adds a specified card to the player's hand
     * @param card card to add
     */
    public void addCardToHand(EuchreCard card){
        hand.add(card);
    } // method addCardToHand

} // class Player
