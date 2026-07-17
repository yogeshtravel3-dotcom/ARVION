import { NextRequest, NextResponse } from "next/server";
import { GoogleGenerativeAI } from "@google/generative-ai";
import { getServerSession } from "next-auth/next";
import { authOptions } from "../../../lib/auth";

export async function POST(req: NextRequest) {
  // Protect API route
  const session = await getServerSession(authOptions);
  if (!session) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  try {
    const { prompt, framework } = await req.json();

    if (!prompt) {
      return NextResponse.json({ error: "Prompt is required" }, { status: 400 });
    }

    const apiKey = process.env.GOOGLE_API_KEY;
    if (!apiKey) {
      return NextResponse.json(
        { error: "GOOGLE_API_KEY environment variable is not configured." },
        { status: 500 }
      );
    }

    // Initialize Gemini AI
    const genAI = new GoogleGenerativeAI(apiKey);
    // Using gemini-2.0-flash for super-fast generation
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });

    const systemPrompt = `You are a legendary senior full-stack UI developer. Generate clean, cohesive, single-file codebases based on user requirements.
If the selected framework is "html", return a modern, fully-functioning, fully self-contained HTML5 file with standard styling (Tailwind CSS loaded from CDN via script) and interactivity (using pure JavaScript or Vue/React loaded from CDN).
If the framework is "react", return a self-contained JSX/ES6 bundle loaded via Babel CDN in HTML.
NEVER wrap your response in markdown formatting or code fences (like \`\`\`html ... \`\`\`).
Return ONLY the raw HTML code itself, with zero explanatory text before or after, so it can be rendered inside a sandboxed iframe. Ensure it looks premium, with animations, beautiful gradients, and icons (e.g. from FontAwesome or Lucide).`;

    const result = await model.generateContent({
      contents: [
        {
          role: "user",
          parts: [{ text: `${systemPrompt}\n\nGenerate an app based on this requirement: ${prompt}\nSelected Target: ${framework}` }]
        }
      ]
    });

    const response = await result.response;
    let code = response.text();

    // Sanitation: Strip any markdown code fences if the model included them by accident
    if (code.startsWith("```html")) {
      code = code.replace(/^```html\s*/, "");
    } else if (code.startsWith("```")) {
      code = code.replace(/^```[a-zA-Z]*\s*/, "");
    }
    if (code.endsWith("```")) {
      code = code.substring(0, code.length - 3);
    }
    code = code.trim();

    return NextResponse.json({ code });
  } catch (error: any) {
    console.error("Gemini generation error:", error);
    return NextResponse.json(
      { error: error.message || "An error occurred during code generation." },
      { status: 500 }
    );
  }
}
