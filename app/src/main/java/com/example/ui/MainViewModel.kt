package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.CodeIssue
import com.example.api.GeminiClient
import com.example.data.AppDatabase
import com.example.data.Project
import com.example.data.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen {
    object Home : Screen()
    object Editor : Screen()
    object Projects : Screen()
    object Templates : Screen()
    object Settings : Screen()
    object Monetization : Screen()
    object TeamCollaboration : Screen()
    object IncomeDashboard : Screen()
    object Marketplace : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProjectRepository
    
    // UI State variables
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isEnglishLanguage = MutableStateFlow(true)
    val isEnglishLanguage: StateFlow<Boolean> = _isEnglishLanguage.asStateFlow()

    fun toggleLanguage() {
        _isEnglishLanguage.value = !_isEnglishLanguage.value
    }

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _templates = MutableStateFlow<List<Project>>(emptyList())
    val templates: StateFlow<List<Project>> = _templates.asStateFlow()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    private val _promptInput = MutableStateFlow("")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    private val _selectedFramework = MutableStateFlow("html") // html, react
    val selectedFramework: StateFlow<String> = _selectedFramework.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow("")
    val generationProgress: StateFlow<String> = _generationProgress.asStateFlow()

    private val _isListeningVoice = MutableStateFlow(false)
    val isListeningVoice: StateFlow<Boolean> = _isListeningVoice.asStateFlow()

    // Editor / Sandbox state
    private val _activeCode = MutableStateFlow("")
    val activeCode: StateFlow<String> = _activeCode.asStateFlow()

    private val _isCodeQualityChecking = MutableStateFlow(false)
    val isCodeQualityChecking: StateFlow<Boolean> = _isCodeQualityChecking.asStateFlow()

    private val _codeIssues = MutableStateFlow<List<CodeIssue>>(emptyList())
    val codeIssues: StateFlow<List<CodeIssue>> = _codeIssues.asStateFlow()

    // Custom Keys in Settings
    private val _customGeminiKey = MutableStateFlow("")
    val customGeminiKey: StateFlow<String> = _customGeminiKey.asStateFlow()

    private val _customOpenAIKey = MutableStateFlow("")
    val customOpenAIKey: StateFlow<String> = _customOpenAIKey.asStateFlow()

    private val _customAnthropicKey = MutableStateFlow("")
    val customAnthropicKey: StateFlow<String> = _customAnthropicKey.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProjectRepository(database.projectDao())
        
        // Listen to Room flows
        viewModelScope.launch {
            repository.allProjects.collect {
                _projects.value = it
            }
        }
        
        viewModelScope.launch {
            repository.allTemplates.collect {
                _templates.value = it
            }
        }

        // Setup some default templates on first run if database is empty
        viewModelScope.launch {
            repository.allTemplates.collect { list ->
                if (list.isEmpty()) {
                    createPredefinedTemplates()
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setPromptInput(text: String) {
        _promptInput.value = text
    }

    fun setFramework(framework: String) {
        _selectedFramework.value = framework
    }

    fun setCode(code: String) {
        _activeCode.value = code
        // Update current project if open
        val current = _currentProject.value
        if (current != null) {
            val updated = current.copy(code = code)
            _currentProject.value = updated
            viewModelScope.launch {
                repository.updateProject(updated)
            }
        }
    }

    fun setVoiceListening(listening: Boolean) {
        _isListeningVoice.value = listening
    }

    // Custom API Keys
    fun setCustomGeminiKey(key: String) {
        _customGeminiKey.value = key
    }
    fun setCustomOpenAIKey(key: String) {
        _customOpenAIKey.value = key
    }
    fun setCustomAnthropicKey(key: String) {
        _customAnthropicKey.value = key
    }

    /**
     * Generates a new project code from user input prompt
     */
    fun generateNewProject(promptText: String, framework: String, onComplete: () -> Unit = {}) {
        if (promptText.isBlank()) return

        _isGenerating.value = true
        _generationProgress.value = "Establishing neural connection to Gemini..."
        navigateTo(Screen.Editor)

        viewModelScope.launch {
            _generationProgress.value = "Architecting application blueprints ($framework)..."
            val key = if (_customGeminiKey.value.isNotEmpty()) _customGeminiKey.value else null
            
            val result = GeminiClient.generateCode(promptText, framework, key)
            result.fold(
                onSuccess = { generatedCode ->
                    _generationProgress.value = "Sanitizing source buffers..."
                    _activeCode.value = generatedCode

                    // Auto-generate a fun, short name
                    val name = generateProjectName(promptText)
                    val newProject = Project(
                        name = name,
                        prompt = promptText,
                        code = generatedCode,
                        framework = framework
                    )
                    
                    val projectId = repository.insertProject(newProject)
                    val projectWithId = newProject.copy(id = projectId.toInt())
                    _currentProject.value = projectWithId
                    
                    _isGenerating.value = false
                    _promptInput.value = ""
                    
                    // Run security scan on new code
                    runCodeQualityScan(generatedCode)
                    onComplete()
                },
                onFailure = { error ->
                    _generationProgress.value = "Generation error: ${error.localizedMessage}"
                    _activeCode.value = "<!-- Error generating code: ${error.localizedMessage} -->\n\n<h1>Failed to generate application</h1>\n<p>Please double check your API configuration and connection.</p>"
                    _isGenerating.value = false
                }
            )
        }
    }

    /**
     * Refines/updates the current project's code via AI Chat
     */
    fun refineCurrentCode(instruction: String) {
        val current = _currentProject.value ?: return
        _isGenerating.value = true
        _generationProgress.value = "Consulting AI architect..."

        viewModelScope.launch {
            _generationProgress.value = "Refining components and styles..."
            val key = if (_customGeminiKey.value.isNotEmpty()) _customGeminiKey.value else null
            
            val result = GeminiClient.refineCode(_activeCode.value, instruction, key)
            result.fold(
                onSuccess = { updatedCode ->
                    _generationProgress.value = "Syncing local storage..."
                    _activeCode.value = updatedCode
                    
                    val updatedProject = current.copy(code = updatedCode)
                    _currentProject.value = updatedProject
                    repository.updateProject(updatedProject)
                    
                    _isGenerating.value = false
                    
                    // Rescan quality
                    runCodeQualityScan(updatedCode)
                },
                onFailure = { error ->
                    _isGenerating.value = false
                }
            )
        }
    }

    /**
     * Scans the code for vulnerabilities or quality issues
     */
    fun runCodeQualityScan(code: String) {
        _isCodeQualityChecking.value = true
        viewModelScope.launch {
            val key = if (_customGeminiKey.value.isNotEmpty()) _customGeminiKey.value else null
            val result = GeminiClient.checkCodeQuality(code, key)
            result.fold(
                onSuccess = { list ->
                    _codeIssues.value = list
                    _isCodeQualityChecking.value = false
                },
                onFailure = {
                    _isCodeQualityChecking.value = false
                }
            )
        }
    }

    /**
     * Selects an existing project to edit
     */
    fun selectProject(project: Project) {
        _currentProject.value = project
        _activeCode.value = project.code
        _selectedFramework.value = project.framework
        navigateTo(Screen.Editor)
        runCodeQualityScan(project.code)
    }

    /**
     * CRUD operations
     */
    fun renameProject(project: Project, newName: String) {
        viewModelScope.launch {
            val updated = project.copy(name = newName)
            if (_currentProject.value?.id == project.id) {
                _currentProject.value = updated
            }
            repository.updateProject(updated)
        }
    }

    fun duplicateProject(project: Project) {
        viewModelScope.launch {
            val copy = project.copy(
                id = 0,
                name = "${project.name} (Copy)",
                createdAt = System.currentTimeMillis()
            )
            repository.insertProject(copy)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            if (_currentProject.value?.id == project.id) {
                _currentProject.value = null
                _activeCode.value = ""
            }
            repository.deleteProject(project)
        }
    }

    fun saveCurrentAsTemplate(category: String = "Custom") {
        val current = _currentProject.value ?: return
        viewModelScope.launch {
            val template = current.copy(
                id = 0,
                name = "${current.name} Template",
                isTemplate = true,
                category = category,
                createdAt = System.currentTimeMillis()
            )
            repository.insertProject(template)
        }
    }

    fun deployProject(project: Project, subdomainInput: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            // Simulate deploying to cloudflare workers
            val cleanSubdomain = subdomainInput.trim().lowercase().replace(Regex("[^a-z0-9]"), "")
            val finalSubdomain = if (cleanSubdomain.isEmpty()) "project-${UUID.randomUUID().toString().take(6)}" else cleanSubdomain
            val deployedUrl = "https://$finalSubdomain.arvion.com"
            
            val updated = project.copy(deployedSubdomain = finalSubdomain)
            _currentProject.value = updated
            repository.updateProject(updated)
            onComplete(deployedUrl)
        }
    }

    private fun generateProjectName(prompt: String): String {
        val words = prompt.trim().split(Regex("\\s+"))
        val firstTwo = words.take(2).joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        val cleanName = firstTwo.replace(Regex("[^A-Za-z0-9 ]"), "")
        return if (cleanName.length > 3) cleanName else "ARVION Project"
    }

    private suspend fun createPredefinedTemplates() {
        // Predefined beautiful mock templates so the user immediately gets rich demo templates even offline!
        val templatesList = listOf(
            Project(
                name = "Todo App with Dark Mode",
                prompt = "Build a stylish todo app with task filters, local storage persistence, animated additions, and an eye-safe dark mode toggle.",
                framework = "react",
                code = getMockTodoCode(),
                isTemplate = true,
                category = "Todo App"
            ),
            Project(
                name = "Creative Portfolio",
                prompt = "Generate a creative minimal landing portfolio for a designer featuring typewriter animations, visual work cards, and a modal inquiry form.",
                framework = "html",
                code = getMockPortfolioCode(),
                isTemplate = true,
                category = "Portfolio"
            ),
            Project(
                name = "Smart Commerce Dashboard",
                prompt = "Build an interactive E-commerce shop with product grid filterable by price and rating, an animated shopping drawer cart, and check out pricing summative.",
                framework = "react",
                code = getMockStoreCode(),
                isTemplate = true,
                category = "E-commerce"
            )
        )
        for (tpl in templatesList) {
            repository.insertProject(tpl)
        }
    }

    private fun getMockTodoCode(): String {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Dynamic Todo Workspace</title>
    <script src="https://unpkg.com/react@18/umd/react.development.js"></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Plus Jakarta Sans', sans-serif; }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen transition-colors duration-300">
    <div id="root"></div>

    <script type="text/babel">
        function App() {
            const [todos, setTodos] = React.useState([
                { id: 1, text: "Explore ARVION Workspace", completed: true, priority: "High" },
                { id: 2, text: "Build a production-ready NextJS app", completed: false, priority: "Medium" },
                { id: 3, text: "Decompile generated Android manifest", completed: false, priority: "Low" }
            ]);
            const [newTodo, setNewTodo] = React.useState("");
            const [priority, setPriority] = React.useState("Medium");
            const [filter, setFilter] = React.useState("All");
            const [isDark, setIsDark] = React.useState(true);

            const addTodo = (e) => {
                e.preventDefault();
                if (!newTodo.trim()) return;
                setTodos([
                    ...todos,
                    { id: Date.now(), text: newTodo, completed: false, priority }
                ]);
                setNewTodo("");
            };

            const toggleTodo = (id) => {
                setTodos(todos.map(t => t.id === id ? { ...t, completed: !t.completed } : t));
            };

            const deleteTodo = (id) => {
                setTodos(todos.filter(t => t.id !== id));
            };

            const filteredTodos = todos.filter(t => {
                if (filter === "Active") return !t.completed;
                if (filter === "Completed") return t.completed;
                return true;
            });

            return (
                <div className={"min-h-screen transition-all " + (isDark ? "bg-slate-950 text-slate-100" : "bg-slate-50 text-slate-900")}>
                    <div className="max-w-md mx-auto py-12 px-4">
                        <header className="flex justify-between items-center mb-8">
                            <div>
                                <h1 className="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">ARVION Task</h1>
                                <p className="text-xs text-slate-400 mt-1">Refined sandboxed runtime</p>
                            </div>
                            <button onClick={() => setIsDark(!isDark)} className="p-2.5 rounded-xl border border-slate-800 hover:scale-105 active:scale-95 transition">
                                <i className={"fa-solid " + (isDark ? "fa-sun text-yellow-400" : "fa-moon text-indigo-500")}></i>
                            </button>
                        </header>

                        <form onSubmit={addTodo} className="mb-6 space-y-3">
                            <div className="flex gap-2">
                                <input 
                                    type="text" 
                                    value={newTodo}
                                    onChange={(e) => setNewTodo(e.target.value)}
                                    placeholder="Draft a new task..." 
                                    className={"flex-1 px-4 py-3 rounded-xl border outline-none transition " + (isDark ? "bg-slate-900 border-slate-800 text-slate-100 focus:border-indigo-500" : "bg-white border-slate-200 text-slate-900 focus:border-indigo-500")}
                                />
                                <button type="submit" className="px-5 py-3 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold transition active:scale-95">
                                    <i className="fa-solid fa-plus"></i>
                                </button>
                            </div>
                            <div className="flex gap-3 justify-end items-center">
                                <span className="text-xs text-slate-400">Priority:</span>
                                {["Low", "Medium", "High"].map((p) => (
                                    <button 
                                        key={p} 
                                        type="button"
                                        onClick={() => setPriority(p)}
                                        className={"px-3 py-1 rounded-lg text-xs font-semibold border transition " + (priority === p ? "bg-indigo-600 text-white border-indigo-500" : isDark ? "border-slate-800 hover:bg-slate-900" : "border-slate-200 hover:bg-slate-100")}
                                    >
                                        {p}
                                    </button>
                                ))}
                            </div>
                        </form>

                        <div className="flex gap-2 mb-6 border-b border-slate-800 pb-3">
                            {["All", "Active", "Completed"].map((f) => (
                                <button 
                                    key={f} 
                                    onClick={() => setFilter(f)}
                                    className={"px-4 py-1.5 rounded-lg text-xs font-medium transition " + (filter === f ? "bg-indigo-600/20 text-indigo-400 border border-indigo-500/30" : "text-slate-400 hover:text-slate-200")}
                                >
                                    {f}
                                </button>
                            ))}
                        </div>

                        <div className="space-y-2.5">
                            {filteredTodos.map((todo) => (
                                <div 
                                    key={todo.id} 
                                    className={"flex items-center justify-between p-4 rounded-xl border transition " + (isDark ? "bg-slate-900/50 border-slate-900 hover:border-slate-800" : "bg-white border-slate-100 hover:shadow-md")}
                                >
                                    <div className="flex items-center gap-3">
                                        <button onClick={() => toggleTodo(todo.id)} className="text-lg">
                                            <i className={"fa-regular " + (todo.completed ? "fa-circle-check text-green-400" : "fa-circle text-slate-400")}></i>
                                        </button>
                                        <span className={"text-sm " + (todo.completed ? "line-through text-slate-500" : "")}>
                                            {todo.text}
                                        </span>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <span className={"text-[10px] px-2 py-0.5 rounded-full font-bold " + (todo.priority === "High" ? "bg-red-500/10 text-red-400" : todo.priority === "Medium" ? "bg-yellow-500/10 text-yellow-400" : "bg-green-500/10 text-green-400")}>
                                            {todo.priority}
                                        </span>
                                        <button onClick={() => deleteTodo(todo.id)} className="text-slate-500 hover:text-red-400 transition">
                                            <i className="fa-regular fa-trash-can text-sm"></i>
                                        </button>
                                    </div>
                                </div>
                            ))}
                            {filteredTodos.length === 0 && (
                                <div className="text-center py-12 text-slate-500">
                                    <i className="fa-regular fa-folder-open text-3xl mb-3 block"></i>
                                    <p className="text-sm">Workspace clear. No tasks match the criteria.</p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            );
        }

        const root = ReactDOM.createRoot(document.getElementById('root'));
        root.render(<App />);
    </script>
</body>
</html>
        """.trimIndent()
    }

    private fun getMockPortfolioCode(): String {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sleek Minimalist Portfolio</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;700;800&family=Plus+Jakarta+Sans:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        h1, h2, h3 { font-family: 'Syne', sans-serif; }
        body { font-family: 'Plus Jakarta Sans', sans-serif; }
    </style>
</head>
<body class="bg-neutral-950 text-neutral-100 min-h-screen selection:bg-indigo-500 selection:text-white">
    <div class="max-w-4xl mx-auto px-6 py-20">
        <header class="flex justify-between items-center mb-32">
            <div class="text-xl font-bold tracking-tight">a.f.</div>
            <nav class="flex gap-8 text-sm text-neutral-400">
                <a href="#work" class="hover:text-white transition">Work</a>
                <a href="#about" class="hover:text-white transition">About</a>
                <a href="#contact" class="hover:text-white transition">Inquire</a>
            </nav>
        </header>

        <section class="mb-40">
            <p class="text-indigo-400 font-semibold mb-4 text-sm tracking-widest uppercase">Visual Product Engineer</p>
            <h1 class="text-5xl md:text-7xl font-extrabold leading-none mb-8">Crafting pristine interfaces.</h1>
            <p class="text-lg text-neutral-400 max-w-lg leading-relaxed">
                Focused on digital minimalism, elegant system design, and creating lightning-fast sandboxed playgrounds with ARVION.
            </p>
        </section>

        <section id="work" class="mb-40">
            <h2 class="text-2xl font-bold mb-12 border-b border-neutral-800 pb-4">Selected Explorations</h2>
            <div class="grid md:grid-cols-2 gap-8">
                <div class="group cursor-pointer">
                    <div class="bg-neutral-900 rounded-2xl aspect-[4/3] mb-4 overflow-hidden flex items-center justify-center p-8 border border-neutral-900 group-hover:border-neutral-800 transition">
                        <i class="fa-solid fa-cube text-5xl text-indigo-500 group-hover:scale-110 transition duration-300"></i>
                    </div>
                    <h3 class="text-lg font-bold mb-1">Decentralized Asset Canvas</h3>
                    <p class="text-sm text-neutral-500">System Architecture, React Web3</p>
                </div>
                <div class="group cursor-pointer">
                    <div class="bg-neutral-900 rounded-2xl aspect-[4/3] mb-4 overflow-hidden flex items-center justify-center p-8 border border-neutral-900 group-hover:border-neutral-800 transition">
                        <i class="fa-solid fa-wind text-5xl text-purple-500 group-hover:scale-110 transition duration-300"></i>
                    </div>
                    <h3 class="text-lg font-bold mb-1">Atmospheric Canvas Library</h3>
                    <p class="text-sm text-neutral-500">Pure JavaScript, CSS Motion</p>
                </div>
            </div>
        </section>

        <footer class="text-center text-neutral-600 text-xs border-t border-neutral-900 pt-12">
            <p>© 2026 Studio ARVION. All rights conserved.</p>
        </footer>
    </div>
</body>
</html>
        """.trimIndent()
    }

    private fun getMockStoreCode(): String {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Commerce Engine</title>
    <script src="https://unpkg.com/react@18/umd/react.development.js"></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
    <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Space Grotesk', sans-serif; }
    </style>
</head>
<body class="bg-zinc-950 text-zinc-100 min-h-screen">
    <div id="root"></div>

    <script type="text/babel">
        function App() {
            const [products] = React.useState([
                { id: 1, name: "Forge Slate Keyboard", price: 189, rating: 4.8, image: "fa-keyboard", color: "indigo" },
                { id: 2, name: "Omni Mouse Pro", price: 99, rating: 4.6, image: "fa-computer-mouse", color: "purple" },
                { id: 3, name: "Studio Monitor Stand", price: 149, rating: 4.9, image: "fa-desktop", color: "amber" },
                { id: 4, name: "Braid USB-C Cable", price: 29, rating: 4.5, image: "fa-cable-car", color: "rose" }
            ]);
            
            const [cart, setCart] = React.useState([]);
            const [isCartOpen, setIsCartOpen] = React.useState(false);

            const addToCart = (product) => {
                const exist = cart.find(x => x.id === product.id);
                if (exist) {
                    setCart(cart.map(x => x.id === product.id ? Object.assign({}, exist, { qty: exist.qty + 1 }) : x));
                } else {
                    setCart([...cart, Object.assign({}, product, { qty: 1 })]);
                }
            };

            const removeFromCart = (id) => {
                setCart(cart.filter(x => x.id !== id));
            };

            const cartCount = cart.reduce((a, c) => a + c.qty, 0);
            const cartTotal = cart.reduce((a, c) => a + c.qty * c.price, 0);

            return (
                <div className="min-h-screen flex flex-col justify-between">
                    <header className="border-b border-zinc-900 bg-zinc-950/80 backdrop-blur sticky top-0 z-40">
                        <div className="max-w-5xl mx-auto px-6 h-16 flex items-center justify-between">
                            <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-violet-400 to-indigo-400 bg-clip-text text-transparent">ARVION Store</span>
                            <button onClick={() => setIsCartOpen(true)} className="relative p-2 rounded-lg hover:bg-zinc-900 transition">
                                <i className="fa-solid fa-bag-shopping text-lg"></i>
                                {cartCount > 0 && (
                                    <span className="absolute -top-1 -right-1 bg-indigo-600 text-white text-[10px] w-5 h-5 rounded-full flex items-center justify-center font-bold">
                                        {cartCount}
                                    </span>
                                )}
                            </button>
                        </div>
                    </header>

                    <main className="max-w-5xl mx-auto px-6 py-12 flex-1 w-full">
                        <div className="mb-8">
                            <h2 className="text-2xl font-bold tracking-tight">Premium Developer Tools</h2>
                            <p className="text-sm text-zinc-500">Directly sourced. Instant application.</p>
                        </div>

                        <div className="grid sm:grid-cols-2 md:grid-cols-4 gap-6">
                            {products.map(p => (
                                <div key={p.id} className="bg-zinc-900/40 border border-zinc-900 rounded-2xl p-5 hover:border-zinc-800 transition flex flex-col justify-between">
                                    <div>
                                        <div className="bg-zinc-900 aspect-square rounded-xl mb-4 flex items-center justify-center">
                                            <i className={"fa-solid " + p.image + " text-4xl text-" + p.color + "-500"}></i>
                                        </div>
                                        <h3 className="font-bold text-sm text-zinc-200 mb-1">{p.name}</h3>
                                        <div className="flex items-center gap-1.5 mb-4 text-xs">
                                            <i className="fa-solid fa-star text-amber-500"></i>
                                            <span className="text-zinc-400">{p.rating}</span>
                                        </div>
                                    </div>
                                    <div className="flex items-center justify-between mt-2">
                                        <span className="font-bold text-lg text-zinc-100">{"$" + p.price}</span>
                                        <button onClick={() => addToCart(p)} className="p-2 bg-indigo-600 hover:bg-indigo-500 rounded-lg text-white text-xs transition active:scale-95">
                                            Add to Cart
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </main>

                    {isCartOpen && (
                        <div className="fixed inset-0 bg-black/60 z-50 flex justify-end">
                            <div className="w-full max-w-sm bg-zinc-950 border-l border-zinc-900 p-6 flex flex-col justify-between h-full">
                                <div>
                                    <div className="flex items-center justify-between mb-8">
                                        <h2 className="text-xl font-bold">Shopping Drawer</h2>
                                        <button onClick={() => setIsCartOpen(false)} className="text-zinc-400 hover:text-white">
                                            <i className="fa-solid fa-xmark text-lg"></i>
                                        </button>
                                    </div>

                                    <div className="space-y-4 max-h-[60vh] overflow-y-auto">
                                        {cart.map(item => (
                                            <div key={item.id} className="flex gap-4 items-center justify-between border-b border-zinc-900 pb-3">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-10 h-10 bg-zinc-900 rounded-lg flex items-center justify-center">
                                                        <i className={"fa-solid " + item.image + " text-zinc-400"}></i>
                                                    </div>
                                                    <div>
                                                        <p className="text-xs font-bold">{item.name}</p>
                                                        <p className="text-xs text-zinc-500">{"$" + item.price + " × " + item.qty}</p>
                                                    </div>
                                                </div>
                                                <button onClick={() => removeFromCart(item.id)} className="text-zinc-500 hover:text-red-400">
                                                    <i className="fa-solid fa-trash-can text-xs"></i>
                                                </button>
                                            </div>
                                        ))}
                                        {cart.length === 0 && (
                                            <p className="text-center py-12 text-zinc-600 text-sm">Cart is empty.</p>
                                        )}
                                    </div>
                                </div>

                                <div className="border-t border-zinc-900 pt-6">
                                    <div className="flex justify-between font-bold text-sm mb-4">
                                        <span>Total:</span>
                                        <span>{"$" + cartTotal}</span>
                                    </div>
                                    <button className="w-full py-3 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs transition active:scale-95">
                                        Complete Sandbox Checkout
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            );
        }

        const root = ReactDOM.createRoot(document.getElementById('root'));
        root.render(<App />);
    </script>
</body>
</html>
        """.trimIndent()
    }
}
