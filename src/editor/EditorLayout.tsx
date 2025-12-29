import React, { useRef, useState } from "react";
import { PhaserCanvas } from "./PhaserCanvas";
import { HierarchyPanel } from "./HierarchyPanel";
import { InspectorPanel } from "./inspector/InspectorPanel";
import { AssetPanel } from "./AssetPanel";
import type { EditorEntity } from "./types/Entity";

export default function EditorLayout() {
    const [entities, setEntities] = useState<EditorEntity[]>([]);
    const [selectedId, setSelectedId] = useState<string | null>(null);
    const handleUpdateEntity = (updated: EditorEntity) => {
        setEntities(prev =>
            prev.map(e => (e.id === updated.id ? updated : e))
        );
    };


    return (
        <div className="w-screen h-screen bg-black text-white flex flex-col">
            <div className="h-10 flex items-center px-3 border-b border-white">UNIFORGE</div>


            <div className="flex flex-1">
                <HierarchyPanel
                    entities={entities}
                    selectedId={selectedId}
                    onSelect={setSelectedId}
                />


                <PhaserCanvas
                    entities={entities}
                    setEntities={setEntities}
                    selectedId={selectedId}
                    setSelectedId={setSelectedId}
                />


                <InspectorPanel
                    entity={entities.find(e => e.id === selectedId) ?? null}
                    onUpdateEntity={handleUpdateEntity}
                />
            </div>


            <AssetPanel />
        </div>
    );
}
