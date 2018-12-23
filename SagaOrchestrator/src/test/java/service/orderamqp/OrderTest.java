package service.orderamqp;

import service.coreapi.StartSagaCommand;
import service.coreapi.DeleteOrderCommand;
import service.coreapi.SagaStartedEvent;
import service.coreapi.OrderDeletedEvent;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;
import service.entities.OrderEntity;
import service.order.Order;

import static org.junit.Assert.*;


public class OrderTest {
    private AggregateTestFixture<Order> fixture;
    //private FixtureConfiguration<Order> fixture; //version 4.0.3

    @Before

    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(Order.class); //version 3.4.1
        //fixture = new AggregateTestFixture<>(Order.class); //version 4.0.3
    }

    @Test
    public void testCreateOrder() throws Exception {
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