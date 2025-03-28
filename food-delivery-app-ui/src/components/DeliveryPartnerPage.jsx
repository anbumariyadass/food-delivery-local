import { useEffect, useState } from 'react';
import Layout from './Layout';
import axios from 'axios';
import '../styles/RestaurantPage.css';

const DeliveryPartnerPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));
  const [activeTab, setActiveTab] = useState('info');
  const [info, setInfo] = useState(null);
  const [formData, setFormData] = useState({
    deliveryPartnerName: '',
    address: '',
    email: '',
    phone: ''
  });
  const [orders, setOrders] = useState([]);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [showModal, setShowModal] = useState(false);
  const [deliveryPersonals, setDeliveryPersonals] = useState([]);
  const [personalForm, setPersonalForm] = useState({
    userName: '',
    password: '',
    deliveryPersonalName: '',
    address: '',
    email: '',
    phone: '',
    gender: 'Male'
  });
  const [editingUserName, setEditingUserName] = useState(null);
  const [showPersonalModal, setShowPersonalModal] = useState(false);

  const tokenHeader = {
    headers: {
      Authorization: `Bearer ${user?.token}`
    }
  };

  const fetchInfo = () => {
    axios.get('http://localhost:8087/delivery/getDeliveryPartner', tokenHeader)
      .then(res => {
        setInfo(res.data.data);
        setFormData(res.data.data);
      })
      .catch(() => setInfo(null));
  };

  const fetchOrders = () => {
    axios.get('http://localhost:8087/delivery/myorders', tokenHeader)
      .then(res => setOrders(res.data.data))
      .catch(err => console.error(err));
  };

  const fetchDeliveryPersonals = () => {
    axios.get('http://localhost:8087/delivery/allDeliveryPersonal', tokenHeader)
      .then(res => setDeliveryPersonals(res.data.data))
      .catch(err => console.error(err));
  };

  const handleSave = () => {
    axios.post('http://localhost:8087/delivery/saveDlvryPartner', formData, tokenHeader)
      .then(() => {
        alert('Saved Successfully');
        fetchInfo();
        setShowModal(false);
      })
      .catch(err => {
        console.error(err);
        alert('Save failed');
      });
  };

  const updateOrderStatus = (orderId, newStatus) => {
    axios.put(`http://localhost:8087/delivery/${orderId}/updateOrderStatus?orderStatus=${newStatus}`, {}, tokenHeader)
      .then(() => fetchOrders())
      .catch(err => alert('Update Failed'));
  };

  const handleSavePersonal = () => {
    const savePersonal = () => {
      axios.post('http://localhost:8087/delivery/saveDlvryPersonal', personalForm, tokenHeader)
        .then(() => {
          alert('Personal saved successfully');
          fetchDeliveryPersonals();
          setShowPersonalModal(false);
        })
        .catch(() => alert('Save failed'));
    };
  
    if (!editingUserName) {
      const identityPayload = {
        username: personalForm.userName,
        password: personalForm.password,
        active: 'Y'
      };
  
      axios.post('http://localhost:8081/identity/register/deliveryPersonal', identityPayload, tokenHeader)
        .then(() => savePersonal())
        .catch(() => alert('User registration failed'));
    } else {
      savePersonal();
    }
  };

  const handleDeletePersonal = (userName) => {
    axios.delete(`http://localhost:8087/delivery/delete/deliveryPersonal/${userName}`, tokenHeader)
      .then(() => {
        alert('Deleted successfully');
        fetchDeliveryPersonals();
      })
      .catch(() => alert('Delete failed'));
  };

  useEffect(() => {
    if (activeTab === 'info') fetchInfo();
    if (activeTab === 'orders') fetchOrders();
    if (activeTab === 'personals') fetchDeliveryPersonals();
  }, [activeTab]);

  const filteredOrders = filterStatus === 'ALL' ? orders : orders.filter(o => o.orderStatus === filterStatus);

  return (
    <Layout>
      <div className="restaurant-tabs">
        <button onClick={() => setActiveTab('info')} className={activeTab === 'info' ? 'active' : ''}>My Info</button>
        <button onClick={() => setActiveTab('orders')} className={activeTab === 'orders' ? 'active' : ''}>All Orders</button>
        <button onClick={() => setActiveTab('personals')} className={activeTab === 'personals' ? 'active' : ''}>Manage Delivery Personals</button>
      </div>

      <div className="restaurant-content">
        {activeTab === 'info' && (
          <div>
            {info ? (
              <div>
                <button className="edit-btn" onClick={() => setShowModal(true)}>Edit Info</button>
                <p><strong>Name:</strong> {info.deliveryPartnerName}</p>
                <p><strong>Address:</strong> {info.address}</p>
                <p><strong>Email:</strong> {info.email}</p>
                <p><strong>Phone:</strong> {info.phone}</p>
              </div>
            ) : (
              <div>
                <p>No delivery partner info found.</p>
                <button className="add-btn" onClick={() => setShowModal(true)}>➕ Add Info</button>
              </div>
            )}
          </div>
        )}

        {activeTab === 'orders' && (
          <div>
            <h3>All Orders</h3>
            <label>Status Filter: </label>
            <select onChange={(e) => setFilterStatus(e.target.value)} value={filterStatus}>
              <option value="ALL">ALL</option>
              <option value="ORDERED">ORDERED</option>
              <option value="INTRANSIT">INTRANSIT</option>
              <option value="DELIVERED">DELIVERED</option>
              <option value="ABANDONED">ABANDONED</option>
            </select>
            {filteredOrders.map(order => (
              <div key={order.orderId} className="order-box">
                <p><strong>Order ID:</strong> {order.orderId}</p>
                <p><strong>Restaurant:</strong> {order.restaurantName}</p>
                <p><strong>Customer:</strong> {order.customerContactName}</p>
                <p><strong>Address:</strong> {order.customerContactAddress}</p>
                <p><strong>Status:</strong> {order.orderStatus}</p>
                <p><strong>Total:</strong> ₹{order.totalPrice}</p>
                {(order.orderStatus === 'ORDERED' || order.orderStatus === 'INTRANSIT') && (
                  <div>
                    {order.orderStatus !== 'INTRANSIT' && (
                      <button className="edit-btn" onClick={() => updateOrderStatus(order.orderId, 'INTRANSIT')}>Mark In-Transit</button>
                    )}
                    <button className="add-btn" onClick={() => updateOrderStatus(order.orderId, 'DELIVERED')}>Mark Delivered</button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {activeTab === 'personals' && (
          <div>
            <div className="header-row">
              <h3>Delivery Personals</h3>
              <button className="add-btn" onClick={() => {
                setEditingUserName(null);
                setPersonalForm({
                  userName: '', password: '', deliveryPersonalName: '', address: '', email: '', phone: '', gender: 'Male'
                });
                setShowPersonalModal(true);
              }}>+ Add</button>
            </div>
            <div style={{ overflowX: 'auto' }}>
              <table className="styled-table">
                <thead>
                  <tr>
                    <th>User Name</th>
                    <th>Name</th>
                    <th>Partner Username</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Gender</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {deliveryPersonals.map(personal => (
                    <tr key={personal.userName}>
                      <td>{personal.userName}</td>
                      <td>{personal.deliveryPersonalName}</td>
                      <td>{personal.deliveryPartnerUserName}</td>
                      <td>{personal.address}</td>
                      <td>{personal.email}</td>
                      <td>{personal.phone}</td>
                      <td>{personal.gender}</td>
                      <td>
                        <button className="edit-btn" onClick={() => {
                          setEditingUserName(personal.userName);
                          setPersonalForm({...personal, password: ''});
                          setShowPersonalModal(true);
                        }}>Edit</button>
                        <button className="delete-btn" onClick={() => handleDeletePersonal(personal.userName)}>Delete</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>{info ? 'Edit' : 'Add'} Delivery Partner Info</h3>
            <input type="text" placeholder="Name" value={formData.deliveryPartnerName} onChange={(e) => setFormData({ ...formData, deliveryPartnerName: e.target.value })} />
            <input type="text" placeholder="Address" value={formData.address} onChange={(e) => setFormData({ ...formData, address: e.target.value })} />
            <input type="email" placeholder="Email" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} />
            <input type="text" placeholder="Phone" value={formData.phone} onChange={(e) => setFormData({ ...formData, phone: e.target.value })} />
            <div className="modal-buttons">
              <button className="add-btn" onClick={handleSave}>Save</button>
              <button className="delete-btn" onClick={() => setShowModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

      {showPersonalModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>{editingUserName ? 'Edit' : 'Add'} Delivery Personal</h3>
            <input type="text" placeholder="Username" value={personalForm.userName} readOnly={!!editingUserName} onChange={(e) => setPersonalForm({ ...personalForm, userName: e.target.value })} />
            {!editingUserName && (
              <input type="password" placeholder="Password" value={personalForm.password} onChange={(e) => setPersonalForm({ ...personalForm, password: e.target.value })} />
            )}
            <input type="text" placeholder="Name" value={personalForm.deliveryPersonalName} onChange={(e) => setPersonalForm({ ...personalForm, deliveryPersonalName: e.target.value })} />
            <input type="text" placeholder="Address" value={personalForm.address} onChange={(e) => setPersonalForm({ ...personalForm, address: e.target.value })} />
            <input type="email" placeholder="Email" value={personalForm.email} onChange={(e) => setPersonalForm({ ...personalForm, email: e.target.value })} />
            <input type="text" placeholder="Phone" value={personalForm.phone} onChange={(e) => setPersonalForm({ ...personalForm, phone: e.target.value })} />
            <select value={personalForm.gender} onChange={(e) => setPersonalForm({ ...personalForm, gender: e.target.value })}>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
            <div className="modal-buttons">
              <button className="add-btn" onClick={handleSavePersonal}>Save</button>
              <button className="delete-btn" onClick={() => setShowPersonalModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
};

export default DeliveryPartnerPage;
