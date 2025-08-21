package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Article {

    private long id;
    private int category;
    private int article;
    private String color;
    private String size;
    private double price;
    private int num;

    public Article() {}

    public Article(long id, int category, int article, String color, String size, double price, int num) {
        this.id = id;
        this.category = category;
        this.article = article;
        this.color = color;
        this.size = size;
        this.price = price;
        this.num = num;
    }

    public long getId() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public int getArticle() {
        return article;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public int getNum() {
        return num;
    }
}
