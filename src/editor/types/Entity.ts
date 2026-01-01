import type { EditorVariable } from "./Variable";
import type { EditorEvent } from "./Event";
import type { EditorComponent } from "./Component";
import type { GameRule } from "../core/events/RuleEngine";
import type { EditorModule } from "./Module";

export interface EditorEntity {
    id: string;
    type: "sprite" | "container" | "nineSlice";
    name: string;
    x: number;
    y: number;
    z: number;
    texture?: string;
    variables: Record<string, any>;
    events: Record<string, string>;
    components: any[];

    /** EAC 시스템을 위한 게임 규칙 목록 */
    rules: GameRule[];

    /** 기능별 모듈 목록 */
    modules: EditorModule[];
}  // StatusModule, KineticModule 등