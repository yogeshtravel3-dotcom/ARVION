"use client";

import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Plus, Terminal, LayoutGrid, Calendar, ChevronRight, Laptop, Sparkles, X } from "lucide-react";
import Link from "next/link";

interface Project {
  id: string;
  name: string;
  description: string;
  framework: string;
  createdAt: string;
}

export default function Dashboard() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  
  // Create project form states
  const [projName, setProjName] = useState("");
  const [projDesc, setProjDesc] = useState("");
  const [framework, setFramework] = useState("html");
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    if (status === "unauthenticated") {
      router.push("/");
    } else if (status === "authenticated") {
      fetchProjects();
    }
  }, [status, router]);

  const fetchProjects = async () => {
    try {
      const res = await fetch("/api/projects");
      if (res.ok) {
        const data = await res.json();
        setProjects(data);
      }
    } catch (err) {
      console.error("Error fetching projects:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!projName.trim()) return;

    setCreating(true);
    try {
      // Create starting blueprint
      const startingCode = framework === "html" ? `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>${projName}</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-neutral-950 text-white min-h-screen flex flex-col justify-center items-center">
  <div class="text-center space-y-4 max-w-md px-6">
    <div class="text-5xl">🚀</div>
    <h1 class="text-3xl font-extrabold bg-gradient-to-r from-white to-neutral-400 bg-clip-text text-transparent">${projName}</h1>
    <p class="text-neutral-400 text-sm">${projDesc || "Your brand-new application is ready to be forged with the floating AI Assistant!"}</p>
    <div class="pt-4">
      <span class="bg-indigo-500/15 border border-indigo-500/30 text-indigo-400 text-xs px-3 py-1 rounded-full font-bold">HTML5 Static Scaffold</span>
    </div>
  </div>
</body>
</html>` : `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>${projName}</title>
  <script src="https://unpkg.com/react@18/umd/react.development.js"></script>
  <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-[#0b0f19] text-slate-100 min-h-screen">
  <div id="root"></div>
  <script type="text/babel">
    function App() {
      return (
        <div className="flex flex-col items-center justify-center min-h-screen px-4 py-8 text-center space-y-6">
          <div className="w-16 h-16 rounded-full bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-3xl">⚛️</div>
          <div>
            <h1 className="text-3xl font-black bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">${projName}</h1>
            <p className="text-slate-400 text-xs mt-2">${projDesc || "Refined React sandbox loaded with Tailwind and FontAwesome references!"}</p>
          </div>
        </div>
      );
    }
    const root = ReactDOM.createRoot(document.getElementById('root'));
    root.render(<App />);
  </script>
</body>
</html>`;

      const res = await fetch("/api/projects", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: projName,
          description: projDesc,
          framework,
          code: startingCode,
        }),
      });

      if (res.ok) {
        const newProj = await res.json();
        router.push(`/builder/${newProj.id}`);
      }
    } catch (err) {
      console.error("Error creating project:", err);
    } finally {
      setCreating(false);
      setModalOpen(false);
    }
  };

  if (status === "loading" || loading) {
    return (
      <div className="flex-1 flex items-center justify-center bg-[#0a0a0a]">
        <div className="flex flex-col items-center space-y-4">
          <div className="w-12 h-12 border-t-2 border-indigo-500 border-solid rounded-full animate-spin"></div>
          <span className="text-neutral-400 text-sm font-medium">Loading Workspace Projects...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 bg-[#0a0a0a] px-6 py-10 md:px-12 max-w-7xl mx-auto w-full space-y-8">
      {/* User Header Section */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 border-b border-neutral-850 pb-8">
        <div className="flex items-center space-x-4">
          {session?.user?.image ? (
            <img src={session.user.image} alt="User profile" className="w-16 h-16 rounded-full border-2 border-indigo-500/40 p-0.5" />
          ) : (
            <div className="w-16 h-16 rounded-full bg-neutral-900 border border-neutral-800 flex items-center justify-center text-xl font-bold">
              {session?.user?.name?.charAt(0) || "U"}
            </div>
          )}
          <div>
            <h2 className="text-2xl font-black text-white">{session?.user?.name || "Developer Portal"}</h2>
            <p className="text-neutral-400 text-xs mt-1">Ready to compile reactive stacks</p>
          </div>
        </div>

        <button
          onClick={() => setModalOpen(true)}
          className="bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold px-5 py-3 rounded-xl flex items-center justify-center space-x-2 shadow-lg shadow-indigo-600/25 active:scale-95 transition"
        >
          <Plus size={16} />
          <span>Create New Project</span>
        </button>
      </div>

      {/* Projects Grid Container */}
      <div className="space-y-6">
        <div className="flex items-center space-x-2">
          <LayoutGrid size={18} className="text-neutral-400" />
          <h3 className="text-lg font-bold text-neutral-200">Active Blueprints ({projects.length})</h3>
        </div>

        {projects.length === 0 ? (
          <div className="bg-neutral-900/35 border border-neutral-800 rounded-2xl py-16 px-6 text-center max-w-xl mx-auto space-y-4">
            <div className="w-12 h-12 bg-neutral-900 border border-neutral-800 rounded-xl flex items-center justify-center mx-auto text-neutral-500">
              <Terminal size={22} />
            </div>
            <h4 className="text-white font-bold text-lg">No active projects found</h4>
            <p className="text-neutral-400 text-xs leading-relaxed max-w-sm mx-auto">
              You haven't forged any web application stacks yet. Let's create your first customizable blueprint right now!
            </p>
            <button
              onClick={() => setModalOpen(true)}
              className="bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold px-4 py-2 rounded-lg transition"
            >
              Forge My First App
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {projects.map((project) => (
              <motion.div
                key={project.id}
                whileHover={{ y: -5, borderColor: "rgba(99,102,241,0.4)" }}
                className="bg-neutral-900/40 border border-neutral-800 p-6 rounded-2xl flex flex-col justify-between space-y-4 backdrop-blur-sm cursor-pointer relative overflow-hidden group"
              >
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className={`text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-full border ${
                      project.framework === "react" 
                        ? "bg-sky-500/10 border-sky-500/20 text-sky-400" 
                        : "bg-amber-500/10 border-amber-500/20 text-amber-400"
                    }`}>
                      {project.framework === "react" ? "React CDN" : "HTML5 Web"}
                    </span>
                    <span className="text-neutral-500 text-[10px] flex items-center space-x-1">
                      <Calendar size={10} />
                      <span>{new Date(project.createdAt).toLocaleDateString()}</span>
                    </span>
                  </div>
                  <h4 className="text-white font-bold text-lg group-hover:text-indigo-400 transition">{project.name}</h4>
                  <p className="text-neutral-400 text-xs line-clamp-2 leading-relaxed">{project.description || "No description provided."}</p>
                </div>

                <div className="flex items-center justify-between pt-4 border-t border-neutral-800">
                  <div className="flex items-center space-x-1 text-indigo-400 text-xs font-semibold">
                    <Laptop size={12} />
                    <span>View Workspace</span>
                  </div>
                  <Link href={`/builder/${project.id}`} className="p-1.5 rounded-lg bg-neutral-900 border border-neutral-800 text-neutral-400 hover:text-white transition">
                    <ChevronRight size={14} />
                  </Link>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>

      {/* Creation Modal */}
      <AnimatePresence>
        {modalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setModalOpen(false)}
              className="absolute inset-0 bg-black/60 backdrop-blur-sm"
            />

            <motion.div
              initial={{ opacity: 0, scale: 0.95, y: 15 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: 15 }}
              className="bg-neutral-950 border border-neutral-800 w-full max-w-lg rounded-2xl p-6 z-10 shadow-2xl space-y-6"
            >
              <div className="flex items-center justify-between border-b border-neutral-800 pb-4">
                <div className="flex items-center space-x-2">
                  <Sparkles size={18} className="text-indigo-400" />
                  <h4 className="text-white font-bold text-lg">Forge Web Blueprint</h4>
                </div>
                <button onClick={() => setModalOpen(false)} className="text-neutral-500 hover:text-white transition">
                  <X size={18} />
                </button>
              </div>

              <form onSubmit={handleCreateProject} className="space-y-4 text-sm">
                <div className="space-y-1.5">
                  <label className="text-neutral-400 text-xs font-semibold">Project Name</label>
                  <input
                    type="text"
                    required
                    value={projName}
                    onChange={(e) => setProjName(e.target.value)}
                    placeholder="E.g., Crypto Pulse Dashboard"
                    className="w-full bg-neutral-900 border border-neutral-800 rounded-xl px-4 py-2.5 text-white outline-none focus:border-indigo-500 transition"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-neutral-400 text-xs font-semibold">Description / App Theme</label>
                  <textarea
                    value={projDesc}
                    onChange={(e) => setProjDesc(e.target.value)}
                    placeholder="Briefly state the goal of the application..."
                    className="w-full bg-neutral-900 border border-neutral-800 rounded-xl px-4 py-2.5 text-white h-20 outline-none focus:border-indigo-500 transition resize-none"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-neutral-400 text-xs font-semibold">Base Tech Framework</label>
                  <div className="grid grid-cols-2 gap-4">
                    <button
                      type="button"
                      onClick={() => setFramework("html")}
                      className={`p-3 rounded-xl border flex flex-col text-left space-y-1 transition ${
                        framework === "html"
                          ? "bg-indigo-600/10 border-indigo-500 text-white"
                          : "bg-neutral-900 border-neutral-800 text-neutral-400 hover:bg-neutral-800"
                      }`}
                    >
                      <span className="font-bold text-xs text-white">HTML5 Web Page</span>
                      <span className="text-[10px] text-neutral-400">Single self-contained viewport</span>
                    </button>

                    <button
                      type="button"
                      onClick={() => setFramework("react")}
                      className={`p-3 rounded-xl border flex flex-col text-left space-y-1 transition ${
                        framework === "react"
                          ? "bg-indigo-600/10 border-indigo-500 text-white"
                          : "bg-neutral-900 border-neutral-800 text-neutral-400 hover:bg-neutral-800"
                      }`}
                    >
                      <span className="font-bold text-xs text-white">React Bundle</span>
                      <span className="text-[10px] text-neutral-400">Reactive states & component tags</span>
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={creating}
                  className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-800 text-white font-bold py-3 rounded-xl flex items-center justify-center space-x-2 shadow-lg shadow-indigo-600/20 transition active:scale-95"
                >
                  {creating ? (
                    <>
                      <div className="w-4 h-4 border-2 border-white border-solid border-t-transparent rounded-full animate-spin"></div>
                      <span>Spawning sandbox...</span>
                    </>
                  ) : (
                    <span>Instantiate Workspace</span>
                  )}
                </button>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
