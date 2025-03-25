import LogoutButton from './LogoutButton';

const CustomerPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));

  return (
    <div>
      <h2>Hello Customer</h2>
      <p>Your username is <strong>{user?.username}</strong></p>
      <LogoutButton />
    </div>
  );
};

export default CustomerPage;
