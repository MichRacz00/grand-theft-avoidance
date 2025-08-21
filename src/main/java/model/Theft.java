package model;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

/**
 * Models theft in a specific store and location.
 * This is used to store data from table Alarm in the database.
 *
 * @author Michael
 */

@XmlRootElement
public class Theft {
    private String time;
    private String date;
    private String article;
    private String articleType;
    private String location;
    private String epc;

    public Theft() {}

    public Theft(String date, String time, String epc, String article, String location, String articleType) {
        this.article = article;
        this.location = location;
        this.epc = epc;
        this.date = date;
        this.time = time;
        this.articleType = articleType;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getArticle() {
        return article;
    }

    public String getLocation() {
        return location;
    }

    public String getEpc() {
        return epc;
    }

    public String getArticleType() {
        return articleType;
    }
}
