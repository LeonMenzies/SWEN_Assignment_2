package Cards;

import java.awt.*;

public class Card implements Cloneable {
    public String name;
    public Image cardImage;

    public Card(String name, Image img) {
        this.name = name;
        this.cardImage = img;
    }

    @Override
    public Card clone(){
        return this;
    }

    public String getName(){
        return this.name;
    }

    public Image getCardImage(){
        return this.cardImage;
    }
}