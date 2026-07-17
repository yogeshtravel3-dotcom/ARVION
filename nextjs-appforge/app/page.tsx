"use client";

import { useSession, signIn } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { Sparkles, Terminal, Code, Cpu, ShieldAlert, Rocket } from "lucide-react";

export default function Home() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [typingText, setTypingText] = useState("");
  const targetText = "Build reactive SaaS models, custom portfolio screens, or HTML5 apps in under 30 seconds.";

  useEffect(() => {
    if (status === "authenticated") {
      router.push("/dashboard");
    }
  }, [status, router]);

  useEffect(() => {
    let index = 0;
    const interval = setInterval(() => {
      setTypingText((prev) => prev + targetText.charAt(index));
      index++;
      if (index >= targetText.length) {
        clearInterval(interval);
      }
    }, 45);
    return () => clearInterval(interval);
  }, []);

  if (status === "loading" || status === "authenticated") {
    return (
      <div className="flex-1 flex items-center justify-center bg-[#0a0a0a]">
        <div className="flex flex-col items-center space-y-4">
          <div className="w-12 h-12 border-t-2 border-blue-500 border-solid rounded-full animate-spin"></div>
          <span className="text-neutral-400 text-sm font-medium">Entering ARVION Portal...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 bg-[#0a0a0a] relative overflow-hidden flex flex-col justify-center px-6 py-12 md:py-24">
      {/* Floating Particles Background */}
      <div className="absolute inset-0 pointer-events-none opacity-25">
        {[...Array(25)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-1 h-1 bg-blue-500 rounded-full"
            style={{
              top: `${Math.random() * 100}%`,
              left: `${Math.random() * 100}%`,
            }}
            animate={{
              y: [0, -30, 0],
              opacity: [0.2, 0.8, 0.2],
            }}
            transition={{
              duration: 3 + Math.random() * 4,
              repeat: Infinity,
              ease: "easeInOut",
            }}
          />
        ))}
      </div>

      <div className="max-w-5xl mx-auto text-center z-10 space-y-8">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="inline-flex items-center space-x-2 bg-blue-500/10 border border-blue-500/20 px-3.5 py-1.5 rounded-full text-blue-400 text-xs font-semibold"
        >
          <Sparkles size={12} className="text-blue-400" />
          <span>Google Gemini 2.0 Pro Core Engine Active</span>
        </motion.div>

        <motion.h1
          initial={{ opacity: 0, y: 25 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.7, delay: 0.1 }}
          className="text-4xl sm:text-6xl md:text-7xl font-extrabold tracking-tight text-white leading-none"
        >
          ARVION <br />
          <span className="bg-gradient-to-r from-blue-400 to-indigo-500 bg-clip-text text-transparent">
            Build Apps with AI
          </span>
        </motion.h1>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8, delay: 0.3 }}
          className="max-w-2xl mx-auto h-16 sm:h-12 text-neutral-400 font-medium text-sm sm:text-base leading-relaxed"
        >
          {typingText}
          <span className="animate-pulse font-bold text-blue-500">|</span>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.5, delay: 0.4 }}
          className="flex justify-center"
        >
          <button
            onClick={() => signIn("google")}
            className="group relative bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white font-bold text-base px-8 py-4 rounded-xl shadow-2xl shadow-blue-500/20 active:scale-95 transition duration-200"
          >
            {/* Button pulse glow */}
            <span className="absolute inset-0 rounded-xl bg-blue-500 blur-md opacity-25 group-hover:opacity-40 transition" />
            <span className="relative flex items-center space-x-2">
              <Terminal size={18} />
              <span>Get Started & Build Free</span>
            </span>
          </button>
        </motion.div>

        {/* Feature Cards Grid with hover lifts */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.5 }}
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 pt-12 text-left"
        >
          {[
            {
              icon: <Code className="text-blue-400" size={24} />,
              title: "NextJS & HTML5 Engine",
              desc: "Instant live rendering environment supporting fully responsive HTML, CDN bundles, and structured NextJS files.",
            },
            {
              icon: <Cpu className="text-indigo-400" size={24} />,
              title: "Gemini 2.0 Flash Core",
              desc: "Leverage advanced streaming AI generation context windows to synthesize interactive logic, routing, and visuals.",
            },
            {
              icon: <Rocket className="text-blue-400" size={24} />,
              title: "One-Click Edge Deploy",
              desc: "Deploy sandboxed designs to personalized high-speed mock subdomains with interactive shareable QR codes.",
            },
            {
              icon: <ShieldAlert className="text-indigo-400" size={24} />,
              title: "Live Guard Quality Checks",
              desc: "Auto-scan and verify security vulnerabilities, script injection hazards, and code bugs in real-time.",
            },
          ].map((item, index) => (
            <motion.div
              key={index}
              whileHover={{ y: -6, borderColor: "rgba(59,130,246,0.4)" }}
              className="bg-neutral-900/40 border border-neutral-800 p-6 rounded-2xl backdrop-blur-sm transition duration-200 cursor-pointer"
            >
              <div className="w-10 h-10 bg-neutral-900 rounded-lg flex items-center justify-center mb-4 border border-neutral-800">
                {item.icon}
              </div>
              <h3 className="text-white font-bold text-base mb-2">{item.title}</h3>
              <p className="text-neutral-400 text-xs leading-relaxed">{item.desc}</p>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </div>
  );
}
