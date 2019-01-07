package service.database;


import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
public class StockEntity {

    @Id
    private String id;

    private String articleId;
    private String article;
    private Integer quantity;

    public StockEntity(String articleId, String article, Integer quantity) {
        this.articleId = articleId;
        this.article = article;
        this.quantity = quantity;
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
