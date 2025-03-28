import { useEffect, useState } from 'react';
import Layout from './Layout';
import axios from 'axios';
import '../styles/RestaurantPage.css';

const RestaurantPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const [activeTab, setActiveTab] = useState('info');

  const [restaurantInfo, setRestaurantInfo] = useState(null);
  const [orders, setOrders] = useState([]);
  const [notDeliveredOrders, setNotDeliveredOrders] = useState([]);

  const [showEditModal, setShowEditModal] = useState(false);
  const [formData, setFormData] = useState(null); // Will hold editable data

  const [showDishModal, setShowDishModal] = useState(false);
  const [dishesData, setDishesData] = useState([]);

  const [expandedOrders, setExpandedOrders] = useState({});
  const [orderStatusFilter, setOrderStatusFilter] = useState('ALL');

  const tokenHeader = {
    headers: {
      Authorization: `Bearer ${user?.token}`
    }
  };

  const toggleOrderDetails = (orderId) => {
    setExpandedOrders((prev) => ({
      ...prev,
      [orderId]: !prev[orderId],
    }));
  };
  

  const openEditModal = () => {
    if (restaurantInfo) {
      // Pre-fill with existing data
      setFormData(restaurantInfo);
    } else {
      // Empty form for new addition
      setFormData({
        name: '',
        phone: '',
        cuisine: '',
        averagePricePerPerson: 0,
        address: {
          street: '',
          city: '',
          state: '',
          zipCode: '',
        },
        rating: 0,
        openingHours: {
          Monday: '',
          Tuesday: '',
          Wednesday: '',
          Thursday: '',
          Friday: '',
          Saturday: '',
          Sunday: '',
        },
        deliveryOptions: {
          takeaway: false,
          homeDelivery: false,
        }
      });
    }
    setShowEditModal(true);
  };

  const openDishModal = () => {
    if (restaurantInfo?.dishes) {
      // Clone to avoid direct mutation
      setDishesData([...restaurantInfo.dishes]);
      setShowDishModal(true);
    }
  };
  
  
  
  const handleUpdateRestaurant = async () => {
    try {      
      await axios.put('http://localhost:8083/restaurant/update', formData, {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      });
  
      alert('Restaurant info updated successfully!');
      setShowEditModal(false);
  
      // Refresh after update
      const response = await axios.get('http://localhost:8083/restaurant/myDetail', tokenHeader);
      setRestaurantInfo(response.data.data);
    } catch (err) {
      console.error(err);
      alert('Failed to update restaurant info.');
    }
  };

  const handleSaveDishes = async () => {
    try {
      const response = await axios.put('http://localhost:8083/restaurant/dishes', dishesData, {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      });
  
      alert(response.data.message);
      setShowDishModal(false);
  
      // Refresh restaurant info
      const updated = await axios.get('http://localhost:8083/restaurant/myDetail', tokenHeader);
      setRestaurantInfo(updated.data.data);
    } catch (err) {
      console.error(err);
      alert('Failed to update dishes.');
    }
  };
  
  

  useEffect(() => {
    if (activeTab === 'info') {
      axios.get('http://localhost:8083/restaurant/myDetail', tokenHeader)
        .then(res => setRestaurantInfo(res.data.data))
        .catch(err => console.error(err));
    }

    if (activeTab === 'orders') {
      axios.get('http://localhost:8086/order/restaurant', tokenHeader)
        .then(res => setOrders(res.data.data))
        .catch(err => console.error(err));
    }

    if (activeTab === 'notdelivered') {
      axios.get('http://localhost:8086/order/restaurant/notdelivered', tokenHeader)
        .then(res => setNotDeliveredOrders(res.data.data))
        .catch(err => console.error(err));
    }
  }, [activeTab]);

  return (
    <Layout>
      <div className="restaurant-tabs">
        <button onClick={() => setActiveTab('info')} className={activeTab === 'info' ? 'active' : ''}>My Restaurant Info</button>
        <button onClick={() => setActiveTab('orders')} className={activeTab === 'orders' ? 'active' : ''}>All Orders</button>
        <button onClick={() => setActiveTab('notdelivered')} className={activeTab === 'notdelivered' ? 'active' : ''}>Not Delivered Orders</button>
      </div>

      <div className="restaurant-content">
          {activeTab === 'info' && (
          <>
            {restaurantInfo ? (
              <div>
                <button className="edit-btn" onClick={openEditModal}>Edit Info</button>
                <button className="edit-btn" onClick={openDishModal}>Manage Dishes</button>
                <h3>{restaurantInfo.name}</h3>
                <p><strong>Cuisine:</strong> {restaurantInfo.cuisine}</p>
                <p><strong>Phone:</strong> {restaurantInfo.phone}</p>
                <p><strong>Address:</strong> {restaurantInfo.address.street}, {restaurantInfo.address.city}, {restaurantInfo.address.state} - {restaurantInfo.address.zipCode}</p>
                <p><strong>Average Price Per Person:</strong> ₹{restaurantInfo.averagePricePerPerson}</p>
                <p><strong>Delivery Options:</strong> {restaurantInfo.deliveryOptions.takeaway ? 'Takeaway' : ''}, {restaurantInfo.deliveryOptions.homeDelivery ? 'Home Delivery' : ''}</p>
                <h4>Dishes:</h4>
                <ul>
                  {restaurantInfo.dishes.map(dish => (
                    <li key={dish.id}>{dish.name} – ₹{dish.price}</li>
                  ))}
                </ul>
              </div>
            ) : (
              <div>
                <p>No restaurant information found.</p>
                <button className="add-btn" onClick={openEditModal}>➕ Add Restaurant Info</button>
              </div>
            )}
          </>
        )}

        {activeTab === 'orders' && (
          <div>
            <h3>All Orders</h3>

            <div style={{ marginBottom: '10px' }}>
              <label style={{ marginRight: '8px' }}>Filter by Status:</label>
              <select value={orderStatusFilter} onChange={(e) => setOrderStatusFilter(e.target.value)}>
                <option value="ALL">All</option>
                <option value="ORDERED">ORDERED</option>
                <option value="ASSIGNED">ASSIGNED</option>
                <option value="INTRANSIT">INTRANSIT</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="ABANDONED">ABANDONED</option>
              </select>
            </div>

            {orders
            .filter(order => orderStatusFilter === 'ALL' || order.orderStatus === orderStatusFilter)
            .map(order => (
              <div key={order.orderId} className="order-box">
                <div className="order-summary">
                  <p><strong>Order ID:</strong> {order.orderId} | <strong>Status:</strong> {order.orderStatus}</p>
                  <button className="toggle-btn" onClick={() => toggleOrderDetails(order.orderId)}>
                    {expandedOrders[order.orderId] ? 'Hide Details ▲' : 'Show Details ▼'}
                  </button>
                </div>

                {expandedOrders[order.orderId] && (
                  <div className="order-details">
                    <p><strong>Customer:</strong> {order.customerUserName}</p>
                    <p><strong>Delivery Partner:</strong> {order.dlvryPartnerName}</p>
                    <p><strong>Total:</strong> ₹{order.totalPrice}</p>                    
                    <table className="items-table">
                      <thead>
                        <tr>
                          <th>Item Name</th>
                          <th>Quantity</th>
                          <th>Price</th>
                        </tr>
                      </thead>
                      <tbody>
                        {order.orderDetails.map(item => (
                          <tr key={item.orderDtlId}>
                            <td>{item.itemName}</td>
                            <td>{item.quantity}</td>
                            <td>₹{item.totalPrice}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}


        {activeTab === 'notdelivered' && (
          <div>
            <h3>Not Delivered Orders</h3>

            <div style={{ marginBottom: '10px' }}>
              <label style={{ marginRight: '8px' }}>Filter by Status:</label>
              <select value={orderStatusFilter} onChange={(e) => setOrderStatusFilter(e.target.value)}>
                <option value="ALL">All</option>
                <option value="ORDERED">ORDERED</option>
                <option value="ASSIGNED">ASSIGNED</option>
                <option value="INTRANSIT">INTRANSIT</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="ABANDONED">ABANDONED</option>
              </select>
            </div>
            
            {notDeliveredOrders
                .filter(order => orderStatusFilter === 'ALL' || order.orderStatus === orderStatusFilter)
                .map(order => (
              <div key={order.orderId} className="order-box">
              <div className="order-summary">
                <p><strong>Order ID:</strong> {order.orderId} | <strong>Status:</strong> {order.orderStatus}</p>
                <button className="toggle-btn" onClick={() => toggleOrderDetails(order.orderId)}>
                  {expandedOrders[order.orderId] ? 'Hide Details ▲' : 'Show Details ▼'}
                </button>
              </div>
            
              {expandedOrders[order.orderId] && (
                <div className="order-details">
                  <p><strong>Customer:</strong> {order.customerUserName}</p>
                  <p><strong>Delivery Partner:</strong> {order.dlvryPartnerName}</p>
                  <p><strong>Total:</strong> ₹{order.totalPrice}</p>
                  <h5>Items:</h5>
                    <table className="items-table">
                      <thead>
                        <tr>
                          <th>Item Name</th>
                          <th>Quantity</th>
                          <th>Price</th>
                        </tr>
                      </thead>
                      <tbody>
                        {order.orderDetails.map(item => (
                          <tr key={item.orderDtlId}>
                            <td>{item.itemName}</td>
                            <td>{item.quantity}</td>
                            <td>₹{item.totalPrice}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                </div>
              )}
            </div>            
            ))}
          </div>
        )}
      </div>

      {showEditModal && (
        <div className="modal-overlay">
          <div className="modal-box large">
            <h3>Edit Restaurant Info</h3>

            <input type="text" placeholder="Name" value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })} />
            <input type="text" placeholder="Phone" value={formData.phone} onChange={(e) => setFormData({ ...formData, phone: e.target.value })} />
            <input type="text" placeholder="Cuisine" value={formData.cuisine} onChange={(e) => setFormData({ ...formData, cuisine: e.target.value })} />
            <input type="number" placeholder="Average Price" value={formData.averagePricePerPerson} onChange={(e) => setFormData({ ...formData, averagePricePerPerson: parseInt(e.target.value) })} />

            <h4>Address</h4>
            <input type="text" placeholder="Street" value={formData.address.street} onChange={(e) => setFormData({ ...formData, address: { ...formData.address, street: e.target.value } })} />
            <input type="text" placeholder="City" value={formData.address.city} onChange={(e) => setFormData({ ...formData, address: { ...formData.address, city: e.target.value } })} />
            <input type="text" placeholder="State" value={formData.address.state} onChange={(e) => setFormData({ ...formData, address: { ...formData.address, state: e.target.value } })} />
            <input type="text" placeholder="Zip Code" value={formData.address.zipCode} onChange={(e) => setFormData({ ...formData, address: { ...formData.address, zipCode: e.target.value } })} />

            <h4>Opening Hours</h4>
            {Object.keys(formData.openingHours).map(day => (
              <input
                key={day}
                type="text"
                placeholder={day}
                value={formData.openingHours[day]}
                onChange={(e) => setFormData({
                  ...formData,
                  openingHours: { ...formData.openingHours, [day]: e.target.value }
                })}
              />
            ))}

            <h4>Delivery Options</h4>
            <label>
              <input type="checkbox" checked={formData.deliveryOptions.takeaway} onChange={(e) => setFormData({ ...formData, deliveryOptions: { ...formData.deliveryOptions, takeaway: e.target.checked } })} />
              Takeaway
            </label>
            <label>
              <input type="checkbox" checked={formData.deliveryOptions.homeDelivery} onChange={(e) => setFormData({ ...formData, deliveryOptions: { ...formData.deliveryOptions, homeDelivery: e.target.checked } })} />
              Home Delivery
            </label>

            <div className="modal-buttons">
              <button className="add-btn" onClick={handleUpdateRestaurant}>Save</button>
              <button className="delete-btn" onClick={() => setShowEditModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {showDishModal && (
        <div className="modal-overlay">
          <div className="modal-box large">
            <h3>Manage Dishes</h3>

            {dishesData.map((dish, index) => (
              <div key={index} className="dish-row">
                <input
                  type="text"
                  placeholder="Name"
                  value={dish.name}
                  onChange={(e) =>
                    setDishesData(dishesData.map((d, i) => i === index ? { ...d, name: e.target.value } : d))
                  }
                />
                <input
                  type="text"
                  placeholder="Description"
                  value={dish.description}
                  onChange={(e) =>
                    setDishesData(dishesData.map((d, i) => i === index ? { ...d, description: e.target.value } : d))
                  }
                />
                <input
                  type="number"
                  placeholder="Price"
                  value={dish.price}
                  onChange={(e) =>
                    setDishesData(dishesData.map((d, i) => i === index ? { ...d, price: parseFloat(e.target.value) } : d))
                  }
                />
                <button
                  className="delete-btn"
                  onClick={() => setDishesData(dishesData.filter((_, i) => i !== index))}
                >
                  ❌
                </button>
              </div>
            ))}

            <button className="add-btn" onClick={() => setDishesData([...dishesData, { name: '', description: '', price: 0 }])}>
              ➕ Add Dish
            </button>

            <div className="modal-buttons">
              <button className="add-btn" onClick={handleSaveDishes}>Save</button>
              <button className="delete-btn" onClick={() => setShowDishModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}



    </Layout>
  );
};

export default RestaurantPage;

