public class Card implements Cloneable {
    String name;
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