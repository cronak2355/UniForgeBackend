import { useState } from "react";
import type { SceneState, EditorEntity } from "./EditorState";
import { HierarchyPanel } from "./HierarchyPanel";
import { InspectorPanel } from "./inspector/InspectorPanel";
import { AssetPanel } from "./AssetPanel";
import type { EditorEntity } from "./types/Entity";
import { CameraView } from "./CameraView";
import { AssetPanel } from "./AssetPanel";
import { InspectorPanel } from "./InspectorPanel";

const initialScene: SceneState = { entities: [] };

export default function EditorLayout() {
    const [entities, setEntities] = useState<EditorEntity[]>([]);
    const [scene, setScene] = useState<SceneState>(initialScene);
    const [selectedId, setSelectedId] = useState<string | null>(null);
    const handleUpdateEntity = (updated: EditorEntity) => {
        setEntities(prev =>
            prev.map(e => (e.id === updated.id ? updated : e))
        );
    };

    const addEntity = (entity: EditorEntity) => {
        setScene((prev) => {
            const sameTypeCount = prev.entities.filter(
                (e) => e.type === entity.type
            ).length;

            const name =
                sameTypeCount === 0
                    ? entity.type
                    : `${entity.type} (${sameTypeCount})`;

            return {
                entities: [...prev.entities, { ...entity, name }],
            };
        });
    };

    const moveEntity = (id: string, x: number, y: number) => {
        setScene((prev) => ({
            entities: prev.entities.map((e) =>
                e.id === id ? { ...e, x, y } : e
            ),
        }));
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
        <div className="editor-root">
            <div className="editor-topbar">
                <span>file</span>
                <span>assets</span>
                <span>edit</span>
            </div>

            <div className="editor-main">
                <div className="editor-panel">
                    <div className="editor-panel-header">Hierarchy</div>
                    <HierarchyPanel
                        entities={scene.entities}
                        selectedId={selectedId}
                        onSelect={setSelectedId}
                    />
                </div>

                <CameraView
                    entities={scene.entities}
                    onCreateEntity={addEntity}
                    onMoveEntity={moveEntity}
                />

                <div className="editor-panel right">
                    <div className="editor-panel-header">Inspector</div>
                    <InspectorPanel
                        selectedId={selectedId}
                        entities={scene.entities}
                    />
                </div>
            </div>
            <AssetPanel />
        </div>
        </div>
    );
}
