import LogoutButton from './LogoutButton';

const DeliveryPartnerPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));

  return (
    <div>
      <h2>Hello Delivery Partner</h2>
      <p>Your username is <strong>{user?.username}</strong></p>
      <LogoutButton />
    </div>
  );
};

export default DeliveryPartnerPage;
