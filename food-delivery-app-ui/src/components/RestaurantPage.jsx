import LogoutButton from './LogoutButton';

const RestaurantPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));

  return (
    <div>
      <h2>Hello Restaurant</h2>
      <p>Your username is <strong>{user?.username}</strong></p>
      <LogoutButton />
    </div>
  );
};

export default RestaurantPage;
