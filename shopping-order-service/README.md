## Running the sample code

1. Start Service:

    ```
    mvn compile exec:exec 
    ```

2. Try it:

    ```
	curl --location --request POST 'http://localhost:8080/orders' \
	--header 'Content-Type: application/json' \
	--data-raw '{
	    "cartId": 1,
	    "items": [
	        {
	            "itemId": 1,
	            "quantity": 5
	        }
	    ]
	}'
    ```
