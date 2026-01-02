// src/AssetsEditor/context/AssetsEditorContext.tsx

import {
  createContext,
  useContext,
  useState,
  useRef,
  useCallback,
  useEffect,
  type ReactNode,
} from 'react';
import { PixelEngine, type RGBA, type PixelSize } from '../engine/PixelEngine';
import type { Frame } from '../engine/FrameManager';

export type Tool = 'brush' | 'eraser' | 'eyedropper' | 'fill';

interface Asset {
  id: string;
  name: string;
  type: 'character' | 'object' | 'tile';
  imageData: string;
  stats: { hp: number; speed: number; attack: number };
  createdAt: Date;
}

interface AssetsEditorContextType {
  // Canvas & Engine
  canvasRef: React.RefObject<HTMLCanvasElement | null>;
  initEngine: () => void;

  // Tool state
  tool: Tool;
  setTool: (tool: Tool) => void;
  currentTool: Tool;
  setCurrentTool: (tool: Tool) => void;

  // Color state
  color: RGBA;
  setColor: (color: RGBA) => void;
  currentColor: RGBA;
  setCurrentColor: (color: RGBA) => void;

  // Resolution & Zoom
  pixelSize: PixelSize;
  setPixelSize: (size: PixelSize) => void;
  zoom: number;
  setZoom: (zoom: number) => void;

  // Brush Size
  brushSize: number;
  setBrushSize: (size: number) => void;

  // Canvas Actions
  clear: () => void;
  clearCanvas: () => void;

  // Pointer Events
  handlePointerDown: (e: React.PointerEvent<HTMLCanvasElement>) => void;
  handlePointerMove: (e: React.PointerEvent<HTMLCanvasElement>) => void;
  handlePointerUp: () => void;

  // Undo / Redo
  undo: () => void;
  redo: () => void;
  canUndo: boolean;
  canRedo: boolean;
  historyState: { undoCount: number; redoCount: number };

  // Frame Management
  frames: Frame[];
  currentFrameIndex: number;
  maxFrames: number;
  addFrame: () => void;
  deleteFrame: (index: number) => void;
  duplicateFrame: (index: number) => void;
  selectFrame: (index: number) => void;
  getFrameThumbnail: (index: number) => string | null;

  // Animation Preview
  isPlaying: boolean;
  setIsPlaying: (playing: boolean) => void;
  fps: number;
  setFps: (fps: number) => void;

  // AI Image
  loadAIImage: (blob: Blob) => Promise<void>;
  applyImageData: (imageData: ImageData) => void;
  getWorkCanvas: () => HTMLCanvasElement | null;
  isLoading: boolean;
  setIsLoading: (loading: boolean) => void;

  featherAmount: number;
  setFeatherAmount: (amount: number) => void;

  // Export
  downloadWebP: (filename: string) => Promise<void>;
  saveToLibrary: (name: string, type: Asset['type'], stats: Asset['stats']) => Promise<void>;

  // Library
  assets: Asset[];
  deleteAsset: (id: string) => void;
  loadAsset: (id: string) => void;
}

const AssetsEditorContext = createContext<AssetsEditorContextType | null>(null);

export function AssetsEditorProvider({ children }: { children: ReactNode }) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const engineRef = useRef<PixelEngine | null>(null);

  const [currentTool, setCurrentTool] = useState<Tool>('brush');
  const [currentColor, setCurrentColor] = useState<RGBA>({ r: 255, g: 255, b: 255, a: 255 });
  const [pixelSize, setPixelSizeState] = useState<PixelSize>(128);
  const [zoom, setZoomState] = useState(8);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const [featherAmount, setFeatherAmount] = useState(0);
  const [originalAIImage, setOriginalAIImage] = useState<ImageBitmap | null>(null);
  const [canUndo, setCanUndo] = useState(false);
  const [canRedo, setCanRedo] = useState(false);
  const [historyState, setHistoryState] = useState({ undoCount: 0, redoCount: 0 });
  const [brushSize, setBrushSizeState] = useState(1);

  // Frame state
  const [frames, setFrames] = useState<Frame[]>([]);
  const [currentFrameIndex, setCurrentFrameIndex] = useState(0);
  const [maxFrames, setMaxFrames] = useState(4);

  // Animation state
  const [isPlaying, setIsPlaying] = useState(false);
  const [fps, setFps] = useState(8);

  const isDrawingRef = useRef(false);
  const lastPointRef = useRef<{ x: number; y: number } | null>(null);

  // 프레임 상태 동기화
  const syncFrameState = useCallback(() => {
    if (!engineRef.current) return;
    setFrames([...engineRef.current.getAllFrames()]);
    setCurrentFrameIndex(engineRef.current.getCurrentFrameIndex());
    setMaxFrames(engineRef.current.maxFrames);
  }, []);

  // 히스토리 상태 업데이트
  const updateHistoryState = useCallback(() => {
    if (!engineRef.current) return;
    setCanUndo(engineRef.current.canUndo());
    setCanRedo(engineRef.current.canRedo());
    const state = engineRef.current.getHistoryState();
    setHistoryState({ undoCount: state.undoCount, redoCount: state.redoCount });
  }, []);

  const setZoom = useCallback((newZoom: number) => {
    setZoomState(Math.min(20, Math.max(2, newZoom)));
  }, []);

  const initEngine = useCallback(() => {
    if (!canvasRef.current || engineRef.current) return;
    engineRef.current = new PixelEngine(canvasRef.current, pixelSize, 50);
    updateHistoryState();
    syncFrameState();
  }, [pixelSize, updateHistoryState, syncFrameState]);

  const setPixelSize = useCallback((size: PixelSize) => {
    setPixelSizeState(size);
    if (engineRef.current) {
      // 새 엔진 생성하지 않고 해상도만 변경 (캐시 유지)
      engineRef.current.changeResolution(size);
      updateHistoryState();
      syncFrameState();
    }
  }, [updateHistoryState, syncFrameState]);

  const clearCanvas = useCallback(() => {
    engineRef.current?.clear();
    updateHistoryState();
  }, [updateHistoryState]);

  const setBrushSize = useCallback((size: number) => {
    setBrushSizeState(Math.min(16, Math.max(1, size)));
  }, []);

  // ==================== Frame Management ====================

  const addFrame = useCallback(() => {
    if (!engineRef.current) return;
    engineRef.current.addFrame();
    syncFrameState();
  }, [syncFrameState]);

  const deleteFrame = useCallback((index: number) => {
    if (!engineRef.current) return;
    engineRef.current.deleteFrame(index);
    syncFrameState();
    updateHistoryState();
  }, [syncFrameState, updateHistoryState]);

  const duplicateFrame = useCallback((index: number) => {
    if (!engineRef.current) return;
    engineRef.current.duplicateFrame(index);
    syncFrameState();
  }, [syncFrameState]);

  const selectFrame = useCallback((index: number) => {
    if (!engineRef.current) return;
    engineRef.current.selectFrame(index);
    syncFrameState();
    updateHistoryState();
  }, [syncFrameState, updateHistoryState]);

  const getFrameThumbnail = useCallback((index: number): string | null => {
    if (!engineRef.current) return null;
    return engineRef.current.generateFrameThumbnail(index, 48);
  }, []);

  // 좌표 계산 헬퍼
  const getPixelCoords = useCallback((e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!canvasRef.current) return null;
    const rect = canvasRef.current.getBoundingClientRect();
    const scaleX = canvasRef.current.width / rect.width;
    const scaleY = canvasRef.current.height / rect.height;
    return {
      x: Math.floor((e.clientX - rect.left) * scaleX),
      y: Math.floor((e.clientY - rect.top) * scaleY),
    };
  }, []);

  // 픽셀 그리기/지우기 실행
  const executeToolAt = useCallback((x: number, y: number) => {
    if (!engineRef.current) return;

    switch (currentTool) {
      case 'brush':
        engineRef.current.drawPixelAt(x, y, currentColor, brushSize);
        break;
      case 'eraser':
        engineRef.current.erasePixelAt(x, y, brushSize);
        break;
      case 'fill':
        engineRef.current.floodFill(x, y, currentColor);
        break;
      case 'eyedropper':
        const color = engineRef.current.getPixelColorAt(x, y);
        if (color.a > 0) {
          setCurrentColor(color);
          setCurrentTool('brush');
        }
        break;
    }
  }, [currentTool, currentColor, brushSize]);

  // Bresenham line algorithm for smooth strokes
  const drawLine = useCallback((x0: number, y0: number, x1: number, y1: number) => {
    const dx = Math.abs(x1 - x0);
    const dy = Math.abs(y1 - y0);
    const sx = x0 < x1 ? 1 : -1;
    const sy = y0 < y1 ? 1 : -1;
    let err = dx - dy;

    let x = x0;
    let y = y0;

    while (true) {
      executeToolAt(x, y);

      if (x === x1 && y === y1) break;

      const e2 = 2 * err;
      if (e2 > -dy) {
        err -= dy;
        x += sx;
      }
      if (e2 < dx) {
        err += dx;
        y += sy;
      }
    }
  }, [executeToolAt]);

  // ==================== Pointer Events ====================

  const handlePointerDown = useCallback((e: React.PointerEvent<HTMLCanvasElement>) => {
    if (e.button !== 0) return;

    const coords = getPixelCoords(e);
    if (!coords || !engineRef.current) return;

    isDrawingRef.current = true;

    if (currentTool === 'fill') {
      engineRef.current.beginStroke('fill');
      executeToolAt(coords.x, coords.y);
      engineRef.current.endStroke();
      isDrawingRef.current = false;
      updateHistoryState();
      syncFrameState(); // 썸네일 업데이트
      return;
    }

    if (currentTool === 'eyedropper') {
      executeToolAt(coords.x, coords.y);
      isDrawingRef.current = false;
      return;
    }

    const actionType = currentTool === 'eraser' ? 'erase' : 'stroke';
    engineRef.current.beginStroke(actionType);
    executeToolAt(coords.x, coords.y);
    lastPointRef.current = { x: coords.x, y: coords.y };
  }, [currentTool, getPixelCoords, executeToolAt, updateHistoryState, syncFrameState]);

  const handlePointerMove = useCallback((e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!isDrawingRef.current) return;
    if (currentTool !== 'brush' && currentTool !== 'eraser') return;

    const coords = getPixelCoords(e);
    if (!coords) return;

    // Use line interpolation for smooth strokes
    if (lastPointRef.current) {
      drawLine(lastPointRef.current.x, lastPointRef.current.y, coords.x, coords.y);
    } else {
      executeToolAt(coords.x, coords.y);
    }
    lastPointRef.current = { x: coords.x, y: coords.y };
  }, [currentTool, getPixelCoords, executeToolAt, drawLine]);

  const handlePointerUp = useCallback(() => {
    if (!isDrawingRef.current) return;

    isDrawingRef.current = false;
    lastPointRef.current = null;
    engineRef.current?.endStroke();
    updateHistoryState();
    syncFrameState(); // 썸네일 업데이트
  }, [updateHistoryState, syncFrameState]);

  // ==================== Undo / Redo ====================

  const undo = useCallback(() => {
    if (!engineRef.current) return;
    engineRef.current.undo();
    updateHistoryState();
    syncFrameState();
  }, [updateHistoryState, syncFrameState]);

  const redo = useCallback(() => {
    if (!engineRef.current) return;
    engineRef.current.redo();
    updateHistoryState();
    syncFrameState();
  }, [updateHistoryState, syncFrameState]);

  // ==================== Keyboard Shortcuts ====================

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const isMac = navigator.platform.toUpperCase().includes('MAC');
      const modKey = isMac ? e.metaKey : e.ctrlKey;

      if (modKey && e.key === 'z') {
        e.preventDefault();
        if (e.shiftKey) {
          redo();
        } else {
          undo();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [undo, redo]);

  // ==================== AI Image ====================

  const loadAIImage = useCallback(async (blob: Blob) => {
    if (!engineRef.current) return;

    setIsLoading(true);
    try {
      const imageBitmap = await createImageBitmap(blob);
      setOriginalAIImage(imageBitmap);
      // Reset feather to 0 or keep? Let's reset to 0 for new image
      setFeatherAmount(0);
    } finally {
      setIsLoading(false);
    }
  }, []); // Remove dependencies triggered by state implementation detail

  // Real-time processing effect
  useEffect(() => {
    if (!originalAIImage || !engineRef.current) return;

    const processAIImage = () => {
      const tempCanvas = document.createElement('canvas');
      tempCanvas.width = pixelSize;
      tempCanvas.height = pixelSize;
      const tempCtx = tempCanvas.getContext('2d');
      if (!tempCtx) return;

      tempCtx.imageSmoothingEnabled = false;
      tempCtx.drawImage(originalAIImage, 0, 0, pixelSize, pixelSize);

      const imageData = tempCtx.getImageData(0, 0, pixelSize, pixelSize);
      const data = imageData.data;
      const width = pixelSize;
      const height = pixelSize;

      // 1. Global Green Chroma Key (Fixed Tolerance)
      // We assume AI generates "Solid Bright Green".
      // Hardcode a good tolerance (e.g., equivalent to old slider ~60)
      const tolerance = 60; // Fairly aggressive
      const strictness = 100 - tolerance; // 40
      const threshold = Math.max(10, tolerance * 2);

      for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];

        if (data[i + 3] === 0) continue; // Already transparent

        // Green logic: G dominant and significantly bright
        if (g > 100 && g > r + 40 && g > b + 40) {
          // Dynamic Threshold Check
          if (g > r + strictness && g > b + strictness) {
            data[i + 3] = 0; // Alpha 0
          }
        }
      }

      // 2. Feather (Erosion)
      // "Shave off" white/green edges
      if (featherAmount > 0) {
        // Create a copy to read neighbors from
        const originalAlpha = new Uint8Array(width * height);
        for (let i = 0; i < width * height; i++) {
          originalAlpha[i] = data[i * 4 + 3];
        }

        // Simple erosion: Valid pixel if all neighbors are opaque (or neighbor alpha > 0)
        // We can do 'featherAmount' passes or use distance field.
        // Given pixel art, 'featherAmount' as 'pixels' makes sense.
        // But 0-10 range: 1 = 1px erosion?
        // Maybe just 1 pass is enough, but check neighbors distance?
        // Let's implement multi-pass erosion for simplicity if amount is integer.

        const passes = Math.ceil(featherAmount / 2); // 0-5 passes for slider 0-10?
        // Or just strictly follow amount. Slider 0-10 -> 0-3px?
        // User wants "slightly shave". 
        // Let's interpret slider 0-100 as "Threshold of neighbor transparency"? No.
        // Let's try: Feather 1 = remove pixels adjacent to transparency.
        // Feather 2 = remove pixels adjacent to transparency (2 layers deep).

        // Using 2 buffers
        let currentAlpha = originalAlpha;
        const iterations = Math.floor(featherAmount / 20) + 1; // 0-20: 1px, 20-40: 2px...
        // Actually user requested "Feather intensity bar".
        // Let's use threshold-based erosion on alpha?
        // No, pixel art is binary alpha usually.

        // Let's stick to simple neighbor check.
        // For 'featherAmount' (0-10), we erode that many pixels? No, too much.
        // Let's scale: Slider 0-100.
        // 0: No erosion.
        // 1-30: 1 pixel erosion.
        // 31-60: 2 pixels.
        // 61-100: 3 pixels.

        const erosionSteps = Math.floor(featherAmount / 25); // 0, 1, 2, 3, 4

        if (erosionSteps > 0) {
          let src = originalAlpha;
          let dst = new Uint8Array(width * height);

          for (let step = 0; step < erosionSteps; step++) {
            for (let y = 0; y < height; y++) {
              for (let x = 0; x < width; x++) {
                const idx = y * width + x;
                if (src[idx] === 0) {
                  dst[idx] = 0;
                  continue;
                }

                // Check 4 neighbors
                let isEdge = false;
                const neighbors = [[x + 1, y], [x - 1, y], [x, y + 1], [x, y - 1]];
                for (const [nx, ny] of neighbors) {
                  if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                    // Boundary is "transparent" -> remove edge pixels at frame boundary? 
                    // Usually no, keep frame boundary.
                    continue;
                  }
                  const nIdx = ny * width + nx;
                  if (src[nIdx] === 0) {
                    isEdge = true;
                    break;
                  }
                }

                if (isEdge) {
                  dst[idx] = 0; // Erode
                } else {
                  dst[idx] = 255;
                }
              }
            }
            // Swap for next iteration
            src = new Uint8Array(dst);
          }

          // Apply back to data
          for (let i = 0; i < width * height; i++) {
            data[i * 4 + 3] = src[i];
          }
        }
      }

      engineRef.current?.applyAIImage(imageData);
      // Do not update history on every frame of slider? 
      // It's fine for "Real-time" preview, but history might get spammed.
      // Ideally we only update history on "MouseUp" of slider. 
      // But for now, let's just apply.
      // syncFrameState is needed for thumbnails.
      syncFrameState();
    };

    processAIImage();
  }, [originalAIImage, featherAmount, pixelSize, syncFrameState]); // Removed history update to avoid spam

  // Manual cleanup when changing images?
  // Not needed, state replacement handles it.

  const applyImageData = useCallback((imageData: ImageData) => {
    if (!engineRef.current) return;
    engineRef.current.applyAIImage(imageData);
    updateHistoryState();
    syncFrameState();
  }, [updateHistoryState, syncFrameState]);

  const getWorkCanvas = useCallback((): HTMLCanvasElement | null => {
    if (!engineRef.current) return null;
    // 현재 프레임의 캔버스 스냅샷 생성
    const tempCanvas = document.createElement('canvas');
    tempCanvas.width = pixelSize;
    tempCanvas.height = pixelSize;
    const ctx = tempCanvas.getContext('2d');
    if (!ctx || !canvasRef.current) return null;

    ctx.imageSmoothingEnabled = false;
    ctx.drawImage(canvasRef.current, 0, 0, pixelSize, pixelSize);
    return tempCanvas;
  }, [pixelSize]);

  // ==================== Export ====================

  const downloadWebP = useCallback(async (filename: string) => {
    if (!engineRef.current) return;

    const base64 = await engineRef.current.exportAsBase64();
    const link = document.createElement('a');
    link.href = base64;
    link.download = filename.endsWith('.webp') ? filename : `${filename}.webp`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }, []);

  const saveToLibrary = useCallback(
    async (name: string, type: Asset['type'], stats: Asset['stats']) => {
      if (!engineRef.current) return;

      const imageData = await engineRef.current.exportAsBase64();
      const newAsset: Asset = {
        id: crypto.randomUUID(),
        name,
        type,
        imageData,
        stats,
        createdAt: new Date(),
      };
      setAssets((prev) => [...prev, newAsset]);
    },
    []
  );

  const deleteAsset = useCallback((id: string) => {
    setAssets((prev) => prev.filter((a) => a.id !== id));
  }, []);

  const loadAsset = useCallback((id: string) => {
    console.log('Load asset:', id);
  }, []);

  return (
    <AssetsEditorContext.Provider
      value={{
        canvasRef,
        initEngine,
        tool: currentTool,
        setTool: setCurrentTool,
        currentTool,
        setCurrentTool,
        color: currentColor,
        setColor: setCurrentColor,
        currentColor,
        setCurrentColor,
        pixelSize,
        setPixelSize,
        zoom,
        setZoom,
        brushSize,
        setBrushSize,
        clear: clearCanvas,
        clearCanvas,
        handlePointerDown,
        handlePointerMove,
        handlePointerUp,
        undo,
        redo,
        canUndo,
        canRedo,
        historyState,
        frames,
        currentFrameIndex,
        maxFrames,
        addFrame,
        deleteFrame,
        duplicateFrame,
        selectFrame,
        getFrameThumbnail,
        isPlaying,
        setIsPlaying,
        fps,
        setFps,
        loadAIImage,
        applyImageData,
        getWorkCanvas,
        isLoading,
        setIsLoading,
        featherAmount,
        setFeatherAmount,
        // Export
        downloadWebP,
        saveToLibrary,
        assets,
        deleteAsset,
        loadAsset,
      }}
    >
      {children}
    </AssetsEditorContext.Provider>
  );
}

export function useAssetsEditor() {
  const context = useContext(AssetsEditorContext);
  if (!context) {
    throw new Error('useAssetsEditor must be used within AssetsEditorProvider');
  }
  return context;
}
