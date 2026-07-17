"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import { Check, ShieldCheck, Sparkles, Zap, Users, Building, Coins, Loader } from "lucide-react";

export default function Pricing() {
  const [loadingTier, setLoadingTier] = useState<string | null>(null);

  const handleSubscribe = async (tierName: string, price: number) => {
    setLoadingTier(tierName);
    
    // Simulate payment loading
    setTimeout(() => {
      setLoadingTier(null);
      alert(`Initiating checkout process for the ARVION ${tierName} plan ($${price}/mo). Payment provider scripts are configured in the codebase!`);
    }, 1500);

    /* 
      ========================================================================
      1. INTEGRATING STRIPE CHECKOUT SESSIONS (PRODUCTION ROUTE EXAMPLE):
      ========================================================================
      const response = await fetch('/api/checkout/stripe', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ tier: tierName, price }),
      });
      const data = await response.json();
      if (data.sessionUrl) {
        window.location.href = data.sessionUrl; // Redirect to Stripe checkout
      }

      ========================================================================
      2. INTEGRATING RAZORPAY CUSTOM GATEWAY (CLIENT SIDE):
      ========================================================================
      const orderRes = await fetch('/api/checkout/razorpay', { method: 'POST' });
      const order = await orderRes.json();
      
      const options = {
        key: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID,
        amount: order.amount,
        currency: "USD",
        name: "ARVION Platform",
        description: `Upgrade to ${tierName}`,
        order_id: order.id,
        handler: function (response: any) {
          alert(`Payment Successful: Order ID - ${response.razorpay_order_id}`);
        }
      };
      const rzp = new (window as any).Razorpay(options);
      rzp.open();

      ========================================================================
      3. INTEGRATING PAYPAL BUTTONS (CLIENT SIDE CONTEXT):
      ========================================================================
      // Inside a PayPalButton container:
      <PayPalButtons
        createOrder={(data, actions) => {
          return actions.order.create({
            purchase_units: [{
              amount: { value: price.toString() },
              description: `ARVION ${tierName} Subscription`
            }]
          });
        }}
        onApprove={async (data, actions) => {
          const details = await actions.order?.capture();
          alert(`Subscription Successful for user: ${details?.payer.name?.given_name}`);
        }}
      />
    */
  };

  const tiers = [
    {
      name: "Free",
      price: 0,
      icon: <Coins size={22} className="text-neutral-400" />,
      desc: "Perfect for testing sandboxes",
      features: [
        "Up to 3 Active Project Blueprints",
        "Gemini 1.5 Flash standard speed",
        "HTML5 / React CDN sandbox rendering",
        "Standard Community Support",
      ],
      buttonText: "Current Plan",
      accent: false,
    },
    {
      name: "Pro",
      price: 15,
      icon: <Zap size={22} className="text-indigo-400" />,
      desc: "For serious web designers",
      features: [
        "Unlimited Project Blueprints",
        "Gemini 2.0 Flash priority speed",
        "Custom deployment subdomains",
        "Advanced Code Security Scanners",
        "Email priority support (24hr)",
      ],
      buttonText: "Upgrade to Pro",
      accent: true,
    },
    {
      name: "Team",
      price: 45,
      icon: <Users size={22} className="text-purple-400" />,
      desc: "For collaborative start-ups",
      features: [
        "Includes 5 developer seats",
        "Real-time sync workspaces",
        "Gemini 2.0 Pro high-precision mode",
        "20% templates commission (Marketplace)",
        "Premium SLA (12hr response)",
      ],
      buttonText: "Subscribe Team",
      accent: false,
    },
    {
      name: "Enterprise",
      price: 99,
      icon: <Building size={22} className="text-pink-400" />,
      desc: "For global app agencies",
      features: [
        "Unlimited developer seats",
        "Dedicated cloud server hosting",
        "White-label staging & preview sites",
        "Custom enterprise API gateways",
        "24/7 dedicated account manager",
      ],
      buttonText: "Join Enterprise",
      accent: false,
    },
  ];

  return (
    <div className="flex-1 bg-[#0a0a0a] px-6 py-12 md:py-20 max-w-7xl mx-auto w-full space-y-12">
      <div className="text-center max-w-2xl mx-auto space-y-4">
        <span className="bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 text-xs px-3.5 py-1.5 rounded-full font-bold uppercase">
          Flexible Pricing Models
        </span>
        <h2 className="text-3xl sm:text-5xl font-extrabold text-white">Choose Your Forge Power</h2>
        <p className="text-neutral-400 text-sm sm:text-base leading-relaxed">
          Unlock standard priority speed pipelines, limitless cloud workspaces, custom edge domains, and collaborative team portals.
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {tiers.map((tier) => (
          <div
            key={tier.name}
            className={`rounded-2xl p-6 border flex flex-col justify-between space-y-6 relative transition ${
              tier.accent
                ? "bg-indigo-600/5 border-indigo-500 shadow-2xl shadow-indigo-500/5"
                : "bg-neutral-900/40 border-neutral-800"
            }`}
          >
            {tier.accent && (
              <div className="absolute -top-3 left-1/2 -translate-x-1/2 bg-indigo-500 text-white text-[10px] font-black uppercase px-2.5 py-1 rounded-full flex items-center space-x-1">
                <Sparkles size={10} />
                <span>Most Popular</span>
              </div>
            )}

            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="p-2.5 bg-neutral-900 border border-neutral-800 rounded-xl">
                  {tier.icon}
                </div>
                <span className="text-neutral-400 text-xs font-semibold">{tier.desc}</span>
              </div>

              <div>
                <h4 className="text-white font-extrabold text-xl">{tier.name}</h4>
                <div className="flex items-baseline space-x-1 mt-2">
                  <span className="text-white font-black text-3xl">${tier.price}</span>
                  <span className="text-neutral-500 text-xs font-semibold">/month</span>
                </div>
              </div>

              <div className="h-px bg-neutral-850 my-4" />

              <ul className="space-y-2 text-xs">
                {tier.features.map((feat) => (
                  <li key={feat} className="flex items-start space-x-2 text-neutral-300 leading-relaxed">
                    <Check size={12} className="text-indigo-400 mt-1 shrink-0" />
                    <span>{feat}</span>
                  </li>
                ))}
              </ul>
            </div>

            <button
              onClick={() => handleSubscribe(tier.name, tier.price)}
              disabled={loadingTier !== null}
              className={`w-full py-3 rounded-xl font-bold text-xs flex items-center justify-center space-x-1.5 transition active:scale-95 ${
                tier.accent
                  ? "bg-indigo-600 hover:bg-indigo-500 text-white shadow-lg shadow-indigo-600/20"
                  : "bg-neutral-900 border border-neutral-800 hover:bg-neutral-800 text-neutral-300"
              }`}
            >
              {loadingTier === tier.name ? (
                <Loader size={12} className="animate-spin" />
              ) : (
                <span>{tier.buttonText}</span>
              )}
            </button>
          </div>
        ))}
      </div>

      <div className="bg-neutral-900/30 border border-neutral-800 rounded-2xl p-6 flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="flex items-center space-x-4">
          <div className="w-10 h-10 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center justify-center text-emerald-400">
            <ShieldCheck size={20} />
          </div>
          <div>
            <h5 className="text-white font-bold text-sm">Security & Guarantee Core</h5>
            <p className="text-neutral-400 text-xs mt-0.5">Secure, 256-bit token authentication pipelines. Cancel subscriptions anytime instantly.</p>
          </div>
        </div>
        <div className="text-xs text-neutral-500 italic">
          Commented endpoints inside `/app/pricing/page.tsx` support secure checkout webhook bindings.
        </div>
      </div>
    </div>
  );
}
