import java.util.*;

public class EuchreDeck {

    public final ArrayList<EuchreCard> deck;

    public EuchreDeck(){
        deck = new ArrayList<>(24);
        for (EuchreCard.Suit suit : EuchreCard.Suit.values()){
            for (EuchreCard.Rank rank : EuchreCard.Rank.values()){
                deck.add(new EuchreCard(rank, suit));
            }
        }
    }

    public void addCard(EuchreCard card){
        deck.add(card);
    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    public EuchreCard getTopCard(){
        return deck.get(0);
    }
    public void removeTopCard(){
        deck.remove(0);
    }
}
