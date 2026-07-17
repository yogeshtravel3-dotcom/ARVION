"use client";

import { useSession } from "next-auth/react";
import { useRouter, useParams } from "next/navigation";
import { useEffect, useState, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Smartphone,
  Tablet,
  Monitor,
  Play,
  Save,
  CloudLightning,
  Sparkles,
  MessageSquare,
  Send,
  X,
  Trash2,
  Check,
  AlertTriangle,
  QrCode,
  Download,
  Terminal,
  Loader
} from "lucide-react";

interface Project {
  id: string;
  name: string;
  description: string;
  code: string;
  framework: string;
}

interface ChatMessage {
  sender: "user" | "ai";
  text: string;
}

export default function Builder() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const params = useParams();
  const projectId = params.id as string;

  // Project data states
  const [project, setProject] = useState<Project | null>(null);
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [showSaveSuccess, setShowSaveSuccess] = useState(false);

  // Preview sizing states
  const [deviceSize, setDeviceSize] = useState<"mobile" | "tablet" | "desktop">("desktop");

  // Floating AI Chat states
  const [chatOpen, setChatOpen] = useState(false);
  const [chatPrompt, setChatPrompt] = useState("");
  const [chatHistory, setChatHistory] = useState<ChatMessage[]>([
    { sender: "ai", text: "Welcome to ARVION Workspace! Highlight a feature or describe an integration you would like to write (e.g. 'Add a dark mode toggle button' or 'Add an automated digital timer clock')." }
  ]);
  const [aiGenerating, setAiGenerating] = useState(false);
  const [showConfetti, setShowConfetti] = useState(false);

  // Deployment states
  const [deploying, setDeploying] = useState(false);
  const [deployedUrl, setDeployedUrl] = useState("");
  const [qrCodeUrl, setQrCodeUrl] = useState("");
  const [showDeployModal, setShowDeployModal] = useState(false);

  const iframeRef = useRef<HTMLIFrameElement>(null);

  useEffect(() => {
    if (status === "unauthenticated") {
      router.push("/");
    } else if (status === "authenticated" && projectId) {
      fetchProject();
    }
  }, [status, projectId, router]);

  const fetchProject = async () => {
    try {
      const res = await fetch(`/api/projects/${projectId}`);
      if (res.ok) {
        const data = await res.json();
        setProject(data);
        setCode(data.code);
      } else {
        router.push("/dashboard");
      }
    } catch (err) {
      console.error("Error loading project:", err);
      router.push("/dashboard");
    } finally {
      setLoading(false);
    }
  };

  const handleManualSave = async () => {
    if (!project) return;
    setSaving(true);
    try {
      const res = await fetch(`/api/projects/${projectId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code }),
      });
      if (res.ok) {
        setShowSaveSuccess(true);
        setTimeout(() => setShowSaveSuccess(false), 2500);
      }
    } catch (err) {
      console.error("Error saving code:", err);
    } finally {
      setSaving(false);
    }
  };

  const handleDeploy = async () => {
    if (!project) return;
    setDeploying(true);
    try {
      const res = await fetch("/api/deploy", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ projectId }),
      });
      if (res.ok) {
        const data = await res.json();
        setDeployedUrl(data.deployedUrl);
        setQrCodeUrl(data.qrCode);
        setShowDeployModal(true);
      }
    } catch (err) {
      console.error("Deployment failure:", err);
    } finally {
      setDeploying(false);
    }
  };

  const handleDeleteProject = async () => {
    if (!confirm("Are you sure you want to delete this project? This action is irreversible.")) return;
    try {
      const res = await fetch(`/api/projects/${projectId}`, {
        method: "DELETE",
      });
      if (res.ok) {
        router.push("/dashboard");
      }
    } catch (err) {
      console.error("Error deleting project:", err);
    }
  };

  const handleAiRefinement = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!chatPrompt.trim() || aiGenerating) return;

    const userMessage = chatPrompt;
    setChatHistory((prev) => [...prev, { sender: "user", text: userMessage }]);
    setChatPrompt("");
    setAiGenerating(true);

    try {
      const promptPayload = `The user wants to refine/add a feature to their current application code.
Current application source code:
${code}

Requested modification / addition instruction:
${userMessage}

Please return the COMPLETE updated code. Do NOT write explanations. Return ONLY the raw code.`;

      const res = await fetch("/api/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          prompt: promptPayload,
          framework: project?.framework || "html",
        }),
      });

      if (res.ok) {
        const data = await res.json();
        setCode(data.code);
        setChatHistory((prev) => [
          ...prev,
          { sender: "ai", text: "Successfully forged and updated your application codebase! Check the live preview panel on the right." }
        ]);

        // Auto-save updated code to db
        await fetch(`/api/projects/${projectId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ code: data.code }),
        });

        // Trigger Confetti Pop Effect
        setShowConfetti(true);
        setTimeout(() => setShowConfetti(false), 5000);
      } else {
        const data = await res.json();
        setChatHistory((prev) => [
          ...prev,
          { sender: "ai", text: `Failed to compile modifications: ${data.error || "Unknown server error"}` }
        ]);
      }
    } catch (err: any) {
      setChatHistory((prev) => [
        ...prev,
        { sender: "ai", text: `Error connecting to generation engine: ${err.message}` }
      ]);
    } finally {
      setAiGenerating(false);
    }
  };

  const triggerIframeRefresh = () => {
    if (iframeRef.current) {
      iframeRef.current.srcdoc = code;
    }
  };

  // Auto-refresh preview whenever code changes
  useEffect(() => {
    if (code) {
      const timer = setTimeout(() => {
        triggerIframeRefresh();
      }, 500);
      return () => clearTimeout(timer);
    }
  }, [code]);

  if (status === "loading" || loading) {
    return (
      <div className="flex-1 flex items-center justify-center bg-[#0a0a0a]">
        <div className="flex flex-col items-center space-y-4">
          <div className="w-12 h-12 border-t-2 border-indigo-500 border-solid rounded-full animate-spin"></div>
          <span className="text-neutral-400 text-sm font-medium">Entering Visual Workspace...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col bg-[#0a0a0a] overflow-hidden text-sm h-[calc(100vh-69px)]">
      {/* Top Builder Control Ribbon */}
      <header className="bg-neutral-900 border-b border-neutral-800 px-6 py-3 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center space-x-3">
          <h3 className="font-extrabold text-white text-base">{project?.name}</h3>
          <span className="text-[10px] bg-neutral-800 border border-neutral-750 text-neutral-400 px-2 py-0.5 rounded-full font-semibold uppercase">
            {project?.framework === "react" ? "React Engine" : "HTML5 Engine"}
          </span>
          <AnimatePresence>
            {showSaveSuccess && (
              <motion.span
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0 }}
                className="text-xs text-emerald-400 flex items-center space-x-1"
              >
                <Check size={12} />
                <span>Changes saved to SQLite</span>
              </motion.span>
            )}
          </AnimatePresence>
        </div>

        {/* Workspace controls */}
        <div className="flex items-center space-x-3">
          {/* Sizing toggles */}
          <div className="hidden sm:flex bg-neutral-950 border border-neutral-850 p-1 rounded-lg space-x-1">
            <button
              onClick={() => setDeviceSize("mobile")}
              className={`p-1.5 rounded-md hover:text-white transition ${
                deviceSize === "mobile" ? "bg-indigo-600 text-white shadow" : "text-neutral-400"
              }`}
              title="Mobile Preview (375px)"
            >
              <Smartphone size={15} />
            </button>
            <button
              onClick={() => setDeviceSize("tablet")}
              className={`p-1.5 rounded-md hover:text-white transition ${
                deviceSize === "tablet" ? "bg-indigo-600 text-white shadow" : "text-neutral-400"
              }`}
              title="Tablet Preview (768px)"
            >
              <Tablet size={15} />
            </button>
            <button
              onClick={() => setDeviceSize("desktop")}
              className={`p-1.5 rounded-md hover:text-white transition ${
                deviceSize === "desktop" ? "bg-indigo-600 text-white shadow" : "text-neutral-400"
              }`}
              title="Desktop Preview (100%)"
            >
              <Monitor size={15} />
            </button>
          </div>

          <button
            onClick={handleManualSave}
            disabled={saving}
            className="bg-neutral-850 border border-neutral-750 hover:bg-neutral-800 disabled:opacity-50 text-white font-semibold text-xs px-3.5 py-2 rounded-lg flex items-center space-x-1.5 transition active:scale-95"
          >
            {saving ? (
              <Loader size={12} className="animate-spin" />
            ) : (
              <Save size={12} />
            )}
            <span>Save</span>
          </button>

          <button
            onClick={handleDeploy}
            disabled={deploying}
            className="bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-bold text-xs px-3.5 py-2 rounded-lg flex items-center space-x-1.5 shadow-md shadow-indigo-600/20 transition active:scale-95"
          >
            {deploying ? (
              <Loader size={12} className="animate-spin" />
            ) : (
              <CloudLightning size={12} />
            )}
            <span>Deploy</span>
          </button>

          <button
            onClick={handleDeleteProject}
            className="p-2 rounded-lg border border-neutral-800 text-neutral-500 hover:text-rose-400 hover:border-rose-950 transition active:scale-95"
            title="Delete Blueprint"
          >
            <Trash2 size={14} />
          </button>
        </div>
      </header>

      {/* Split Workspace Viewport */}
      <div className="flex-1 flex flex-col md:flex-row overflow-hidden relative">
        {/* Left Side: Code Editor Textarea */}
        <div className="w-full md:w-1/2 flex flex-col h-1/2 md:h-full border-b md:border-b-0 md:border-r border-neutral-800 bg-[#0a0a0a]">
          <div className="bg-neutral-950 px-4 py-2 border-b border-neutral-850 flex items-center justify-between text-xs text-neutral-400 font-mono">
            <span className="flex items-center space-x-1.5">
              <Terminal size={12} />
              <span>Source Editor</span>
            </span>
            <button
              onClick={triggerIframeRefresh}
              className="text-indigo-400 hover:text-indigo-300 font-bold transition flex items-center space-x-1"
            >
              <Play size={10} />
              <span>Compile View</span>
            </button>
          </div>

          <textarea
            value={code}
            onChange={(e) => setCode(e.target.value)}
            className="flex-1 w-full bg-neutral-950 text-neutral-200 font-mono text-xs p-6 outline-none resize-none leading-relaxed overflow-y-auto focus:text-white"
            placeholder="Write HTML, CSS, or React bundles here..."
            spellCheck={false}
          />
        </div>

        {/* Right Side: Responsive Sandbox Iframe Preview */}
        <div className="w-full md:w-1/2 flex flex-col h-1/2 md:h-full bg-neutral-950 justify-center items-center p-4 overflow-hidden relative">
          <div className="absolute top-2 left-4 text-[10px] text-neutral-500 font-mono pointer-events-none uppercase">
            Reactive Preview Canvas
          </div>

          <motion.div
            layout
            className="bg-[#0c0c0c] border border-neutral-800 rounded-xl overflow-hidden shadow-2xl h-full w-full flex flex-col"
            style={{
              maxWidth:
                deviceSize === "mobile"
                  ? "375px"
                  : deviceSize === "tablet"
                  ? "768px"
                  : "100%",
              height: "90%",
            }}
          >
            <div className="bg-neutral-900 border-b border-neutral-800 px-3 py-1.5 flex items-center justify-between">
              <div className="flex items-center space-x-1.5">
                <div className="w-2.5 h-2.5 rounded-full bg-rose-500/80" />
                <div className="w-2.5 h-2.5 rounded-full bg-amber-500/80" />
                <div className="w-2.5 h-2.5 rounded-full bg-emerald-500/80" />
              </div>
              <span className="text-[10px] font-mono text-neutral-500">
                {deviceSize === "mobile"
                  ? "Mobile Sandbox (375x667)"
                  : deviceSize === "tablet"
                  ? "Tablet Sandbox (768x1024)"
                  : "Desktop Sandbox (Liquid-Fluid)"}
              </span>
              <div className="w-8" />
            </div>

            <iframe
              ref={iframeRef}
              title="Sandbox"
              sandbox="allow-scripts allow-modals allow-same-origin allow-popups"
              className="flex-1 bg-white"
              srcDoc={code}
            />
          </motion.div>
        </div>
      </div>

      {/* Floating Confetti Success Overlay */}
      <AnimatePresence>
        {showConfetti && (
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0 }}
            className="fixed bottom-24 right-6 z-40 bg-neutral-900/95 border border-indigo-500/50 p-5 rounded-2xl shadow-2xl max-w-sm backdrop-blur-md text-center"
          >
            <div className="w-10 h-10 bg-indigo-500/10 border border-indigo-500/30 rounded-xl flex items-center justify-center mx-auto mb-3 text-indigo-400">
              <Sparkles size={20} />
            </div>
            <h4 className="text-white font-bold text-base">Workspace Refined!</h4>
            <p className="text-neutral-400 text-xs mt-1 leading-relaxed">
              Your modifications have been successfully woven into the live preview canvas seamlessly.
            </p>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Floating AI Chat Assistant Drawer */}
      <div className="fixed bottom-6 right-6 z-30">
        <button
          onClick={() => setChatOpen(!chatOpen)}
          className="bg-indigo-600 hover:bg-indigo-500 text-white p-4 rounded-full shadow-2xl shadow-indigo-600/30 border border-indigo-500 active:scale-95 transition flex items-center justify-center"
        >
          {chatOpen ? <X size={20} /> : <MessageSquare size={20} />}
        </button>

        <AnimatePresence>
          {chatOpen && (
            <motion.div
              initial={{ opacity: 0, y: 15, scale: 0.95 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: 15, scale: 0.95 }}
              className="absolute bottom-16 right-0 w-80 sm:w-96 h-96 bg-neutral-950 border border-neutral-800 rounded-2xl flex flex-col overflow-hidden shadow-2xl"
            >
              {/* Chat Title bar */}
              <div className="bg-neutral-900 px-4 py-3 border-b border-neutral-800 flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Sparkles size={14} className="text-blue-400 animate-pulse" />
                  <span className="font-bold text-white text-xs">ARVION AI Architect</span>
                </div>
                <span className="text-[9px] bg-blue-500/15 border border-blue-500/30 text-blue-400 px-2 py-0.5 rounded-full font-extrabold uppercase">
                  Connected
                </span>
              </div>

              {/* Chat Message Stream */}
              <div className="flex-1 overflow-y-auto p-4 space-y-3.5 scrollbar-thin scrollbar-thumb-neutral-800">
                {chatHistory.map((msg, i) => (
                  <div
                    key={i}
                    className={`flex flex-col max-w-[80%] ${
                      msg.sender === "user" ? "ml-auto items-end" : "mr-auto items-start"
                    }`}
                  >
                    <div
                      className={`px-3 py-2 rounded-xl text-xs leading-relaxed ${
                        msg.sender === "user"
                          ? "bg-indigo-600 text-white rounded-br-none"
                          : "bg-neutral-900 border border-neutral-800 text-neutral-300 rounded-bl-none"
                      }`}
                    >
                      {msg.text}
                    </div>
                  </div>
                ))}
                {aiGenerating && (
                  <div className="flex items-center space-x-2 text-neutral-500 text-xs">
                    <Loader size={12} className="animate-spin text-indigo-500" />
                    <span>AI is editing source code...</span>
                  </div>
                )}
              </div>

              {/* Chat Input form */}
              <form onSubmit={handleAiRefinement} className="p-3 bg-neutral-900 border-t border-neutral-800 flex items-center space-x-2">
                <input
                  type="text"
                  required
                  disabled={aiGenerating}
                  value={chatPrompt}
                  onChange={(e) => setChatPrompt(e.target.value)}
                  placeholder="Describe your design additions..."
                  className="flex-1 bg-neutral-950 border border-neutral-850 rounded-xl px-3 py-2 text-xs text-white outline-none focus:border-indigo-500 transition"
                />
                <button
                  type="submit"
                  disabled={aiGenerating}
                  className="p-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl shadow-lg active:scale-95 transition"
                >
                  <Send size={12} />
                </button>
              </form>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Deployment QR Modal */}
      <AnimatePresence>
        {showDeployModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setShowDeployModal(false)}
              className="absolute inset-0 bg-black/60 backdrop-blur-sm"
            />

            <motion.div
              initial={{ opacity: 0, scale: 0.95, y: 15 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: 15 }}
              className="bg-neutral-950 border border-neutral-800 w-full max-w-sm rounded-2xl p-6 z-10 shadow-2xl text-center space-y-5"
            >
              <div className="flex justify-between items-center border-b border-neutral-850 pb-3">
                <span className="font-extrabold text-white text-sm">Deployment Portal</span>
                <button onClick={() => setShowDeployModal(false)} className="text-neutral-500 hover:text-white transition">
                  <X size={16} />
                </button>
              </div>

              <div className="space-y-4">
                <div className="inline-flex p-1 bg-neutral-900 border border-neutral-800 rounded-xl">
                  {qrCodeUrl && (
                    <img src={qrCodeUrl} alt="Deployment QR Code" className="w-48 h-48 rounded-lg" />
                  )}
                </div>

                <div className="space-y-1">
                  <h4 className="text-white font-bold text-base">Your App is Live!</h4>
                  <p className="text-neutral-400 text-xs leading-relaxed px-4">
                    Scan the QR code to open the sandboxed web viewport directly on your mobile device.
                  </p>
                </div>

                <div className="bg-neutral-900 border border-neutral-850 p-2.5 rounded-xl flex items-center justify-between text-xs font-mono text-neutral-300">
                  <span className="truncate pr-4">{deployedUrl}</span>
                  <a
                    href={deployedUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-indigo-400 font-bold hover:underline shrink-0"
                  >
                    Open
                  </a>
                </div>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
