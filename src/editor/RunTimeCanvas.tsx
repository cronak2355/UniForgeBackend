import { colors } from "./constants/colors";

export function RunTimeCanvas() {
    return (
        <div style={{
            flex: 1,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: colors.bgViewport,
            color: colors.textMuted,
            flexDirection: 'column',
            gap: '16px'
        }}>
            <div style={{ fontSize: '48px', opacity: 0.2 }}>
                <i className="fa-solid fa-gamepad"></i>
            </div>
            <div style={{ fontSize: '14px' }}>
                Runtime Mode (Preview)
            </div>
        </div>
    );
}
