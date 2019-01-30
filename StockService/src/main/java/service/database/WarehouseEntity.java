package service.database;


import org.springframework.data.annotation.Id;

/**
 * This class is useful to store warehouse elements in Mongo db.
 */
public class WarehouseEntity {

    @Id
    private String id;

    private String articleId;
    private String article;
    private Integer availability;

    public WarehouseEntity() {
    }

    public WarehouseEntity(String articleId, String article, Integer availability) {
        this.articleId = articleId;
        this.article = article;
        this.availability = availability;
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

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }
}
