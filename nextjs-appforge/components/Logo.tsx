"use client";

import React from "react";

interface LogoProps {
  iconOnly?: boolean;
  showTagline?: boolean;
  className?: string;
  size?: number; // Size of the SVG icon
  textClass?: string;
}

export default function Logo({
  iconOnly = false,
  showTagline = false,
  className = "",
  size = 32,
  textClass = "text-xl font-extrabold tracking-tight",
}: LogoProps) {
  return (
    <div className={`flex items-center space-x-2.5 select-none ${className}`}>
      {/* Abstract geometric monogram "A" icon */}
      <svg
        width={size}
        height={size}
        viewBox="0 0 100 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="shrink-0"
      >
        {/* Left polygon: Electric Blue (#3B82F6) */}
        <path
          d="M 47,12 L 15,88 L 29,88 L 33,78 L 47,78 L 47,72 L 35.5,72 L 37.5,67 L 47,67 L 47,60 L 38,48 L 47,36 Z"
          fill="#3B82F6"
        />
        {/* Right polygon: Deep Indigo (#4F46E5) */}
        <path
          d="M 53,12 L 85,88 L 71,88 L 67,78 L 53,78 L 53,72 L 64.5,72 L 62.5,67 L 53,67 L 53,60 L 62,48 L 53,36 Z"
          fill="#4F46E5"
        />
      </svg>

      {!iconOnly && (
        <div className="flex flex-col">
          <span className={`text-white ${textClass}`}>
            ARVION
          </span>
          {showTagline && (
            <span className="text-neutral-500 text-[10px] tracking-wide mt-[-2px] font-medium uppercase">
              Build Apps with AI
            </span>
          )}
        </div>
      )}
    </div>
  );
}
