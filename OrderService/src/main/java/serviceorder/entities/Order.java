package serviceorder.entities;


public class Order {

    private String sagaID;
    private String orderID;
    private String user;
    private String article;
    private Integer quantity;
    private Float price;
    private String date;
    private String hour;

    public Order(String sagaID, String orderID, String user, String article, Integer quantity, Float price,
                 String date, String hour) {
        this.sagaID = sagaID;
        this.orderID = orderID;
        this.user = user;
        this.article = article;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.hour = hour;
    }

    public Order() {

    }

    public String getSagaID() {
        return sagaID;
    }

    public void setSagaID(String sagaID) {
        this.sagaID = sagaID;
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
