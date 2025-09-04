import React, { useState } from "react";
import { Menu, LogOut, Settings, Save, Upload, Trash2 } from "lucide-react";

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isOptionsOpen, setIsOptionsOpen] = useState(false);

  return (
    <header className="w-full bg-gray-900 text-white shadow-md px-6 py-4 flex justify-between items-center">
      <div className="text-2xl font-bold tracking-wide">Warhammer Army Builder</div>
      <nav className="flex items-center gap-6">
        <button className="hover:text-gray-300">Armie</button>
        <button className="hover:text-gray-300">Jednostki</button>
        <button className="hover:text-gray-300">Ulepszenia</button>
        <div className="relative">
          <button
            className="flex items-center gap-2 hover:text-gray-300"
            onClick={() => setIsOptionsOpen(!isOptionsOpen)}
          >
            <Settings size={18} /> Opcje
          </button>
          {isOptionsOpen && (
            <div className="absolute right-0 mt-2 w-48 bg-white text-gray-900 rounded-lg shadow-lg p-2 z-50">
              <button className="flex items-center gap-2 w-full text-left px-4 py-2 hover:bg-gray-100">
                <Save size={16} /> Zapisz armię
              </button>
              <button className="flex items-center gap-2 w-full text-left px-4 py-2 hover:bg-gray-100">
                <Upload size={16} /> Załaduj armię
              </button>
              <button className="flex items-center gap-2 w-full text-left px-4 py-2 hover:bg-gray-100 text-red-600">
                <Trash2 size={16} /> Usuń armię
              </button>
              <button className="flex items-center gap-2 w-full text-left px-4 py-2 hover:bg-gray-100">
                <LogOut size={16} /> Wyloguj
              </button>
            </div>
          )}
        </div>
      </nav>
      <button
        className="lg:hidden"
        onClick={() => setIsMenuOpen(!isMenuOpen)}
      >
        <Menu size={24} />
      </button>
    </header>
  );
}
