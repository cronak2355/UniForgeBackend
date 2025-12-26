// src/AssetsEditor/components/RightPanel.tsx

import { useState } from 'react';
import { useAssetsEditor } from '../context/AssetsEditorContext';

export function RightPanel() {
  const { downloadWebP, loadAIImage, isLoading } = useAssetsEditor();
  const [fps, setFps] = useState(12);
  const [prompt, setPrompt] = useState('');
  const [style, setStyle] = useState('pixel-art');
  const [isGenerating, setIsGenerating] = useState(false);

  const handleGenerate = async () => {
    if (!prompt.trim() || isGenerating) return;
    
    setIsGenerating(true);
    try {
      // TODO: 실제 AI API 연동
      const response = await fetch('/api/generate-pixel-art', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ prompt, style }),
      });
      
      if (response.ok) {
        const blob = await response.blob();
        await loadAIImage(blob);
      }
    } catch (error) {
      console.error('AI generation error:', error);
    } finally {
      setIsGenerating(false);
    }
  };

  const styles = [
    { id: 'pixel-art', label: 'Pixel Art' },
    { id: '8-bit', label: '8-bit' },
    { id: '16-bit', label: '16-bit' },
    { id: 'isometric', label: 'Isometric' },
  ];

  return (
    <div className="w-[260px] bg-black border-l border-neutral-800 flex flex-col">
      {/* Preview */}
      <div className="p-3 border-b border-neutral-800">
        <div className="text-xs text-neutral-500 mb-2">Preview</div>
        <div className="bg-neutral-900 aspect-square flex items-center justify-center border border-neutral-800">
          <div className="w-20 h-20 bg-neutral-800 flex items-center justify-center">
            <span className="text-2xl opacity-30">👁️</span>
          </div>
        </div>
        
        {/* FPS */}
        <div className="flex items-center gap-2 mt-2 text-xs">
          <span className="text-neutral-500 w-14">{fps} FPS</span>
          <input
            type="range"
            min="1"
            max="30"
            value={fps}
            onChange={(e) => setFps(Number(e.target.value))}
            className="flex-1 h-1 accent-[#3b82f6] bg-neutral-800"
          />
        </div>
      </div>

      {/* Layers */}
      <div className="border-b border-neutral-800">
        <div className="flex items-center justify-between px-3 py-2 border-b border-neutral-800">
          <span className="text-xs text-neutral-500">Layers</span>
          <button className="text-[#3b82f6] text-xs hover:text-[#60a5fa]">+ Add</button>
        </div>
        <div className="p-2">
          <div className="flex items-center gap-2 px-2 py-1.5 bg-neutral-900 border border-[#2563eb] text-xs">
            <span className="text-white">Layer 1</span>
            <span className="ml-auto text-neutral-500">👁️</span>
          </div>
        </div>
      </div>

      {/* Transform */}
      <div className="border-b border-neutral-800">
        <div className="px-3 py-2 border-b border-neutral-800">
          <span className="text-xs text-neutral-500">Transform</span>
        </div>
        <div className="p-2 grid grid-cols-4 gap-1">
          {[
            { icon: '↔️', label: 'Flip H' },
            { icon: '↕️', label: 'Flip V' },
            { icon: '↻', label: 'Rotate' },
            { icon: '⊞', label: 'Center' },
          ].map((item) => (
            <button 
              key={item.label}
              className="aspect-square bg-neutral-900 hover:bg-neutral-800 border border-neutral-800 flex items-center justify-center text-sm text-neutral-400 hover:text-white transition-colors" 
              title={item.label}
            >
              {item.icon}
            </button>
          ))}
        </div>
      </div>

      {/* AI Generate */}
      <div className="flex-1 flex flex-col min-h-0 border-b border-neutral-800">
        <div className="px-3 py-2 border-b border-neutral-800 flex items-center gap-2">
          <span className="text-xs text-neutral-500">AI Generate</span>
          <span className="text-[10px] px-1.5 py-0.5 bg-[#2563eb] text-white">BETA</span>
        </div>
        
        <div className="p-3 flex flex-col gap-3">
          {/* Prompt */}
          <div>
            <label className="text-[10px] text-neutral-600 block mb-1">Prompt</label>
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="A cute robot character..."
              className="w-full h-16 bg-neutral-900 border border-neutral-800 text-xs text-white p-2 resize-none focus:border-[#3b82f6] focus:outline-none placeholder-neutral-600"
            />
          </div>

          {/* Style */}
          <div>
            <label className="text-[10px] text-neutral-600 block mb-1">Style</label>
            <div className="grid grid-cols-2 gap-1">
              {styles.map((s) => (
                <button
                  key={s.id}
                  onClick={() => setStyle(s.id)}
                  className={`py-1 text-[10px] transition-colors ${
                    style === s.id
                      ? 'bg-[#2563eb] text-white'
                      : 'bg-neutral-900 text-neutral-500 hover:bg-neutral-800 hover:text-white border border-neutral-800'
                  }`}
                >
                  {s.label}
                </button>
              ))}
            </div>
          </div>

          {/* Generate Button */}
          <button
            onClick={handleGenerate}
            disabled={!prompt.trim() || isGenerating || isLoading}
            className={`w-full py-2 text-xs font-medium transition-colors flex items-center justify-center gap-2 ${
              !prompt.trim() || isGenerating || isLoading
                ? 'bg-neutral-800 text-neutral-600 cursor-not-allowed'
                : 'bg-gradient-to-r from-[#2563eb] to-[#7c3aed] hover:from-[#3b82f6] hover:to-[#8b5cf6] text-white'
            }`}
          >
            {isGenerating || isLoading ? (
              <>
                <span className="w-3 h-3 border-2 border-neutral-600 border-t-white rounded-full animate-spin" />
                Generating...
              </>
            ) : (
              <>
                <span>Generate</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Export */}
      <div className="p-3 space-y-2">
        <button
          onClick={() => downloadWebP('pixel-art')}
          className="w-full py-2 text-xs bg-[#2563eb] hover:bg-[#3b82f6] text-white transition-colors font-medium"
        >
          Download WebP
        </button>
        <button className="w-full py-2 text-xs bg-neutral-900 hover:bg-neutral-800 border border-neutral-800 text-neutral-400 hover:text-white transition-colors">
          Export PNG
        </button>
      </div>
    </div>
  );
}