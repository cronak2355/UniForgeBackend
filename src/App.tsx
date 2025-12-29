import "./App.css";
import { useState } from "react";
import { InspectorPanel } from "./editor/inspector/InspectorPanel";
import type { EditorEntity } from "./editor/types/Entity";
import { EventSection } from "./editor/inspector/EventSection";
import type { EditorEvent } from "./editor/types/Event";
/**
 * App
 * ------------------------------------------------------
 * 에디터 전체 레이아웃의 진입 컴포넌트
 * 현재 단계에서는 실제 Hierarchy / Canvas 연동 없이
 * Inspector UI와 데이터 구조를 검증하기 위한 더미 엔티티를 사용한다.
 */
function App() {
  const [selectedEntity, setselectedEntity] = useState<EditorEntity>({
    id: "entity-1",
    name: "Test Player",
    x: 120,
    y: 300,
    variables: [
      {
        id: "var-1",
        name: "hp",
        type: "int",
        value: 100,
      },
      {
        id: "var-2",
        name: "speed",
        type: "float",
        value: 2.5,
      },
      {
        id: "var-3",
        name: "nickname",
        type: "string",
        value: "hero",
      },
    ],
    events: [
      {
        id: "event-1",
        trigger: "OnStart",
        action: "ShowText",
      },
    ],
  });

  const dummyEvents: EditorEvent[] = [
    {
      id: "event-1",
      trigger: "OnStart",
      action: "ShowText",
    },
  ];

  return (
    <div className="editor-root">
      <div className="editor-topbar">
        <span>file</span>
        <span>assets</span>
        <span>edit</span>
      </div>

      <div className="editor-main">
        <div className="editor-panel left">
          <div className="editor-panel-header">Hierarchy</div>
        </div>

        <div className="editor-camera">
          <div className="editor-camera-header">Camera</div>
          <div className="editor-camera-viewport">
            {/* PhaserCanvas 삽입 */}
          </div>
        </div>

        <div className="editor-panel right">
          <div className="editor-panel-header">Inspector</div>
          <InspectorPanel entity={selectedEntity}
            onUpdateEntity={setselectedEntity}
          />
        </div>
      </div>

      <div className="editor-assets">
        <div className="editor-assets-tabs">
          <span>Tile</span>
          <span>Sfx</span>
        </div>
        <div className="editor-assets-grid">
          <div className="asset-item" />
          <div className="asset-item" />
          <div className="asset-item" />
        </div>
      </div>
    </div>
  );
}

export default App;
