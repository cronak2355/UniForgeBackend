import React, { createContext, useContext, useRef, useEffect, useState } from "react";
import { EditorState } from "../editor/EditorCore";

const EditorCoreContext = createContext<EditorState | null>(null);

export function EditorCoreProvider({ children }: { children: React.ReactNode }) {
  const ref = useRef<EditorState | null>(null);
  if (!ref.current) ref.current = new EditorState();

  return <EditorCoreContext.Provider value={ref.current}>{children}</EditorCoreContext.Provider>;
}

export function useEditorCore(): EditorState {
  const ctx = useContext(EditorCoreContext);
  if (!ctx) throw new Error("useEditorCore must be used within EditorCoreProvider");
  return ctx;
}

// 편리한 훅: EditorState를 구독하고 스냅샷을 반환
export function useEditorCoreSnapshot() {
    const core = useEditorCore();
    const [, setVersion] = useState(0);
    useEffect(() => {
        const unsub = core.subscribe(() => setVersion((v) => v + 1));
        return () => {
        unsub();
        };
    }, [core]);
    
    return {
        core,
        assets: Array.from(core.getAssets()),
        entities: Array.from(core.getEntities().values()),
        selectedAsset: core.getSelectedAsset(),
        draggedAsset: core.getDraggedAsset(),
        selectedEntity: core.getSelectedEntity(),
        editorMode: core.getEditorMode(),
        
    };
}
