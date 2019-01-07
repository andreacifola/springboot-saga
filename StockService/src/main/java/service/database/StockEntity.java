package service.database;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StockEntity {

    private String articleID;
    private String article;
    private Integer quantity;

    public StockEntity(String articleID, String article, Integer quantity) {
        this.articleID = articleID;
        this.article = article;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
