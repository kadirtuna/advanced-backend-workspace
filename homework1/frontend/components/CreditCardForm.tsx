"use client";

interface Props {
  details: Record<string, string>;
  onChange: (key: string, value: string) => void;
}

export default function CreditCardForm({ details, onChange }: Props) {
  return (
    <div className="space-y-4">
      <div>
        <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
          Card Number
        </label>
        <input
          type="text"
          placeholder="1234 5678 9012 3456"
          maxLength={19}
          value={details.cardNumber ?? ""}
          onChange={(e) => {
            const v = e.target.value.replace(/\D/g, "").slice(0, 16);
            onChange("cardNumber", v.replace(/(.{4})/g, "$1 ").trim());
          }}
          className="w-full border border-gray-200 bg-white rounded-xl px-4 py-3 text-sm font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-orange-400"
        />
      </div>
      <div>
        <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
          Cardholder Name
        </label>
        <input
          type="text"
          placeholder="JOHN DOE"
          value={details.cardHolderName ?? ""}
          onChange={(e) => onChange("cardHolderName", e.target.value.toUpperCase())}
          className="w-full border border-gray-200 bg-white rounded-xl px-4 py-3 text-sm uppercase tracking-widest focus:outline-none focus:ring-2 focus:ring-orange-400"
        />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
            Expiry Date
          </label>
          <input
            type="text"
            placeholder="MM/YY"
            maxLength={5}
            value={details.expiryDate ?? ""}
            onChange={(e) => {
              const v = e.target.value.replace(/\D/g, "").slice(0, 4);
              onChange("expiryDate", v.length > 2 ? `${v.slice(0, 2)}/${v.slice(2)}` : v);
            }}
            className="w-full border border-gray-200 bg-white rounded-xl px-4 py-3 text-sm font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-orange-400"
          />
        </div>
        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
            CVV
          </label>
          <input
            type="password"
            placeholder="•••"
            maxLength={4}
            value={details.cvv ?? ""}
            onChange={(e) => onChange("cvv", e.target.value.replace(/\D/g, "").slice(0, 4))}
            className="w-full border border-gray-200 bg-white rounded-xl px-4 py-3 text-sm font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-orange-400"
          />
        </div>
      </div>
    </div>
  );
}
