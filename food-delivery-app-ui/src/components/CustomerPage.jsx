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

  const openDishModal = (restaurant) => {
    setSelectedRestaurant(restaurant);
    setShowDishModal(true);
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
                      <button className="add-btn small">Add</button>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No dishes available for this restaurant.</p>
              )}
              <div className="modal-buttons">
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
