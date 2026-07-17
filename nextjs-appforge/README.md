# ARVION Next.js 14 Web Application Builder Stack

ARVION is an AI-powered visual builder that lets developers sign in with Google, describe application layouts, instantly compile complete codebases via Gemini Pro APIs, test responsiveness on simulated mobile/tablet viewports, and deploy instantly.

This subdirectory contains the complete, production-ready, TypeScript-strictly-typed full-stack codebase for the ARVION visual builder platform built on **Next.js 14 (App Router)**.

---

## 🛠️ Architecture Stack & Integrations

- **Frontend Core:** Next.js 14 (App Router) with React, styled using Tailwind CSS and structured with Glassmorphic dark layouts.
- **Visual Identity:** Custom flat geometric SVG monogram Logo.tsx using Electric Blue (#3B82F6) and Deep Indigo (#4F46E5).
- **Micro-animations:** Liquid motion effects, button glows, and page typing animations using Framer Motion.
- **Authentication:** Secure Google OAuth provider sessions managed via NextAuth.js and JWT tokens.
- **Database Layer:** SQLite client persistence utilizing Prisma ORM with strict schemas mapping Users, Sessions, Accounts, and Projects.
- **AI Synthesis Core:** Live text-to-code compiler integrating Google Generative AI (Gemini 2.0 Flash) pipelines.
- **Deployment & Sharing:** Sandboxed iframe compilers with custom subdomains and dynamically rendered base64 QR codes using the node `qrcode` package.
- **Pricing & Checkout Models:** Premium plans (Pro, Team, Enterprise) featuring commented scripts for Stripe Checkout Webhooks, Razorpay gateways, and PayPal buttons.
- **Creator Economy Marketplace:** Community portal supporting buying/selling of pre-built responsive templates with commented 20% platform commission payout queries.

---

## 🚀 Step-by-Step Local Deployment Setup

To run ARVION seamlessly on your local machine:

### 1. Prerequisite Installations
Ensure you have Node.js 18+ and npm installed.

### 2. Install Project Dependencies
Navigate to the project directory and run the install script:
```bash
cd nextjs-arvion
npm install
```

### 3. Setup Environment Variables
Create a `.env.local` file at the root of the `nextjs-arvion` directory. Copy the contents of `.env.example` and fill in your developer keys:
```bash
cp .env.example .env.local
```

Open `.env.local` and add your real developer keys:
- `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`: Generate Google Client credentials in the Google Cloud Console (APIs & Services > Credentials). Add `http://localhost:3000/api/auth/callback/google` as an Authorized Redirect URI.
- `NEXTAUTH_SECRET`: Generate a random 32-character string for securing JWT payloads (e.g. run `openssl rand -base64 32`).
- `GOOGLE_API_KEY`: Get a free key from Google AI Studio (https://aistudio.google.com) to drive the code generation engine.

### 4. Create and Migrate the Database
Initialize your SQLite local database and create the Prisma client mappings:
```bash
npx prisma migrate dev --name init
```

### 5. Spin Up the Local Development Server
Launch the local Hot Reload development tunnel:
```bash
npm run dev
```

Open **[http://localhost:3000](http://localhost:3000)** in your browser!

---

## 📂 Core Folder Structure

```
/nextjs-arvion/
├── prisma/
│   └── schema.prisma         # SQLite database schema (Users, Accounts, Sessions, Projects)
├── lib/
│   ├── prisma.ts             # PrismaClient singleton initializer
│   └── auth.ts               # NextAuth Google Provider config & callbacks
├── middleware.ts             # Route guard protecting /dashboard and /builder paths
├── components/
│   └── Navbar.tsx            # Global Navigation component with Auth Dropdown
├── app/
│   ├── layout.tsx            # App root layout with dynamic provider wrapper
│   ├── providers.tsx         # NextAuth client session provider
│   ├── page.tsx              # Homepage Landing with animations & CTA
│   ├── dashboard/
│   │   └── page.tsx          # Project Grid view, user profile, and creation modal
│   ├── builder/
│   │   └── [id]/
│   │       └── page.tsx      # Split-screen Editor & Live Sandbox Iframe with AI Chat
│   ├── pricing/
│   │   └── page.tsx          # Subscription plans & commented checkout integrations
│   ├── marketplace/
│   │   └── page.tsx          # Shared templates feed & commented commission queries
│   └── api/
│       ├── auth/
│       │   └── [...nextauth]/
│       │       └── route.ts  # NextAuth API router handler
│       ├── generate/
│       │   └── route.ts      # Gemini Code Synthesis core controller
│       ├── projects/
│       │   ├── route.ts      # Projects GET list and POST save endpoint
│       │   └── [id]/
│       │       └── route.ts  # Single Project GET / PUT update / DELETE endpoint
│       └── deploy/
│           └── route.ts      # Subdomain generator and dynamic QR Code builder
```
