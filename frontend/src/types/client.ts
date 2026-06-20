export interface Client {
  id: number;
  organizationName: string;
  email: string;
  phone: string | null;
  address: string | null;
  createdAt: string;
}

export interface CreateClientRequest {
  organizationName: string;
  email: string;
  phone?: string;
  address?: string;
}

export type UpdateClientRequest = CreateClientRequest;
