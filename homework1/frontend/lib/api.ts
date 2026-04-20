const BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export interface PaymentMethodInfo {
  name: string;
  displayName: string;
  description: string;
}

export interface PaymentResponse {
  id?: number;
  transactionId?: string;
  paymentMethod: string;
  amount: number | string;
  currency: string;
  status: string;
  message?: string;
  description?: string;
  createdAt?: string;
}

export async function fetchPaymentMethods(): Promise<PaymentMethodInfo[]> {
  const res = await fetch(`${BASE}/api/payments/methods`);
  if (!res.ok) throw new Error("Failed to fetch payment methods");
  return res.json();
}

export async function processPayment(payload: {
  paymentMethod: string;
  amount: number;
  currency: string;
  description?: string;
  paymentDetails: Record<string, string>;
}): Promise<PaymentResponse> {
  const res = await fetch(`${BASE}/api/payments`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.message ?? "Payment failed");
  return data;
}

export async function fetchPaymentHistory(): Promise<PaymentResponse[]> {
  const res = await fetch(`${BASE}/api/payments`);
  if (!res.ok) throw new Error("Failed to fetch payment history");
  return res.json();
}
