import { Routes, Route } from 'react-router-dom';
import Login from '../components/Login';
import Signup from '../components/Signup';
import AdminPage from '../components/AdminPage';
import CustomerPage from '../components/CustomerPage';
import RestaurantPage from '../components/RestaurantPage';
import DeliveryPartnerPage from '../components/DeliveryPartnerPage';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/admin" element={<AdminPage />} />
      <Route path="/customer" element={<CustomerPage />} />
      <Route path="/restaurant" element={<RestaurantPage />} />
      <Route path="/delivery" element={<DeliveryPartnerPage />} />
    </Routes>
  );
};

export default AppRoutes;
