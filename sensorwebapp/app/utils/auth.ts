// utils/auth.ts

// Import necessary functions from Remix for handling sessions or cookies
import { createCookieSessionStorage, redirect } from '@remix-run/node';

// In a file where API_BASE_URL is used
import { API_BASE_URL } from '../config/config'; // Adjust the import path as necessary


// Define your session storage strategy (configure according to your needs)
export const sessionStorage = createCookieSessionStorage({
  // The `cookie` key allows you to set options for the cookie
  cookie: {
    name: "_sensorwebapp_session", // A name for your cookie
    // Secure should be set to true in production to ensure cookies are set over HTTPS
    secure: process.env.NODE_ENV === "production",
    secrets: ["s3cret1"], // Replace 's3cret1' with a real secret key
    httpOnly: true, // This means the cookie is not accessible via JavaScript (for security reasons)
    sameSite: "lax", // Helps protect against CSRF attacks
    path: "/", // The cookie will be accessible for the entire site
    maxAge: 60 * 60 * 24 * 7, // Cookie expiry set to one week (in seconds)
    // Optionally, set the domain where the cookie is valid (useful for subdomains)
    // domain: "yourdomain.com",
  },
});

// Updated login function
export async function login(username: string, password: string): Promise<string | null> {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  if (!response.ok) {
    // Login failed
    return null;
  }

  const { jwtToken } = await response.json();
  return jwtToken; // Return the token for further handling, e.g., saving in session
}


// Function to validate the current session by calling the /checktoken endpoint
export async function validateSession(request: Request): Promise<boolean> {
  const session = await sessionStorage.getSession(request.headers.get("Cookie"));
  const jwtToken = session.get("jwtToken"); // Assuming the JWT token is stored in the session with key 'jwtToken'

  if (!jwtToken) {
    return false; // No token found in session
  }

  const response = await fetch(`${API_BASE_URL}/checktoken`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${jwtToken}` },
  });

  return response.status === 200; // True if token is valid, false otherwise
}


// Function to log out by invalidating the token and clearing the session
export async function logout(request: Request): Promise<Response> {
  const session = await sessionStorage.getSession(request.headers.get("Cookie"));
  const jwtToken = session.get("jwtToken"); // Assuming the JWT token is stored in the session with key 'jwtToken'

  if (jwtToken) {
    // Optionally call the /invalidatetoken endpoint to invalidate the token on the server side
    await fetch(`${API_BASE_URL}/invalidatetoken`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${jwtToken}` },
    });
  }

  // Clear the session on the client side
  return redirect('/login', {
    headers: {
      "Set-Cookie": await sessionStorage.destroySession(session),
    },
  });
}
