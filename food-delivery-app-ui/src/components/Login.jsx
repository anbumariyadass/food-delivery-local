import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/AuthPage.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    if (!username || !password) {
      alert('Please enter username and password.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8081/identity/login', {
        username,
        password,
      });

      const { data } = response.data;

      if (!data || !data.role || !data.token) {
        alert('Login failed: Incomplete data received');
        return;
      }

      localStorage.setItem('user', JSON.stringify({
        username: data.username,
        role: data.role,
        token: data.token
      }));

      switch (data.role) {
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
          alert('Unknown role. Cannot proceed.');
      }

    } catch (error) {
      const errMsg = error.response?.data?.message || error.response?.data?.data || 'Login failed';
      alert(errMsg);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1 className="app-name">🍽️ Food Delivery App</h1>
        <h2>Sign In</h2>
        <input
          type="text"
          placeholder="User Name"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button onClick={handleLogin}>Sign In</button>
        <button className="link-button" onClick={() => navigate('/signup')}>
          Go to Signup
        </button>
      </div>
    </div>
  );
};

export default Login;
