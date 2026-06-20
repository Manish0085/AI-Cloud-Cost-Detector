import api from "./client"
import type { AuthResponse, CurrentUser } from "./types"

export async function login(email: string, password: string): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>("/auth/login", { email, password })
  return data
}

export async function register(
  name: string,
  email: string,
  password: string
): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>("/auth/register", { name, email, password })
  return data
}

export async function getMe(): Promise<CurrentUser> {
  const { data } = await api.get<CurrentUser>("/auth/me")
  // With oauth2Login enabled, the backend answers an unauthenticated request
  // with a 302 redirect that axios follows and resolves as HTML — so a 200 is
  // not proof of a valid session. Require a real user payload.
  if (!data || typeof data !== "object" || !("email" in data) || !data.email) {
    throw new Error("Not authenticated")
  }
  return data
}
