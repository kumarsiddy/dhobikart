package freakydevelopers.dhobikart.pojo;

/**
 * Created by PURUSHOTAM on 10/21/2016.
 */

public class Cart {
    private String link;
    private int price;
    String name;
    int number;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
