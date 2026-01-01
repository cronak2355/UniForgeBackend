import { ActionRegistry, type ActionContext } from "../ActionRegistry";
import { Vec3 } from "../../modules/IModule";

/**
 * 기본 액션 등록
 * 모듈들이 로드될 때 이 파일도 함께 로드되어야 합니다.
 */

// --- Kinetic Actions ---

ActionRegistry.register("Move", (ctx: ActionContext, params: Record<string, unknown>) => {
    const kinetic = ctx.modules.Kinetic;
    if (!kinetic) return;

    // 파라미터에서 방향과 힘을 가져옴
    // { x: 1, y: 0, speed: 100 }
    const x = (params.x as number) ?? 0;
    const y = (params.y as number) ?? 0;
    const speed = (params.speed as number) ?? 0;

    // 3D 확장성을 위해 z는 0으로 처리하거나 파라미터에서 받음
    const z = (params.z as number) ?? 0;

    // 힘이나 속도를 적용 (구현에 따라 다름, 여기서는 position 직접 수정보다 force 적용이 물리적으로 자연스러움)
    // 하지만 간편한 조작을 위해 speed * direction을 velocity에 더하거나 설정할 수 있음.
    // KineticModule에 'move' 메서드가 있다면 그것을 사용하는 것이 좋음.
    // 현재 KineticModule은 update에서 키 입력을 처리하지만, 여기서는 직접 명령을 내림.

    // 임시: 위치 직접 이동 (나중에 KineticModule에 move 메서드 추가 권장)
    /* 
    kinetic.position = Vec3.add(kinetic.position, {
        x: x * speed * 0.016, // dt 가정
        y: y * speed * 0.016, 
        z: z 
    });
    */

    // KineticModule에 run 메서드나 force 적용 메서드가 필요함.
    // 현재 구현된 KineticModule을 확인해봐야 함. 일단은 로그만 출력.
    console.log(`[Action] Move: ${x}, ${y} (Speed: ${speed})`);
});

ActionRegistry.register("Jump", (ctx: ActionContext, params: Record<string, unknown>) => {
    const kinetic = ctx.modules.Kinetic;
    if (!kinetic) return;

    const force = (params.force as number) ?? 500;

    // KineticModule의 jump 메서드 호출
    if (typeof kinetic.jump === 'function') {
        kinetic.jump(force);
    }
});

// --- Combat Actions ---

ActionRegistry.register("Attack", (ctx: ActionContext, params: Record<string, unknown>) => {
    const combat = ctx.modules.Combat;
    if (!combat) return;

    // 타겟 위치가 있다면 거기로 공격
    const targetX = params.targetX as number | undefined;
    const targetY = params.targetY as number | undefined;

    if (targetX !== undefined && targetY !== undefined) {
        combat.attack(Vec3.create(targetX, targetY, 0));
    } else {
        // 타겟 없으면 정면 공격 등 기본 동작
        // 예: 현재 보는 방향으로
        combat.attack(Vec3.create(1, 0, 0)); // 임시
    }
});

// --- Status Actions ---

ActionRegistry.register("TakeDamage", (ctx: ActionContext, params: Record<string, unknown>) => {
    const status = ctx.modules.Status;
    if (!status) return;

    const amount = (params.amount as number) ?? 1;
    status.takeDamage(amount);
});

console.log("[DefaultActions] Actions registered.");
