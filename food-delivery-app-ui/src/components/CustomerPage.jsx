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

  const [showOrderModal, setShowOrderModal] = useState(false);
  const [deliveryPartners, setDeliveryPartners] = useState([]);
  const [contactInfo, setContactInfo] = useState({
    contactName: '',
    contactAddress: '',
    contactEmail: '',
    contactPhone: '',
  });
  const [selectedPartner, setSelectedPartner] = useState('');




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

  const handleClearCart = async () => {
    try {
      await axios.delete('http://localhost:8085/cart/mycart', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      setCart(null);
      alert('Cart cleared successfully!');
    } catch (err) {
      console.error('Failed to clear cart:', err);
      alert('Could not clear cart.');
    }
  };
  

  const handleDeleteCartItem = async (itemId) => {
    const updatedDishes = cart.dishes.filter(d => d.itemId !== itemId);
  
    if (updatedDishes.length === 0) {
      // No items left – delete entire cart
      handleClearCart();
      return;
    }
  
    const updatedCart = { ...cart, dishes: updatedDishes };
  
    try {
      const res = await axios.post('http://localhost:8085/cart/save', updatedCart, {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      alert('Item removed from cart.');
      fetchCart();
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
  
    handlePlaceOrderModalOpen(); // Fetch delivery partners + customer info and show modal
  };

  const handlePlaceOrderModalOpen = async () => {
    setShowOrderModal(true);
  
    try {
      const partnerRes = await axios.get('http://localhost:8087/delivery/getAllDeliveryPartners', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      setDeliveryPartners(partnerRes.data.data);
  
      const userRes = await axios.get('http://localhost:8084/customer/get', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      const info = userRes.data.data;
      setContactInfo({
        contactName: info.customerName,
        contactAddress: info.address,
        contactEmail: info.email,
        contactPhone: info.phone,
      });
    } catch (err) {
      console.error('Failed to fetch info', err);
    }
  };

  const handleSubmitOrder = async () => {
    if (!selectedPartner || !contactInfo.contactName || !contactInfo.contactAddress || !contactInfo.contactPhone) {
      alert('Please fill all mandatory fields.');
      return;
    }
  
    const selected = deliveryPartners.find(p => p.userName === selectedPartner);
  
    const orderPayload = {
      restaurantUserName: cart.restaurantUserName,
      restaurantName: cart.restaurantName,
      dlvryPartnerUserName: selected.userName,
      dlvryPartnerName: selected.deliveryPartnerName,
      orderStatus: "Pending",
      totalPrice: cart.dishes.reduce((sum, d) => sum + parseFloat(d.totalPrice), 0),
      contactName: contactInfo.contactName,
      contactAddress: contactInfo.contactAddress,
      contactEmail: contactInfo.contactEmail,
      contactPhone: contactInfo.contactPhone,
      orderDetails: cart.dishes.map(d => ({
        itemName: d.itemName,
        quantity: d.quantity,
        price: parseFloat(d.price),
        totalPrice: parseFloat(d.totalPrice),
      })),
    };
  
    try {
      const res = await axios.post('http://localhost:8086/order/create', orderPayload, {
        headers: { Authorization: `Bearer ${user?.token}` },
      });

      // Clear the cart via DELETE API
      await axios.delete('http://localhost:8085/cart/mycart', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      alert(`Order placed successfully! Order ID: ${res.data.data.orderId}`);
      setShowOrderModal(false);
      setCart(null);
    } catch (err) {
      console.error('Order creation failed:', err);
      alert('Order placement failed.');
    }
  };
  
  
  
  const handleQtyInputChange = (dish, value) => {
    if (/^\d*$/.test(value)) {
      const parsedValue = value === '' ? '' : parseInt(value);
      setSelectedRestaurant((prev) => ({
        ...prev,
        dishes: prev.dishes.map(d =>
          d.id === dish.id ? { ...d, quantity: parsedValue } : d
        ),
      }));
    }
  };

  const validateDishQty = (dish) => {
    setSelectedRestaurant((prev) => ({
      ...prev,
      dishes: prev.dishes.map(d =>
        d.id === dish.id
          ? { ...d, quantity: isNaN(d.quantity) || d.quantity < 0 ? 0 : d.quantity }
          : d
      ),
    }));
  };
  
  
  

  const getDishQty = (dish) => {
    const item = selectedDishes.find(d => d.itemId === String(dish.id));
    return item && !isNaN(item.quantity) ? item.quantity : 0;
  };
  

  const updateDishQty = (dish, newQtyOrDelta, isDirect = false) => {
    const itemId = String(dish.id);
    const existing = selectedDishes.find(d => d.itemId === itemId);
  
    if (isDirect) {
      const qty = Number(newQtyOrDelta);
      if (isNaN(qty) || qty < 0) return; // Reject invalid or negative input
  
      if (qty === 0) {
        setSelectedDishes(selectedDishes.filter(d => d.itemId !== itemId));
      } else if (existing) {
        setSelectedDishes(selectedDishes.map(d =>
          d.itemId === itemId
            ? {
                ...d,
                quantity: qty,
                totalPrice: String(qty * parseFloat(d.price)),
              }
            : d
        ));
      } else {
        setSelectedDishes([...selectedDishes, {
          itemId,
          itemName: dish.name,
          price: String(dish.price),
          quantity: qty,
          totalPrice: String(qty * parseFloat(dish.price)),
        }]);
      }
    } else {
      // Handle + / - buttons
      if (!existing && newQtyOrDelta > 0) {
        setSelectedDishes([...selectedDishes, {
          itemId,
          itemName: dish.name,
          price: String(dish.price),
          quantity: 1,
          totalPrice: String(dish.price),
        }]);
      } else if (existing) {
        const updatedQty = existing.quantity + newQtyOrDelta;
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

  const handleDeleteCart = async () => {
    if (!window.confirm('Are you sure you want to delete the entire cart?')) return;
  
    try {
      await axios.delete('http://localhost:8085/cart/mycart', {
        headers: { Authorization: `Bearer ${user?.token}` },
      });
      alert('Cart deleted successfully.');
      setCart(null);
    } catch (err) {
      console.error('Error deleting cart:', err);
      alert('Failed to delete cart.');
    }
  };
  
  const handleQuantityChange = (itemId, value) => {
    const qty = parseInt(value, 10);
  
    // Block non-numeric, empty, or 0 values
    if (isNaN(qty) || qty < 1) return;
  
    const updatedDishes = cart.dishes.map(dish =>
      dish.itemId === itemId
        ? {
            ...dish,
            quantity: qty,
            totalPrice: String(qty * parseFloat(dish.price)),
          }
        : dish
    );
  
    const updatedCart = {
      ...cart,
      dishes: updatedDishes,
    };
  
    setCart(updatedCart);
  };
  
  const resetToSavedCart = async () => {
    try {
      const response = await axios.get('http://localhost:8085/cart/mycart', {
        headers: {
          Authorization: `Bearer ${user?.token}`
        }
      });
      setCart(response.data.data); // Update state with saved cart
    } catch (error) {
      console.error('Failed to reset cart:', error);
      alert('Unable to fetch saved cart.');
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
              <table className="cart-table">
              <thead>
                <tr>
                  <th>Item</th>
                  <th>Price</th>
                  <th>Quantity</th>
                  <th>Total</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {cart.dishes.map((dish, index) => (
                  <tr key={index}>
                    <td>{dish.itemName}</td>
                    <td>₹{dish.price}</td>
                    <td>
                      <input
                        type="number"
                        min="1"
                        value={dish.quantity}
                        onChange={(e) => handleQuantityChange(dish.itemId, e.target.value)}
                        className="quantity-input"
                      />
                    </td>
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
                <tr>
                  <td colSpan="3" style={{ textAlign: 'right', fontWeight: 'bold' }}>Grand Total</td>
                  <td colSpan="2" style={{ fontWeight: 'bold' }}>
                    ₹{cart.dishes.reduce((sum, d) => sum + parseFloat(d.totalPrice), 0)}
                  </td>
                </tr>
              </tbody>
            </table>

        
              <div style={{ marginTop: '20px' }}>
                <button className="add-btn" onClick={handlePlaceOrder}>Place Order</button>
                <button
                  className="delete-btn"
                  onClick={handleDeleteCart}
                  style={{ marginLeft: '10px', backgroundColor: '#dc3545' }}
                >
                  🗑 Delete Cart
                </button>
                <button className="reset-btn" onClick={resetToSavedCart}>Reset                 
                </button>
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
              <div className="dish-header">
                <div className="dish-name">Dish</div>
                <div className="dish-desc">Description</div>
                <div className="dish-price">Price</div>
                <div className="quantity-controls">Added Count</div>
              </div>

              {selectedRestaurant.dishes && selectedRestaurant.dishes.length > 0 ? (
                <ul className="dish-list">
                  {selectedRestaurant.dishes.map(dish => (
                    <li key={dish.id} className="dish-item">
                    <div className="dish-name">{dish.name}</div>
                    <div className="dish-desc">{dish.description}</div>
                    <div className="dish-price">₹{dish.price}</div>
                    <div className="quantity-controls">
                      <button onClick={() => updateDishQty(dish, -1)}>-</button>
                      <input
                        type="text"
                        className="quantity-input"
                        value={getDishQty(dish).toString()}
                        onChange={(e) => {
                          const value = e.target.value;
                          // Only allow digits
                          if (/^\d*$/.test(value)) {
                            // Remove leading 0s (but keep "0" if it's the only digit)
                            const clean = value.replace(/^0+(?!$)/, '') || '0';
                            updateDishQty(dish, clean, true);
                          }
                        }}
                      />
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

        {showOrderModal && (
          <div className="modal-overlay">
            <div className="modal-box large">
              <h3>Place Your Order</h3>
              <h4>Restaurant: {cart.restaurantName}</h4>
              <label>Delivery Partner (Required)</label>
              <select value={selectedPartner} onChange={e => setSelectedPartner(e.target.value)} required>
                <option value="">-- Select Delivery Partner --</option>
                {deliveryPartners.map(partner => (
                  <option key={partner.userName} value={partner.userName}>
                    {partner.deliveryPartnerName}
                  </option>
                ))}
              </select>

              <h4>Contact Info</h4>
              <input placeholder="Contact Name" value={contactInfo.contactName} onChange={e => setContactInfo({ ...contactInfo, contactName: e.target.value })} />
              <input placeholder="Contact Address" value={contactInfo.contactAddress} onChange={e => setContactInfo({ ...contactInfo, contactAddress: e.target.value })} />
              <input placeholder="Contact Email (optional)" value={contactInfo.contactEmail} onChange={e => setContactInfo({ ...contactInfo, contactEmail: e.target.value })} />
              <input placeholder="Contact Phone" value={contactInfo.contactPhone} onChange={e => setContactInfo({ ...contactInfo, contactPhone: e.target.value })} />

              <h4>Cart Summary</h4>
              <div className="cart-summary">
                <div className="cart-summary-header">
                  <div>Item</div>
                  <div>Qty</div>
                  <div>Price</div>
                  <div>Total</div>
                </div>
                {cart.dishes.map((dish, idx) => (
                  <div className="cart-summary-row" key={idx}>
                    <div>{dish.itemName}</div>
                    <div>{dish.quantity}</div>
                    <div>₹{dish.price}</div>
                    <div>₹{dish.totalPrice}</div>
                  </div>
                ))}
                <div className="cart-summary-total">
                  <strong>Grand Total:</strong> ₹{cart.dishes.reduce((sum, d) => sum + parseFloat(d.totalPrice), 0)}
                </div>
              </div>


              <p><strong>Total: ₹{cart.dishes.reduce((sum, d) => sum + parseFloat(d.totalPrice), 0)}</strong></p>

              <div className="modal-buttons">
                <button className="add-btn" onClick={handleSubmitOrder}>Place Order</button>
                <button className="delete-btn" onClick={() => setShowOrderModal(false)}>Cancel</button>
              </div>
            </div>
          </div>
        )}


      </div>
    </Layout>
  );
};

export default CustomerPage;
