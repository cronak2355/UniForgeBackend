import { VariableSection } from "./VariableSection";
import type { EditorEntity } from "../types/Entity";
import { EventSection } from "./EventSection";
import type { EditorEvent } from "../types/Event";

/**
 * InspectorPanel
 * 선택된 `EditorEntity`의 속성과 이벤트를 편집할 수 있는 사이드 패널입니다.
 * 주요 역할:
 * - 선택된 엔티티의 기본 정보(이름, 위치)를 표시
 * - `VariableSection`을 통해 변수 추가/수정 처리
 * - `EventSection`을 통해 이벤트 추가/수정 처리
 */
type Props = {
  entity: EditorEntity | null;
  onUpdateEntity: (entity: EditorEntity) => void;
};
export function InspectorPanel({
  entity,
  onUpdateEntity,
}: Props) {
  if (!entity) {
    return (
      <div className="inspector-root opacity-50">
        Inspector<br />
        아무 오브젝트도 선택되지 않았습니다.
      </div>
    );
  }

  /**
   * 새로운 이벤트를 Entity에 추가
   */
  const handleAddEvent = () => {
    onUpdateEntity({
      ...entity,
      events: [
        ...entity.events,
        {
          id: crypto.randomUUID(),
          trigger: "OnStart",
          action: "ShowText",
        },
      ],
    });
  };

  /**
 * 기존 이벤트 수정
 */
  const handleUpdateEvent = (updatedEvent: EditorEvent) => {
    onUpdateEntity({
      ...entity,
      events: entity.events.map(e =>
        e.id === updatedEvent.id ? updatedEvent : e)
      //기존 이벤트 수정
      //전달받은 `updatedEvent`와 동일한 id를 가진 이벤트를 교체
    });
  };

  return (
    <div className="inspector-root">
      <div className="font-bold text-base"></div>

      {/* BASIC */}
      <section className="inspector-section">
        <div className="inspector-section-title">Basic</div>

        <div className="inspector-row">
          <span className="inspector-label">Name</span>
          {/* 엔티티 이름 표시*/}
          <span>{entity.name}</span>
        </div>

        <div className="inspector-row">
          <span className="inspector-label">Position</span>
          {/* 엔티티의 현재 좌표 표시 */}
          <span>X:{entity.x} Y:{entity.y}</span>
        </div>
      </section>

      {/* VARIABLES */}
      <div className="inspector-scroll">
        <section className="inspector-section">
          {/* 변수 목록과 추가/수정 핸들러 전달
              - onAdd: 새 변수를 기본값으로 추가
              - onUpdate: 특정 변수를 업데이트 */}
          <VariableSection
            variables={entity.variables}
            onAdd={() => {
              onUpdateEntity({
                ...entity,
                variables: [
                  ...entity.variables,
                  {
                    id: crypto.randomUUID(),
                    name: "newVar",
                    type: "int",
                    value: 0,
                  },
                ],
              });
            }}
            onUpdate={(updatedVar) => {
              onUpdateEntity({
                ...entity,
                variables: entity.variables.map(v =>
                  v.id === updatedVar.id ? updatedVar : v
                ),
              });
            }}
          />
        </section>

        {/* EVENTS */}
        <section className="inspector-section opacity-50">
          {/* 이벤트 섹션
              - onAdd: 새로운 이벤트 추가 (handleAddEvent)
              - onUpdate: 이벤트 업데이트 처리 (handleUpdateEvent)
              - 현재 섹션은 `opacity-50`로 시각적으로 구분 */}
          <div className="inspector-section-title"></div>
          <EventSection
            events={entity.events}
            onAdd={handleAddEvent}
            onUpdate={handleUpdateEvent}
          />
        </section>
      </div>
    </div>
  );
}

