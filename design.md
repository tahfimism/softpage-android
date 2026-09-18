# SoftPage / DeskMate - Design Specification

This document contains the guidelines and design specifications for the project interface. Future developers should refer to this file to maintain a consistent visual and functional format.

## Theme Overview
- **Style:** Dark Minimal Design
- **Concept:** Information-dense but uncluttered interface using a dark canvas with warm accent colors (avoiding heavy blues) and high-quality glassmorphism effects. 

## Typography
The project relies on clean, sans-serif typography for maximum readability.
- **Primary Font:** `'Inter'`, `-apple-system`, `BlinkMacSystemFont`, `sans-serif` (fallback)
- **Monospace Font (Code/Hardware Sections):** `'SF Mono'`, `'Fira Code'`, `'Courier New'`, `monospace`
- **Line Height:** `1.6` base
- **Font Smoothing:** Antialiased (`-webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale;`)

## Color Palette

### Backgrounds
- **Primary Page Background:** `#0a0a0a`
- **Secondary Background:** `#111111`
- **Tertiary Background:** `#1a1a1a`
- **Card Background:** `#151515`

### Text
- **Primary Text:** `#ffffff`
- **Secondary Text:** `#a0a0a0`
- **Muted Text:** `#666666`

### Accents
- **Primary Accent (Warm Orange):** `#ff6b35`
- **Secondary Accent:** `#e8e8e8`
- **Accent Glow:** `rgba(255, 107, 53, 0.15)`

### Borders
- **Default Border:** `#2a2a2a`
- **Light Border:** `#333333`

## CSS Variables / Tailwind Config
The theme aligns directly with the `tailwind.config.cjs` settings and CSS Variables defined in `:root`. 
Always use predefined Tailwind classes (e.g. `bg-bg-primary`, `text-accent-primary`) or CSS map variables when creating new components.

## UI Elements & Border Radius
- **General Elements (Buttons, Inputs, Small cards):** `8px` (`rounded-lg` or `border-radius: 8px`)
- **Code Blocks, State Blocks, Hardware Items:** `12px` (`border-radius: 12px`)
- **Main Feature Cards, Device Mockups:** `16px` (`rounded-2xl` or `border-radius: 16px`)
- **Scrollbar Thumbs:** `4px`
- **Ranges/Sliders Thumbs:** Circular border radius `50%`

## Component Styling Specifications

### Glassmorphism Effect
Utility class `.glass` used for Navbars and overlays:
- **Background:** `rgba(10, 10, 10, 0.8)`
- **Backdrop Filter:** `blur(20px)`

### Focus & Selection States
- **Focus-visible:** Any interactable element uses `outline: 2px solid #ff6b35; outline-offset: 2px;`
- **Text Selection:** Text selection highlight is built with `background: rgba(255, 107, 53, 0.3); color: #ffffff;`

### Custom Scrollbar
- **Width/Height:** `8px`
- **Track Color:** `#111111`
- **Thumb Default:** `#333333` (Rounded 4px)
- **Thumb Hover:** `#444444`

### Input Ranges (Sliders)
- Completely customized without default `-webkit-appearance`.
- **Track Height:** `6px`, background `#1a1a1a`, radius `3px`.
- **Thumb Elements:** `16px x 16px`, background `#ff6b35`, `50%` wrapper. Includes a soft box-shadow `0 2px 6px rgba(255, 107, 53, 0.3)`. Hover scales up to `1.1`.

### Buttons
Buttons should feel tactile.
- **Transitions:** Usually set to `0.3s ease` (`var(--transition-medium)`).
- **Primary Button:** Background `@accent-primary`, Text `Black (#000)`. On hover it elevates `-2px` on the Y-axis. 
- **Secondary Button:** Transparent background, `1px solid var(--border-light)`. On hover moves to tertiary backgrounds.

## Layout & Spacing Defaults
- **Max Container Width:** `1200px`
- **Standard Section Padding:** Responsive clamp based on viewport: `clamp(4rem, 10vw, 8rem)`
- Basic grid layouts default to 3-columns collapsing into 1-column responsive layout grids.

## Animations
Built-in mapped animations across keyframes:
- **Fade In Up:** `.animate-fade-in-up` / `fadeInUp 0.6s ease forwards`
- **Spin:** `.animate-spin` / `1s linear infinite`
- **Pulse:** `.animate-pulse` / `2s cubic-bezier(0.4, 0, 0.6, 1) infinite`

## Transitions Speeds
- **Fast:** `0.15s ease` (Hovers, input selections)
- **Medium:** `0.3s ease` (Button states, structural changes)