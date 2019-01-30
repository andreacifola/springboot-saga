package service.entities;


public class WareHouseEntity {

    private String article;
    private Integer available;

    public WareHouseEntity(String article, Integer available) {
        this.article = article;
        this.available = available;
    }

    public WareHouseEntity() {
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
