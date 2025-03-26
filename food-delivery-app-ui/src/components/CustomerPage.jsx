import { useEffect, useState } from 'react';
import axios from 'axios';
import Layout from './Layout';
import '../styles/CustomerPage.css';

const CustomerPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const [activeTab, setActiveTab] = useState('restaurants');
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [showDishModal, setShowDishModal] = useState(false);
  const [selectedDishes, setSelectedDishes] = useState([]);
  const [cartRestaurantUserName, setCartRestaurantUserName] = useState(null);
  const [cart, setCart] = useState(null);
  const [cartLoading, setCartLoading] = useState(false);



  useEffect(() => {
    if (activeTab === 'restaurants') {
      fetchRestaurants();
    } else if (activeTab === 'cart') {
      fetchCart();
    }
  }, [activeTab]);


  const fetchRestaurants = async () => {
    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8083/restaurant/customer/order', {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      });
      setRestaurants(response.data.data);
    } catch (error) {
      console.error('Error fetching restaurants', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchCart = async () => {
    try {
      setCartLoading(true);
      const response = await axios.get('http://localhost:8085/cart/mycart', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      setCart(response.data.data);
    } catch (error) {
      console.error('Error fetching cart', error);
      setCart(null);
    } finally {
      setCartLoading(false);
    }
  };

  const handleDeleteCartItem = async (itemId) => {
    const updatedDishes = cart.dishes.filter(d => d.itemId !== itemId);
    const updatedCart = { ...cart, dishes: updatedDishes };
  
    try {
      const res = await axios.post('http://localhost:8085/cart/save', updatedCart, {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      alert('Item removed from cart.');
      fetchCart(); // Refresh
    } catch (error) {
      console.error('Delete failed', error);
      alert('Failed to update cart.');
    }
  };

  const handlePlaceOrder = () => {
    if (!cart || cart.dishes.length === 0) {
      alert('Cart is empty.');
      return;
    }
  
    // You will replace this with your actual order API later
    alert('Order placed successfully!');
  };
  
  
  

  const getDishQty = (dish) => {
    const item = selectedDishes.find(d => d.itemId === String(dish.id));
    return item ? item.quantity : 0;
  };
  
  const updateDishQty = (dish, delta) => {
    const itemId = String(dish.id);
    const existing = selectedDishes.find(d => d.itemId === itemId);
  
    if (!existing && delta > 0) {
      setSelectedDishes([...selectedDishes, {
        itemId,
        itemName: dish.name,
        price: String(dish.price),
        quantity: 1,
        totalPrice: String(dish.price),
      }]);
    } else if (existing) {
      const updatedQty = existing.quantity + delta;
      if (updatedQty <= 0) {
        setSelectedDishes(selectedDishes.filter(d => d.itemId !== itemId));
      } else {
        setSelectedDishes(selectedDishes.map(d =>
          d.itemId === itemId
            ? {
                ...d,
                quantity: updatedQty,
                totalPrice: String(updatedQty * parseFloat(d.price)),
              }
            : d
        ));
      }
    }
  };
  

  const openDishModal = async (restaurant) => {
    try {
      const cartRes = await axios.get('http://localhost:8085/cart/mycart', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
  
      const cartData = cartRes.data.data;
      if (
        cartData
      ) {
        alert('Cart is already available. Please order it or remove the cart then try.');
        return;
      }
  
      setCartRestaurantUserName(cartData?.restaurantUserName || restaurant.userName);
      setSelectedDishes([]); // Reset dish selection
      setSelectedRestaurant(restaurant);
      setShowDishModal(true);
    } catch (err) {
      console.error('Error loading cart', err);
      alert('Failed to validate cart.');
    }
  };

  const handleSaveCartAndClose = async () => {
    if (selectedDishes.length === 0) {
      alert('No dishes selected.');
      return;
    }
  
    const cartPayload = {
      customerUserName: user?.username,
      restaurantUserName: selectedRestaurant.userName,
      restaurantName: selectedRestaurant.name,
      dishes: selectedDishes,
    };
  
    try {
      const res = await axios.post('http://localhost:8085/cart/save', cartPayload, {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      alert(res.data.message);
      setShowDishModal(false);
    } catch (err) {
      console.error('Cart save failed', err);
      alert('Failed to save cart.');
    }
  };
  
  

  return (
    <Layout>
      <div className="customer-tabs">
        <button onClick={() => setActiveTab('restaurants')} className={activeTab === 'restaurants' ? 'active' : ''}>Restaurants</button>
        <button onClick={() => setActiveTab('cart')}>My Cart</button>
        <button onClick={() => setActiveTab('orders')}>My Orders</button>
        <button onClick={() => setActiveTab('notdelivered')}>Not Delivered</button>
        <button onClick={() => setActiveTab('track')}>Track Order</button>
        <button onClick={() => setActiveTab('profile')}>My Profile</button>
      </div>

      <div className="customer-content">
        {activeTab === 'restaurants' && (
          <div>
            <h3>Available Restaurants</h3>
            {loading ? (
              <p>Loading...</p>
            ) : (
              <div className="restaurant-grid">
                {restaurants.map((res, index) => (
                  <div key={index} className="restaurant-card">
                    <h4>{res.name}</h4>
                    <p><strong>Cuisine:</strong> {res.cuisine}</p>
                    <p><strong>Address:</strong> {res.address.street}, {res.address.city}</p>
                    <p><strong>Phone:</strong> {res.phone}</p>
                    <p><strong>Avg Price:</strong> ₹{res.averagePricePerPerson}</p>
                    <button className="add-btn" onClick={() => openDishModal(res)}>Add to Cart</button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'cart' && (
          <div>
          <h3>My Cart</h3>
          {cartLoading ? (
            <p>Loading cart...</p>
          ) : cart && cart.dishes && cart.dishes.length > 0 ? (
            <div>
              <p><strong>Restaurant:</strong> {cart.restaurantName}</p>
              <table style={{ width: '100%', marginTop: '15px', borderCollapse: 'collapse' }}>
                <thead>
                  <tr>
                    <th style={{ borderBottom: '1px solid #ccc' }}>Item</th>
                    <th style={{ borderBottom: '1px solid #ccc' }}>Price</th>
                    <th style={{ borderBottom: '1px solid #ccc' }}>Quantity</th>
                    <th style={{ borderBottom: '1px solid #ccc' }}>Total</th>
                    <th style={{ borderBottom: '1px solid #ccc' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {cart.dishes.map((dish, index) => (
                    <tr key={index}>
                      <td>{dish.itemName}</td>
                      <td>₹{dish.price}</td>
                      <td>{dish.quantity}</td>
                      <td>₹{dish.totalPrice}</td>
                      <td>
                        <button
                          className="delete-btn"
                          onClick={() => handleDeleteCartItem(dish.itemId)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
        
              <div style={{ marginTop: '20px' }}>
                <button className="add-btn" onClick={handlePlaceOrder}>Place Order</button>
              </div>
            </div>
          ) : (
            <p>Your cart is empty.</p>
          )}
        </div>
        )}  

        {activeTab !== 'restaurants' && activeTab !== 'cart' && (
          <div>
            <p>Coming soon: {activeTab}</p>
          </div>
        )}

        {showDishModal && selectedRestaurant && (
          <div className="modal-overlay">
            <div className="modal-box large">
              <h3>{selectedRestaurant.name} - Dishes</h3>
              {selectedRestaurant.dishes && selectedRestaurant.dishes.length > 0 ? (
                <ul className="dish-list">
                  {selectedRestaurant.dishes.map(dish => (
                    <li key={dish.id} className="dish-item">
                      <strong>{dish.name}</strong>: {dish.description} - ₹{dish.price}
                      <div className="quantity-controls">
                        <button onClick={() => updateDishQty(dish, -1)}>-</button>
                        <span>{getDishQty(dish)}</span>
                        <button onClick={() => updateDishQty(dish, 1)}>+</button>
                      </div>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No dishes available for this restaurant.</p>
              )}
              <div className="modal-buttons">
              <button className="add-btn" onClick={handleSaveCartAndClose}>Save</button>
              <button className="delete-btn" onClick={() => setShowDishModal(false)}>Close</button>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
};

export default CustomerPage;
