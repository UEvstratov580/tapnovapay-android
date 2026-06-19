# 💰 TapNovaPay Wallet

**Безпечний некастодіальний криптогаманець для монети TapNovaPay (TNP)**

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://play.google.com/store/apps/details?id=org.tapnovapay.wallet)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://material.io/design)

---

## 📱 Опис

**TapNovaPay Wallet** — це сучасний, безпечний та зручний криптогаманець для монети TapNovaPay (TNP). 
Vсі приватні ключі та seed-фрази зберігаються **ВИКЛЮЧНО** на вашому пристрої, забезпечуючи повний контроль над вашими коштами.

---

## ✨ Основні функції

### 🔐 Безпека
- ✅ Приватні ключі зберігаються **ТІЛЬКИ** локально
- ✅ Seed-фраза генерується на пристрої
- ✅ PIN-код для захисту гаманця
- ✅ Підтримка відбитка пальця (Android 6+)
- ✅ Android Keystore для шифрування

### 📱 Функціонал
- ✅ Створення та відновлення гаманця
- ✅ Відправлення та отримання TNP
- ✅ QR-код для швидких платежів
- ✅ Сканування QR-коду
- ✅ Історія транзакцій
- ✅ Бекап seed-фрази
- ✅ Копіювання та поширення адреси

### 🎨 Дизайн
- ✅ Сучасний темний дизайн (як у Trust Wallet)
- ✅ Зручна навігація
- ✅ Інтуїтивний інтерфейс
- ✅ Підтримка світлої/темної теми

---

## 🛠️ Технології

| Технологія | Версія | Опис |
|---|---|---|
| Android SDK | 34 | Платформа |
| Java | 17 | Мова програмування |
| Gradle | 8.x | Збірка |
| Material Design | 1.10.0 | UI компоненти |
| OkHttp | 4.12.0 | HTTP клієнт |
| Gson | 2.10.1 | JSON парсер |
| ZXing | 3.5.1 | QR-код |
| Biometric | 1.1.0 | Відбиток пальця |

---

## 📦 Встановлення

### Завантажте з Google Play
Скоро буде доступно в Google Play Store!

### Збірка з вихідного коду
bash
# Клонуйте репозиторій
git clone https://github.com/uevstratov/TapNovaPay-Android.git

# Відкрийте в Android Studio
# Або зберіть через Gradle
./gradlew assembleDebug


## 🏗️ Структура проекту

TapNovaPay-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/tapnovapay/wallet/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── BalanceFragment.java
│   │   │   │   ├── SendFragment.java
│   │   │   │   ├── ReceiveFragment.java
│   │   │   │   ├── HistoryFragment.java
│   │   │   │   ├── SettingsActivity.java
│   │   │   │   ├── PinActivity.java
│   │   │   │   ├── TapNovaPayAPI.java
│   │   │   │   └── WalletKeyGenerator.java
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── drawable/
│   │   │       └── values/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md

---

## 🔐 Безпека

- **Некастодіальний** — ми не зберігаємо ваші ключі
- **Локальне зберігання** — всі дані на вашому пристрої
- **Android Keystore** — захищене зберігання ключів
- **Відкритий код** — ви можете перевірити безпеку самостійно

---

## 📋 Політика конфіденційності

[Посилання на політику конфіденційності](https://uevstratov.github.io/tapnovapay-privacy/)

---

## 📧 Контакти

- **Email:** uevstratov@gmail.com
- **GitHub:** [uevstratov](https://github.com/uevstratov)

---

## 📄 Ліцензія

MIT License

---

## ⭐ Подяка

Дякуємо за використання TapNovaPay Wallet! 
Ваша фінансова свобода — наша мета! 💰🚀

---

**TapNovaPay Wallet — Ваш ключ до фінансової свободи!** 🔑
