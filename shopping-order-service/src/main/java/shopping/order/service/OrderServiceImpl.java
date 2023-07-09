package shopping.order.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shopping.order.dto.Item;
import shopping.order.dto.OrderRequest;
import shopping.order.dto.OrderResponse;

public class OrderServiceImpl implements OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Override
	public OrderResponse createOrder(OrderRequest orderRequest) {
		int total = 0;
		for (Item item : orderRequest.getItems()) {
			total += item.getQuantity();
		}
		logger.info("Order {} items from cart {}.", total, orderRequest.getCartId());
		return new OrderResponse(true, UUID.randomUUID().toString());
	}

}
