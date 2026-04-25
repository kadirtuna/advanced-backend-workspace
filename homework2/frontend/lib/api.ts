const API_BASE = "http://localhost:8083";

interface TokenResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
}

export interface WelcomeInfo {
  username: string;
  greeting: string;
  project: string;
  author: string;
  serverTime: string;
}

async function parseError(res: Response): Promise<string> {
  try {
    const body = await res.json();
    return body.message ?? `HTTP ${res.status}`;
  } catch {
    return `HTTP ${res.status}`;
  }
}

export async function login(username: string, password: string): Promise<TokenResponse> {
  const res = await fetch(`${API_BASE}/token`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ grant_type: "password", username, password }),
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export async function refreshTokens(refreshToken: string): Promise<TokenResponse> {
  const res = await fetch(`${API_BASE}/token`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ grant_type: "refresh_token", refresh_token: refreshToken }),
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export async function getWelcomeInfo(accessToken: string): Promise<WelcomeInfo> {
  const res = await fetch(`${API_BASE}/message`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}
