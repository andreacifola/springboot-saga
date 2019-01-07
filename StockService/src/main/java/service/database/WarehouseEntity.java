package service.database;


import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
public class WarehouseEntity {

    @Id
    private String id;

    private String article;
    private Integer availability;

    public WarehouseEntity(String article, Integer availability) {
        this.article = article;
        this.availability = availability;
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
