package service.entities;


public class OrderEntity {

    private String orderID;
    private String user;
    private String article;
    private Integer quantity;
    private String price;
    //private String date;
    //private String hour;


    public OrderEntity() {

    }

    public OrderEntity(String orderID, String user, String article, Integer quantity, String price) {
        this.orderID = orderID;
        this.user = user;
        this.article = article;
        this.quantity = quantity;
        this.price = price;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
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
