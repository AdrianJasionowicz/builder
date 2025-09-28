const BASE_URL = "http://localhost:8080";

export async function register(username, password, email) {
  const res = await fetch(`${BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, email }),
  });
  return res;
}

export async function login(username, password) {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
    credentials: "include",
  });
  return res;
}

export async function logout() {
  await fetch(`${BASE_URL}/auth/logout`, {
    method: "POST",
    credentials: "include",
  });
}

export async function getMe() {
  const res = await fetch(`${BASE_URL}/me`, {
    method: "GET",
    credentials: "include",
  });
  if (res.ok) return res.text();
  throw new Error("Not authenticated");
}
