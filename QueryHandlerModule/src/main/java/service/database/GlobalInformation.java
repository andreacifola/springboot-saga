package service.database;

import org.springframework.data.annotation.Id;

public class GlobalInformation {

    @Id
    private String id;

    private String orderId;
    private String user;
    private String article;
    private Integer quantity;
    private Integer availability;
    private String price;

    public GlobalInformation() {
    }

    public GlobalInformation(String orderId, String user, String article, Integer quantity, Integer availability, String price) {
        this.orderId = orderId;
        this.user = user;
        this.article = article;
        this.quantity = quantity;
        this.availability = availability;
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

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format(
                "\n{\n  id :           '%s',\n  orderId :      '%s',\n  user :         '%s',\n  article :      '%s',\n  quantity :     '%s',\n  availability : '%s',\n  price :        '%s',\n}\n",
                id, orderId, user, article, quantity, availability, price);
    }
}
