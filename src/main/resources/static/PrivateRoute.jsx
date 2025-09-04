import React from 'react';
import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ children }) => {
  const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('jwt='))?.split('=')[1];

  return token ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
