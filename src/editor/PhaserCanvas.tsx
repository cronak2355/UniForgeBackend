import React, { useEffect, useRef, useState } from "react";
import Phaser from "phaser";
import type { EditorEntity } from "./EditorLayout";
import {EditorMode, CameraMode, TilingMode} from "./editorMode/editorModes"
import type { Asset } from "../data/Asset"
const tileSize = 32;
let gridGfx: Phaser.GameObjects.Graphics;
type Props = {
    assets:Asset[];
    selected_asset:Asset | null;
};

function loadImages(scene: Phaser.Scene, assets: Asset[]) {
  return new Promise<void>((resolve, reject) => {
    let pending = 0;

    const onFileComplete = (key: string) => {
      pending--;
      if (pending === 0) {
        cleanup();
        resolve();
      }
    };

    const onError = () => {
      cleanup();
      reject(new Error("asset load error"));
    };

    const cleanup = () => {
      scene.load.off(Phaser.Loader.Events.FILE_COMPLETE, onFileComplete);
      scene.load.off(Phaser.Loader.Events.FILE_LOAD_ERROR, onError);
    };

    // 이벤트 등록
    scene.load.on(Phaser.Loader.Events.FILE_COMPLETE, onFileComplete);
    scene.load.on(Phaser.Loader.Events.FILE_LOAD_ERROR, onError);

    // 큐에 추가
    for (let i = 0; i < assets.length; i++) {
      const asset = assets[i];

      // 이미 있으면 스킵
      if (scene.textures.exists(asset.name)) continue;

      scene.load.image(asset.name, asset.url);
      pending++;
    }

    // 로드할 게 없으면 바로 끝
    if (pending === 0) {
      cleanup();
      resolve();
      return;
    }

    // 로더 시작
    scene.load.start();
  });
}


//그리드 표시
function redrawGrid(scene: Phaser.Scene) {
  const cam = scene.cameras.main;
  const view = cam.worldView; // 현재 카메라가 보는 월드 영역

  // 보이는 영역을 tileSize 경계로 스냅
  const left = Math.floor(view.x / tileSize) * tileSize;
  const right = Math.ceil((view.x + view.width) / tileSize) * tileSize;
  const top = Math.floor(view.y / tileSize) * tileSize;
  const bottom = Math.ceil((view.y + view.height) / tileSize) * tileSize;

  gridGfx.clear();

  // 선 스타일 (두께 1, 색은 아무거나)
  gridGfx.lineStyle(1, 0xffffff, 0.15);

  // 세로선
  for (let x = left; x <= right; x += tileSize) {
    gridGfx.beginPath();
    gridGfx.moveTo(x, top);
    gridGfx.lineTo(x, bottom);
    gridGfx.strokePath();
  }

  // 가로선
  for (let y = top; y <= bottom; y += tileSize) {
    gridGfx.beginPath();
    gridGfx.moveTo(left, y);
    gridGfx.lineTo(right, y);
    gridGfx.strokePath();
  }
}
class EditorScene extends Phaser.Scene {
    ready = false;
    entityGroups!: Phaser.GameObjects.Container;
    private editorMode:EditorMode = new CameraMode();
    constructor() {
        super("EditorScene");
    }


    create() {
        this.ready = true;
        //this.input.enabled = false;
        gridGfx = this.add.graphics();
        gridGfx.setDepth(9999); // 항상 위에 보이게 (필요하면)

        this.entityGroups = this.add.container(0, 0);

        //마우스 다운
        this.input.on("pointerdown", (pointer: Phaser.Input.Pointer) => {
            this.editorMode.onPointerDown(this, pointer);
        });

        // 2) 마우스 업
        this.input.on("pointerup", (pointer: Phaser.Input.Pointer) => {
            this.editorMode.onPointerUp(this, pointer);
        });

        // 3) 마우스 움직이기
        this.input.on("pointermove", (pointer: Phaser.Input.Pointer) => {
            this.editorMode.onPointerMove(this, pointer);
        });

        // 4) 휠 줌
        this.input.on("wheel",(pointer: Phaser.Input.Pointer, _objs: any, _dx: number, dy: number) => {
            this.editorMode.onScroll(this, dy);
        });
    }

    updateEntities(entities: EditorEntity[]) {
        if (!this.ready) return;

        this.children.removeAll();
        entities.forEach(e => {
            const rect = this.add.rectangle(e.x, e.y, 40, 40, 0xffffff);
            rect.setData("id", e.id);
        });
    }
    setEditorMode(mode:EditorMode)
    {
        this.editorMode = mode;
    }
    update()
    {
        redrawGrid(this)
    }
}

export function PhaserCanvas({ assets, selected_asset }: Props) {
    const ref = useRef<HTMLDivElement>(null);
    const sceneRef = useRef<EditorScene | null>(null);
    const [currentEditorMode, setEditorMode] = useState<EditorMode>(() => new CameraMode());
    useEffect(() => {
        if (!ref.current) return;

        const config: Phaser.Types.Core.GameConfig = {
            type: Phaser.AUTO,
            scale: { mode: Phaser.Scale.RESIZE },
            parent: ref.current,
            scene: [EditorScene],
            audio: {
                noAudio : true
            }
        };
        
        const game = new Phaser.Game(config);
        return () =>
        {
            game.destroy(true);
        }
    }, []);
    return (
        <div className="flex-1 p-2">
            <div className="border border-white px-2 py-1 mb-2 w-fit d-flex justify-content-left">
                <div className="editor-item px-1">Camera</div>
                { currentEditorMode instanceof TilingMode &&
                    <div className="border border-white mx-1">
                        <button>그리기</button>
                        <button>지우기</button>
                    </div>
                }
                
            </div>

            <div ref={ref} />
        </div>
    );
}