import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [username, setUsername] = useState('');
  const [userType, setUserType] = useState('');
  const navigate = useNavigate();

  const handleLogin = () => {
    if (!username || !userType) {
      alert('Please enter username and select user typeaa.');
      return;
    }

    // Save to localStorage (later we replace with API & auth context)
    localStorage.setItem('user', JSON.stringify({ username, userType }));

    switch (userType) {
      case 'ADMIN':
        navigate('/admin');
        break;
      case 'CUSTOMER':
        navigate('/customer');
        break;
      case 'RESTAURANT':
        navigate('/restaurant');
        break;
      case 'DELIVERY_PARTNER':
        navigate('/delivery');
        break;
      default:
        alert('Invalid role');
    }
  };

  return (
    <div>
      <h2>Sign In</h2>
      <input
        type="text"
        placeholder="User Name"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <select value={userType} onChange={(e) => setUserType(e.target.value)}>
        <option value="">Select Role</option>
        <option value="ADMIN">ADMIN</option>
        <option value="CUSTOMER">CUSTOMER</option>
        <option value="RESTAURANT">RESTAURANT</option>
        <option value="DELIVERY_PARTNER">DELIVERY_PARTNER</option>
      </select>
      <button onClick={handleLogin}>Sign In</button>
      <br />
      <button onClick={() => navigate('/signup')}>Go to Signup</button>
    </div>
  );
};

export default Login;
