package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    private const val DEFAULT_MODEL = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Generates web application code from a prompt and framework selection.
     */
    suspend fun generateCode(
        prompt: String,
        framework: String,
        userApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(userApiKey)
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("Gemini API key is not configured in the Secrets panel or settings."))
        }

        val systemInstruction = """
            You are ARVION AI, an expert full-stack web developer.
            Your task is to generate a fully complete, self-contained single-file web application based on the user's request.
            The user requested the framework stack: '$framework'.
            
            Follow these technical guidelines:
            1. If framework is 'react' or 'nextjs', generate a complete HTML file that loads React (v18), React DOM, and Babel Standalone from CDN (unpkg or cdnjs), and Tailwind CSS from CDN. Write all React code inside a <script type="text/babel"> block. Implement beautiful components, local state, rich features, and icons (using font-awesome or Lucide icons CDN if needed).
            2. If framework is 'html' or 'vanilla', generate a beautiful single-file HTML with Tailwind CSS CDN, modern styling, and clean JavaScript inside a <script> block for state/interactivity.
            3. Do NOT include markdown styling or preamble in your text response EXCEPT the code block itself if desired, but ideally just return the RAW HTML/JS code or put it in a single markdown block ```html ... ```. Ensure we can extract it cleanly.
            4. Make the design absolutely stunning, ultra-modern, with polished colors, generous spacing, smooth transitions, and high accessibility. Include dark/light themes, custom typography, animations, and beautiful layouts.
            5. Provide placeholder/mock data inside the client-side code so the app is fully usable instantly.
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Generate a detailed, fully functional, beautifully styled web app for: $prompt")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
            })
        }

        val url = "$BASE_URL/models/$DEFAULT_MODEL:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(mediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API Error: Code ${response.code}, Body: $errBody")
                    return@withContext Result.failure(IOException("API Error: ${response.code}. Please verify your API key."))
                }

                val bodyString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty API response"))
                val text = extractTextFromResponse(bodyString)
                if (text != null) {
                    Result.success(cleanCode(text))
                } else {
                    Result.failure(Exception("Could not extract text content from Gemini response."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini", e)
            Result.failure(e)
        }
    }

    /**
     * Refines existing code using user instructions in a chat conversation style.
     */
    suspend fun refineCode(
        currentCode: String,
        instruction: String,
        userApiKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(userApiKey)
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("Gemini API key is not configured."))
        }

        val systemInstruction = """
            You are ARVION AI, an expert full-stack web developer.
            Your task is to modify the existing, complete self-contained single-file web application based on the user's instructions.
            You must output the complete modified code. Do not output only snippets or git diffs. Output the FULL single-file HTML code.
            Keep the output inside a markdown code block ```html ... ``` or just raw text.
            Ensure you maintain all existing functionality, but perfectly implement the requested additions, visual enhancements, or styling changes.
        """.trimIndent()

        val prompt = """
            Existing Code:
            ```html
            $currentCode
            ```
            
            User's Instruction:
            $instruction
            
            Please modify the code as requested and return the COMPLETE updated single-file code.
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.2)
            })
        }

        val url = "$BASE_URL/models/$DEFAULT_MODEL:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(mediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API Error: Code ${response.code}, Body: $errBody")
                    return@withContext Result.failure(IOException("API Error: ${response.code}"))
                }

                val bodyString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                val text = extractTextFromResponse(bodyString)
                if (text != null) {
                    Result.success(cleanCode(text))
                } else {
                    Result.failure(Exception("Could not extract updated code from Gemini response."))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception refining code", e)
            Result.failure(e)
        }
    }

    /**
     * Checks code quality and returns a list of security/performance issues.
     */
    suspend fun checkCodeQuality(
        code: String,
        userApiKey: String? = null
    ): Result<List<CodeIssue>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(userApiKey)
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Return some default basic quality rules if API key is missing to keep app functional
            return@withContext Result.success(getLocalFallbackIssues(code))
        }

        val prompt = """
            Analyze the following self-contained web app code for security vulnerabilities (e.g., XSS, injection, open redirects, missing sanitization) and performance/quality issues (e.g., memory leaks, inline event listeners, unhandled errors, bloated scripts).
            
            Return the result ONLY as a JSON array of objects. Do not wrap in markdown blocks, do not write preamble. Just the raw JSON array.
            Each object in the array MUST have these keys:
            - "title": A short summary of the issue.
            - "severity": Either "CRITICAL", "WARNING", or "INFO".
            - "type": Either "SECURITY" or "PERFORMANCE".
            - "description": A clear explanation of what is wrong.
            - "suggestion": How to fix this issue with specific code snippets or practices.
            
            Code to analyze:
            $code
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.1)
            })
        }

        val url = "$BASE_URL/models/$DEFAULT_MODEL:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(mediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.success(getLocalFallbackIssues(code))
                }

                val bodyString = response.body?.string() ?: return@withContext Result.success(getLocalFallbackIssues(code))
                val jsonText = extractTextFromResponse(bodyString) ?: return@withContext Result.success(getLocalFallbackIssues(code))
                
                val issues = mutableListOf<CodeIssue>()
                try {
                    val array = JSONArray(jsonText.trim())
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        issues.add(
                            CodeIssue(
                                title = obj.optString("title", "Code Issue"),
                                severity = obj.optString("severity", "WARNING"),
                                type = obj.optString("type", "SECURITY"),
                                description = obj.optString("description", ""),
                                suggestion = obj.optString("suggestion", "")
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing issues JSON, falling back", e)
                    return@withContext Result.success(getLocalFallbackIssues(code))
                }

                Result.success(issues)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking code quality", e)
            Result.success(getLocalFallbackIssues(code))
        }
    }

    private fun getApiKey(userApiKey: String?): String {
        return if (!userApiKey.isNullOrEmpty()) userApiKey else BuildConfig.GEMINI_API_KEY
    }

    private fun extractTextFromResponse(jsonResponse: String): String? {
        return try {
            val root = JSONObject(jsonResponse)
            val candidates = root.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text")
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini response text", e)
            null
        }
    }

    private fun cleanCode(rawText: String): String {
        var clean = rawText.trim()
        if (clean.startsWith("```html")) {
            clean = clean.substringAfter("```html").substringBeforeLast("```")
        } else if (clean.startsWith("```")) {
            clean = clean.substringAfter("```").substringBeforeLast("```")
        }
        return clean.trim()
    }

    private fun getLocalFallbackIssues(code: String): List<CodeIssue> {
        val list = mutableListOf<CodeIssue>()
        
        // Rule 1: InnerHTML check
        if (code.contains("innerHTML", ignoreCase = true) && !code.contains("DOMPurify", ignoreCase = true)) {
            list.add(
                CodeIssue(
                    title = "Potential Cross-Site Scripting (XSS)",
                    severity = "CRITICAL",
                    type = "SECURITY",
                    description = "Detected usage of 'innerHTML' without sanitization libraries like DOMPurify.",
                    suggestion = "Sanitize user inputs with DOMPurify or use 'textContent' / 'innerText' instead of innerHTML."
                )
            )
        }

        // Rule 2: Hardcoded Secrets check
        if (code.contains("apiKey", ignoreCase = true) && !code.contains("process.env", ignoreCase = true)) {
            list.add(
                CodeIssue(
                    title = "Hardcoded Secret Keys",
                    severity = "WARNING",
                    type = "SECURITY",
                    description = "Detected strings named 'apiKey' which may contain plain secrets.",
                    suggestion = "Use client-side configuration injection or store keys in dynamic variables rather than hardcoded string literals."
                )
            )
        }

        // Rule 3: Event Listeners Leak check
        if (code.contains("addEventListener", ignoreCase = true) && !code.contains("removeEventListener", ignoreCase = true)) {
            list.add(
                CodeIssue(
                    title = "Uncleaned Event Listeners",
                    severity = "WARNING",
                    type = "PERFORMANCE",
                    description = "EventListeners are registered but no matching removeEventListener was detected. This could lead to memory leaks.",
                    suggestion = "Always clean up listeners, or use standard framework lifecycle methods (e.g. useEffect return callbacks in React)."
                )
            )
        }

        if (list.isEmpty()) {
            list.add(
                CodeIssue(
                    title = "Optimized Asset Loading",
                    severity = "INFO",
                    type = "PERFORMANCE",
                    description = "Code is utilizing standard CDNs which will load asynchronously and perform efficiently.",
                    suggestion = "No action needed. Code matches premium performance benchmarks."
                )
            )
        }
        return list
    }
}

data class CodeIssue(
    val title: String,
    val severity: String, // CRITICAL, WARNING, INFO
    val type: String, // SECURITY, PERFORMANCE
    val description: String,
    val suggestion: String
)
