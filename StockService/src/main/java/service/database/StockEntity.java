package service.database;


import org.springframework.data.annotation.Id;


/**
 * This class is useful to store stock elements in Mongo db.
 */
public class StockEntity {

    @Id
    private String id;

    private String stockId;
    private String articleId;
    private String article;
    private Integer quantity;

    public StockEntity() {
    }

    public StockEntity(String stockId, String articleId, String article, Integer quantity) {
        this.stockId = stockId;
        this.articleId = articleId;
        this.article = article;
        this.quantity = quantity;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
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
