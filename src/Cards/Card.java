package Cards;

public class Card implements Cloneable {
    public String name;
    public Card(String name) {
    this.name = name;
    }

    @Override
    public Card clone(){
        return this;
    }

    public String getName(){
        return this.name;
    }
}