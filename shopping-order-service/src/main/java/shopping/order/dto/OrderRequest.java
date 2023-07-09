/**
 * 
 */
package shopping.order.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author loinguyenx
 *
 */
public class OrderRequest {
	private String cartId;
	private List<Item> items;

	@JsonCreator
	public OrderRequest(@JsonProperty("cartId") String cartId, @JsonProperty("items") List<Item> items) {
		super();
		this.cartId = cartId;
		this.items = items;
	}

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
