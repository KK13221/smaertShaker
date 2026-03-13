# SmartShake Vending — Full Android Kotlin Project

Complete Android Kotlin + XML project for the SmartShake Vending kiosk UI,
generated from Figma file `shake-own` (node 1-155).

## Quick Start

1. Open Android Studio → **Open** → select this `smart_sheker` folder as the project root
2. Add font TTF files and image assets (see sections below)
3. Click **Run** — targets API 26+, landscape orientation

---

## File Structure

```
smart_sheker/
└── res/
    ├── drawable/          Shape drawables, selectors, vector icons
    ├── font/              Font family XML + TTF placeholder (see setup)
    ├── layout/            Screen layouts + item/component layouts
    └── values/            colors, dimens, strings, themes
```

---

## Screens

| Fragment | Figma Node | Description |
|---|---|---|
| `fragment_welcome.xml` | 1:156 | Welcome — brand name, VENDING badge, CTA button, shake illustration |
| `fragment_flavour.xml` | 1:223 / 1:341 | Choose Your Flavour — horizontal RecyclerView of 5 flavour cards |
| `fragment_base.xml` | 1:459 / 1:498 | Choose Your Base — Milk vs Water selection cards |
| `fragment_payment.xml` | 1:537 | Payment — 3-column layout: order list, nutrition info, Scan & Pay QR |

---

## Reusable Components

| File | Usage |
|---|---|
| `item_flavour_card.xml` | RecyclerView item — 175×235dp card with image, wave, price badge, name + counter |
| `item_order_row.xml` | Payment screen order list row — image, name, counter, price, close |
| `view_logo_chip.xml` | Top-right SMARTSHAKE / VENDING chip — included on screens 2–4 |
| `view_nutrition_row.xml` | Single nutrition label+value row — included 6× in payment center panel |

---

## Drawables

| File | Purpose |
|---|---|
| `bg_button_outline_green.xml` | Pill button — transparent fill, #BBF172 2.5dp stroke |
| `bg_button_filled_green.xml` | Pill button — #BBF172 filled |
| `bg_button_inactive.xml` | Pill button — #626262 inactive state |
| `bg_base_card_selector.xml` | Base card state selector (selected ↔ normal) |
| `bg_base_card_selected.xml` | #BBF172 fill + 2dp stroke, 20dp corner |
| `bg_base_card_normal.xml` | #F0EFE3 fill, 20dp corner |
| `bg_card_shape.xml` | Flavour card background — #FCE6C4, 20dp top corners |
| `bg_circle_subtle.xml` | Welcome screen concentric circle rings |
| `bg_counter_btn.xml` | Flavour card counter ± buttons — semi-transparent white circle |
| `bg_order_counter_btn.xml` | Payment order counter ± buttons — #F0EFE3 stroke circle |
| `bg_order_img.xml` | Payment order row image placeholder — #FCE6C4, 8dp corner |
| `bg_panel.xml` | Payment panel border — #5C5C5C 1dp stroke, 6dp corner |
| `bg_payment_badge.xml` | Payment method badge (Visa/GPay/ApplePay) — white, 4dp corner |
| `bg_payment_badge_amex.xml` | AMEX badge — #1F72CD fill, 4dp corner |
| `bg_price_badge.xml` | Card price badge — white, 100dp corner pill |
| `bg_qr_frame.xml` | QR code area — white fill, 8dp corner |
| `bg_vending_badge.xml` | Logo chip VENDING text background — #BBF172 |
| `wave_card_bottom.xml` | VectorDrawable Bezier wave in #FCE6C4 overlaid at card image bottom |
| `ic_plus.xml` | + icon (white) |
| `ic_minus.xml` | − icon (white) |
| `ic_plus_dark.xml` | + icon (#F0EFE3) for payment order counter |
| `ic_minus_dark.xml` | − icon (#F0EFE3) for payment order counter |
| `ic_close.xml` | × icon (#5E1706) for removing order row |

---

## Design Tokens

**Colors** — `res/values/colors.xml`

| Token | Hex | Usage |
|---|---|---|
| `ss_background` | `#1B1D1A` | All screen backgrounds |
| `ss_green` | `#BBF172` | Accent — buttons, selected states, headings |
| `ss_card_bg` | `#FCE6C4` | Flavour card body |
| `ss_text_primary` | `#F0EFE3` | Body text on dark bg |
| `ss_text_muted` | `#9E9E9E` | Secondary / label text |
| `ss_chocolate_text` | `#5E1706` | Dark text on card bg |
| `ss_btn_inactive` | `#626262` | Disabled button fill |
| `ss_panel_border` | `#5C5C5C` | Payment panel border |
| `ss_amex_bg` | `#1F72CD` | AMEX badge background |

**Fonts** — `res/font/`

| Token | Typeface | Weight |
|---|---|---|
| `poppins_light` | Poppins | 300 |
| `poppins_regular` | Poppins | 400 |
| `poppins_medium` | Poppins | 500 |
| `poppins_semibold` | Poppins | 600 |
| `inter_regular` | Inter | 400 |
| `inter_bold` | Inter | 700 |
| `reddit_sans_medium` | Reddit Sans | 500 |

---

## Setup Instructions

### 1. Copy files into your project

Copy the entire `res/` folder into your Android project's `app/src/main/res/`.
If any subfolders already exist, merge (do not replace) their contents.

### 2. Add font TTF files

Download and rename font files into `res/font/` — see `res/font/fonts.xml` for
exact filenames and download links.

### 3. Apply the theme

In `AndroidManifest.xml`, set your activity theme:

```xml
<activity
    android:name=".MainActivity"
    android:theme="@style/Theme.SmartShake"
    android:screenOrientation="landscape" />
```

### 4. Add image assets

The layouts reference these drawable names — add your own images:

| Drawable name | Used in | Description |
|---|---|---|
| `img_shake_cup` | `fragment_welcome` | Hero shake illustration (right panel) |
| `img_milk` | `fragment_base` | Milk carton image for base card |
| `img_water` | `fragment_base` | Water bottle image for base card |
| `img_chocolate` | Flavour card adapter | Chocolate flavour food photo |
| `img_vanilla` | Flavour card adapter | Vanilla flavour food photo |
| `img_banana` | Flavour card adapter | Banana flavour food photo |
| `img_strawberry` | Flavour card adapter | Strawberry flavour food photo |
| `img_coffee` | Flavour card adapter | Coffee flavour food photo |
| `img_qr_code` | `fragment_payment` | QR code for payment |
| `img_visa` / `img_amex` / `img_gpay` / `img_applepay` | `fragment_payment` | Payment method logos |

### 5. SpannableString for headings

Two screens require colored spans applied in Kotlin (green `#BBF172` on the
"your …" portion of the heading):

**fragment_flavour — "Choose your flavour":**
```kotlin
val s = SpannableString("Choose your flavour")
s.setSpan(ForegroundColorSpan(Color.parseColor("#BBF172")), 7, 19, 0)
tv_heading.text = s
```

**fragment_base — "Choose your base":**
```kotlin
val s = SpannableString("Choose your base")
s.setSpan(ForegroundColorSpan(Color.parseColor("#BBF172")), 7, 16, 0)
tv_heading.text = s
```

### 6. Gradle dependencies

```groovy
dependencies {
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'com.google.android.material:material:1.11.0'
}
```

---

## Scale Reference

The Figma design is 2253×1272px at 2× density (landscape tablet/kiosk).
All dp/sp values in this project = Figma pixel value ÷ 2.

Target screen: ~1280×720dp landscape (e.g. 10" kiosk tablet at xxhdpi).

---

## Navigation Flow

```
WelcomeFragment
    └─(Start Shake)──► FlavourFragment
                            └─(Continue)──► BaseFragment
                                                └─(Continue)──► PaymentFragment
                                                                    └─(Back × 3)──► Welcome
```

Each fragment uses a shared `ViewModel` to accumulate:
- `selectedFlavours: Map<FlavourId, Int>` (quantity per flavour)
- `selectedBase: Base` (MILK or WATER)
- `totalPrice: Int` (running ₹ total shown in every bottom bar)
