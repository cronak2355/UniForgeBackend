import type { EditorVariable } from "./Variable";
import type { EditorEvent } from "./Event";

export interface EditorEntity {
  id: string;
  name: string;
  x: number;
  y: number;
  variables: EditorVariable[];
  events: EditorEvent[];
}