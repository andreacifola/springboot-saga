package service.database;


import org.springframework.data.annotation.Id;

/**
 * This class is useful to store order elements in Mongo db.
 */
public class OrderEntity {

    @Id
    private String id;

    private String orderId;
    private String user;
    private String article;
    private Integer quantity;
    private String price;

    public OrderEntity() {
    }

    public OrderEntity(String orderId, String user, String article, Integer quantity, String price) {
        this.orderId = orderId;
        this.user = user;
        this.article = article;
        this.quantity = quantity;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
