"use client";

import { useEffect, useRef, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import {
  clearTokens,
  formatTime,
  getRemainingSeconds,
  getTokens,
  getUsername,
  saveTokens,
  parseJwt,
} from "@/lib/tokenUtils";
import { getWelcomeInfo, refreshTokens, WelcomeInfo } from "@/lib/api";

const ACCESS_TOTAL = 60;
const REFRESH_TOTAL = 3600;
const API_BASE = "http://localhost:8083";

function timerColor(remaining: number, total: number): string {
  const ratio = remaining / total;
  if (ratio > 0.5) return "#4ade80";
  if (ratio > 0.15) return "#facc15";
  return "#f87171";
}

function SectionLabel({ children }: { children: React.ReactNode }) {
  return (
    <p
      className="text-xs font-semibold uppercase tracking-widest mb-4"
      style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
    >
      {children}
    </p>
  );
}

function DataRow({ label, value, mono = false }: { label: string; value: string; mono?: boolean }) {
  return (
    <div
      className="flex justify-between items-center py-3 border-b"
      style={{ borderColor: "var(--color-border)" }}
    >
      <span className="text-xs uppercase tracking-wider" style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}>
        {label}
      </span>
      <span
        className="text-sm"
        style={{
          color: "var(--color-text)",
          fontFamily: mono ? "var(--font-mono)" : "var(--font-body)",
          fontWeight: 500,
        }}
      >
        {value}
      </span>
    </div>
  );
}

type TestResult = { status: number; body: string; duration: number } | null;

function statusColor(status: number): string {
  if (status >= 200 && status < 300) return "#4ade80";
  if (status === 401 || status === 403) return "#f87171";
  return "#facc15";
}

function statusLabel(status: number): string {
  if (status === 200) return "200 OK";
  if (status === 401) return "401 Unauthorized";
  if (status === 403) return "403 Forbidden";
  return String(status);
}

function ApiTestPanel({
  title,
  tag,
  tagColor,
  headers,
  description,
  result,
  loading,
  onTest,
}: {
  title: string;
  tag: string;
  tagColor: string;
  headers: Record<string, string>;
  description: string;
  result: TestResult;
  loading: boolean;
  onTest: () => void;
}) {
  return (
    <div className="flex flex-col" style={{ background: "var(--color-bg)" }}>
      {/* Panel header */}
      <div className="p-5 border-b" style={{ borderColor: "var(--color-border)" }}>
        <div className="flex items-start justify-between gap-4 mb-3">
          <div>
            <span
              className="inline-block text-xs font-bold px-2 py-0.5 mb-2 uppercase tracking-widest"
              style={{
                background: `${tagColor}15`,
                color: tagColor,
                fontFamily: "var(--font-display)",
                border: `1px solid ${tagColor}30`,
              }}
            >
              {tag}
            </span>
            <p
              className="text-sm font-bold uppercase tracking-wide"
              style={{ fontFamily: "var(--font-display)", color: "var(--color-text)" }}
            >
              {title}
            </p>
          </div>
          <button
            onClick={onTest}
            disabled={loading}
            className="shrink-0 px-4 py-2 text-xs font-bold uppercase tracking-widest transition-opacity disabled:opacity-40"
            style={{
              background: tagColor,
              color: "var(--color-bg)",
              fontFamily: "var(--font-display)",
              letterSpacing: "0.1em",
            }}
          >
            {loading ? "…" : "Test Et"}
          </button>
        </div>
        <p className="text-xs leading-relaxed" style={{ color: "var(--color-muted)" }}>
          {description}
        </p>
      </div>

      {/* Request preview */}
      <div className="px-5 py-4 border-b" style={{ borderColor: "var(--color-border)", background: "var(--color-surface)" }}>
        <p
          className="text-xs uppercase tracking-widest mb-2"
          style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
        >
          Request
        </p>
        <p className="text-xs mb-2" style={{ fontFamily: "var(--font-mono)", color: "#60a5fa" }}>
          <span style={{ color: "var(--color-accent)" }}>GET</span>{"  "}
          {API_BASE}/message
        </p>
        {Object.entries(headers).map(([k, v]) => (
          <p key={k} className="text-xs" style={{ fontFamily: "var(--font-mono)", color: "var(--color-muted)" }}>
            <span style={{ color: "var(--color-text)" }}>{k}:</span> {v}
          </p>
        ))}
      </div>

      {/* Response area */}
      <div className="px-5 py-4 flex-1" style={{ background: "var(--color-bg)", minHeight: "140px" }}>
        {!result && !loading && (
          <p className="text-xs" style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)" }}>
            — henüz istek gönderilmedi —
          </p>
        )}
        {loading && (
          <p className="text-xs" style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)" }}>
            istek gönderiliyor…
          </p>
        )}
        {result && (
          <>
            <div className="flex items-center gap-3 mb-3">
              <span
                className="text-xs font-bold px-2 py-0.5 uppercase tracking-widest"
                style={{
                  background: `${statusColor(result.status)}18`,
                  color: statusColor(result.status),
                  fontFamily: "var(--font-display)",
                  border: `1px solid ${statusColor(result.status)}35`,
                }}
              >
                {statusLabel(result.status)}
              </span>
              <span className="text-xs" style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)" }}>
                {result.duration}ms
              </span>
            </div>
            <pre
              className="text-xs leading-relaxed overflow-x-auto whitespace-pre-wrap break-all"
              style={{ fontFamily: "var(--font-mono)", color: "var(--color-muted)" }}
            >
              {result.body}
            </pre>
          </>
        )}
      </div>
    </div>
  );
}

const INFO_CARDS = [
  {
    tag: "ACCESS",
    title: "Access Token",
    desc: "Kısa ömürlü (1 dk) JWT. Korumalı endpoint'lere erişmek için Authorization header'ına eklenir. Süresi dolunca refresh token ile otomatik yenilenir.",
    color: "#60a5fa",
  },
  {
    tag: "REFRESH",
    title: "Refresh Token",
    desc: "Uzun ömürlü (60 dk) JWT. Sadece /token endpoint'inde yeni access token almak için kullanılır. Korumalı sayfalara doğrudan erişim sağlamaz.",
    color: "#a78bfa",
  },
  {
    tag: "OAUTH2",
    title: "grant_type Pattern",
    desc: "Tek /token endpoint'i — password ile ilk giriş, refresh_token ile yenileme. RFC 6749 uyumlu iki farklı akış, tek endpoint.",
    color: "var(--color-accent)",
  },
];

export default function DashboardPage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [accessSec, setAccessSec] = useState(ACCESS_TOTAL);
  const [refreshSec, setRefreshSec] = useState(REFRESH_TOTAL);
  const [welcomeInfo, setWelcomeInfo] = useState<WelcomeInfo | null>(null);
  const [log, setLog] = useState<string[]>([]);
  const [jwtPayload, setJwtPayload] = useState<Record<string, string>>({});
  const isRefreshingRef = useRef(false);

  const [tokenPreview, setTokenPreview] = useState("Bearer <token>");

  const [authResult, setAuthResult] = useState<TestResult>(null);
  const [noAuthResult, setNoAuthResult] = useState<TestResult>(null);
  const [authLoading, setAuthLoading] = useState(false);
  const [noAuthLoading, setNoAuthLoading] = useState(false);

  const addLog = useCallback((entry: string) => {
    const time = new Date().toLocaleTimeString("tr-TR");
    setLog((prev) => [`[${time}] ${entry}`, ...prev].slice(0, 6));
  }, []);

  const updateJwtPayload = useCallback((token: string) => {
    const claims = parseJwt(token);
    if (!claims) return;
    const fmt = (epoch: number) => new Date(epoch * 1000).toLocaleString("tr-TR");
    setJwtPayload({
      sub: claims.sub ?? "—",
      tokenType: claims.tokenType ?? "—",
      iss: claims.iss ?? "—",
      iat: claims.iat ? fmt(claims.iat) : "—",
      exp: claims.exp ? fmt(claims.exp) : "—",
    });
  }, []);

  const doRefresh = useCallback(async () => {
    if (isRefreshingRef.current) return;
    isRefreshingRef.current = true;
    addLog("⏳ Access token yenileniyor…");
    try {
      const { refreshToken } = getTokens();
      if (!refreshToken) throw new Error("refresh_token bulunamadı");
      const data = await refreshTokens(refreshToken);
      saveTokens(data.access_token, data.refresh_token);
      updateJwtPayload(data.access_token);
      addLog("✅ Access token başarıyla yenilendi");
    } catch (err) {
      const msg = err instanceof Error ? err.message : "bilinmeyen hata";
      addLog(`❌ Yenileme başarısız: ${msg} — çıkış yapılıyor`);
      clearTokens();
      router.push("/");
    } finally {
      isRefreshingRef.current = false;
    }
  }, [addLog, router, updateJwtPayload]);

  useEffect(() => {
    const { accessToken } = getTokens();
    if (!accessToken) { router.push("/"); return; }
    setUsername(getUsername(accessToken));
    updateJwtPayload(accessToken);
    setTokenPreview(`Bearer ${accessToken.slice(0, 24)}…`);
    getWelcomeInfo(accessToken)
      .then(setWelcomeInfo)
      .catch(() => setWelcomeInfo(null));
  }, [router, updateJwtPayload]);

  useEffect(() => {
    const id = setInterval(() => {
      const { accessToken, refreshToken } = getTokens();
      if (!accessToken || !refreshToken) { router.push("/"); return; }

      const a = getRemainingSeconds(accessToken);
      const r = getRemainingSeconds(refreshToken);
      setAccessSec(a);
      setRefreshSec(r);

      if (r === 0) {
        addLog("❌ Refresh token süresi doldu — lütfen tekrar giriş yapın");
        clearTokens();
        router.push("/");
        return;
      }

      if (a <= 30 && a > 0 && !isRefreshingRef.current) {
        doRefresh();
      }
    }, 1000);
    return () => clearInterval(id);
  }, [addLog, doRefresh, router]);

  async function testWithAuth() {
    setAuthLoading(true);
    const { accessToken } = getTokens();
    const start = Date.now();
    try {
      const res = await fetch(`${API_BASE}/message`, {
        headers: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
      });
      const text = await res.text();
      let body: string;
      try {
        body = JSON.stringify(JSON.parse(text), null, 2);
      } catch {
        body = text;
      }
      setAuthResult({ status: res.status, body, duration: Date.now() - start });
    } catch {
      setAuthResult({ status: 0, body: "Bağlantı hatası — backend çalışıyor mu?", duration: Date.now() - start });
    } finally {
      setAuthLoading(false);
    }
  }

  async function testWithoutAuth() {
    setNoAuthLoading(true);
    const start = Date.now();
    try {
      const res = await fetch(`${API_BASE}/message`);
      const text = await res.text();
      let body: string;
      try {
        body = JSON.stringify(JSON.parse(text), null, 2);
      } catch {
        body = text;
      }
      setNoAuthResult({ status: res.status, body, duration: Date.now() - start });
    } catch {
      setNoAuthResult({ status: 0, body: "Bağlantı hatası — backend çalışıyor mu?", duration: Date.now() - start });
    } finally {
      setNoAuthLoading(false);
    }
  }

  function handleLogout() {
    clearTokens();
    router.push("/");
  }

  return (
    <div className="min-h-screen" style={{ background: "var(--color-bg)", fontFamily: "var(--font-body)" }}>

      {/* Navbar */}
      <nav
        className="sticky top-0 z-10 border-b flex items-center justify-between px-8 py-4"
        style={{ background: "var(--color-bg)", borderColor: "var(--color-border)" }}
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
            style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
          >
            N11 Backend Bootcamp
          </span>
          <span style={{ color: "var(--color-border)" }}>·</span>
          <span
            className="text-xs uppercase tracking-wider"
            style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
          >
            JWT Demo
          </span>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm" style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)" }}>
            {username}
          </span>
          <button
            onClick={handleLogout}
            className="px-4 py-1.5 text-xs font-semibold uppercase tracking-widest transition-colors"
            style={{ border: "1px solid var(--color-border)", color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
            onMouseEnter={(e) => {
              (e.target as HTMLElement).style.borderColor = "var(--color-accent)";
              (e.target as HTMLElement).style.color = "var(--color-accent)";
            }}
            onMouseLeave={(e) => {
              (e.target as HTMLElement).style.borderColor = "var(--color-border)";
              (e.target as HTMLElement).style.color = "var(--color-muted)";
            }}
          >
            Çıkış →
          </button>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-8 py-10 space-y-8">

        {/* Welcome banner */}
        <div className="border-l-4 pl-6 py-2" style={{ borderColor: "var(--color-accent)" }}>
          <h1
            className="text-4xl font-extrabold uppercase leading-none"
            style={{ fontFamily: "var(--font-display)", color: "var(--color-text)" }}
          >
            Hoş Geldiniz,{" "}
            <span style={{ color: "var(--color-accent)" }}>{username || "…"}</span>
          </h1>
          <p className="text-sm mt-1" style={{ color: "var(--color-muted)" }}>
            JWT kimlik doğrulaması başarılı — oturumunuz aktif
          </p>
        </div>

        {/* Token timers */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-px" style={{ background: "var(--color-border)" }}>
          {/* Access token */}
          <div className="p-6" style={{ background: "var(--color-bg)" }}>
            <SectionLabel>Access Token</SectionLabel>
            <div
              className="text-7xl font-bold tabular-nums mb-5"
              style={{ fontFamily: "var(--font-mono)", color: timerColor(accessSec, ACCESS_TOTAL), lineHeight: 1 }}
            >
              {formatTime(accessSec)}
            </div>
            <div className="h-px w-full mb-1" style={{ background: "var(--color-border)" }}>
              <div
                className="h-full transition-all duration-1000"
                style={{ width: `${(accessSec / ACCESS_TOTAL) * 100}%`, background: timerColor(accessSec, ACCESS_TOTAL) }}
              />
            </div>
            <div className="flex justify-between mt-1">
              <span className="text-xs" style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)" }}>
                {accessSec}s kaldı
              </span>
              <span
                className="text-xs px-2 py-0.5"
                style={{
                  background: "rgba(96,165,250,0.1)", border: "1px solid rgba(96,165,250,0.2)",
                  color: "#60a5fa", fontFamily: "var(--font-display)", fontSize: "10px", fontWeight: 600, letterSpacing: "0.1em",
                }}
              >
                1 DAKİKA
              </span>
            </div>
            <p className="text-xs mt-4 leading-relaxed" style={{ color: "var(--color-muted)" }}>
              30 saniye kalınca otomatik yenileme tetiklenir.
            </p>
          </div>

          {/* Refresh token */}
          <div className="p-6" style={{ background: "var(--color-bg)" }}>
            <SectionLabel>Refresh Token</SectionLabel>
            <div
              className="text-7xl font-bold tabular-nums mb-5"
              style={{ fontFamily: "var(--font-mono)", color: timerColor(refreshSec, REFRESH_TOTAL), lineHeight: 1 }}
            >
              {formatTime(refreshSec)}
            </div>
            <div className="h-px w-full mb-1" style={{ background: "var(--color-border)" }}>
              <div
                className="h-full transition-all duration-1000"
                style={{ width: `${(refreshSec / REFRESH_TOTAL) * 100}%`, background: timerColor(refreshSec, REFRESH_TOTAL) }}
              />
            </div>
            <div className="flex justify-between mt-1">
              <span className="text-xs" style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)" }}>
                {Math.floor(refreshSec / 60)}dk {refreshSec % 60}s kaldı
              </span>
              <span
                className="text-xs px-2 py-0.5"
                style={{
                  background: "rgba(167,139,250,0.1)", border: "1px solid rgba(167,139,250,0.2)",
                  color: "#a78bfa", fontFamily: "var(--font-display)", fontSize: "10px", fontWeight: 600, letterSpacing: "0.1em",
                }}
              >
                60 DAKİKA
              </span>
            </div>
            <p className="text-xs mt-4 leading-relaxed" style={{ color: "var(--color-muted)" }}>
              Süresi bittiğinde tekrar giriş yapmanız gerekir.
            </p>
          </div>
        </div>

        {/* User info + Activity log */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-px" style={{ background: "var(--color-border)" }}>
          <div className="p-6" style={{ background: "var(--color-bg)" }}>
            <SectionLabel>Backend Yanıtı — /message</SectionLabel>
            {welcomeInfo ? (
              <div className="space-y-0">
                <DataRow label="Kullanıcı" value={welcomeInfo.username} mono />
                <DataRow label="Mesaj" value={welcomeInfo.greeting} />
                <DataRow label="Proje" value={welcomeInfo.project} />
                <DataRow label="Yazar" value={welcomeInfo.author} />
                <DataRow label="Sunucu Zamanı" value={welcomeInfo.serverTime} mono />
              </div>
            ) : (
              <p style={{ color: "var(--color-muted)", fontFamily: "var(--font-mono)", fontSize: "13px" }}>
                Veri yükleniyor…
              </p>
            )}
          </div>

          <div className="p-6" style={{ background: "var(--color-bg)" }}>
            <SectionLabel>Token Aktivite Logu</SectionLabel>
            {log.length === 0 ? (
              <p className="text-sm leading-relaxed" style={{ color: "var(--color-muted)" }}>
                Henüz aktivite yok. Access token 30 saniyeye düştüğünde otomatik yenileme burada görünür.
              </p>
            ) : (
              <ul className="space-y-1">
                {log.map((entry, i) => (
                  <li
                    key={i}
                    className="text-xs px-3 py-2 border-l"
                    style={{
                      fontFamily: "var(--font-mono)", color: "var(--color-muted)",
                      background: "var(--color-surface)",
                      borderColor: i === 0 ? "var(--color-accent)" : "var(--color-border)",
                    }}
                  >
                    {entry}
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>

        {/* API Tester */}
        <div className="border" style={{ borderColor: "var(--color-border)" }}>
          {/* Tester header */}
          <div
            className="px-6 py-4 border-b flex items-center justify-between"
            style={{ borderColor: "var(--color-border)", background: "var(--color-surface)" }}
          >
            <div>
              <SectionLabel>Endpoint Test Aracı — GET /message</SectionLabel>
              <p className="text-xs -mt-2 leading-relaxed" style={{ color: "var(--color-muted)" }}>
                Kimlik doğrulamalı ve doğrulamasız istekleri karşılaştırın. Backend, token olmadan{" "}
                <span style={{ color: "#f87171", fontFamily: "var(--font-mono)" }}>401 Unauthorized</span>{" "}
                döner.
              </p>
            </div>
            <span
              className="shrink-0 text-xs px-2 py-0.5 font-bold uppercase tracking-widest"
              style={{
                fontFamily: "var(--font-display)",
                background: "rgba(96,165,250,0.08)",
                color: "#60a5fa",
                border: "1px solid rgba(96,165,250,0.2)",
              }}
            >
              Live
            </span>
          </div>

          {/* Two panels */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-px" style={{ background: "var(--color-border)" }}>
            <ApiTestPanel
              title="Kimlik Doğrulamalı İstek"
              tag="200 OK beklenir"
              tagColor="#4ade80"
              headers={{ Authorization: tokenPreview }}
              description="Access token Authorization header'ına eklenerek gönderilir. Spring Security token'ı doğrular, kullanıcı bilgilerini döner."
              result={authResult}
              loading={authLoading}
              onTest={testWithAuth}
            />
            <ApiTestPanel
              title="Kimlik Doğrulamasız İstek"
              tag="401 beklenir"
              tagColor="#f87171"
              headers={{ "(header yok)": "" }}
              description="Authorization header gönderilmez. Spring Security isteği reddeder ve hata detayını içeren bir JSON yanıtı döner."
              result={noAuthResult}
              loading={noAuthLoading}
              onTest={testWithoutAuth}
            />
          </div>
        </div>

        {/* JWT Payload Decoder */}
        <div className="p-6 border" style={{ borderColor: "var(--color-border)", background: "var(--color-bg)" }}>
          <SectionLabel>Decoded JWT Payload — Access Token</SectionLabel>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-px" style={{ background: "var(--color-border)" }}>
            {[
              { key: "sub", label: "Subject (kullanıcı)" },
              { key: "tokenType", label: "Token Tipi" },
              { key: "iss", label: "Issuer" },
              { key: "iat", label: "İssued At" },
              { key: "exp", label: "Expires At" },
            ].map(({ key, label }) => (
              <div key={key} className="p-4" style={{ background: "var(--color-bg)" }}>
                <p
                  className="text-xs uppercase tracking-widest mb-1"
                  style={{ color: "var(--color-muted)", fontFamily: "var(--font-display)" }}
                >
                  {label}
                </p>
                <p
                  className="text-sm font-medium break-all"
                  style={{ color: jwtPayload[key] ? "var(--color-accent)" : "var(--color-muted)", fontFamily: "var(--font-mono)" }}
                >
                  {jwtPayload[key] ?? "—"}
                </p>
              </div>
            ))}
          </div>
        </div>

        {/* About cards */}
        <div className="p-6 border" style={{ borderColor: "var(--color-border)" }}>
          <SectionLabel>Bu Proje Hakkında</SectionLabel>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-px" style={{ background: "var(--color-border)" }}>
            {INFO_CARDS.map(({ tag, title, desc, color }) => (
              <div key={tag} className="p-5" style={{ background: "var(--color-bg)" }}>
                <span
                  className="inline-block text-xs font-bold px-2 py-0.5 mb-3 uppercase tracking-widest"
                  style={{
                    background: `${color}15`, color,
                    fontFamily: "var(--font-display)", border: `1px solid ${color}30`,
                  }}
                >
                  {tag}
                </span>
                <p
                  className="text-sm font-bold uppercase tracking-wide mb-2"
                  style={{ fontFamily: "var(--font-display)", color: "var(--color-text)" }}
                >
                  {title}
                </p>
                <p className="text-xs leading-relaxed" style={{ color: "var(--color-muted)" }}>
                  {desc}
                </p>
              </div>
            ))}
          </div>
        </div>

        {/* Footer */}
        <div
          className="border-t pt-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2"
          style={{ borderColor: "var(--color-border)" }}
        >
          <p className="text-xs font-semibold uppercase tracking-widest" style={{ fontFamily: "var(--font-display)", color: "var(--color-muted)" }}>
            N11 Backend Bootcamp
          </p>
          <p className="text-xs" style={{ color: "var(--color-muted)" }}>
            Kadir Tuna – Software Engineer
          </p>
          <p className="text-xs" style={{ color: "var(--color-border)", fontFamily: "var(--font-mono)" }}>
            Spring Boot 3 · JJWT 0.10.7 · Next.js 15 · Tailwind CSS
          </p>
        </div>
      </div>
    </div>
  );
}
