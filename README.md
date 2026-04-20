# Ödeme Sistemi — Ödev 1

Yeni bir ödeme yönteminin SOLID prensiplerine uygun entegrasyonunu gösteren, Java Spring Boot backend ve Next.js frontend'den oluşan tam-stack bir ödeme uygulaması.

---

## Proje Yapısı

```
Homework1/
├── backend/    Java Spring Boot (Port: 8080)
└── frontend/   Next.js 14 App Router (Port: 3000)
```

---

## Mimari Kararlar

### N-Tier (Katmanlı) Mimari

Backend dört ayrı katmandan oluşur:

| Katman | Paket | Sorumluluk |
|--------|-------|------------|
| **Sunum (Presentation)** | `controller/` | HTTP istek/cevap yönetimi |
| **İş Mantığı (Business Logic)** | `service/` | Ödeme akışı koordinasyonu |
| **Veri Erişim (Data Access)** | `repository/` | PostgreSQL CRUD işlemleri |
| **Alan (Domain)** | `entity/`, `dto/` | Veri modelleri |

---

### Chain of Responsibility (Sorumluluk Zinciri) Deseni

Her ödeme isteği sırasıyla üç handler'dan geçer:

```
ValidationHandler → FraudDetectionHandler → PaymentProcessingHandler
```

- **ValidationHandler**: Tutar, para birimi ve ödeme yöntemi geçerliliğini kontrol eder.
- **FraudDetectionHandler**: Yüksek risk kurallarını ve yönteme özgü alanları doğrular.
- **PaymentProcessingHandler**: Reflection registry üzerinden doğru stratejiyi seçer ve ödemeyi işler.

Herhangi bir adım başarısız olursa zincir kırılır ve hata döndürülür. Yeni bir adım eklemek (örn. `RateLimitHandler`) mevcut handler'larda değişiklik gerektirmez; yalnızca `PaymentChainConfig`'e eklenmesi yeterlidir.

---

### Java Reflection ile Dinamik Provider Kaydı

`PaymentStrategyRegistry` sınıfı, uygulama başlangıcında `@PaymentProvider` anotasyonu taşıyan tüm bean'leri **Reflection** kullanarak otomatik olarak keşfeder ve kaydeder:

```java
// Spring tüm @PaymentProvider bean'lerini bulur
Map<String, Object> beans = applicationContext.getBeansWithAnnotation(PaymentProvider.class);

// Java Reflection ile anotasyon metadata'sı okunur
PaymentProvider annotation = beanClass.getAnnotation(PaymentProvider.class);
strategyMap.put(annotation.name(), (PaymentStrategy) bean);
```

Bu sayede hiçbir yerde sabit kodlanmış provider listesi bulunmaz. Yeni bir ödeme yöntemi eklemek için:

1. `PaymentStrategy` arayüzünü implemente eden bir sınıf oluştur
2. `@PaymentProvider(name = "MY_METHOD", displayName = "My Method")` anotasyonunu ekle
3. `@Component` ile Spring bean'i yap

Başka hiçbir değişiklik gerekmez — sistem yeni yöntemi otomatik olarak keşfeder.

---

### SOLID Prensipleri

| Prensip | Uygulama |
|---------|----------|
| **SRP** | Her sınıfın tek bir sorumluluğu var: Controller sadece HTTP, Service sadece iş mantığı, her Strategy sadece kendi ödeme yöntemi |
| **OCP** | Yeni ödeme yöntemi = yeni sınıf + anotasyon. Mevcut kod değişmez |
| **LSP** | Tüm `PaymentStrategy` implementasyonları birbirinin yerine kullanılabilir |
| **ISP** | `PaymentStrategy` arayüzü tek bir sorumluluğa odaklanır |
| **DIP** | `PaymentServiceImpl`, `PaymentStrategy` arayüzüne bağımlıdır; somut sınıflara değil |

---

### Ödeme Yöntemleri

| Yöntem | Sınıf | Durum |
|--------|-------|-------|
| Kredi Kartı | `CreditCardPaymentStrategy` | Mevcut |
| PayPal | `PayPalPaymentStrategy` | **Yeni eklendi** |
| Apple Pay | `ApplePayPaymentStrategy` | **Yeni eklendi** |

---

## Kurulum ve Çalıştırma

### Gereksinimler

- Java 17+
- Apache Maven 3.9+
- PostgreSQL 14+
- Node.js 18+

### 1. PostgreSQL Veritabanı

```sql
CREATE DATABASE payment_db;
```

### 2. Backend

```bash
cd backend

# Veritabanı bağlantısını güncelle (gerekirse)
# src/main/resources/application.properties

mvn spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışır.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend `http://localhost:3000` adresinde çalışır.

---

## API Endpoints

| Method | URL | Açıklama |
|--------|-----|---------|
| `GET` | `/api/payments/methods` | Kayıtlı ödeme yöntemlerini listele |
| `POST` | `/api/payments` | Ödeme işle |
| `GET` | `/api/payments` | Tüm ödemeleri getir |
| `GET` | `/api/payments/{id}` | ID ile ödeme getir |

### Örnek POST İsteği

```json
{
  "paymentMethod": "CREDIT_CARD",
  "amount": 150.00,
  "currency": "USD",
  "description": "Sipariş #12345",
  "paymentDetails": {
    "cardNumber": "4111111111111111",
    "cardHolderName": "Ahmet Yılmaz",
    "expiryDate": "12/27",
    "cvv": "123"
  }
}
```

---

## Teknolojiler

- **Backend**: Java 17, Spring Boot 3.4, Spring Data JPA, PostgreSQL, Lombok
- **Frontend**: Next.js 14 (App Router), TypeScript, Tailwind CSS
- **Desenler**: Chain of Responsibility, Strategy, Reflection-based Registry
