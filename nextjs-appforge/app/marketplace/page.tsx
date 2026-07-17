"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import { ShoppingBag, Sparkles, Code, Flame, Check, Coins, Eye, ShoppingCart, User, MessageSquareCode } from "lucide-react";

export default function Marketplace() {
  const [purchasingId, setPurchasingId] = useState<string | null>(null);

  const handlePurchase = async (templateId: string, templateName: string, price: number) => {
    setPurchasingId(templateId);

    // Simulate buying template
    setTimeout(() => {
      setPurchasingId(null);
      alert(`Successfully purchased template: "${templateName}"! The template is copied to your dashboard blueprints instantly. Checkout commissions are logged under marketplace ledger.`);
    }, 1500);

    /*
      ========================================================================
      MARKETPLACE PLATFORM COMMISSION REVENUE LEDGER LOGIC (20% COMMISSION):
      ========================================================================
      
      const totalAmount = price; // E.g., $25.00
      const commissionRate = 0.20; // 20% ARVION Commission Fee
      const arvionRevenueShare = totalAmount * commissionRate; // $5.00 Platform Revenue
      const sellerPayableEarnings = totalAmount - arvionRevenueShare; // $20.00 Seller Earnings
      
      // Update database tables to record transactional balances:
      await prisma.transaction.create({
        data: {
          templateId,
          buyerId: session.user.id,
          totalAmount,
          commissionFee: arvionRevenueShare,
          sellerEarnings: sellerPayableEarnings,
          status: "SUCCESS"
        }
      });

      // Increment seller balance for payouts:
      await prisma.user.update({
        where: { id: template.sellerId },
        data: {
          marketplaceBalance: {
            increment: sellerPayableEarnings
          }
        }
      });

      // Clone template blueprint records to buyer's active projects:
      await prisma.project.create({
        data: {
          name: `${template.name} (Purchased)`,
          description: template.description,
          code: template.code,
          framework: template.framework,
          userId: session.user.id,
        }
      });
    */
  };

  const templates = [
    {
      id: "tpl_1",
      name: "SaaS Analytics Dashboard",
      desc: "Complete visual dashboard workspace loaded with charts, data grids, task managers, and beautiful glassmorphism gradients.",
      framework: "react",
      price: 29.00,
      rating: "4.9",
      sales: 142,
      seller: "Alex Rivers",
      tags: ["Dashboard", "Charts", "SaaS"],
    },
    {
      id: "tpl_2",
      name: "Neo-Brutalist Portfolio",
      desc: "High-contrast minimalist landing portfolio designed with responsive custom card layouts, grid views, and Inquiry contact models.",
      framework: "html",
      price: 15.00,
      rating: "4.7",
      sales: 85,
      seller: "Sarah Chen",
      tags: ["Portfolio", "Landing", "Brutalist"],
    },
    {
      id: "tpl_3",
      name: "DeFi Crypto Tracker",
      desc: "Crypto exchange rates watchlist dashboard featuring list filters, simulated live tickers, and currency converter tools.",
      framework: "react",
      price: 39.00,
      rating: "5.0",
      sales: 210,
      seller: "Blockchain Labs",
      tags: ["DeFi", "Crypto", "Widgets"],
    },
    {
      id: "tpl_4",
      name: "Calm Mindfulness App",
      desc: "Aesthetic single-viewport meditation companion featuring timers, glowing loop containers, and custom breathing animations.",
      framework: "html",
      price: 19.00,
      rating: "4.8",
      sales: 64,
      seller: "Zen Design Co",
      tags: ["Health", "Animations", "Aesthetic"],
    },
  ];

  return (
    <div className="flex-1 bg-[#0a0a0a] px-6 py-12 md:py-20 max-w-7xl mx-auto w-full space-y-12">
      <div className="text-center max-w-2xl mx-auto space-y-4">
        <span className="bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 text-xs px-3.5 py-1.5 rounded-full font-bold uppercase">
          Creator Economy
        </span>
        <h2 className="text-3xl sm:text-5xl font-extrabold text-white">Templates Marketplace</h2>
        <p className="text-neutral-400 text-sm sm:text-base leading-relaxed">
          Unlock pre-built responsive application blueprints designed by top developers, or sell your own forged code bases to developers worldwide.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {templates.map((tpl) => (
          <motion.div
            key={tpl.id}
            whileHover={{ y: -4, borderColor: "rgba(99,102,241,0.3)" }}
            className="bg-neutral-900/35 border border-neutral-800 p-6 rounded-2xl flex flex-col md:flex-row justify-between gap-6 backdrop-blur-sm"
          >
            <div className="flex-1 flex flex-col justify-between space-y-4">
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  <span className={`text-[9px] font-black uppercase px-2 py-0.5 rounded-full border ${
                    tpl.framework === "react"
                      ? "bg-sky-500/10 border-sky-500/20 text-sky-400"
                      : "bg-amber-500/10 border-amber-500/20 text-amber-400"
                  }`}>
                    {tpl.framework === "react" ? "React CDN" : "HTML5 Web"}
                  </span>
                  <span className="text-neutral-500 text-[10px] flex items-center space-x-1">
                    <User size={10} />
                    <span>{tpl.seller}</span>
                  </span>
                </div>
                <h4 className="text-white font-extrabold text-lg">{tpl.name}</h4>
                <p className="text-neutral-400 text-xs leading-relaxed line-clamp-3">{tpl.desc}</p>
              </div>

              <div className="flex flex-wrap gap-2 pt-2">
                {tpl.tags.map((tag) => (
                  <span key={tag} className="text-[10px] bg-neutral-900 border border-neutral-850 px-2 py-1 rounded-md text-neutral-400 font-semibold">
                    #{tag}
                  </span>
                ))}
              </div>
            </div>

            <div className="w-full md:w-48 bg-neutral-950/80 border border-neutral-800 rounded-xl p-4 flex flex-col justify-between space-y-4 text-center shrink-0">
              <div className="space-y-1">
                <span className="text-neutral-500 text-[10px] font-bold uppercase tracking-wider block">Template Value</span>
                <span className="text-white font-black text-2xl">${tpl.price.toFixed(2)}</span>
                <div className="text-[10px] text-emerald-400 font-medium">⭐ {tpl.rating} rating</div>
              </div>

              <button
                onClick={() => handlePurchase(tpl.id, tpl.name, tpl.price)}
                disabled={purchasingId !== null}
                className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-extrabold text-xs py-2.5 rounded-lg flex items-center justify-center space-x-1.5 transition active:scale-95"
              >
                {purchasingId === tpl.id ? (
                  <div className="w-3.5 h-3.5 border-2 border-white border-solid border-t-transparent rounded-full animate-spin"></div>
                ) : (
                  <>
                    <ShoppingCart size={12} />
                    <span>Buy License</span>
                  </>
                )}
              </button>

              <span className="text-[9px] text-neutral-500">
                Lifetime copy license
              </span>
            </div>
          </motion.div>
        ))}
      </div>

      <div className="bg-blue-600/5 border border-blue-500/25 p-6 rounded-2xl flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="space-y-2 max-w-xl">
          <div className="flex items-center space-x-2">
            <Sparkles size={16} className="text-blue-400 animate-pulse" />
            <h5 className="text-white font-bold text-sm">Build & Monetize Templates</h5>
          </div>
          <p className="text-neutral-400 text-xs leading-relaxed">
            Have you crafted a gorgeous dashboard or portfolio page in ARVION? Package it as a premium Template directly from your workspace and list it here. ARVION charges a 20% platform listing fee to fuel continuous high-speed GPU computing.
          </p>
        </div>
        <button
          onClick={() => alert("Template publication engine is integrated inside each project workspace settings! Build a blueprint first to publish.")}
          className="bg-neutral-900 border border-neutral-800 hover:bg-neutral-800 text-blue-400 font-extrabold text-xs px-5 py-3 rounded-xl transition shrink-0"
        >
          Publish My Blueprint
        </button>
      </div>
    </div>
  );
}
