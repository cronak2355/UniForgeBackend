import React, { useEffect, useRef } from "react";
import Phaser from "phaser";
import type { EditorEntity } from "./EditorLayout";


class EditorScene extends Phaser.Scene {
    ready = false;

    create() {
        this.ready = true;
        this.input.enabled = false;
    }

    updateEntities(entities: EditorEntity[]) {
        if (!this.ready) return;

        this.children.removeAll();
        entities.forEach(e => {
            const rect = this.add.rectangle(e.x, e.y, 40, 40, 0xffffff);
            rect.setData("id", e.id);
        });
    }
}



export function PhaserCanvas({
    entities,
    setEntities,
    selectedId,
    setSelectedId
}: {
    entities: EditorEntity[];
    setEntities: (e: EditorEntity[]) => void;
    selectedId: string | null;
    setSelectedId: (id: string | null) => void;
}) {
    const ref = useRef<HTMLDivElement>(null);
    const sceneRef = useRef<EditorScene | null>(null);


    useEffect(() => {
        if (!ref.current) return;


        const scene = new EditorScene();
        sceneRef.current = scene;


        const game = new Phaser.Game({
            type: Phaser.AUTO,
            width: 800,
            height: 600,
            backgroundColor: "#000000",
            parent: ref.current,
            scene
        });


        return () => {
            game.destroy(true);
        };
    }, []);


    useEffect(() => {
        sceneRef.current?.updateEntities(entities);
    }, [entities]);


    return (
        <div className="flex-1 p-2">
            <div className="border border-white px-2 py-1 mb-2 w-fit">Camera</div>
            <div ref={ref} className="border border-white w-full h-full" />
        </div>
    );
}