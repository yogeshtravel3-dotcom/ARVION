# AppForge: AI-Powered Web Application Engine

AppForge is a premium, offline-first developer platform that translates natural language prompts into complete, production-ready web application stacks instantly with live previews and secure edge deployments. Built entirely for Android using modern Kotlin, Jetpack Compose, and Material Design 3.

---

## 🎨 Design Philosophy & Aesthetic Core
*   **Aesthetic Tone:** Cosmic slate dark-mode (#0A0A0A) featuring high-contrast indigo and violet gradient accents.
*   **Adaptive Layouts:** Liquid components that scale cleanly from compact mobile phones to full expanded tablets and landscape screens.
*   **Micro-interactions:** Smooth animations, glow state pulses, and contextual transition flows.

---

## 🚀 Core Architectural Engine
1.  **AI Code Synthesis (Gemini API):** Leverages direct, streamed Google Gemini 2.0 Flash REST endpoints to generate fully reactive HTML+CSS+JS and React CDN blueprints.
2.  **Speech-to-Text (VoiceInput):** Integrates native Android `SpeechRecognizer` pipelines with real-time error recovery and permission managers.
3.  **Local Workspace Database (Room Database):** Full CRUD mechanics for saving, duplication, renaming, and custom templating of workspaces.
4.  **Sandbox Web Sandbox (Preview Canvas):** WebViews loaded with isolated DOM storage and JavaScript environments to render responsive preview bundles instantly.
5.  **Secure Diagnostics Scanner:** Scans compiled codebases for potential XSS injections, hardcoded secrets, and network leaks, delivering interactive remediation cards.
6.  **Production Deployment:** Simulates secure Cloudflare Worker edge tunnels with custom subdomains, generating click-to-open production addresses.

---

## 🛠️ Folder & File Structure
```
/app/src/main/java/com/example/
├── MainActivity.kt                  # App Entry Point & Window Insets Coordinator
├── data/
│   ├── Project.kt                   # Room Database Entity schema
│   ├── ProjectDao.kt                # Database Queries DAO
│   ├── AppDatabase.kt               # Room Database Holder
│   └── ProjectRepository.kt         # Data Access Abstractor
├── api/
│   └── GeminiClient.kt              # Gemini 2.0 Flash REST Client & Scanners
└── ui/
    ├── Screen.kt                    # Safe route enumeration
    ├── MainViewModel.kt             # Reactive State Engine (StateFlow)
    ├── AppForgeApp.kt               # Master Coordinator View
    ├── theme/
    │   ├── Color.kt                 # Custom Cosmic Slate Palette
    │   ├── Theme.kt                 # Material Theme configuration
    │   └── Type.kt                  # Typography system
    └── components/
        ├── Sidebar.kt               # Collapsible wide-screen sidebar
        ├── VoiceInput.kt            # Speech-to-Text Android Recognizer
        ├── Preview.kt               # DOM Isolated WebView Sandbox Canvas
        ├── Editor.kt                # Custom Syntax-spaced editor with line guides
        └── Chat.kt                  # Interactive AI refiner console
```

---

## ⚙️ Initial Setup & Secrets Management
By default, the platform runs via embedded public Gemini endpoints. To integrate your personal developer tunnels:

1.  Open the **Secrets Panel in AI Studio**.
2.  Add your custom keys:
    *   `GEMINI_API_KEY`: Custom Google AI Studio secret key.
3.  The Secrets Gradle Plugin will automatically map these credentials to your local environment on build.

To verify compiling and running tests:
```bash
# Verify standard JVM compilation
gradle assembleDebug

# Run unit tests
gradle :app:testDebugUnitTest
```
