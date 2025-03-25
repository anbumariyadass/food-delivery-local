import { useEffect, useState } from 'react';
import axios from 'axios';
import LogoutButton from './LogoutButton';
import '../styles/AdminPage.css';
import Layout from './Layout';

const AdminPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const [activeTab, setActiveTab] = useState('restaurants');

  const [restaurants, setRestaurants] = useState([]);
  const [users, setUsers] = useState([]);
  const [deliveryPartners, setDeliveryPartners] = useState([]);
  const [notification, setNotification] = useState({ subject: '', content: '' });
  const [subscriberEmail, setSubscriberEmail] = useState('');
  const [error, setError] = useState('');

  const [showAddModal, setShowAddModal] = useState(false);
  const [newUser, setNewUser] = useState({ username: '', password: '', role: 'RESTAURANT' });


  const fetchRestaurants = async () => {
    try {
      const response = await axios.get('http://localhost:8083/restaurant/all', {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      });
      setRestaurants(response.data.data);
    } catch (err) {
      setError('Failed to load restaurants.');
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:8081/identity/allUsers', {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      });
      setUsers(response.data.data);
    } catch (err) {
      setError('Failed to load users.');
    }
  };

  const fetchDeliveryPartners = async () => {
    try {
      const response = await axios.get('http://localhost:8087/delivery/getAllDeliveryPartners', {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      });
      setDeliveryPartners(response.data.data);
    } catch (err) {
      setError('Failed to load delivery partners.');
    }
  };

  const handleSendNotification = async () => {
    if (!notification.subject || !notification.content) {
      alert('Subject and Content are required');
      return;
    }
  
    try {
      const response = await axios.post(
        'http://localhost:8088/sqssns/notification/publishMessage',
        notification,
        {
          headers: {
            Authorization: `Bearer ${user?.token}`,
          },
        }
      );
  
      alert('Notification sent successfully!');
      setNotification({ subject: '', content: '' }); // Clear the form
    } catch (err) {
      console.error(err);
      alert('Failed to send notification');
    }
  };

  const handleAddSubscriber = async () => {
    if (!subscriberEmail) {
      alert('Please enter a valid email');
      return;
    }
  
    try {
      const response = await axios.post(
        `http://localhost:8088/sqssns/notification/addSubscription/${subscriberEmail}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${user?.token}`,
          },
        }
      );
  
      alert(`Subscription successful for ${subscriberEmail}`);
      setSubscriberEmail('');
    } catch (err) {
      console.error(err);
      alert('Failed to subscribe. Please try again.');
    }
  };
  

  const handleAddUser = async () => {
    if (!newUser.username || !newUser.password || !newUser.role) {
      alert('All fields are required.');
      return;
    }
  
    try {
      const response = await axios.post(
        'http://localhost:8081/identity/register',
        {
          username: newUser.username,
          password: newUser.password,
          role: newUser.role,
          active: 'Y',
        },
        {
          headers: {
            Authorization: `Bearer ${user?.token}`,
          },
        }
      );
  
      alert(response.data.message);
      setShowAddModal(false);
      setNewUser({ username: '', password: '', role: 'RESTAURANT' });
      fetchUsers();
    } catch (error) {
      console.error(error);
      alert('Error adding user');
    }
  };
  

  const handleDeleteUser = async (username) => {
    const confirmDelete = window.confirm(`Are you sure you want to delete user "${username}"?`);
    if (!confirmDelete) return;
  
    try {
      await axios.delete(`http://localhost:8081/identity/deleteUser`, {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
        params: {
          userName: username,
        },
      });
  
      alert(`User "${username}" deleted successfully.`);
      fetchUsers(); // Refresh list after deletion
    } catch (err) {
      console.error(err);
      alert('Error deleting user. Please try again.');
    }
  };
  
  

  useEffect(() => {
    // Load default tab content
    if (activeTab === 'restaurants') fetchRestaurants();
    if (activeTab === 'users') fetchUsers();
    if (activeTab === 'delivery') fetchDeliveryPartners();
  }, [activeTab]);

  return (
    <Layout>
    <div className="admin-container">
        <div className="tab-buttons">
          <button onClick={() => setActiveTab('users')} className={activeTab === 'users' ? 'active' : ''}>Users</button>
          <button onClick={() => setActiveTab('restaurants')} className={activeTab === 'restaurants' ? 'active' : ''}>Restaurants</button>
          <button onClick={() => setActiveTab('delivery')} className={activeTab === 'delivery' ? 'active' : ''}>Delivery Partners</button>
          <button onClick={() => setActiveTab('notifications')} className={activeTab === 'notifications' ? 'active' : ''}>Notifications</button>
        </div>

        {error && <p style={{ color: 'red' }}>{error}</p>}

        {activeTab === 'users' && (
          <div className="data-section">
            <div className="user-header">
              <h3>All Users</h3>
              <button className="add-btn" onClick={() => setShowAddModal(true)}>
                ➕ Add User
              </button>
            </div>

            <table className="user-table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Role</th>
                  <th>Active</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u, idx) => (
                  <tr key={idx}>
                    <td>{u.username}</td>
                    <td>{u.role}</td>
                    <td>{u.active}</td>
                    <td>                                            
                      <button className="delete-btn" onClick={() => handleDeleteUser(u.username)}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}


        {activeTab === 'restaurants' && (
          <div className="data-section">
            <h3>All Restaurants</h3>
            {restaurants.map((res) => (
              <div key={res.id} className="restaurant-card">
                <h4>{res.name}</h4>
                <p><strong>Owner:</strong> {res.userName}</p>
                <p><strong>Address:</strong> {res.address.street}, {res.address.city}</p>
                <p><strong>Cuisine:</strong> {res.cuisine} | Rating: {res.rating}</p>
                <h5>Dishes:</h5>
                <ul>
                  {res.dishes.map((dish) => (
                    <li key={dish.id}>🍽 {dish.name} - ₹{dish.price}</li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        )}

        {activeTab === 'delivery' && (
          <div className="data-section">
            <h3>All Delivery Partners</h3>
            <table className="user-table">
              <thead>
                <tr>
                  <th>Delivery Partner Name</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Address</th>
                </tr>
              </thead>
              <tbody>
                {deliveryPartners.map((dp, idx) => (
                  <tr key={idx}>
                    <td>{dp.deliveryPartnerName}</td>
                    <td>{dp.email}</td>
                    <td>{dp.phone}</td>
                    <td>{dp.address}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}


        {activeTab === 'notifications' && (          
          <div className="data-section">
            <h3>Send Notification</h3>
            <input
              type="text"
              placeholder="Subject"
              value={notification.subject}
              onChange={(e) => setNotification({ ...notification, subject: e.target.value })}
            />
            <textarea
              rows={6}
              placeholder="Message Content"
              value={notification.content}
              onChange={(e) => setNotification({ ...notification, content: e.target.value })}
            />
            <br />
            <button className="add-btn" onClick={handleSendNotification}>Send Notification</button>

            {/* 🔽 Add this block below the above notification form */}
            <div style={{ marginTop: '30px' }}>
              <h4>Add Email Subscriber</h4>
              <input
                type="email"
                placeholder="Enter email address"
                value={subscriberEmail}
                onChange={(e) => setSubscriberEmail(e.target.value)}
                style={{ width: '100%', padding: '8px', marginBottom: '10px' }}
              />
              <button className="add-btn" onClick={handleAddSubscriber}>
                Add Subscription
              </button>
            </div>
          </div>
        )}


      </div>

      {showAddModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>Add New User</h3>

            <input
              type="text"
              placeholder="Username"
              value={newUser.username}
              onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
            />
            <input
              type="password"
              placeholder="Password"
              value={newUser.password}
              onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
            />
            <select
              value={newUser.role}
              onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
            >
              <option value="RESTAURANT">RESTAURANT</option>
              <option value="DELIVERY_PARTNER">DELIVERY_PARTNER</option>
            </select>

            <div className="modal-buttons">
              <button className="add-btn" onClick={handleAddUser}>Submit</button>
              <button className="delete-btn" onClick={() => setShowAddModal(false)}>Close</button>
            </div>
          </div>
        </div>
      )}
 

      </Layout>
  );
};

export default AdminPage;
