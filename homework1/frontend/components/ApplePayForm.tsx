"use client";

interface Props {
  details: Record<string, string>;
  onChange: (key: string, value: string) => void;
}

export default function ApplePayForm({ details, onChange }: Props) {
  return (
    <div className="space-y-3">
      <div>
        <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
          Device Token
        </label>
        <input
          type="text"
          placeholder="apple-device-token-xxxx"
          value={details.deviceId ?? ""}
          onChange={(e) => onChange("deviceId", e.target.value)}
          className="w-full border border-gray-200 bg-white rounded-xl px-4 py-3 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-orange-400"
        />
      </div>
      <p className="text-xs text-gray-400 flex items-center gap-1.5">
        <span>🍎</span>
        Apple Pay uses Face ID or Touch ID for biometric authorization.
      </p>
    </div>
  );
}
