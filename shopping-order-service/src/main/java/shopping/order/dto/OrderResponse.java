/**
 * 
 */
package shopping.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author loinguyenx
 *
 */
public class OrderResponse {
	private final boolean ok;
	private final String orderId;

	@JsonCreator
	public OrderResponse(@JsonProperty("ok") boolean ok, @JsonProperty("orderId") String orderId) {
		this.ok = ok;
		this.orderId = orderId;
	}

	public boolean isOk() {
		return ok;
	}

	public String getOrderId() {
		return orderId;
	}

}
