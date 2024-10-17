export interface IMstCustomer {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string | null;
  address?: string | null;
  loyaltyPoints?: number | null;
}

export class MstCustomer implements IMstCustomer {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public phoneNumber?: string | null,
    public address?: string | null,
    public loyaltyPoints?: number | null,
  ) {}
}
