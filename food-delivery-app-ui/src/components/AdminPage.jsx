import LogoutButton from './LogoutButton';

const AdminPage = () => {
  const user = JSON.parse(localStorage.getItem('user'));

  return (
    <div>
      <h2>Hello Admin</h2>
      <p>Your username is <strong>{user?.username}</strong></p>
      <LogoutButton />
    </div>
  );
};

export default AdminPage;
