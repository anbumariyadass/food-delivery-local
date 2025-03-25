import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Signup = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [retypePassword, setRetypePassword] = useState('');
  const [userType, setUserType] = useState('');
  const navigate = useNavigate();

  const handleSignup = () => {
    if (!username || !password || !retypePassword || !userType) {
      alert('All fields are required.');
      return;
    }

    if (password !== retypePassword) {
      alert('Passwords do not match.');
      return;
    }

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
      <h2>Sign Up</h2>
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
      <select value={userType} onChange={(e) => setUserType(e.target.value)}>
        <option value="">Select Role</option>
        <option value="ADMIN">ADMIN</option>
        <option value="CUSTOMER">CUSTOMER</option>
        <option value="RESTAURANT">RESTAURANT</option>
        <option value="DELIVERY_PARTNER">DELIVERY_PARTNER</option>
      </select>
      <br />
      <button onClick={handleSignup}>Sign Up</button>
    </div>
  );
};

export default Signup;
