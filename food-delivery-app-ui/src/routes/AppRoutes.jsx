import { Routes, Route } from 'react-router-dom';
import Login from '../components/Login';
import Signup from '../components/Signup';
import AdminPage from '../components/AdminPage';
import CustomerPage from '../components/CustomerPage';
import RestaurantPage from '../components/RestaurantPage';
import DeliveryPartnerPage from '../components/DeliveryPartnerPage';
import DeliveryPersonalPage from '../components/DeliveryPersonalPage';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/admin" element={<AdminPage />} />
      <Route path="/customer" element={<CustomerPage />} />
      <Route path="/restaurant" element={<RestaurantPage />} />
      <Route path="/deliverypartner" element={<DeliveryPartnerPage />} />
      <Route path="/deliverypersonal" element={<DeliveryPersonalPage />} />
    </Routes>
  );
};

export default AppRoutes;
