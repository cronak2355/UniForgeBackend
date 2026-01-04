import { ActionRegistry, type ActionContext } from "../ActionRegistry";
import { EventBus } from "../EventBus";
import Phaser from "phaser";
import { editorCore } from "../../../EditorCore";

/**
 * 기본 액션 등록
 * 모듈들이 로드될 때 이 파일도 함께 로드되어야 합니다.
 */

// --- Kinetic Actions ---

ActionRegistry.register("Move", (ctx: ActionContext, params: Record<string, unknown>) => {
    const renderer = ctx.globals?.renderer as any;
    if (!renderer) {
        console.warn("[Action] Move: No renderer in context");
        return;
    }

    const entityId = ctx.entityId;
    const gameObject = renderer.getGameObject?.(entityId);
    if (!gameObject) {
        console.warn(`[Action] Move: GameObject not found for entity ${entityId}`);
        return;
    }

    const x = (params.x as number) ?? 0;
    const y = (params.y as number) ?? 0;
    const speed = (params.speed as number) ?? 200;
    const dt = 0.016;

    gameObject.x += x * speed * dt;
    gameObject.y += y * speed * dt;

    const entity = editorCore.getEntities().get(entityId);
    if (entity) {
        entity.x = gameObject.x;
        entity.y = gameObject.y;
    }
});

ActionRegistry.register("Jump", (_ctx: ActionContext, _params: Record<string, unknown>) => {
    // 물리 엔진에서 처리 - EAC 호환성용
    console.log("[Action] Jump: Handled by physics engine");
});

// --- Combat Actions ---

ActionRegistry.register("Attack", (ctx: ActionContext, params: Record<string, unknown>) => {
    const renderer = ctx.globals?.renderer as any;
    if (!renderer) return;

    const attackerId = ctx.entityId;
    const attackerObj = renderer.getGameObject?.(attackerId);
    if (!attackerObj) return;

    const range = (params.range as number) ?? 100;
    const damage = (params.damage as number) ?? 10;

    const allIds = renderer.getAllEntityIds?.() || [];
    for (const id of allIds) {
        if (id === attackerId) continue;

        const targetObj = renderer.getGameObject?.(id);
        if (!targetObj) continue;

        const distance = Phaser.Math.Distance.Between(
            attackerObj.x, attackerObj.y,
            targetObj.x, targetObj.y
        );

        if (distance <= range) {
            EventBus.emit("ATTACK_HIT", { targetId: id, damage, attackerId });
        }
    }
});

// --- Status Actions ---

ActionRegistry.register("TakeDamage", (ctx: ActionContext, params: Record<string, unknown>) => {
    const status = ctx.modules.Status as any;
    if (!status) return;

    const amount = (params.amount as number) ?? 1;

    if (typeof status.takeDamage === 'function') {
        status.takeDamage(amount);
    } else if (status.hp !== undefined) {
        status.hp = Math.max(0, status.hp - amount);
        if (status.hp <= 0) {
            EventBus.emit("ENTITY_DIED", { entityId: ctx.entityId });
        }
    }
});

ActionRegistry.register("Heal", (ctx: ActionContext, params: Record<string, unknown>) => {
    const status = ctx.modules.Status as any;
    if (!status) return;

    const amount = (params.amount as number) ?? 10;

    if (typeof status.heal === 'function') {
        status.heal(amount);
    } else if (status.hp !== undefined && status.maxHp !== undefined) {
        status.hp = Math.min(status.maxHp, status.hp + amount);
    }
});

// --- Variable Actions ---

/**
 * 변수 설정
 * params: { name: string, value: number | string }
 */
ActionRegistry.register("SetVar", (ctx: ActionContext, params: Record<string, unknown>) => {
    const entity = editorCore.getEntities().get(ctx.entityId);
    if (!entity) return;

    const varName = params.name as string;
    const value = params.value as number | string;
    if (!varName) return;

    if (!entity.variables) entity.variables = [];

    const existingVar = entity.variables.find(v => v.name === varName);
    if (existingVar) {
        existingVar.value = value;
    } else {
        entity.variables.push({
            id: crypto.randomUUID(),
            name: varName,
            type: typeof value === 'number' ? 'float' : 'string',
            value
        });
    }
});

// --- Entity Control Actions ---

/**
 * 엔티티 활성화/비활성화
 * params: { targetId?: string, enabled?: boolean }
 * enabled 생략시 true (활성화)
 */
ActionRegistry.register("Enable", (ctx: ActionContext, params: Record<string, unknown>) => {
    const renderer = ctx.globals?.renderer as any;
    if (!renderer) return;

    const targetId = (params.targetId as string) ?? ctx.entityId;
    const enabled = (params.enabled as boolean) ?? true;
    const gameObject = renderer.getGameObject?.(targetId);

    if (gameObject) {
        gameObject.setVisible(enabled);
        gameObject.setActive(enabled);
        EventBus.emit(enabled ? "ENTITY_ENABLED" : "ENTITY_DISABLED", { entityId: targetId });
    }
});

// --- Scene Actions ---

/**
 * 씬 전환
 * params: { sceneName: string, data?: object }
 */
ActionRegistry.register("ChangeScene", (ctx: ActionContext, params: Record<string, unknown>) => {
    const scene = ctx.globals?.scene as Phaser.Scene | undefined;
    if (!scene) return;

    const sceneName = params.sceneName as string;
    const data = params.data as object | undefined;
    if (!sceneName) return;

    EventBus.emit("SCENE_CHANGING", { from: scene.scene.key, to: sceneName });
    scene.scene.start(sceneName, data);
});

console.log("[DefaultActions] 8 actions registered: Move, Jump, Attack, TakeDamage, Heal, SetVar, Enable, ChangeScene");
