import "./globals.css";
import React from "react";
import Navbar from "../components/Navbar";
import { NextAuthProvider } from "./providers";

export const metadata = {
  title: "ARVION - Build Apps with AI",
  description: "Describe your app idea and instantly compile, edit, and preview fully functional websites with ARVION.",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <head>
        <link
          rel="icon"
          href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Cpath d='M 47,12 L 15,88 L 29,88 L 33,78 L 47,78 L 47,72 L 35.5,72 L 37.5,67 L 47,67 L 47,60 L 38,48 L 47,36 Z' fill='%233B82F6'/%3E%3Cpath d='M 53,12 L 85,88 L 71,88 L 67,78 L 53,78 L 53,72 L 64.5,72 L 62.5,67 L 53,67 L 53,60 L 62,48 L 53,36 Z' fill='%234F46E5'/%3E%3C/svg%3E"
          type="image/svg+xml"
        />
      </head>
      <body className="bg-[#0a0a0a] text-white min-h-screen flex flex-col selection:bg-indigo-500 selection:text-white">
        <NextAuthProvider>
          <Navbar />
          <main className="flex-1 flex flex-col">{children}</main>
        </NextAuthProvider>
      </body>
    </html>
  );
}
