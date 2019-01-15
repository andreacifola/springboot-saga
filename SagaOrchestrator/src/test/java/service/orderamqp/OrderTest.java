package service.orderamqp;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import service.coreapi.*;
import service.entities.OrderEntity;
import service.order.Order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class OrderTest {

    private AggregateTestFixture<Order> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Order.class);
    }

    @Test
    public void testStartSaga() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new StartSagaCommand("1234", "Alice", "shirt", 2, "30$"))
                .expectEvents(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"));
    }

    @Test
    public void testOrderCreated() throws Exception {
        OrderEntity orderEntity = new OrderEntity("1234", "Alice", "shirt", 2, "30$");
        Order order = new Order();
        OrderEntity orderEntityFromOrder = order.on(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"));
        assertEquals("These are equals", orderEntity.getOrderID(), orderEntityFromOrder.getOrderID());
        assertEquals("These are equals", orderEntity.getUser(), orderEntityFromOrder.getUser());
        assertEquals("These are equals", orderEntity.getArticle(), orderEntityFromOrder.getArticle());
        assertEquals("These are equals", orderEntity.getQuantity(), orderEntityFromOrder.getQuantity());
        assertEquals("These are equals", orderEntity.getPrice(), orderEntityFromOrder.getPrice());
    }

    @Test
    public void testDeleteOrder() throws Exception {
        fixture.given(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .when(new DeleteOrderCommand("1234", "Alice", "shirt", 2, "30$"))
                .expectEvents(new OrderDeletedEvent("1234", "Alice", "shirt", 2, "30$"));
    }

    @Test
    public void testEndSaga() throws Exception {
        fixture.given(new SagaStartedEvent("1234", "Alice", "shirt", 2, "30$"))
                .when(new EndSagaOrderCommand("1234", "Alice", "shirt", 2, "30$"))
                .expectEvents(new SagaOrderEndedEvent("1234", "Alice", "shirt", 2, "30$"));
    }

    @Test
    public void testOrderDeleted() throws Exception {
        Order order = new Order();
        OrderEntity orderEntityFromOrder = order.on(new OrderDeletedEvent("1234", "Alice", "shirt", 2, "30$"));
        assertNull("These are equals", orderEntityFromOrder.getOrderID());
        assertNull("These are equals", orderEntityFromOrder.getUser());
        assertNull("These are equals", orderEntityFromOrder.getArticle());
        assertNull("These are equals", orderEntityFromOrder.getQuantity());
        assertNull("These are equals", orderEntityFromOrder.getPrice());
    }

}