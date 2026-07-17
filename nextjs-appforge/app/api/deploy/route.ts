import { NextRequest, NextResponse } from "next/server";
import { getServerSession } from "next-auth/next";
import { authOptions } from "../../../lib/auth";
import { prisma } from "../../../lib/prisma";
import QRCode from "qrcode";

export async function POST(req: NextRequest) {
  const session = await getServerSession(authOptions);
  if (!session) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  try {
    const { projectId } = await req.json();

    if (!projectId) {
      return NextResponse.json({ error: "projectId is required" }, { status: 400 });
    }

    // Retrieve the project and verify it belongs to the authenticated user
    const project = await prisma.project.findUnique({
      where: {
        id: projectId,
        userId: session.user.id,
      },
    });

    if (!project) {
      return NextResponse.json({ error: "Project not found or access denied." }, { status: 404 });
    }

    // Format project name into a slug-friendly subdomain
    const cleanSubdomain = project.name
      .trim()
      .toLowerCase()
      .replace(/[^a-z0-9]/g, "-")
      .replace(/-+/g, "-")
      .replace(/^-|-$/g, "");

    const finalSubdomain = cleanSubdomain || `project-${Math.floor(Math.random() * 100000)}`;
    const deployedUrl = `https://${finalSubdomain}.arvion.com`;

    // Generate an actual QR code base64 Data URL dynamically
    const qrCodeDataUrl = await QRCode.toDataURL(deployedUrl, {
      color: {
        dark: "#3B82F6", // Electric Blue Accent
        light: "#0a0a0a", // Dark Surface Background
      },
      width: 250,
      margin: 2,
    });

    return NextResponse.json({
      deployedUrl,
      qrCode: qrCodeDataUrl,
    });
  } catch (error: any) {
    console.error("Mock edge deployment error:", error);
    return NextResponse.json(
      { error: error.message || "An error occurred during mock deployment." },
      { status: 500 }
    );
  }
}
