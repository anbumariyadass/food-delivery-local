import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/AuthPage.css';

const Signup = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [retypePassword, setRetypePassword] = useState('');
  const navigate = useNavigate();

  const handleSignup = async () => {
    if (!username || !password || !retypePassword) {
      alert('All fields are required.');
      return;
    }

    if (password !== retypePassword) {
      alert('Passwords do not match.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8081/identity/register', {
        username,
        password,
        role: "CUSTOMER",
        active: "Y",
      });

      const { message, data } = response.data;

      if (message && data) {
        alert(message);
        navigate('/'); // Redirect to Login page
      } else {
        alert('Signup successful but invalid response format.');
      }

    } catch (error) {
      const errMsg = error.response?.data?.message || error.message;
      alert(`Signup failed: ${errMsg}`);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1 className="app-name">🍽️ Food Delivery App</h1>
        <h2>Customer Sign Up</h2>
        <input
          type="text"
          placeholder="User Name"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <br />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <br />
        <input
          type="password"
          placeholder="Retype Password"
          value={retypePassword}
          onChange={(e) => setRetypePassword(e.target.value)}
        />        
        <br />
        <button onClick={handleSignup}>Sign Up</button>
        <button className="link-button" onClick={() => navigate('/')}>
          Go back to Sign In
        </button>
    </div>
    </div>
  );
};

export default Signup;
