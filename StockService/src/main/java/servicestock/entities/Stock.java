package servicestock.entities;


public class Stock {

    private String sagaID;
    private String articleID;
    private String article;
    private Integer available;

    public Stock(String sagaID, String articleID, String article, Integer available) {
        this.sagaID = sagaID;
        this.articleID = articleID;
        this.article = article;
        this.available = available;
    }

    public Stock() {

    }

    public String getSagaID() {
        return sagaID;
    }

    public void setSagaID(String sagaID) {
        this.sagaID = sagaID;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}
