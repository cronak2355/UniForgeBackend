/**
 * GameCore - 엔진 독립적 게임 로직 계층
 * 
 * 모든 게임 로직(캐릭터 이동, 상태 관리, 게임 규칙 등)은 이 클래스에서 관리합니다.
 * Phaser 등 렌더링 엔진에 대한 의존성이 없으며, IRenderer 인터페이스만 사용합니다.
 */

import type { IRenderer } from "../renderer/IRenderer";
import type { EditorComponent, AutoRotateComponent, PulseComponent } from "../types/Component";
import type { EditorVariable } from "../types/Variable";
import type { EditorModule } from "../types/Module";
import type { GameRule } from "./events/RuleEngine";
import type { InputState } from "./RuntimePhysics";
import { EventBus } from "./events/EventBus";
import type { IModule } from "./modules/IModule";
import { KineticModule } from "./modules/KineticModule";
import { StatusModule } from "./modules/StatusModule";
import { CombatModule, type TargetInfo, type ProjectileSpawnSignal } from "./modules/CombatModule";
import { NarrativeModule } from "./modules/NarrativeModule";

/**
 * 게임 엔티티 데이터 구조 (순수 JavaScript 객체)
 */
export interface GameEntity {
    id: string;
    type: string;
    name: string;
    x: number;
    y: number;
    z: number;  // 기본값 0, Phaser에서는 depth로 사용
    rotationX: number;
    rotationY: number;
    rotationZ: number;
    scaleX: number;
    scaleY: number;
    scaleZ: number;
    variables: EditorVariable[];
    components: EditorComponent[];
    modules: EditorModule[];
    rules: GameRule[];
}

/**
 * 엔티티 생성 옵션
 */
export interface CreateEntityOptions {
    name?: string;
    z?: number;
    rotationX?: number;
    rotationY?: number;
    rotationZ?: number;
    scaleX?: number;
    scaleY?: number;
    scaleZ?: number;
    variables?: EditorVariable[];
    components?: EditorComponent[];
    modules?: EditorModule[];
    rules?: GameRule[];
    texture?: string;
    width?: number;
    height?: number;
    color?: number;
}

/**
 * 컴포넌트 런타임 데이터
 */
interface ComponentRuntime {
    entityId: string;
    component: EditorComponent;
    // Pulse 컴포넌트용 초기 스케일
    initialScale?: { x: number; y: number };
}

interface ModuleRuntime {
    entityId: string;
    module: IModule;
}

interface ProjectileRuntime {
    id: string;
    fromId: string;
    targetId: string | null;
    x: number;
    y: number;
    z: number;
    dirX: number;
    dirY: number;
    speed: number;
    damage: number;
    pierceCount: number;
    explosionRadius: number;
    life: number;
    hitTargets: Set<string>;
}

/**
 * GameCore - 엔진 독립적 로직 계층
 * 
 * 설계 원칙:
 * 1. ID 동기화: 외부에서 전달받은 ID를 사용하며, 중복 검사 수행
 * 2. 순수 데이터: 모든 상태는 순수 JavaScript 객체로 관리
 * 3. 렌더러 추상화: IRenderer 인터페이스만 호출
 */
export class GameCore {
    private renderer: IRenderer;

    // ===== 엔티티 관리 - ID 동기화 보장 =====
    private entities: Map<string, GameEntity> = new Map();

    // ===== 컴포넌트 런타임 (최적화된 업데이트 루프) =====
    private componentRuntimes: ComponentRuntime[] = [];
    private moduleRuntimes: Map<string, ModuleRuntime[]> = new Map();
    private projectileRuntimes: Map<string, ProjectileRuntime> = new Map();
    private inputState: InputState = { left: false, right: false, up: false, down: false, jump: false };
    private groundY = 500;
    private readonly projectileTtl = 2;
    private readonly projectileSize = 6;
    private readonly projectileColor = 0xffcc00;
    private readonly projectileHitRadius = 8;

    // ===== 구독자 (상태 변경 알림) =====
    private listeners: Set<() => void> = new Set();

    constructor(renderer: IRenderer) {
        this.renderer = renderer;
    }

    // ===== Entity Management - ID 동기화 보장 =====

    /**
     * 엔티티 생성
     * @param id 외부에서 전달받은 ID (자체 생성 금지)
     * @param type 엔티티 타입
     * @param x X 좌표
     * @param y Y 좌표
     * @param z Z 좌표
     * @param options 추가 옵션
     * @returns 생성 성공 여부
     */
    createEntity(
        id: string,
        type: string,
        x: number,
        y: number,
        options: CreateEntityOptions = {}
    ): boolean {
        // ID 중복 검사 - EditorState와의 동기화 보장
        if (this.entities.has(id)) {
            console.error(`[GameCore] Entity with id "${id}" already exists! ID sync violation.`);
            return false;
        }

        const entity: GameEntity = {
            id,
            type,
            name: options.name ?? `Entity_${id.slice(0, 8)}`,
            x,
            y,
            z: options.z ?? 0,
            rotationX: options.rotationX ?? 0,
            rotationY: options.rotationY ?? 0,
            rotationZ: options.rotationZ ?? 0,
            scaleX: options.scaleX ?? 1,
            scaleY: options.scaleY ?? 1,
            scaleZ: options.scaleZ ?? 1,
            variables: options.variables ?? [],
            components: options.components ?? [],
            modules: options.modules ?? [],
            rules: options.rules ?? [],
        };

        // 1. 로컬 상태에 저장
        this.entities.set(id, entity);

        // 2. 렌더러에 스폰 요청
        this.renderer.spawn(id, type, x, y, entity.z, {
            texture: options.texture,
            width: options.width,
            height: options.height,
            color: options.color,
        });
        this.renderer.update(id, entity.x, entity.y, entity.z, entity.rotationZ);
        this.renderer.setScale(id, entity.scaleX, entity.scaleY, entity.scaleZ);

        // 3. 컴포넌트 런타임 등록
        this.registerComponentRuntimes(entity);
        this.registerModuleRuntimes(entity);

        // 4. 구독자 알림
        this.notify();

        console.log(`[GameCore] Created entity: ${id} (${type}) at (${x}, ${y}, ${entity.z})`);
        return true;
    }

    /**
     * 엔티티 이동
     */
    moveEntity(id: string, x: number, y: number, z?: number): void {
        const entity = this.entities.get(id);
        if (!entity) {
            console.warn(`[GameCore] Cannot move: entity "${id}" not found`);
            return;
        }

        entity.x = x;
        entity.y = y;
        if (z !== undefined) {
            entity.z = z;
        }

        const kinetic = this.getModuleByType<KineticModule>(id, "Kinetic");
        if (kinetic) {
            kinetic.position = { x: entity.x, y: entity.y, z: entity.z };
        }

        this.renderer.update(id, x, y, entity.z, entity.rotationZ);
        this.notify();
    }

    /**
     * 엔티티 회전
     */
    rotateEntity(id: string, rotation: number): void {
        const entity = this.entities.get(id);
        if (!entity) return;

        entity.rotationZ = rotation;
        this.renderer.update(id, entity.x, entity.y, entity.z, rotation);
        this.notify();
    }

    /**
     * 엔티티 제거
     */
    removeEntity(id: string): void {
        const entity = this.entities.get(id);
        if (!entity) {
            console.warn(`[GameCore] Cannot remove: entity "${id}" not found`);
            return;
        }

        // 1. 컴포넌트 런타임 제거
        this.unregisterComponentRuntimes(id);
        this.unregisterModuleRuntimes(id);
        this.removeProjectilesForEntity(id);

        // 2. 렌더러에서 제거
        this.renderer.remove(id);

        // 3. 로컬 상태에서 제거
        this.entities.delete(id);

        // 4. 구독자 알림
        this.notify();

        console.log(`[GameCore] Removed entity: ${id}`);
    }

    /**
     * 엔티티 조회
     */
    getEntity(id: string): GameEntity | undefined {
        return this.entities.get(id);
    }

    /**
     * 엔티티 존재 여부 확인
     */
    hasEntity(id: string): boolean {
        return this.entities.has(id);
    }

    /**
     * 모든 엔티티 반환
     */
    getAllEntities(): Map<string, GameEntity> {
        return new Map(this.entities);
    }

    /**
     * 엔티티 수 반환
     */
    getEntityCount(): number {
        return this.entities.size;
    }

    /**
     * ID 동기화 검증
     * GameCore와 Renderer의 엔티티 ID가 일치하는지 확인
     */
    validateIdSync(): boolean {
        const coreIds = Array.from(this.entities.keys()).sort();
        const rendererIds = this.renderer.getAllEntityIds().sort();

        if (coreIds.length !== rendererIds.length) {
            console.error(`[GameCore] ID sync mismatch: core=${coreIds.length}, renderer=${rendererIds.length}`);
            return false;
        }

        for (let i = 0; i < coreIds.length; i++) {
            if (coreIds[i] !== rendererIds[i]) {
                console.error(`[GameCore] ID mismatch at index ${i}: core="${coreIds[i]}", renderer="${rendererIds[i]}"`);
                return false;
            }
        }

        return true;
    }

    /**
     * 런타임 입력 상태 갱신
     */
    setInputState(input: InputState): void {
        this.inputState = { ...input };
    }

    /**
     * Platformer 바닥 높이 설정
     */
    setGroundY(y: number): void {
        this.groundY = y;
    }

    // ===== Component System =====

    /**
     * 엔티티에 컴포넌트 추가
     */
    addComponent(entityId: string, component: EditorComponent): void {
        const entity = this.entities.get(entityId);
        if (!entity) {
            console.warn(`[GameCore] Cannot add component: entity "${entityId}" not found`);
            return;
        }

        entity.components.push(component);

        // 런타임 등록
        this.componentRuntimes.push({
            entityId,
            component,
            initialScale: { x: entity.scaleX, y: entity.scaleY },
        });

        this.notify();
    }

    /**
     * 엔티티에서 컴포넌트 제거
     */
    removeComponent(entityId: string, componentId: string): void {
        const entity = this.entities.get(entityId);
        if (!entity) return;

        const idx = entity.components.findIndex(c => c.id === componentId);
        if (idx >= 0) {
            entity.components.splice(idx, 1);
        }

        // 런타임 제거
        this.componentRuntimes = this.componentRuntimes.filter(
            r => !(r.entityId === entityId && r.component.id === componentId)
        );

        this.notify();
    }

    /**
     * 컴포넌트 런타임 등록 (내부용)
     */
    private registerComponentRuntimes(entity: GameEntity): void {
        for (const comp of entity.components) {
            this.componentRuntimes.push({
                entityId: entity.id,
                component: comp,
                initialScale: { x: entity.scaleX, y: entity.scaleY },
            });
        }
    }

    /**
     * 컴포넌트 런타임 제거 (내부용)
     */
    private unregisterComponentRuntimes(entityId: string): void {
        this.componentRuntimes = this.componentRuntimes.filter(r => r.entityId !== entityId);
    }

    private getModules(entityId: string): ModuleRuntime[] {
        return this.moduleRuntimes.get(entityId) ?? [];
    }

    private getModuleByType<T extends IModule>(entityId: string, type: string): T | undefined {
        const modules = this.getModules(entityId);
        const match = modules.find(m => m.module.type === type);
        return match?.module as T | undefined;
    }

    private registerModuleRuntimes(entity: GameEntity): void {
        if (!entity.modules || entity.modules.length === 0) {
            return;
        }

        const runtimes: ModuleRuntime[] = [];

        for (const moduleData of entity.modules) {
            switch (moduleData.type) {
                case "Status": {
                    const status = new StatusModule(moduleData.id, {
                        hp: moduleData.hp,
                        maxHp: moduleData.maxHp,
                        mp: moduleData.mp,
                        maxMp: moduleData.maxMp,
                        attack: moduleData.attack,
                        defense: moduleData.defense,
                        speed: moduleData.speed,
                    });
                    runtimes.push({ entityId: entity.id, module: status });
                    break;
                }
                case "Kinetic": {
                    const kinetic = new KineticModule(moduleData.id, {
                        mode: moduleData.mode,
                        maxSpeed: moduleData.maxSpeed,
                        friction: moduleData.friction,
                        gravity: moduleData.gravity,
                        jumpForce: moduleData.jumpForce,
                    });
                    kinetic.position = { x: entity.x, y: entity.y, z: entity.z };
                    runtimes.push({ entityId: entity.id, module: kinetic });
                    break;
                }
                case "Combat": {
                    const combat = new CombatModule(moduleData.id, {
                        attackRange: moduleData.attackRange,
                        attackInterval: moduleData.attackInterval,
                        damage: moduleData.damage,
                        bulletPattern: moduleData.bulletPattern,
                        bulletCount: moduleData.bulletCount,
                    });
                    combat.onSpawnProjectile = (signal) => this.spawnProjectile(signal);
                    runtimes.push({ entityId: entity.id, module: combat });
                    break;
                }
                case "Narrative": {
                    const narrative = new NarrativeModule(moduleData.id);
                    runtimes.push({ entityId: entity.id, module: narrative });
                    break;
                }
            }
        }

        if (runtimes.length > 0) {
            this.moduleRuntimes.set(entity.id, runtimes);
        }
    }

    private unregisterModuleRuntimes(entityId: string): void {
        const runtimes = this.moduleRuntimes.get(entityId);
        if (runtimes) {
            for (const runtime of runtimes) {
                runtime.module.destroy();
            }
        }
        this.moduleRuntimes.delete(entityId);
    }

    private updateModules(dt: number): void {
        if (this.moduleRuntimes.size === 0) return;

        const targets = this.buildTargetInfo();

        for (const [entityId, runtimes] of this.moduleRuntimes) {
            const entity = this.entities.get(entityId);
            if (!entity) continue;

            for (const runtime of runtimes) {
                switch (runtime.module.type) {
                    case "Kinetic": {
                        const kinetic = runtime.module as KineticModule;
                        if (kinetic.mode === "TopDown") {
                            kinetic.processTopDownInput(this.inputState);
                        } else if (kinetic.mode === "Platformer") {
                            kinetic.processPlatformerInput(this.inputState);
                        }

                        kinetic.update(dt);

                        if (kinetic.mode === "Platformer" && kinetic.position.y >= this.groundY) {
                            kinetic.land(this.groundY);
                        }

                        entity.x = kinetic.position.x;
                        entity.y = kinetic.position.y;
                        entity.z = kinetic.position.z ?? entity.z;
                        this.renderer.update(entity.id, entity.x, entity.y, entity.z, entity.rotationZ);
                        break;
                    }
                    case "Combat": {
                        const combat = runtime.module as CombatModule;
                        combat.position = { x: entity.x, y: entity.y, z: entity.z };
                        combat.update(dt);
                        combat.updateAutoAttack(targets);
                        break;
                    }
                    default:
                        runtime.module.update(dt);
                        break;
                }
            }
        }
    }

    private buildTargetInfo(): TargetInfo[] {
        const targets: TargetInfo[] = [];
        for (const [id, entity] of this.entities) {
            const status = this.getModuleByType<StatusModule>(id, "Status");
            targets.push({
                id,
                position: { x: entity.x, y: entity.y, z: entity.z },
                hp: status?.hp,
            });
        }
        return targets;
    }

    private spawnProjectile(signal: ProjectileSpawnSignal): void {
        if (this.projectileRuntimes.has(signal.id)) return;

        this.projectileRuntimes.set(signal.id, {
            id: signal.id,
            fromId: signal.fromId,
            targetId: signal.targetId,
            x: signal.position.x,
            y: signal.position.y,
            z: signal.position.z ?? 0,
            dirX: signal.direction.x,
            dirY: signal.direction.y,
            speed: signal.speed,
            damage: signal.damage,
            pierceCount: signal.pierceCount,
            explosionRadius: signal.explosionRadius,
            life: this.projectileTtl,
            hitTargets: new Set(),
        });

        this.renderer.spawn(signal.id, "projectile", signal.position.x, signal.position.y, signal.position.z ?? 0, {
            width: this.projectileSize,
            height: this.projectileSize,
            color: this.projectileColor,
        });
    }

    private updateProjectiles(dt: number): void {
        for (const [id, projectile] of this.projectileRuntimes) {
            projectile.life -= dt;
            if (projectile.life <= 0) {
                this.removeProjectile(id);
                continue;
            }

            projectile.x += projectile.dirX * projectile.speed * dt;
            projectile.y += projectile.dirY * projectile.speed * dt;
            this.renderer.update(id, projectile.x, projectile.y, projectile.z);

            if (projectile.targetId) {
                const target = this.entities.get(projectile.targetId);
                if (!target) continue;

                if (projectile.hitTargets.has(target.id)) continue;

                const dx = target.x - projectile.x;
                const dy = target.y - projectile.y;
                const hit = (dx * dx + dy * dy) <= (this.projectileHitRadius * this.projectileHitRadius);
                if (!hit) continue;

                this.applyProjectileHit(projectile, target.id);
                projectile.hitTargets.add(target.id);

                if (projectile.pierceCount > 0) {
                    projectile.pierceCount -= 1;
                } else {
                    this.removeProjectile(id);
                }
            }
        }
    }

    private applyProjectileHit(projectile: ProjectileRuntime, targetId: string): void {
        if (projectile.explosionRadius > 0) {
            for (const [id, entity] of this.entities) {
                if (id === projectile.fromId) continue;
                if (projectile.hitTargets.has(id)) continue;
                const dx = entity.x - projectile.x;
                const dy = entity.y - projectile.y;
                if ((dx * dx + dy * dy) <= (projectile.explosionRadius * projectile.explosionRadius)) {
                    this.applyDamage(id, projectile.damage, projectile.fromId);
                    projectile.hitTargets.add(id);
                }
            }
            return;
        }

        if (targetId === projectile.fromId) return;
        this.applyDamage(targetId, projectile.damage, projectile.fromId);
    }

    private applyDamage(targetId: string, damage: number, attackerId?: string): void {
        const status = this.getModuleByType<StatusModule>(targetId, "Status");
        if (status) {
            status.takeDamage(damage);
            if (!status.isAlive) {
                EventBus.emit("ENTITY_DIED", { entityId: targetId, attackerId });
            }
        }

        EventBus.emit("ATTACK_HIT", { targetId, damage, attackerId });
    }

    private removeProjectile(id: string): void {
        this.projectileRuntimes.delete(id);
        this.renderer.remove(id);
    }

    private removeProjectilesForEntity(entityId: string): void {
        for (const [id, projectile] of this.projectileRuntimes) {
            if (projectile.fromId === entityId || projectile.targetId === entityId) {
                this.removeProjectile(id);
            }
        }
    }

    // ===== Update Loop =====

    /**
     * 프레임 업데이트 (렌더러의 update에서 호출)
     * @param time 현재 시간 (ms)
     * @param deltaTime 이전 프레임으로부터의 시간 (초)
     */
    update(time: number, deltaTime: number): void {
        const dt = deltaTime / 1000;

        this.updateModules(dt);
        this.updateProjectiles(dt);

        for (const runtime of this.componentRuntimes) {
            const entity = this.entities.get(runtime.entityId);
            if (!entity) continue;

            this.processComponent(entity, runtime, time, dt);
        }
    }

    /**
     * 컴포넌트 처리 (순수 로직)
     */
    private processComponent(
        entity: GameEntity,
        runtime: ComponentRuntime,
        time: number,
        dt: number
    ): void {
        const comp = runtime.component;
        //todo : ECS패턴으로 리팩토링
        switch (comp.type) {
            case "AutoRotate": {
                const c = comp as AutoRotateComponent;
                entity.rotationZ += c.speed * dt;
                this.renderer.update(entity.id, entity.x, entity.y, entity.z, entity.rotationZ);
                break;
            }
            case "Pulse": {
                const c = comp as PulseComponent;
                const t = (time / 1000) * c.speed;
                const scaleRange = (c.maxScale - c.minScale) / 2;
                const baseScale = (c.maxScale + c.minScale) / 2;
                const currentScale = baseScale + Math.sin(t) * scaleRange;

                entity.scaleX = currentScale;
                entity.scaleY = currentScale;
                this.renderer.setScale(entity.id, entity.scaleX, entity.scaleY, entity.scaleZ);
                // Note: 스케일 업데이트는 렌더러에서 별도 처리 필요
                break;
            }
        }
    }

    // ===== Subscription =====

    /**
     * 상태 변경 구독
     */
    subscribe(callback: () => void): () => void {
        this.listeners.add(callback);
        return () => this.listeners.delete(callback);
    }

    /**
     * 구독자에게 알림
     */
    private notify(): void {
        for (const cb of this.listeners) {
            cb();
        }
    }

    // ===== Lifecycle =====

    /**
     * GameCore 정리
     * 모든 엔티티와 컴포넌트 런타임 해제
     */
    destroy(): void {
        // 1. 모든 엔티티를 렌더러에서 제거
        for (const id of this.entities.keys()) {
            this.renderer.remove(id);
        }

        for (const id of this.projectileRuntimes.keys()) {
            this.renderer.remove(id);
        }

        for (const runtimes of this.moduleRuntimes.values()) {
            for (const runtime of runtimes) {
                runtime.module.destroy();
            }
        }

        // 2. 로컬 상태 정리
        this.entities.clear();
        this.componentRuntimes = [];
        this.moduleRuntimes.clear();
        this.projectileRuntimes.clear();
        this.listeners.clear();

        console.log("[GameCore] Destroyed - all entities and runtimes cleaned up");
    }

    // ===== Serialization (저장/불러오기용) =====

    /**
     * 모든 엔티티 데이터를 JSON으로 직렬화
     */
    serialize(): GameEntity[] {
        return Array.from(this.entities.values());
    }

    /**
     * JSON 데이터로부터 엔티티 복원
     */
    deserialize(data: GameEntity[]): void {
        // 기존 엔티티 정리
        this.destroy();

        // 새 엔티티 생성
        for (const entityData of data) {
            this.createEntity(entityData.id, entityData.type, entityData.x, entityData.y, {
                name: entityData.name,
                z: entityData.z,
                rotationX: entityData.rotationX,
                rotationY: entityData.rotationY,
                rotationZ: entityData.rotationZ,
                scaleX: entityData.scaleX,
                scaleY: entityData.scaleY,
                scaleZ: entityData.scaleZ,
                variables: entityData.variables,
                components: entityData.components,
                modules: entityData.modules,
                rules: entityData.rules,
            });
        }
    }
}
