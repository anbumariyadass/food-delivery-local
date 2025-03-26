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

  useEffect(() => {
    if (activeTab === 'restaurants') {
      fetchRestaurants();
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

        {activeTab !== 'restaurants' && (
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
