package com.iris.food_delivery.cart_service.repository;

import com.iris.food_delivery.cart_service.entity.Cart;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import org.springframework.stereotype.Repository;

@Repository
public class CartRepository {

    private final DynamoDbTable<Cart> cartTable;

    public CartRepository(DynamoDbEnhancedClient enhancedClient) {
        this.cartTable = enhancedClient.table("Cart", TableSchema.fromBean(Cart.class));
    }

    public void save(Cart cart) {
        cartTable.putItem(cart);
    }

    public Cart findByUserName(String userName) {
        return cartTable.getItem(r -> r.key(k -> k.partitionValue(userName)));
    }

    public void deleteByUserName(String userName) {
        cartTable.deleteItem(r -> r.key(k -> k.partitionValue(userName)));
    }
    
}

