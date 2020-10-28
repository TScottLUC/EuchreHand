import java.util.*;

public class Player {

    public ArrayList<EuchreCard> hand = new ArrayList<>(5);
    public int team;
    public boolean computer;
    private String name;
    private Player nextTo;

    Scanner keyboard = new Scanner(System.in);

    public Player(int t, boolean c, String n){
        team = t;
        computer = c;
        name = n;
    }

    public void setNextTo(Player p){
        nextTo = p;
    }

    public Player getNextTo(){
        return nextTo;
    }

    public String getName(){
        return name;
    }

    public EuchreCard playCard(int i){
        EuchreCard cardToPlay = hand.get(i);
        System.out.println(getName() +  " played the " + cardToPlay.toString());
        hand.remove(i);
        return cardToPlay;
    }

    public void printHand(){
        for (int i=0;i<hand.size();i++){
            System.out.println(hand.get(i).toString() + "[" + (i+1) + "] ");
        }
        System.out.println();
    }

    public void replaceCard(int i, EuchreCard card){
        hand.set(i, card);
    }

    public void addCardToHand(EuchreCard card){
        hand.add(card);
    }
}
