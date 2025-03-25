import LogoutButton from './LogoutButton';
import '../styles/Layout.css';

const Layout = ({ children }) => {
  const user = JSON.parse(localStorage.getItem('user'));

  return (
    <div className="layout-container">
      <header className="layout-header">
        <span>Hello, <strong>{user?.username}</strong></span>
        <LogoutButton />
      </header>

      <main className="layout-content">
        {children}
      </main>

      <footer className="layout-footer">
        &copy; {new Date().getFullYear()} Food Delivery App. All rights reserved.
      </footer>
    </div>
  );
};

export default Layout;
