"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { login } from "@/lib/api";
import { saveTokens, getTokens } from "@/lib/tokenUtils";

const STEPS = [
  { num: "01", label: "Giriş Yap", desc: "Kimlik doğrulanır, access + refresh token çifti üretilir." },
  { num: "02", label: "Dashboard", desc: "Access token (1 dk) ve refresh token (60 dk) geri sayım başlar." },
  { num: "03", label: "Otomatik Yenileme", desc: "30 saniye kala sistem refresh token ile yeni bir access token alır." },
  { num: "04", label: "Süre Dolunca", desc: "Refresh token süresi biterse oturum kapanır, yeniden giriş gerekir." },
];

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const { accessToken } = getTokens();
    if (accessToken) router.push("/dashboard");
  }, [router]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const data = await login(username, password);
      saveTokens(data.access_token, data.refresh_token);
      router.push("/dashboard");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Bir hata oluştu");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      className="min-h-screen flex flex-col"
      style={{ background: "var(--color-bg)", fontFamily: "var(--font-body)" }}
    >
      {/* Top bar */}
      <header
        className="border-b flex items-center justify-between px-8 py-4"
        style={{ borderColor: "var(--color-border)" }}
      >
        <div className="flex items-center gap-3">
          <div
            className="w-7 h-7 flex items-center justify-center text-xs font-bold"
            style={{ background: "var(--color-accent)", color: "var(--color-bg)", fontFamily: "var(--font-display)" }}
          >
            N
          </div>
          <span
            className="text-sm font-semibold tracking-widest uppercase"
            style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)", letterSpacing: "0.15em" }}
          >
            N11 Backend Bootcamp
          </span>
        </div>
        <span
          className="text-xs tracking-widest uppercase"
          style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
        >
          JWT AUTH DEMO
        </span>
      </header>

      {/* Center content */}
      <main className="flex-1 flex flex-col items-center justify-center px-6 py-12">

        {/* Page title */}
        <div className="mb-10 text-center">
          <p
            className="text-xs font-semibold tracking-widest uppercase mb-3"
            style={{ color: "var(--color-accent)", fontFamily: "var(--font-display)" }}
          >
            Refresh Token Demo
          </p>
          <h1
            className="text-5xl font-extrabold uppercase leading-none"
            style={{ fontFamily: "var(--font-display)", color: "var(--color-text)" }}
          >
            Kimlik Doğrulama
          </h1>
        </div>

        {/* Login card */}
        <div
          className="w-full max-w-md border"
          style={{ borderColor: "var(--color-border)", background: "var(--color-surface)" }}
        >
          {/* Credential hint */}
          <div
            className="border-b px-6 py-4 flex items-center gap-6"
            style={{ borderColor: "var(--color-border)", borderLeftWidth: "3px", borderLeftColor: "var(--color-accent)" }}
          >
            <p
              className="text-xs font-semibold uppercase tracking-widest shrink-0"
              style={{ color: "var(--color-accent)", fontFamily: "var(--font-display)" }}
            >
              Test Hesabı
            </p>
            <div className="flex gap-6">
              <div>
                <p className="text-xs mb-0.5" style={{ color: "var(--color-muted)" }}>Kullanıcı Adı</p>
                <p className="text-sm font-semibold" style={{ fontFamily: "var(--font-mono)", color: "var(--color-text)" }}>
                  kadirtuna
                </p>
              </div>
              <div>
                <p className="text-xs mb-0.5" style={{ color: "var(--color-muted)" }}>Şifre</p>
                <p className="text-sm font-semibold" style={{ fontFamily: "var(--font-mono)", color: "var(--color-text)" }}>
                  123
                </p>
              </div>
            </div>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="px-6 py-6 space-y-4">
            <div>
              <label
                className="block text-xs font-semibold uppercase tracking-widest mb-2"
                style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
              >
                Kullanıcı Adı
              </label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="kadirtuna"
                required
                className="w-full px-4 py-3 text-sm outline-none transition-colors"
                style={{
                  background: "var(--color-bg)",
                  border: "1px solid var(--color-border)",
                  color: "var(--color-text)",
                  fontFamily: "var(--font-mono)",
                }}
                onFocus={(e) => (e.target.style.borderColor = "var(--color-accent)")}
                onBlur={(e) => (e.target.style.borderColor = "var(--color-border)")}
              />
            </div>

            <div>
              <label
                className="block text-xs font-semibold uppercase tracking-widest mb-2"
                style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
              >
                Şifre
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••"
                required
                className="w-full px-4 py-3 text-sm outline-none transition-colors"
                style={{
                  background: "var(--color-bg)",
                  border: "1px solid var(--color-border)",
                  color: "var(--color-text)",
                  fontFamily: "var(--font-mono)",
                }}
                onFocus={(e) => (e.target.style.borderColor = "var(--color-accent)")}
                onBlur={(e) => (e.target.style.borderColor = "var(--color-border)")}
              />
            </div>

            {error && (
              <div
                className="px-4 py-3 text-sm border-l-2"
                style={{
                  background: "rgba(239,68,68,0.08)",
                  borderColor: "#ef4444",
                  color: "#f87171",
                  fontFamily: "var(--font-mono)",
                }}
              >
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 text-sm font-bold uppercase tracking-widest transition-opacity disabled:opacity-40"
              style={{
                background: "var(--color-accent)",
                color: "var(--color-bg)",
                fontFamily: "var(--font-display)",
                letterSpacing: "0.12em",
                marginTop: "4px",
              }}
            >
              {loading ? "Giriş Yapılıyor…" : "Giriş Yap →"}
            </button>
          </form>
        </div>

        {/* Steps row */}
        <div
          className="w-full max-w-2xl mt-10 border"
          style={{ borderColor: "var(--color-border)" }}
        >
          <div
            className="px-5 py-3 border-b"
            style={{ borderColor: "var(--color-border)" }}
          >
            <p
              className="text-xs font-semibold uppercase tracking-widest"
              style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
            >
              Sistem Akışı
            </p>
          </div>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-px" style={{ background: "var(--color-border)" }}>
            {STEPS.map((s, i) => (
              <div key={s.num} className="px-4 py-4" style={{ background: "var(--color-bg)" }}>
                <span
                  className="text-2xl font-bold block leading-none mb-2"
                  style={{
                    color: "var(--color-accent)",
                    fontFamily: "var(--font-display)",
                    opacity: i === 0 ? 1 : 0.4,
                  }}
                >
                  {s.num}
                </span>
                <p
                  className="text-xs font-bold uppercase tracking-wide mb-1"
                  style={{ fontFamily: "var(--font-display)", color: "var(--color-text)" }}
                >
                  {s.label}
                </p>
                <p className="text-xs leading-relaxed" style={{ color: "var(--color-muted)" }}>
                  {s.desc}
                </p>
              </div>
            ))}
          </div>
        </div>

        <p className="text-xs mt-8" style={{ color: "var(--color-muted)" }}>
          Kadir Tuna – Software Engineer &nbsp;·&nbsp; Spring Boot 3 · JJWT 0.10.7 · Next.js 15
        </p>
      </main>
    </div>
  );
}
