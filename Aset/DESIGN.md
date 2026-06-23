---
name: Piringku Precision Health
colors:
  surface: '#f8faf6'
  surface-dim: '#d8dbd7'
  surface-bright: '#f8faf6'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f0'
  surface-container: '#eceeea'
  surface-container-high: '#e7e9e5'
  surface-container-highest: '#e1e3df'
  on-surface: '#191c1a'
  on-surface-variant: '#404943'
  inverse-surface: '#2e312f'
  inverse-on-surface: '#eff1ed'
  outline: '#707973'
  outline-variant: '#bfc9c1'
  surface-tint: '#2c694e'
  primary: '#0f5238'
  on-primary: '#ffffff'
  primary-container: '#2d6a4f'
  on-primary-container: '#a8e7c5'
  inverse-primary: '#95d4b3'
  secondary: '#9b4500'
  on-secondary: '#ffffff'
  secondary-container: '#fc8a40'
  on-secondary-container: '#672c00'
  tertiary: '#713638'
  on-tertiary: '#ffffff'
  tertiary-container: '#8d4d4e'
  on-tertiary-container: '#ffcfce'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#b1f0ce'
  primary-fixed-dim: '#95d4b3'
  on-primary-fixed: '#002114'
  on-primary-fixed-variant: '#0e5138'
  secondary-fixed: '#ffdbc9'
  secondary-fixed-dim: '#ffb68d'
  on-secondary-fixed: '#331200'
  on-secondary-fixed-variant: '#763300'
  tertiary-fixed: '#ffdad9'
  tertiary-fixed-dim: '#ffb3b3'
  on-tertiary-fixed: '#390b0e'
  on-tertiary-fixed-variant: '#6f3537'
  background: '#f8faf6'
  on-background: '#191c1a'
  surface-variant: '#e1e3df'
  health-green-vibrant: '#40916C'
  energy-orange-light: '#FFB385'
  data-blue: '#4361EE'
  error-red: '#E63946'
  surface-gray: '#F8F9FA'
  border-subtle: '#E9ECEF'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  metric-display:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 48px
    letterSpacing: -0.04em
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  button-text:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 20px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  container-padding: 20px
  gutter: 16px
  stack-sm: 4px
  stack-md: 12px
  stack-lg: 24px
---

## Brand & Style

The design system is engineered to feel like a high-end medical instrument that has been softened for consumer use. It balances **scientific authority** with **encouraging accessibility**, specifically targeting users who require a structured, data-driven approach to obesity prevention. 

The visual style is **Modern Corporate**, utilizing heavy whitespace and a strict mathematical grid to ensure data density remains legible and non-overwhelming. To counter the clinical nature of nutrition tracking, we employ "Soft-Precision" elements: high-radius corners (16px+) and subtle depth cues that make the interface feel tactile and responsive rather than flat and static.

**Design Principles:**
- **Clarity over Decoration:** Every visual element must serve a functional purpose in communicating nutritional data.
- **Encouraging Feedback:** Use of vibrant colors and micro-animations to celebrate hitting daily goals.
- **Trust via Consistency:** Strict adherence to the grid and typographic hierarchy to maintain a professional, reliable atmosphere.

## Colors

The palette is anchored by **Fresh Green**, symbolizing health, growth, and nutritional balance. This is used for primary actions and "success" states (e.g., staying within calorie limits). **Vibrant Orange** is utilized as a high-energy accent for progress indicators, warnings, and metabolic activity tracking.

- **Primary (Fresh Green):** Used for the main brand identity, "Add" actions, and positive metric states.
- **Secondary (Vibrant Orange):** Used for energy-related metrics (calories burned) and as a contrast element for secondary calls to action.
- **Neutrals:** A range of cool grays provides the scaffolding. Surface-gray is used for background layering to prevent pure-white eye strain.
- **Semantic Colors:** Blue is reserved for hydration/water tracking, and Red is strictly for "limit exceeded" states or destructive actions (Delete).

## Typography

This design system uses **Inter** for the majority of the interface to maintain a clean, neutral, and highly legible experience across all densities. To emphasize the "scientific" and "data-driven" nature of the product, **JetBrains Mono** is introduced for small labels, nutritional facts, and units (e.g., "kcal", "g").

**Typographic Guidelines:**
- **Metric Display:** Used for the primary "Remaining Calories" number. It should be the largest element on the screen.
- **Label Caps:** Used for category headers (e.g., BREAKFAST, MACRONUTRIENTS) to provide clear sectioning.
- **Tight Leading:** Headlines use a tighter line height to maintain a compact, "instrument" feel.

## Layout & Spacing

The system follows an **8px base grid** to ensure consistency. 

- **Dashboard Layout:** Utilizes a fixed vertical stack. The "Hero" section (Metric Assessment) occupies the top 35% of the viewport.
- **Grid:** A 12-column grid is used for desktop, collapsing to a single-column list view for mobile with 20px side margins.
- **Grouping:** Related data points (e.g., Protein, Fat, Carbs) should be grouped with `stack-sm` spacing, while distinct food entries use `stack-md`.
- **Safe Areas:** Interactive elements like the FAB (Floating Action Button) must maintain a 24px clearance from the bottom and right edges of the screen.

## Elevation & Depth

To maintain a "modern and clean" aesthetic while ensuring the UI is intuitive, the system uses **Tonal Layering** combined with **Ambient Shadows**.

- **Level 0 (Background):** Surface-gray (`#F8F9FA`).
- **Level 1 (Cards):** Pure White with a 1px subtle border (`#E9ECEF`). No shadow. Used for list items.
- **Level 2 (Active/Hero):** Pure White with a 12% opacity shadow (Blur 16px, Y-Offset 4px) tinted with the primary green. Used for the Daily Metric Widget.
- **Level 3 (Modals/FAB):** Pure White with a 20% opacity shadow. Used for the Floating Action Button and BottomSheets to indicate they are at the top of the stack.

## Shapes

The shape language is defined by "Humanist Geometry." 

- **Containers:** 16px radius (rounded-lg) is the standard for all dashboard cards and input fields.
- **Hero Widgets:** 24px radius (rounded-xl) to make them feel softer and more prominent.
- **Interactive Elements:** Buttons and Stepper controls use a 12px radius to feel distinct from larger layout containers.
- **Progress Bars:** Fully rounded (caps) to emphasize a smooth, continuous journey toward health goals.

## Components

### Buttons & Inputs
- **Primary FAB:** A circular 56px button in Primary Green with a white `+` icon. High elevation.
- **Stepper Controls:** A three-part segment. Minus and Plus icons should be Primary Green on a subtle gray background, with the numeric value centered in bold Inter.
- **Search Bar:** 16px rounded, subtle border, with an "active" state that thickens the border and adds a soft green outer glow.

### Data Visualization
- **Circular Progress:** 8px stroke width. The "track" is a light gray version of the primary color, while the "indicator" is the vibrant Primary Green.
- **Linear Progress:** 6px height. Used for macros. Each macro has a specific assigned color (Protein: Blue, Fat: Orange, Carbs: Green).

### Cards & Lists
- **Food Item Card:** Flat design with a 1px border. On long-press, the card should elevate (shadow appears) and provide a haptic pulse.
- **Nutrition Fact Table:** Monospaced fonts for numbers to ensure decimal alignment.

### Feedback Systems
- **Skeleton Loaders:** Use a subtle "shimmer" animation over light gray shapes during API fetch states.
- **Snack-bars:** Dark gray background with a high-contrast "Undo" button in Secondary Orange.